package ua.com.lozinska;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SearchingMovie {
    public static final String CREATE_TABLE_MOVIE_QUERY = "CREATE TABLE IF NOT EXISTS movie(id INT PRIMARY KEY AUTO_INCREMENT, "
            + " movie_year INT, movie_name VARCHAR(255), movie_genre VARCHAR(255), movie_rating DOUBLE," +
            " vote_counter INT, movie_description VARCHAR(255))";

    public static void createTable(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute(CREATE_TABLE_MOVIE_QUERY);
        ResultSet resultSet = statement.executeQuery("SELECT * FROM movie");
        if (!resultSet.next()) {
            createTestData(connection);
        }
        statement.close();
    }

    private static void createTestData(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("INSERT INTO movie(movie_year, movie_name, movie_genre, movie_rating," +
                " vote_counter, movie_description) VALUES (2018, 'Movie1', 'Horror', 3, 1, 'Des')");
        statement.close();
    }

    public static void createData(Connection connection) throws SQLException {

        System.out.print("Enter movie year: ");
        int movieYear = ConsoleInput.enterInt();

        System.out.print("Enter movie name: ");
        String movieName = ConsoleInput.enterString();

        System.out.print("Enter movie genre: ");
        MovieGenre movieGenre;
        try {
            movieGenre = MovieGenre.valueOf(ConsoleInput.enterString().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println("Wrong input information!");
            System.out.print("Please, input genre: Drama, Comedy or Horror.");
            return;
        }
        System.out.print("Enter movie description: ");
        String movieDescription = ConsoleInput.enterString();

        String sql = "INSERT INTO movie(movie_year, movie_name, movie_genre, movie_rating," +
                " vote_counter, movie_description) VALUES (?, ?, ?, ?, ?, ?)";

        PreparedStatement statement = connection.prepareStatement(sql);

        statement.setInt(1, movieYear);
        statement.setString(2, movieName);
        statement.setString(3, movieGenre.toString());
        statement.setDouble(4, 0);
        statement.setInt(5, 0);
        statement.setString(6, movieDescription);

        statement.executeUpdate();
        statement.close();
    }

    public static void searchByYear(Connection connection) {
        int movieYearInput;

        System.out.println();
        System.out.println("Searching movie by year:");
        System.out.println("Please, make your choice! Enter the year of movie production:");
        System.out.println();
        System.out.println("To finish the operation, input 0.");

        movieYearInput = ConsoleInput.enterInt();
        if (movieYearInput == 0) {
            return;
        }

        List<Movie> filteredMovies = new ArrayList<>();

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM movie WHERE movie_year = ?");
            statement.setInt(1, movieYearInput);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Movie movie = new Movie(resultSet.getInt(2), resultSet.getString(3),
                        MovieGenre.valueOf(resultSet.getString(4).toUpperCase()), resultSet.getDouble(5),
                        resultSet.getInt(6), resultSet.getString(7));
                filteredMovies.add(movie);
            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }


        if (filteredMovies.isEmpty()) {
            System.out.println();
            System.out.println("Movies with selected year [" + movieYearInput + "]not found");
            System.out.println();
        } else {
            for (Movie movie : filteredMovies) {
                System.out.println(movie);
            }
            System.out.println();
        }
    }

    public static void searchByName(Connection connection) {

        String movieNameInput;
        System.out.println();
        System.out.println("Searching movie by name:");
        System.out.println("Please, input the name of movie:");
        System.out.println();
        System.out.println("To finish the operation, input 0.");

        movieNameInput = ConsoleInput.enterString();

        if (movieNameInput.equals("0")) {
            return;
        }

        Movie filteredMovie = null;

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM movie WHERE movie_name = ?");
            statement.setString(1, movieNameInput);
            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                filteredMovie = new Movie(resultSet.getInt(2), resultSet.getString(3),
                        MovieGenre.valueOf(resultSet.getString(4).toUpperCase()), resultSet.getDouble(5),
                        resultSet.getInt(6), resultSet.getString(7));
            }
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (filteredMovie == null) {
            System.out.println();
            System.out.println("Movie with selected name [" + movieNameInput + "] not found");
            System.out.println();
            return;
        } else {
            System.out.println(filteredMovie);
            System.out.println();
        }

        System.out.println();
        System.out.println("If you want to rate the movie, input 1, to exit, input 0.");
        switch (ConsoleInput.enterString()) {
            case "1":
                setRating(filteredMovie, connection);
                break;
            case "0":
                return;
            default:
                System.out.println();
                System.out.println("Please, input 0 or 1!");
        }
    }

    public static void searchByGenre(Connection connection) {

        System.out.println();
        System.out.println("Searching movie by genre:");
        System.out.println("Please, make your choice! Input genre: COMEDY, DRAMA, or HORROR:");
        System.out.println();
        System.out.println("To finish the operation, input 0.");

        String input = ConsoleInput.enterString();
        if (input.equals("0")) {
            return;
        }

        try {
            MovieGenre movieGenre = MovieGenre.valueOf(input.toUpperCase());

            List<Movie> filteredMovies = new ArrayList<>();

            PreparedStatement statement = null;
            try {
                statement = connection.prepareStatement("SELECT * FROM movie WHERE movie_genre = ?");
                statement.setString(1, input);
                ResultSet resultSet = statement.executeQuery();

                while (resultSet.next()) {
                    Movie movie = new Movie(resultSet.getInt(2), resultSet.getString(3),
                            MovieGenre.valueOf(resultSet.getString(4).toUpperCase()), resultSet.getDouble(5),
                            resultSet.getInt(6), resultSet.getString(7));
                    filteredMovies.add(movie);
                }

                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            if (filteredMovies.isEmpty()) {
                System.out.println();
                System.out.println("Movies with selected genre [" + movieGenre + "] not found");
                System.out.println();
            } else {
                for (Movie movie : filteredMovies) {
                    System.out.println(movie);
                }
                System.out.println();
            }
        } catch (IllegalArgumentException e) {
            System.out.println();
            System.out.println("Please enter a valid genre!");
        }
    }

    public static void searchByRating(Connection connection) {

        System.out.println();
        System.out.println("Searching movie by rating:");
        System.out.println("Please, input movie rating 1..5:");
        System.out.println();
        System.out.println("To finish the operation, input 0.");

        int movieRatingInput = ConsoleInput.enterInt();

        if (movieRatingInput == 0) {
            return;
        }

        if (movieRatingInput > 5) {
            System.out.println();
            System.out.println("Please, input movie rating from 1 to 5!");
            return;
        }

        List<Movie> filteredMovies = new ArrayList<>();

        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement("SELECT * FROM movie WHERE movie_rating >= ?");
            statement.setInt(1, movieRatingInput);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {
                Movie movie = new Movie(resultSet.getInt(2), resultSet.getString(3),
                        MovieGenre.valueOf(resultSet.getString(4).toUpperCase()), resultSet.getDouble(5),
                        resultSet.getInt(6), resultSet.getString(7));
                filteredMovies.add(movie);
            }

            statement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (filteredMovies.isEmpty()) {
            System.out.println();
            System.out.println("Movies with rate [" + movieRatingInput + "] or bigger not found");
            System.out.println();
        } else {
            for (Movie movie : filteredMovies) {
                System.out.println(movie);
            }
            System.out.println();
        }
    }

    public static void setRating(Movie myMovie, Connection connection) {
        int oldVoteCounter = myMovie.getVoteCounter();
        double oldRating = myMovie.getMovieRating();

        System.out.println();
        System.out.println("Input your rating for movie from 1 to 5: ");
        int guestRating = ConsoleInput.enterInt();
        if ((guestRating < 1) || (guestRating > 5)) {
            System.out.println();
            System.out.println("Wrong rating!");
            return;
        }

        myMovie.setMovieRating(((oldRating * oldVoteCounter) + guestRating) / (oldVoteCounter + 1));
        myMovie.setVoteCounter(oldVoteCounter + 1);

        System.out.println();
        System.out.println("New rating = " + myMovie.getMovieRating());
        System.out.println("Voters count = " + myMovie.getVoteCounter());

        try {
            PreparedStatement statement = connection.prepareStatement
                    ("UPDATE movie SET movie_rating=?, vote_counter=? WHERE movie_name=?");

            statement.setDouble(1, myMovie.getMovieRating());
            statement.setInt(2, myMovie.getVoteCounter());
            statement.setString(3, myMovie.getMovieName());
            statement.execute();

        } catch (
                SQLException e) {
            e.printStackTrace();
        }
    }
}

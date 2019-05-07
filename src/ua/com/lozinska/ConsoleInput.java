package ua.com.lozinska;

import java.util.Scanner;

public class ConsoleInput {

    public static String enterString() {
        Scanner scanner = new Scanner(System.in);
        return scanner.nextLine();
    }

    public static int enterInt() {
        Scanner scanner = new Scanner(System.in);
        int i = 0;
        try {
            i = scanner.nextInt();
        } catch (Exception e) {
            System.out.println("Input a number!");
            scanner.next();
        }
        return i;
    }
}

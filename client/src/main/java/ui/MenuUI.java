package ui;

import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.InputMismatchException;
import java.util.Scanner;

public class MenuUI {

    enum NextState {
        PreLogin,
        PostLogin,
        Quit
    }

    private PrintStream out;
    private Scanner in;

    public static void main(String[] args) {
        var menu = new MenuUI();
        menu.startMenuLoop();
    }

    public void startMenuLoop() {
        out = new PrintStream(System.out, true, StandardCharsets.UTF_8);
        in = new Scanner(System.in);

        displayWelcome();

        var nextState = NextState.PreLogin;
        while (nextState != NextState.Quit) {
            switch (nextState) {
                case PreLogin -> nextState = displayPreLoginUI();
                case PostLogin -> nextState = displayPostLoginUI();
            }
        }

        displayGoodbye();
    }

    private void displayWelcome() {
        out.println("Welcome to 240 Chess!");
    }

    private void displayGoodbye() {
        out.println("Goodbye!");
    }

    private NextState displayPreLoginUI() {
        out.println();
        out.println("What would you like to do? (enter a number 1-4)");
        out.println();
        out.println("1. Register");
        out.println("2. Login");
        out.println("3. Quit");
        out.println("4. Help");
        out.print("> ");

        try {
            var input = in.nextInt();
            switch (input) {
                case 1 -> {
                    doRegisterUser();
                    return NextState.PostLogin;
                }
                case 2 -> {
                    doLoginUser();
                    return NextState.PostLogin;
                }
                case 3 -> {
                    return NextState.Quit;
                }
                case 4 -> {
                    out.println(
                            "Enter 1 to register a new user. Enter 2 to login with an existing user. Enter 3 to quit " +
                                    "the program. Enter 4 to display this message.");
                    return NextState.PreLogin;
                }
                default -> {
                    out.println("Please enter a number from 1-4.");
                    return NextState.PreLogin;
                }
            }
        } catch (InputMismatchException ex) {
            out.println("Please enter a number from 1-4.");
            return NextState.PreLogin;
        }
    }

    private void doRegisterUser() {
        out.println("TODO: register user");
    }

    private void doLoginUser() {
        out.println("TODO: login user");
    }

    private NextState displayPostLoginUI() {
        out.println("TODO: post login ui");
        return NextState.PreLogin;
    }
}

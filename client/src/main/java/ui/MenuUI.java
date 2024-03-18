package ui;

import request.LoginRequest;
import request.RegisterRequest;
import response.RegisterResponse;
import serverFacade.ServerFacade;

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
    private final ServerFacade facade = new ServerFacade();
    private String username;
    private String authToken;

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
                    return doRegisterUser();
                }
                case 2 -> {
                    return doLoginUser();
                }
                case 3 -> {
                    return NextState.Quit;
                }
                case 4 -> {
                    out.println("Enter 1 to register a new user. Enter 2 to login with an existing user.");
                    out.println("Enter 3 to quit the program. Enter 4 to display this message.");
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

    private NextState doRegisterUser() {
        out.println("Register new user");
        out.print("Enter username: ");
        String username = in.next();
        out.print("Enter password: ");
        String password = in.next();
        out.print("Enter email: ");
        String email = in.next();
        out.println("Registering user...");

        RegisterRequest request = new RegisterRequest(username, password, email);
        RegisterResponse response = facade.register(request);
        out.println("DEBUG: request: " + request);
        out.println("DEBUG: response: " + response);
        this.username = response.username();
        authToken = response.authToken();
        out.println("User registered!");

        return NextState.PostLogin;
    }

    private NextState doLoginUser() {
        out.println("Login");
        out.print("Enter username: ");
        String username = in.next();
        out.print("Enter password: ");
        String password = in.next();
        out.println("Logging in...");

        var request = new LoginRequest(username, password);
        var response = facade.login(request);
        this.username = response.username();
        authToken = response.authToken();
        out.println("User logged in!");

        return NextState.PostLogin;
    }

    private NextState displayPostLoginUI() {
        out.println();
        out.println("[" + username + "] What would you like to do? (enter a number 1-7)");
        out.println();
        out.println("1. Create game");
        out.println("2. List games");
        out.println("3. Join game");
        out.println("4. Observe game");
        out.println("5. Logout");
        out.println("6. Quit");
        out.println("7. Help");
        out.print("> ");

        try {
            var input = in.nextInt();
            switch (input) {
                case 1 -> {
                    doCreateGame();
                    return NextState.PostLogin;
                }
                case 2 -> {
                    doListGames();
                    return NextState.PostLogin;
                }
                case 3 -> {
                    doJoinGame();
                    return NextState.PostLogin;
                }
                case 4 -> {
                    doObserveGame();
                    return NextState.PostLogin;
                }
                case 5 -> {
                    doLogout();
                    return NextState.PreLogin;
                }
                case 6 -> {
                    return NextState.Quit;
                }
                case 7 -> {
                    out.println(
                            "Enter 1 to create a new game (does not join the game). Enter 2 to list all existing " +
                                    "games.");
                    out.println(
                            "Enter 3 to join an existing game. Enter 4 to observe an existing game. Enter 5 to logout" +
                                    ".");
                    out.println("Enter 6 to quit. Enter 7 to display this message.");
                    return NextState.PostLogin;
                }
                default -> {
                    out.println("Please enter a number from 1-7.");
                    return NextState.PostLogin;
                }
            }
        } catch (InputMismatchException ex) {
            out.println("Please enter a number from 1-7.");
            return NextState.PostLogin;
        }
    }

    private void doCreateGame() {
        out.println("TODO: Create game.");
    }

    private void doListGames() {
        out.println("TODO: List games.");
    }

    private void doJoinGame() {
        out.println("TODO: Join game.");
    }

    private void doObserveGame() {
        out.println("TODO: Observe game.");
    }

    private void doLogout() {
        out.println("TODO: logout.");
    }
}

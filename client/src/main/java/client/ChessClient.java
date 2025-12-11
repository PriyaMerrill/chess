package client;

import java.util.Arrays;
import java.util.Scanner;
import client.ServerFacade.GameData;
import ui.BoardPrinter;

public class ChessClient {
    private final ServerFacade server;
    private final Scanner scanner = new Scanner(System.in);
    private State state = State.LOGGED_OUT;
    private String username = null;
    private GameData[] games = null;

    private enum State {
        LOGGED_OUT,
        LOGGED_IN
    }

    public ChessClient(int port) {
        server = new ServerFacade(port);
    }

    public ChessClient(String serverLink) {
        server = new ServerFacade(serverLink);
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess! Type 'help' to get started.");

        while (true) {
            printAll();
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");

            if (tokens.length == 0 || tokens[0].isEmpty()) {
                continue;
            }

            String rules = tokens[0].toLowerCase();
            String[] arguments = Arrays.copyOfRange(tokens, 1, tokens.length);

            try {
                if (state == State.LOGGED_OUT) {
                    if (!ruleHandling(rules, arguments)) {
                        return; // quit was called
                    }
                } else {
                    loginCall(rules, arguments);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }


    private void printAll() {
        if (state == State.LOGGED_OUT) {
            System.out.print("[LOGGED_OUT] >>> ");
        } else {
            System.out.print("[" + username + "] >>> ");
        }
    }

    private boolean ruleHandling(String command, String[] args) throws Exception {
        switch (command) {
            case "help" -> printLogin();
            case "quit" -> {
                System.out.println("Goodbye!");
                return false;
            }
            case "login" -> login(args);
            case "register" -> register(args);
            default -> System.out.println("Unknown command. Type 'help' for options.");
        }
        return true;
    }

    private void loginCall(String command, String[] args) throws Exception {
        switch (command) {
            case "help" -> postLogin();
            case "logout" -> logout();
            case "create" -> newGame(args);
            case "list" -> games();
            case "play" -> play(args);
            case "observe" -> watch(args);
            case "quit" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Unknown command. Type 'help' for options.");
        }
    }

    private void printLogin() {
        System.out.println("  help - Show available commands");
        System.out.println("  quit - Exit the program");
        System.out.println("  login <USERNAME> <PASSWORD> - Login to your account");
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - Create a new account");
    }

    private void postLogin() {
        System.out.println("  help - Show available commands");
        System.out.println("  logout - Logout of your account");
        System.out.println("  create <NAME> - Create a new game");
        System.out.println("  list - List all games");
        System.out.println("  play <GAME_NUMBER> <WHITE|BLACK> - Join a game");
        System.out.println("  observe <GAME_NUMBER> - Watch a game");
        System.out.println("  quit - Exit the program");
    }

    private void login(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }
        var auth = server.login(args[0], args[1]);
        username = auth.username();
        state = State.LOGGED_IN;
        System.out.println("Logged in as " + username);
    }

    private void register(String[] args) throws Exception {
        if (args.length != 3) {
            System.out.println("Usage: register <USERNAME> <PASSWORD> <EMAIL>");
            return;
        }
        var auth = server.register(args[0], args[1], args[2]);
        username = auth.username();
        state = State.LOGGED_IN;
        System.out.println("Registered and logged in as " + username);
    }

    private void logout() throws Exception {
        server.logout();
        username = null;
        state = State.LOGGED_OUT;
        System.out.println("Logged out");
    }

    private void newGame(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: create <NAME>");
            return;
        }
        var game = server.createGame(args[0]);
        System.out.println("Created game: " + args[0]);
    }

    private void games() throws Exception {
        var gameList = server.games();
        games = gameList.games();
        if (games == null || games.length == 0) {
            System.out.println("No games available.");
            return;
        }
        System.out.println("Games:");
        for (int i = 0; i < games.length; i++) {
            var game = games[i];
            String white = game.whiteUsername() == null ? "OPEN" : game.whiteUsername();
            String black = game.blackUsername() == null ? "OPEN" : game.blackUsername();
            System.out.printf("  %d. %s - White: %s, Black: %s%n", i + 1, game.gameName(), white, black);
        }
    }

    private void play(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: play <GAME_NUMBER> <WHITE|BLACK>");
            return;
        }
        if (games == null) {
            System.out.println("Please list games first.");
            return;
        }
        int numGames;
        try {
            numGames = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number.");
            return;
        }
        if (numGames < 1 || numGames > games.length) {
            System.out.println("Game number out of range.");
            return;
        }
        String color = args[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Color must be WHITE or BLACK.");
            return;
        }

        GameData game = games[numGames - 1];
        server.joinGame(color, game.gameID());
        System.out.println("Joined game as " + color);
        BoardPrinter.printBoard(color.equals("BLACK"));
    }
    private void watch(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: observe <GAME_NUMBER>");
            return;
        }
        if (games == null) {
            System.out.println("Please list games first.");
            return;
        }
        int numGames;
        try {
            numGames = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number.");
            return;
        }
        if (numGames < 1 || numGames > games.length) {
            System.out.println("Game number out of range.");
            return;
        }

        System.out.println("Observing game: " + games[numGames - 1].gameName());
        BoardPrinter.printBoard(false); // white perspective for observers
    }
}


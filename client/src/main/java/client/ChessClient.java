package client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Scanner;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPosition;
import chess.ChessPiece;
import client.ServerFacade.GameData;
import ui.BoardPrinter;

public class ChessClient implements WebSocketFacade.GameHandler {
    private final ServerFacade server;
    private final String serverUrl;
    private final Scanner scanner = new Scanner(System.in);
    private State state = State.LOGGED_OUT;
    private String username = null;
    private String authToken = null;
    private GameData[] games = null;

    private WebSocketFacade ws;
    private ChessGame currentGame = null;
    private int currentGameID = 0;
    private ChessGame.TeamColor playerColor = null;

    private enum State {
        LOGGED_OUT,
        LOGGED_IN,
        GAMEPLAY
    }

    public ChessClient(int port) {
        server = new ServerFacade(port);
        serverUrl = "http://localhost:" + port;
    }

    public ChessClient(String serverUrl) {
        server = new ServerFacade(serverUrl);
        this.serverUrl = serverUrl;
    }

    @Override
    public void gameUpdate(ChessGame game) {
        this.currentGame = game;
        System.out.println();
        BoardPrinter.printBoard(currentGame, playerColor == ChessGame.TeamColor.BLACK);
        printPrompt();
    }

    @Override
    public void message(String message) {
        System.out.println("\n" + message);
        printPrompt();
    }

    private void printPrompt() {
        if (state == State.LOGGED_OUT) {
            System.out.print("[LOGGED_OUT] >>> ");
        } else if (state == State.GAMEPLAY) {
            System.out.print("[GAMEPLAY] >>> ");
        } else {
            System.out.print("[" + username + "] >>> ");
        }
    }

    public void run() {
        System.out.println("♕ Welcome to 240 Chess! Type 'help' to get started.");

        while (true) {
            printPrompt();
            String input = scanner.nextLine().trim();
            String[] tokens = input.split("\\s+");

            if (tokens.length == 0 || tokens[0].isEmpty()) {
                continue;
            }

            String command = tokens[0].toLowerCase();
            String[] args = Arrays.copyOfRange(tokens, 1, tokens.length);

            try {
                if (state == State.LOGGED_OUT) {
                    if (!handlePrelogin(command, args)) {
                        return;
                    }
                } else if (state == State.LOGGED_IN) {
                    handlePostlogin(command, args);
                } else if (state == State.GAMEPLAY) {
                    handleGameplay(command, args);
                }
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    private boolean handlePrelogin(String command, String[] args) throws Exception {
        switch (command) {
            case "help" -> printPreloginHelp();
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

    private void handlePostlogin(String command, String[] args) throws Exception {
        switch (command) {
            case "help" -> printPostloginHelp();
            case "logout" -> logout();
            case "create" -> createGame(args);
            case "list" -> listGames();
            case "play" -> playGame(args);
            case "observe" -> observeGame(args);
            case "quit" -> {
                System.out.println("Goodbye!");
                System.exit(0);
            }
            default -> System.out.println("Unknown command. Type 'help' for options.");
        }
    }

    private void handleGameplay(String command, String[] args) throws Exception {
        switch (command) {
            case "help" -> printGameplayHelp();
            case "redraw" -> redrawBoard();
            case "leave" -> leaveGame();
            case "move" -> makeMove(args);
            case "resign" -> resign();
            case "highlight" -> highlightMoves(args);
            default -> System.out.println("Unknown command. Type 'help' for options.");
        }
    }

    private void printPreloginHelp() {
        System.out.println("  help - Show available commands");
        System.out.println("  quit - Exit the program");
        System.out.println("  login <USERNAME> <PASSWORD> - Login to your account");
        System.out.println("  register <USERNAME> <PASSWORD> <EMAIL> - Create a new account");
    }

    private void printPostloginHelp() {
        System.out.println("  help - Show available commands");
        System.out.println("  logout - Logout of your account");
        System.out.println("  create <NAME> - Create a new game");
        System.out.println("  list - List all games");
        System.out.println("  play <GAME_NUMBER> <WHITE|BLACK> - Join a game");
        System.out.println("  observe <GAME_NUMBER> - Watch a game");
        System.out.println("  quit - Exit the program");
    }

    private void printGameplayHelp() {
        System.out.println("  help - Show available commands");
        System.out.println("  redraw - Redraw the chess board");
        System.out.println("  leave - Leave the game");
        System.out.println("  move <FROM> <TO> [PROMOTION] - Make a move (e.g., move e2 e4)");
        System.out.println("  resign - Forfeit the game");
        System.out.println("  highlight <POSITION> - Show legal moves for a piece (e.g., highlight e2)");
    }

    private void login(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: login <USERNAME> <PASSWORD>");
            return;
        }
        var auth = server.login(args[0], args[1]);
        username = auth.username();
        authToken = auth.authToken();
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
        authToken = auth.authToken();
        state = State.LOGGED_IN;
        System.out.println("Registered and logged in as " + username);
    }

    private void logout() throws Exception {
        server.logout();
        username = null;
        authToken = null;
        state = State.LOGGED_OUT;
        System.out.println("Logged out");
    }

    private void createGame(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: create <NAME>");
            return;
        }
        server.createGame(args[0]);
        System.out.println("Created game: " + args[0]);
    }

    private void listGames() throws Exception {
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

    private void playGame(String[] args) throws Exception {
        if (args.length != 2) {
            System.out.println("Usage: play <GAME_NUMBER> <WHITE|BLACK>");
            return;
        }
        if (games == null) {
            System.out.println("Please list games first.");
            return;
        }
        int gameNum;
        try {
            gameNum = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number.");
            return;
        }
        if (gameNum < 1 || gameNum > games.length) {
            System.out.println("Game number out of range.");
            return;
        }
        String color = args[1].toUpperCase();
        if (!color.equals("WHITE") && !color.equals("BLACK")) {
            System.out.println("Color must be WHITE or BLACK.");
            return;
        }

        GameData game = games[gameNum - 1];
        server.joinGame(color, game.gameID());
        
        playerColor = color.equals("WHITE") ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;
        currentGameID = game.gameID();

        ws = new WebSocketFacade(serverUrl, this);
        ws.connect(authToken, currentGameID);
        
        state = State.GAMEPLAY;
    }

    private void observeGame(String[] args) throws Exception {
        if (args.length != 1) {
            System.out.println("Usage: observe <GAME_NUMBER>");
            return;
        }
        if (games == null) {
            System.out.println("Please list games first.");
            return;
        }
        int gameNum;
        try {
            gameNum = Integer.parseInt(args[0]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid game number.");
            return;
        }
        if (gameNum < 1 || gameNum > games.length) {
            System.out.println("Game number out of range.");
            return;
        }

        GameData game = games[gameNum - 1];
        currentGameID = game.gameID();
        playerColor = null;

        ws = new WebSocketFacade(serverUrl, this);
        ws.connect(authToken, currentGameID);
        
        state = State.GAMEPLAY;
    }

    private void redrawBoard() {
        if (currentGame != null) {
            BoardPrinter.printBoard(currentGame, playerColor == ChessGame.TeamColor.BLACK);
        }
    }

    private void leaveGame() throws Exception {
        ws.leave(authToken, currentGameID);
        try {
            ws.close();
        } catch (Exception e) {

        }
        ws = null;
        currentGame = null;
        currentGameID = 0;
        playerColor = null;
        state = State.LOGGED_IN;
        System.out.println("Left the game.");
    }

    private void makeMove(String[] args) throws Exception {
        if (playerColor == null) {
            System.out.println("Observers cannot make moves.");
            return;
        }
        if (args.length < 2) {
            System.out.println("Usage: move <FROM> <TO> [PROMOTION]");
            return;
        }
        
        ChessPosition from = parsePosition(args[0]);
        ChessPosition to = parsePosition(args[1]);
        
        if (from == null || to == null) {
            System.out.println("Invalid position. Use format like 'e2' or 'a7'.");
            return;
        }
        
        ChessPiece.PieceType promotion = null;
        if (args.length >= 3) {
            promotion = parsePromotion(args[2]);
            if (promotion == null) {
                System.out.println("Invalid promotion piece. Use: queen, rook, bishop, or knight.");
                return;
            }
        }
        
        ChessMove move = new ChessMove(from, to, promotion);
        ws.makeMove(authToken, currentGameID, move);
    }

    private void resign() throws Exception {
        if (playerColor == null) {
            System.out.println("Observers cannot resign.");
            return;
        }
        System.out.print("Are you sure you want to resign? (yes/no): ");
        String confirm = scanner.nextLine().trim().toLowerCase();
        if (confirm.equals("yes")) {
            ws.resign(authToken, currentGameID);
        } else {
            System.out.println("Resignation cancelled.");
        }
    }

    private void highlightMoves(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: highlight <POSITION>");
            return;
        }
        
        ChessPosition pos = parsePosition(args[0]);
        if (pos == null) {
            System.out.println("Invalid position. Use format like 'e2' or 'a7'.");
            return;
        }
        
        if (currentGame == null) {
            System.out.println("No game loaded.");
            return;
        }
        
        Collection<ChessMove> validMoves = currentGame.validMoves(pos);
        BoardPrinter.printBoardWithHighlights(currentGame, playerColor == ChessGame.TeamColor.BLACK, pos, validMoves);
    }

    private ChessPosition parsePosition(String pos) {
        if (pos.length() != 2) {
            return null;
        }
        char colChar = pos.toLowerCase().charAt(0);
        char rowChar = pos.charAt(1);
        
        if (colChar < 'a' || colChar > 'h' || rowChar < '1' || rowChar > '8') {
            return null;
        }
        
        int col = colChar - 'a' + 1;
        int row = rowChar - '0';
        
        return new ChessPosition(row, col);
    }

    private ChessPiece.PieceType parsePromotion(String piece) {
        return switch (piece.toLowerCase()) {
            case "queen", "q" -> ChessPiece.PieceType.QUEEN;
            case "rook", "r" -> ChessPiece.PieceType.ROOK;
            case "bishop", "b" -> ChessPiece.PieceType.BISHOP;
            case "knight", "n" -> ChessPiece.PieceType.KNIGHT;
            default -> null;
        };
    }
}

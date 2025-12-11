package server.websocket;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.GameData;
import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.annotations.OnWebSocketMessage;
import org.eclipse.jetty.websocket.api.annotations.WebSocket;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import java.io.IOException;

@WebSocket
public class WebSocketHandler {

    private final ConnectionManager connections = new ConnectionManager();
    private final DataAccess dataAccess;
    private final Gson gson = new Gson();

    public WebSocketHandler(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    @OnWebSocketMessage
    public void onMessage(Session session, String message) throws IOException {
        UserGameCommand command = gson.fromJson(message, UserGameCommand.class);
        
        switch (command.getCommandType()) {
            case CONNECT -> connect(session, command);
            case MAKE_MOVE -> {
                MakeMoveCommand moveCmd = gson.fromJson(message, MakeMoveCommand.class);
                makeMove(session, moveCmd);
            }
            case LEAVE -> leave(session, command);
            case RESIGN -> resign(session, command);
        }
    }

    private void connect(Session session, UserGameCommand command) throws IOException {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }
            
            GameData game = dataAccess.getGame(command.getGameID());
            if (game == null) {
                sendError(session, "Error: game not found");
                return;
            }

            String username = auth.username();
            connections.add(username, session, command.getGameID());

            // Send game to the connecting user
            var loadGame = new LoadGameMessage(game.game());
            session.getRemote().sendString(gson.toJson(loadGame));

            // Notify others
            String role;
            if (username.equals(game.whiteUsername())) {
                role = "white";
            } else if (username.equals(game.blackUsername())) {
                role = "black";
            } else {
                role = "observer";
            }
            
            String notification = String.format("%s joined the game as %s", username, role);
            var notifyMsg = new NotificationMessage(notification);
            connections.broadcast(command.getGameID(), username, gson.toJson(notifyMsg));
            
        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void makeMove(Session session, MakeMoveCommand command) throws IOException {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }

            GameData gameData = dataAccess.getGame(command.getGameID());
            if (gameData == null) {
                sendError(session, "Error: game not found");
                return;
            }

            ChessGame game = gameData.game();
            String username = auth.username();

            // Check if game is over
            if (game.isGameOver()) {
                sendError(session, "Error: game is over");
                return;
            }

            // Check if it's the player's turn
            ChessGame.TeamColor playerColor = null;
            if (username.equals(gameData.whiteUsername())) {
                playerColor = ChessGame.TeamColor.WHITE;
            } else if (username.equals(gameData.blackUsername())) {
                playerColor = ChessGame.TeamColor.BLACK;
            }

            if (playerColor == null) {
                sendError(session, "Error: you are observing this game");
                return;
            }

            if (game.getTeamTurn() != playerColor) {
                sendError(session, "Error: not your turn");
                return;
            }

            // Make the move
            ChessMove move = command.getMove();
            game.makeMove(move);

            // Update database
            GameData updatedGame = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
            );
            dataAccess.updateGame(updatedGame);

            // Send updated game to all clients
            var loadGame = new LoadGameMessage(game);
            connections.broadcast(command.getGameID(), null, gson.toJson(loadGame));

            // Send move notification
            String moveStr = formatMove(move);
            var notifyMsg = new NotificationMessage(username + " moved " + moveStr);
            connections.broadcast(command.getGameID(), username, gson.toJson(notifyMsg));

            // Check for check/checkmate/stalemate
            ChessGame.TeamColor opponent = playerColor == ChessGame.TeamColor.WHITE 
                ? ChessGame.TeamColor.BLACK : ChessGame.TeamColor.WHITE;
            
            if (game.isInCheckmate(opponent)) {
                var checkmate = new NotificationMessage(opponent + " is in checkmate! " + username + " wins!");
                connections.broadcast(command.getGameID(), null, gson.toJson(checkmate));
                game.setGameOver(true);
                dataAccess.updateGame(updatedGame);
            } else if (game.isInStalemate(opponent)) {
                var stalemate = new NotificationMessage("Stalemate! The game is a draw.");
                connections.broadcast(command.getGameID(), null, gson.toJson(stalemate));
                game.setGameOver(true);
                dataAccess.updateGame(updatedGame);
            } else if (game.isInCheck(opponent)) {
                var check = new NotificationMessage(opponent + " is in check!");
                connections.broadcast(command.getGameID(), null, gson.toJson(check));
            }

        } catch (InvalidMoveException e) {
            sendError(session, "Error: invalid move");
        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void leave(Session session, UserGameCommand command) throws IOException {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }

            String username = auth.username();
            GameData gameData = dataAccess.getGame(command.getGameID());
            
            if (gameData != null) {
                // Remove player from game if they were playing
                String white = gameData.whiteUsername();
                String black = gameData.blackUsername();
                
                if (username.equals(white)) {
                    white = null;
                } else if (username.equals(black)) {
                    black = null;
                }
                
                GameData updatedGame = new GameData(
                    gameData.gameID(),
                    white,
                    black,
                    gameData.gameName(),
                    gameData.game()
                );
                dataAccess.updateGame(updatedGame);
            }

            connections.remove(username);
            
            var notifyMsg = new NotificationMessage(username + " left the game");
            connections.broadcast(command.getGameID(), username, gson.toJson(notifyMsg));

        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void resign(Session session, UserGameCommand command) throws IOException {
        try {
            AuthData auth = dataAccess.getAuth(command.getAuthToken());
            if (auth == null) {
                sendError(session, "Error: unauthorized");
                return;
            }

            String username = auth.username();
            GameData gameData = dataAccess.getGame(command.getGameID());
            
            if (gameData == null) {
                sendError(session, "Error: game not found");
                return;
            }

            // Check if user is a player
            if (!username.equals(gameData.whiteUsername()) && !username.equals(gameData.blackUsername())) {
                sendError(session, "Error: observers cannot resign");
                return;
            }

            ChessGame game = gameData.game();
            
            if (game.isGameOver()) {
                sendError(session, "Error: game is already over");
                return;
            }

            // Mark game as over
            game.setGameOver(true);
            GameData updatedGame = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
            );
            dataAccess.updateGame(updatedGame);

            var notifyMsg = new NotificationMessage(username + " resigned. Game over.");
            connections.broadcast(command.getGameID(), null, gson.toJson(notifyMsg));

        } catch (DataAccessException e) {
            sendError(session, "Error: " + e.getMessage());
        }
    }

    private void sendError(Session session, String message) throws IOException {
        var error = new ErrorMessage(message);
        session.getRemote().sendString(gson.toJson(error));
    }

    private String formatMove(ChessMove move) {
        char startCol = (char) ('a' + move.getStartPosition().getColumn() - 1);
        int startRow = move.getStartPosition().getRow();
        char endCol = (char) ('a' + move.getEndPosition().getColumn() - 1);
        int endRow = move.getEndPosition().getRow();
        return "" + startCol + startRow + " to " + endCol + endRow;
    }
}

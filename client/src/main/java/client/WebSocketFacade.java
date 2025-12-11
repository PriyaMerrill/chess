package client;

import chess.ChessGame;
import chess.ChessMove;
import com.google.gson.Gson;
import jakarta.websocket.Endpoint;
import jakarta.websocket.Session;
import websocket.commands.MakeMoveCommand;
import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

import jakarta.websocket.*;
import java.io.IOException;
import java.net.URI;

public class WebSocketFacade extends Endpoint {

    private Session session;
    private final Gson gson = new Gson();
    private GameHandler gameHandler;

    public interface GameHandler {
        void gameUodate(ChessGame game);
        void message(String message);
    }

    public WebSocketFacade(String url, GameHandler gameHandler) throws Exception {
        this.gameHandler = gameHandler;
        url = url.replace("http", "ws") + "/ws";
        URI uri = new URI(url);
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        this.session = container.connectToServer(this, uri);

        this.session.addMessageHandler(new MessageHandler.Whole<String>() {
            @Override
            public void onMessage(String message) {
                handleMessage(message);
            }
        });
    }

    private void handleMessage(String message) {
        ServerMessage serverMessage = gson.fromJson(message, ServerMessage.class);
        
        switch (serverMessage.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage loadGame = gson.fromJson(message, LoadGameMessage.class);
                gameHandler.gameUodate(loadGame.getGame());
            }
            case ERROR -> {
                ErrorMessage error = gson.fromJson(message, ErrorMessage.class);
                gameHandler.message("Error: " + error.getErrorMessage());
            }
            case NOTIFICATION -> {
                NotificationMessage notification = gson.fromJson(message, NotificationMessage.class);
                gameHandler.message(notification.getMessage());
            }
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
    }

    public void connect(String authToken, int gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.CONNECT, authToken, gameID);
        sendCommand(command);
    }

    public void makeMove(String authToken, int gameID, ChessMove move) throws IOException {
        var command = new MakeMoveCommand(authToken, gameID, move);
        sendCommand(command);
    }

    public void leave(String authToken, int gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.LEAVE, authToken, gameID);
        sendCommand(command);
    }

    public void resign(String authToken, int gameID) throws IOException {
        var command = new UserGameCommand(UserGameCommand.CommandType.RESIGN, authToken, gameID);
        sendCommand(command);
    }

    private void sendCommand(UserGameCommand command) throws IOException {
        String json = gson.toJson(command);
        session.getBasicRemote().sendText(json);
    }

    public void close() throws IOException {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }
}

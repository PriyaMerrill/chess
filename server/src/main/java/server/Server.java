package server;

import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import io.javalin.*;

import dataaccess.DataAccess;
import model.UserData;
import service.Clear;
import service.NewUser;
import model.AuthData;
import server.websocket.WebSocketHandler;

import java.util.Collection;
import java.util.Map;
import model.GameData;
import service.GameService;

public class Server {

    private final Javalin javalin;

    private final DataAccess dataAccess;

    public Server() {
        try {
            dataAccess = new SqlDataAccess();
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to initialize database: " + e.getMessage());
        }

        WebSocketHandler webSocketHandler = new WebSocketHandler(dataAccess);

        javalin = Javalin.create(config -> {
            config.staticFiles.add("web");
            config.jetty.webSocketFactoryConfig(wsConfig -> {
                wsConfig.setIdleTimeout(java.time.Duration.ofMinutes(5));
            });
        }).exception(DataAccessException.class, (e, ctx) -> {
            ctx.status(500);
            ctx.json(Map.of("message", "Error: " + e.getMessage()));
        });

        // WebSocket
        javalin.ws("/ws", ws -> {
            ws.onMessage(ctx -> webSocketHandler.onMessage(ctx.session(), ctx.message()));
        });

        //clear
        javalin.delete("/db", ctx -> {
            Clear clearServe = new Clear(dataAccess);
            clearServe.clear();
            ctx.status(200);
            ctx.json(Map.of());
        });

        //Register
        javalin.post("/user", ctx -> {
            UserData user = ctx.bodyAsClass(UserData.class);
            if (user.username() == null || user.password() == null || user.email() == null) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
                return;
            }
            try {
                NewUser newUser = new NewUser(dataAccess);
                AuthData auth = newUser.register(user);
                ctx.status(200);
                ctx.json(auth);
            } catch (DataAccessException e) {
                if (e.getMessage() != null && e.getMessage().contains("connection")) {
                    throw e;
                }
                ctx.status(403);
                ctx.json(new ErrorResponse("Error: already in use"));
            }
        });

        //Login
        javalin.post("/session", ctx -> {
            UserData login = ctx.bodyAsClass(UserData.class);
            if (login.username() == null || login.password() == null) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
                return;
            }
            try {
                NewUser newUser = new NewUser(dataAccess);
                AuthData auth = newUser.login(login.username(), login.password());
                ctx.status(200);
                ctx.json(auth);
            } catch (DataAccessException e) {
                if (e.getMessage() != null && e.getMessage().contains("connection")) {
                    throw e;
                }
                ctx.status(401);
                ctx.json(new ErrorResponse("Error: unauthorized"));
            }
        });

        //Logout
        javalin.delete("/session", ctx -> {
            String authToken = ctx.header("authorization");
            try {
                NewUser newUser = new NewUser(dataAccess);
                newUser.logout(authToken);
                ctx.status(200);
                ctx.json(Map.of());
            } catch (DataAccessException e) {
                if (e.getMessage() != null && e.getMessage().contains("connection")) {
                    throw e;
                }
                ctx.status(401);
                ctx.json(new ErrorResponse("Error: unauthorized"));
            }
        });

        //games
        javalin.get("/game", ctx -> {
            String authToken = ctx.header("authorization");
            try {
                GameService gameService = new GameService(dataAccess);
                Collection<GameData> games = gameService.games(authToken);
                ctx.status(200);
                ctx.json(new GameResponse(games));
            } catch (DataAccessException e) {
                if (e.getMessage() != null && e.getMessage().contains("connection")) {
                    throw e;
                }
                ctx.status(401);
                ctx.json(new ErrorResponse("Error: unauthorized"));
            }
        });

        //create game
        javalin.post("/game", ctx -> {
            String authToken = ctx.header("authorization");
            record GameRequest(String gameName) {

            }
            GameRequest gameRequest = ctx.bodyAsClass(GameRequest.class);
            if (gameRequest.gameName() == null) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
                return;
            }
            try {
                GameService gameService = new GameService(dataAccess);
                int game = gameService.createGame(authToken, gameRequest.gameName());
                ctx.status(200);
                ctx.json(new CreateGameResponse(game));
            } catch (DataAccessException e) {
                if (e.getMessage() != null && e.getMessage().contains("connection")) {
                    throw e;
                }
                ctx.status(401);
                ctx.json(new ErrorResponse("Error: unauthorized"));
            }
        });

        //join game
        javalin.put("/game", ctx -> {
            String authToken = ctx.header("authorization");
            record JoinRequest(String playerColor, Integer gameID) {
            }
            JoinRequest request = ctx.bodyAsClass(JoinRequest.class);
            if (request.playerColor() == null || request.playerColor().isEmpty() || request.gameID() == null) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
                return;
            }
            if (!request.playerColor().equalsIgnoreCase("WHITE") && !request.playerColor().equalsIgnoreCase("BLACK")) {
                ctx.status(400);
                ctx.json(new ErrorResponse("Error: bad request"));
                return;
            }
            try {
                GameService gameService = new GameService(dataAccess);
                gameService.joinGame(authToken, request.playerColor(), request.gameID());
                ctx.status(200);
                ctx.json(Map.of());
            } catch (DataAccessException e) {
                if (e.getMessage() != null && e.getMessage().contains("connection")) {
                    throw e;
                }
                if (e.getMessage().equals("unauthorized")) {
                    ctx.status(401);
                    ctx.json(new ErrorResponse("Error: unauthorized"));
                } else if (e.getMessage().equals("already taken")) {
                    ctx.status(403);
                    ctx.json(new ErrorResponse("Error: already taken"));
                } else {
                    ctx.status(400);
                    ctx.json(new ErrorResponse("Error: bad request"));
                }
            }
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    record ErrorResponse(String message){

    }
    record GameResponse(Collection<GameData> games){

    }
    record CreateGameResponse(int gameID){

    }
}

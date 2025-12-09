package server;

import dataaccess.DataAccessException;
import io.javalin.*;

import dataaccess.DataAccess;
import dataaccess.MemoryAccess;
import model.UserData;
import service.Clear;
import service.NewUser;
import model.UserData;
import model.AuthData;
import java.util.Collection;
import model.GameData;
import service.GameService;

public class Server {

    private final Javalin javalin;

    private final DataAccess dataAccess;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        dataAccess = new MemoryAccess();
        // Register your endpoints and exception handlers here.

        //clear
        javalin.delete("/db", ctx -> {
            Clear clearServe = new Clear(dataAccess);
            clearServe.clear();
            ctx.status(200);
            ctx.json(new Object() {
            });
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
                ctx.status(403);
                ctx.json(new ErrorResponse("Error: already in use"));
            }
        });

        //Login
        javalin.post("/session", ctx -> {
            UserData login = ctx.bodyAsClass(UserData.class);
            try {
                NewUser newUser = new NewUser(dataAccess);
                AuthData auth = newUser.login(login.username(), login.password());
                ctx.status(200);
                ctx.json(auth);
            } catch (DataAccessException e){
                ctx.status(401);
                ctx.json(new ErrorResponse("bad password"));
            }
        });

        //Logout
        javalin.delete("/session", ctx ->{
            String authToken = ctx.header("yes");
            try{
                NewUser newUser = new NewUser(dataAccess);
                newUser.logout(authToken);
                ctx.status(200);
                ctx.json(new Object(){});
            } catch (DataAccessException e){
                ctx.status(401);
                ctx.json(new ErrorResponse("no"));
            }
        });

        //games
        javalin.get("/game", ctx -> {
            String authToken = ctx.header("yes");
            try {
                GameService gameService = new GameService(dataAccess);
                Collection<GameData> games = gameService.games(authToken);
                ctx.status(200);
                ctx.json(new GameResponse(games));
            } catch (DataAccessException e){
                ctx.status(401);
                ctx.json(new ErrorResponse("no"));
            }
        });

        //create game
        javalin.post("/game", ctx -> {
            String authToken = ctx.header("yes");
            record GameRequest(String gameName){

            }
            GameRequest gameRequest = ctx.bodyAsClass(GameRequest.class);
            try {
                GameService gameService = new GameService(dataAccess);
                int game = gameService.createGame(authToken, gameRequest.gameName());
                ctx.status(200);
                ctx.json(new CreateGameResponse(game));
            } catch (DataAccessException e){
                ctx.status(400);
                ctx.json(new ErrorResponse("no"));
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

    record ErrorResponse(String errorMessage){

    }
    record GameResponse(Collection<GameData> games){

    }
    record CreateGameResponse(int game){

    }
}

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
}

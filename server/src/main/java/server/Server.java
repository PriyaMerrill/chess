package server;

import io.javalin.*;

import dataaccess.DataAccess;
import dataaccess.MemoryAccess;
import service.Clear;

public class Server {

    private final Javalin javalin;

    private final DataAccess dataAccess;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        dataAccess = new MemoryAccess();
        // Register your endpoints and exception handlers here.
        javalin.delete("/db", ctx -> {
            Clear clearServe = new Clear(dataAccess);
            clearServe.clear();
            ctx.status(200);
            ctx.json(new Object(){});
        });
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}

package client;

import org.junit.jupiter.api.*;
import server.Server;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        facade = new ServerFacade(port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clear() throws Exception{
        facade.clear();
    }

    @Test
    void registerWorks() throws Exception {
        var auth = facade.register("player1", "password", "player1@email.com");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals("player1", auth.username());
    }
    @Test
    void doubleRegister() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        assertThrows(Exception.class, () ->{
            facade.register("player1", "password", "player1@email.com");
        });
    }


    @Test
    void loginWorks() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        facade.logout();
        var auth = facade.login("player1", "password");
        assertNotNull(auth);
        assertNotNull(auth.authToken());
        assertEquals("player1", auth.username());
    }
    @Test
    void loginFails() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        facade.logout();
        assertThrows(Exception.class, () -> {
            facade.login("player1", "wrongpassword");
        });
    }


    @Test
    void logoutWorks() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        assertDoesNotThrow(() -> facade.logout());
    }
    @Test
    void logoutFails() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        facade.logout();
        assertThrows(Exception.class, () -> facade.logout());
    }


    @Test
    void createGameWorks() throws Exception {
        facade.register("player1", "password", "player1@email.com");
        var game = facade.createGame("testgame");
        assertNotNull(game);
        assertTrue(game.gameID() > 0);
    }
    @Test
    void createGameFails() throws Exception{
        assertThrows(Exception.class, () -> {
            facade.createGame("testgame");
        });
    }


    @Test
    void gamesWorks() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        facade.createGame("game1");
        facade.createGame("game2");
        var games = facade.games();
        assertNotNull(games);
        assertEquals(2, games.games().length);
    }
    @Test
    void gamesFails() throws Exception{
        assertThrows(Exception.class,() -> {
          facade.games();
        });
    }


    @Test
    void joinGameWorks() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        var game = facade.createGame("testgame");
        assertDoesNotThrow(()-> {
            facade.joinGame("WHITE", game.gameID());
        });
    }
    @Test
    void joinGameFails() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        var game = facade.createGame("testgame");
        facade.joinGame("WHITE", game.gameID());
        assertThrows(Exception.class, () -> {
            facade.joinGame("WHITE", game.gameID());
        });
    }


    @Test
    void clearWorks() throws Exception{
        facade.register("player1", "password", "player1@email.com");
        facade.createGame("testgame");
        assertDoesNotThrow(() -> facade.clear());
        assertDoesNotThrow(() -> facade.register("player1", "password", "player1@email.com"));
    }

}

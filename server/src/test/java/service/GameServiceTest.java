package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTest {
    private MemoryAccess dataAccess;
    private GameService gameService;
    private String authToken;

    @BeforeEach
    void  prepare() throws DataAccessException{
        dataAccess = new MemoryAccess();
        gameService = new GameService(dataAccess);

        UserData userData = new UserData("testuser", "testpassword", "testemail@gmail.com");
        dataAccess.createUser(userData);
        AuthData authData = new AuthData("testtoken", "testuser");
        dataAccess.auth(authData);
        authToken = "testtoken";
    }


    @Test
    void gamesWork() throws DataAccessException{
        var games = gameService.games(authToken);
        assertNotNull(games);
    }
    @Test
    void gamesFail(){
        assertThrows(DataAccessException.class, () -> {
            gameService.games("badtoken");
        });
    }


    @Test
    void createGameWorks() throws DataAccessException{
        int game = gameService.createGame(authToken, "Test Game");
        assertTrue(game > 0);
    }
    @Test
    void createGameFails() {
        assertThrows(DataAccessException.class, () ->{
          gameService.createGame("badtoken", "Test Game");
        });
    }


    @Test
    void joinGameWorks() throws DataAccessException{
        int game = gameService.createGame(authToken, "Test Game");

        assertDoesNotThrow(()-> {
            gameService.joinGame(authToken, "WHITE", game);
        });
    }
    @Test
    void joinGameFails() throws DataAccessException{
        int game = gameService.createGame(authToken, "Test Game");
        gameService.joinGame(authToken, "WHITE", game);

        assertThrows(DataAccessException.class, ()->{
            gameService.joinGame(authToken, "WHITE", game);
        });
    }
}

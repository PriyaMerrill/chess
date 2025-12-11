package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class SqlDataAccessTest {
    private static SqlDataAccess dataAccess;

    @BeforeAll
    static void setup() throws DataAccessException {
        dataAccess = new SqlDataAccess();
    }

    @BeforeEach
    void clear() throws DataAccessException {
        dataAccess.clear();
    }



    @Test
    void clearWorks() throws DataAccessException{
        dataAccess.createUser(new UserData("testuser", "password", "testemail@gmail.com"));
        dataAccess.clear();
        assertNull(dataAccess.getUser("testuser"));
    }



    @Test
    void createUserWorks() throws DataAccessException{
        UserData userData = new UserData("testuser", "password", "testemail@gmail.com");
        assertDoesNotThrow(() -> dataAccess.createUser(userData));
    }
    @Test
    void createDoubleUser() throws DataAccessException{
        UserData userData = new UserData("testuser", "password", "testemail@gmail.com");
        dataAccess.createUser(userData);
        assertThrows(DataAccessException.class, ()-> dataAccess.createUser(userData));
    }
    @Test
    void getUserWorks() throws DataAccessException{
        UserData userData = new UserData("testuser", "password", "testemail@gmail.com");
        dataAccess.createUser(userData);
        UserData user = dataAccess.getUser("testuser");
        assertNotNull(user);
        assertEquals("testuser", user.username());
    }
    @Test
    void getUserFails() throws DataAccessException{
        assertNull(dataAccess.getUser("nonexistent"));
    }



    @Test
    void createAuthWorks() throws DataAccessException{
        AuthData authData = new AuthData("token", "testuser");
        dataAccess.auth(authData);
        assertThrows(DataAccessException.class, () -> dataAccess.auth(authData));
    }
    @Test
    void createDoubleAuth() throws DataAccessException{
        AuthData authData = new AuthData("token", "testuser");
        dataAccess.auth(authData);
        assertThrows(DataAccessException.class, () -> dataAccess.auth(authData));
    }
    @Test
    void getAuthWorks() throws DataAccessException{
        AuthData authData = new AuthData("token", "testuser");
        dataAccess.auth(authData);
        AuthData auth = dataAccess.getAuth("token");
        assertNotNull(auth);
        assertEquals("testuser", auth.username());
    }
    @Test
    void getAuthFails() throws DataAccessException{
        assertNull(dataAccess.getAuth("badtoken"));
    }
    @Test
    void authRemoveWorks() throws DataAccessException{
        AuthData authData = new AuthData("token", "testuser");
        dataAccess.auth(authData);
        dataAccess.authRemove("token");
        assertNull(dataAccess.getAuth("token"));
    }
    @Test
    void authRemoveFails() throws DataAccessException{
        assertDoesNotThrow(() -> dataAccess.authRemove("nonexistent"));
    }



    @Test
    void createGameWorks() throws DataAccessException{
        GameData gameData = new GameData(0, null, null, "Test Game", new ChessGame());
        int game = dataAccess.newGame(gameData);
        assertTrue(game > 0);
    }
    @Test
    void createGameFails(){
        GameData gameData = new GameData(0, null, null, null, new ChessGame());
        assertThrows(DataAccessException.class, () -> dataAccess.newGame(gameData));
    }
    @Test
    void getGameWorks() throws DataAccessException{
        GameData gameData = new GameData(0, null, null, "Test Game", new ChessGame());
        int game = dataAccess.newGame(gameData);
        GameData newGame = dataAccess.getGame(game);
        assertNotNull(newGame);
        assertEquals("Test Game", newGame.gameName());
    }
    @Test
    void getGameFails() throws DataAccessException{
        assertNull(dataAccess.getGame(9999));
    }



    @Test
    void gamesSuccess() throws DataAccessException{
        dataAccess.newGame(new GameData(0, null, null, "Game 1", new ChessGame()));
        dataAccess.newGame(new GameData(0, null, null, "Game 2", new ChessGame()));
        var games = dataAccess.games();
        assertEquals(2, games.size());
    }
    @Test
    void gamesFail() throws DataAccessException{
        var games = dataAccess.games();
        assertTrue(games.isEmpty());
    }



    @Test
    void gameUpdateWorks() throws DataAccessException{
        GameData gameData = new GameData(0, null, null, "Test Game", new ChessGame());
        int game = dataAccess.newGame(gameData);
        GameData gameUp = new GameData(game, "whitePlayer", null, "Test Game", new ChessGame());
        dataAccess.gameUpdate(gameUp);
        GameData gameReturn = dataAccess.getGame(game);
        assertEquals("whitePlayer", gameReturn.whiteUsername());
    }
    @Test
    void gameUpdateFails() throws DataAccessException{
        GameData gameData = new GameData(9999, "white", "black", "Fake Game", new ChessGame());
        assertDoesNotThrow(() -> dataAccess.gameUpdate(gameData));
    }
}

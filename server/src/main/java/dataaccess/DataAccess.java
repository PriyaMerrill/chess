package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import javax.xml.crypto.Data;
import java.util.Collection;


public interface DataAccess {
    void clear() throws DataAccessException;
    void createUser(UserData username) throws DataAccessException;
    UserData getUser(String username) throws DataAccessException;
    void auth(AuthData auth) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void authRemove(String authToken) throws DataAccessException;
    int newGame(GameData game) throws DataAccessException;
    GameData getGame(int game) throws DataAccessException;
    Collection<GameData> games() throws DataAccessException;
    void gameUpdate(GameData game) throws DataAccessException;
}

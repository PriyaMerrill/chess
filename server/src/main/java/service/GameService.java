package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.GameData;
import java.util.Collection;

public class GameService {
    private final DataAccess dataAccess;

    public GameService(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public Collection<GameData> games(String authToken) throws DataAccessException {
        if (dataAccess.getAuth(authToken)==null){
            throw new DataAccessException("no");
        }
        return dataAccess.games();
    }

    public int createGame(String authToken, String gameName) throws DataAccessException {
        if (dataAccess.getAuth(authToken) == null) {
            throw new DataAccessException("unauthorized");
        }
        GameData game = new GameData(0, null, null, gameName, null);
        return dataAccess.createGame(game);
    }
}

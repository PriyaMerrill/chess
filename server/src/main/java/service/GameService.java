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
}

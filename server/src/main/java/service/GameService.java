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
        return dataAccess.newGame(game);
    }

    public void joinGame(String authToken, String color, int game) throws DataAccessException {
        if (dataAccess.getAuth(authToken)==null){
            throw new DataAccessException("unauthorized");
        }
        GameData thisGame = dataAccess.getGame(game);
        if (thisGame == null){
            throw new DataAccessException("bad request");
        }

        String username = dataAccess.getAuth(authToken).username();
        String white = thisGame.whiteUsername();
        String black = thisGame.blackUsername();

        if (color !=null && color.equalsIgnoreCase("WHITE")){
            if (white!=null){
                throw new DataAccessException("already taken");
            }
            white=username;
        } else if (color !=null && color.equalsIgnoreCase("BLACK")){
            if (black != null){
                throw new DataAccessException("already taken");
            }
            black=username;
        }
        GameData update = new GameData(game, white, black, thisGame.gameName(), thisGame.game());
        dataAccess.gameUpdate(update);
    }
}

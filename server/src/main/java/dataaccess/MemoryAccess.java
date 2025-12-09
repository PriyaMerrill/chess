package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MemoryAccess implements DataAccess{
    private Map<String, UserData> users = new HashMap<>();
    private Map<String, AuthData> auths = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();
    private int newGame = 1;

    public void clear(){
        users.clear();
        auths.clear();
        games.clear();
        newGame =1;
    }

    public void createUser(UserData user){
        users.put(user.username(),user);
    }
    public UserData getUser(String username){
        return users.get(username);
    }

    public void auth(AuthData auth){
        auths.put(auth.authToken(), auth);
    }
    public AuthData getAuth(String authToken) {
        return auths.get(authToken);
    }
    public void authRemove(String authToken){
        auths.remove(authToken);
    }

    public int newGame(GameData game){
        int currGame = newGame++;
        GameData nextGame = new GameData(currGame, game.whiteUser(), game.blackUser(), game.gameName(), game.game());
        games.put(currGame, nextGame);
        return currGame;
    }
    public GameData getGame(int game){
        return games.get(game);
    }
    public Collection<GameData> games(){
        return games.values();
    }
    public void gameUpdate(GameData game){
        games.put(game.gameID(), game);
    }
}

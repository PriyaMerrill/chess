package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.util.UUID;

public class NewUser {
    private final DataAccess dataAccess;

    public NewUser(DataAccess dataAccess){
        this.dataAccess = dataAccess;
    }

    public AuthData register(UserData user) throws DataAccessException{
        if(dataAccess.getUser(user.username()) != null){
            throw new DataAccessException("username already exists");
        }
        dataAccess.createUser(user);
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, user.username());
        dataAccess.auth(auth);
        return auth;
    }

    public AuthData login(String username, String password) throws DataAccessException {
        UserData user = dataAccess.getUser(username);

        if (user == null || !BCrypt.checkpw(password, user.password())) {
            throw new DataAccessException("unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, username);
        dataAccess.auth(auth);

        return auth;
    }
    public void logout(String authToken) throws DataAccessException{
        if (dataAccess.getAuth(authToken)==null){
            throw new DataAccessException("no");
        }
        dataAccess.authRemove(authToken);
    }
}

package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.AuthData;
import model.UserData;
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
}

package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAccess;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class NewUserTest {
    private MemoryAccess dataAccess;
    private NewUser user;

    @BeforeEach
    void setup(){
        dataAccess = new MemoryAccess();
        user = new NewUser(dataAccess);
    }


    @Test
    void registerWorks() throws DataAccessException{
        UserData userData = new UserData("testuser", "testpassword", "testemail@gmail.com");
        AuthData authData = user.register(userData);

        assertNotNull(authData);
        assertEquals("testuser", authData.username());
        assertNotNull(authData.authToken());
    }
    @Test
    void registerUserDuplicate() throws DataAccessException{
        UserData userData = new UserData("testuser", "testpassword", "testemail@gmail.com");
        user.register(userData);

        assertThrows(DataAccessException.class, () -> {
            user.register(userData);
        });
    }


    @Test
    void loginWorks() throws DataAccessException {
        UserData userData = new UserData("testuser", "testpassword", "testemail@gmail.com");
        user.register(userData);

        AuthData auth = user.login("testuser", "testpassword");
        assertNotNull(auth);
        assertEquals("testuser", auth.username());
    }
    @Test
    void wrongPassword() throws DataAccessException{
        UserData userData = new UserData("testuser", "testpassword", "testemail@gmail.com");
        user.register(userData);

        assertThrows(DataAccessException.class, () ->{
            user.login("testuser", "wrongpassword");
        });
    }


    @Test
    void logoutWorks() throws DataAccessException{
        UserData userData = new UserData("testuser", "testpassword", "testemail@gmail.com");
        AuthData auth = user.register(userData);

        assertDoesNotThrow(() -> {
            user.logout(auth.authToken());
        });
    }
    @Test
    void logoutFails() {
        assertThrows(DataAccessException.class, () -> {
            user.logout("invalid");
        });
    }
}

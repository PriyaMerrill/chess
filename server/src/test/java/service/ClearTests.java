package service;

import dataaccess.DataAccessException;
import dataaccess.MemoryAccess;
import model.UserData;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

public class ClearTests {
    private MemoryAccess dataAccess;
    private Clear clear;

    @BeforeEach
    void prepare(){
        dataAccess = new MemoryAccess();
        clear = new Clear(dataAccess);
    }

    @Test
    void clearWorks() throws DataAccessException{
        dataAccess.createUser(new UserData("testuser", "testpassword", "testemail@gmail.com"));
        clear.clear();
        assertNull(dataAccess.getUser("testuser"));
    }
}

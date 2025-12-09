package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class Clear {
    private final DataAccess dataAccess;

    public Clear(DataAccess dataAccess){
        this.dataAccess=dataAccess;
    }
    public void clear() throws DataAccessException{
        dataAccess.clear();
    }
}

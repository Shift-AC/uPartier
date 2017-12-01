package com.github.shiftac.upartier.serverdata;
import java.sql.*;
import  com.github.shiftac.upartier.data.*;
import  com.github.shiftac.upartier.serverdata.*;


public class Log {
	/**
     * Attempts to login(or register) use the given {@code LoginInf}. Also sets 
     * status field of users to {@code online} in database.
     * 
     * @throws SQLException if SQLException occured when accessing database files
     * @throws NoSuchUserException if no such user exists or wrong password is given.
     */
	static public void login(LoginInf inf) throws SQLException,NoSuchUserException {
		if(inf.isNewUser==true) {
			InsertUsers.insert(inf);
		}
		else {
			CheckUsers.checkusers(inf);
			
		}
	}
	
	/**
     * Attempts to set the status field of user to {@code offline} in database
     * 
     * @throws SQLException if SQLException occured when accessing database files.
     */
	static public void logout(int id)throws SQLException {
		Update.Updateustate(0, id);
	}
}

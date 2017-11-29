package com.github.shiftac.upartier.serverdata;
import java.sql.*;
import  com.github.shiftac.upartier.data.*;
import  com.github.shiftac.upartier.serverdata.*;


public class log {
	/**
     * Attempts to login(or register) use the given {@code LoginInf}. Also sets 
     * status field of users to {@code online} in database.
     * 
     * @throws SQLException if SQLException occured when accessing database files
     * @throws NoSuchUserException if no such user exists or wrong password is given.
     */
	public void login(LoginInf inf) throws SQLException,NoSuchUserException {
		if(inf.isNewUser==true) {
			InsertUsers u1=new InsertUsers();
			u1.insert(inf);
		}
		else {
			CheckUsers u2=new CheckUsers();
			u2.checkusers(inf);
			
		}
	}
	
	/**
     * Attempts to set the status field of user to {@code offline} in database
     * 
     * @throws IOException if IOException occured when accessing database files.
     */
	public void logout(int id)throws Exception {
		new Update().Updateustate(0, id);
	}
}

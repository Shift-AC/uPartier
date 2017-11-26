package com.github.shiftac.upartier.serverdata;
import  com.github.shiftac.upartier.data.LoginInf;
import  com.github.shiftac.upartier.serverdata.*;

public class log {
	public void login(LoginInf inf) throws Exception {
		if(inf.isNewUser==true) {
			InsertUsers u1=new InsertUsers();
			u1.insert(inf);
		}
		else {
			CheckUsers u2=new CheckUsers();
			u2.checkusers(inf);
			
		}
	}
	
	public void logout(int id)throws Exception {
		new Update().Updateustate(0, id);
	}
}

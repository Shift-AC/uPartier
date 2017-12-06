package com.github.shiftac.upartier.serverdata;
import java.sql.*;
import  com.github.shiftac.upartier.data.LoginInf;
import  com.github.shiftac.upartier.data.NoSuchUserException;

public class CheckUsers {
	static final String url ="jdbc:mysql://162.105.175.115:8040/group4?useSSL=false"; 
	static final String USER ="group4";
	static final String PASS="group4";
	
	static public void checkusers(LoginInf inf) throws NoSuchUserException,SQLException {
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);
			
			
			System.out.println("Creating statement....");
			sql="SELECT UserId,UserPassword FROM user where id=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, inf.id);
			ResultSet rs = stmt.executeQuery(sql);
			if(rs==null) {
				NoSuchUserException e=new NoSuchUserException();
				throw e;
			}
			
			 while(rs.next()) {
				 int id= rs.getInt("UserId");
				 String password=rs.getString("UserPassword");
                 String inpassword=inf.passwd.toString();
                 if(password.equals(inpassword))
                 { 
                	 Update.Updateustate(1, inf.id); //1 for online
                 }
                 else 
                 {NoSuchUserException e=new NoSuchUserException();
                	 throw e;}
			 }
			 
			 rs.close();
			 stmt.close();
			 conn.close();
			 return;
		}
}

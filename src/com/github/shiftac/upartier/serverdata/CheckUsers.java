package com.github.shiftac.upartier.serverdata;
import java.io.IOException;
import java.sql.*;
import  com.github.shiftac.upartier.data.LoginInf;
import  com.github.shiftac.upartier.data.NoSuchUserException;

public class CheckUsers {
	static final String url ="jdbc:mysql://127.0.0.1:3306/upartier?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="tyy971012";
	
	public void checkusers(LoginInf inf) throws Exception {
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);
			
			
			System.out.println("Creating statement....");
			sql="SELECT UserId,UserPassword FROM upartier.user where id=?";
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
                	 new Update().Updateustate(1, inf.id); //1表示在线
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

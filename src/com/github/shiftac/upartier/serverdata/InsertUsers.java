package com.github.shiftac.upartier.serverdata;
import java.io.IOException;
import java.sql.*;
import  com.github.shiftac.upartier.data.LoginInf;


public class InsertUsers {
	static final String url ="jdbc:mysql://127.0.0.1:3306/upartier?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="tyy971012";
	
	static public  void insert(LoginInf inf) throws SQLException {
	Connection conn = null;
	String sql;
	
	try {
		System.out.println("connecting to database....");
		conn = DriverManager.getConnection(url,USER,PASS);
		
		
		System.out.println("Creating statement....");
		sql="insert into upartier.user(UserId,UserPassword) values(?,?) ";
		PreparedStatement stmt = conn.prepareStatement(sql);
		stmt.setInt(1, inf.id);
		String passwd=inf.passwd.toString();
		stmt.setString(2,passwd);
	    stmt.execute(sql);
		 
		 stmt.close();
		 conn.close();
	}
	catch(SQLException e) {
		throw e;
	}
	
	
}

}

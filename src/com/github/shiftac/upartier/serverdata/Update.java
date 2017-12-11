package com.github.shiftac.upartier.serverdata;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class Update {
	static final String url ="jdbc:mysql://localhost:3306/group4?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="group4";

	
	static public void Updateustate(int state,int id) throws SQLException{
		Connection conn = null;
		String sql;
		try {
			System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);
			System.out.println("Creating statement....");
			sql="UPDATE user SET State=? Where UserId=? ";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, state);
			stmt.setInt(2, id);
		    stmt.execute();
			 
			 stmt.close();
			 conn.close();
		}
		catch(SQLException e) {
			throw e;
		}

	}
	
	
}

package com.github.shiftac.upartier.serverdata;
import java.sql.*;


import  com.github.shiftac.upartier.data.*;
public class fetch {
	static final String url ="jdbc:mysql://127.0.0.1:3306/upartier?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="tyy971012";

	public Block[] fetchBlocks() throws SQLException {
		Connection conn = null;
		Statement stmt=null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select top 3 * from upartier.block order By BlockId desc";
			stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			Block[] block=new Block[3];
			int i=0;
			 while(rs.next()) {
				 block[i].id=rs.getInt("BlockId");
				 String name=rs.getString("BlockName");
				 block[i].name=new BString(name);
				 block[i].postCount=rs.getInt("PostCount");
				 i++;
				 }
			 
			 rs.close();
			 stmt.close();
			 conn.close();
			 return block;
		}
	
	public Post[] fetchPostForBlock(int blockid,int count) throws SQLException {
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select * from upartier.post where BlockId = ? order by PostId desc limit ? ";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, blockid);
			stmt.setInt(2, count);
			ResultSet rs = stmt.executeQuery(sql);
			Post[] post=new Post[10];
			int i=0;
			 while(rs.next()) {
				 post[i].id=rs.getInt("PostId");
				 post[i].blockID=rs.getInt("BlockId");
				 post[i].label=new BString(rs.getString("PostLabel"));
				 post[i].name=new BString(rs.getString("PostName"));
				 post[i].note=new BString(rs.getString("PostNote"));
				 post[i].place=new BString(rs.getString("PostPlace"));
				 post[i].time=rs.getLong("Time");
				 i++;
				 }
			 
			 rs.close();
			 stmt.close();
			 conn.close();
			 return post;
		}
	
	public Post[] fetchPostForUser(int userid,int count) throws SQLException,NoSuchUserException {
		Connection conn = null;
		String sql,sql2;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql2="select UserId from upartier.user where UserId=?";
			PreparedStatement stmt2=conn.prepareStatement(sql2);
			stmt2.setInt(1, userid);
			ResultSet rs2 = stmt2.executeQuery(sql2);
			if(rs2==null) 
			{NoSuchUserException e= new NoSuchUserException();
			  throw e;
			}
			
			sql="select * from upartier.post where PostId=(select PostId from upartier.userpost where UserId = ? order by PostId desc) limit ?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			stmt.setInt(2, count);
			ResultSet rs = stmt.executeQuery(sql);
			Post[] post=new Post[10];
			int i=0;
			 while(rs.next()) {
				 post[i].id=rs.getInt("PostId");
				 post[i].blockID=rs.getInt("BlockId");
				 post[i].label=new BString(rs.getString("PostLabel"));
				 post[i].name=new BString(rs.getString("PostName"));
				 post[i].note=new BString(rs.getString("PostNote"));
				 post[i].place=new BString(rs.getString("PostPlace"));
				 post[i].time=rs.getLong("Time");
				 i++;
				 }
			 
			 rs.close();
			 stmt.close();
			 conn.close();
			 return post;
		}
	
	public User[] fetchPostUserList(int id) throws SQLException,NoSuchPostException{
		User[] user=new User[20];
		
		Connection conn = null;
		String sql,sql2;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql2="select UserId from upartier.userpost where PostId=?";
			PreparedStatement stmt2=conn.prepareStatement(sql2);
			stmt2.setInt(1, id);
			ResultSet rs2 = stmt2.executeQuery(sql2);
			if(rs2==null) 
			{NoSuchPostException e= new NoSuchPostException();
			  throw e;
			}
		
			else {	
				int useri;
				int i=0;
		
	        while(rs2.next()) 
	        {   
	        	useri=rs2.getInt("UserId");
	            sql="select * from upartier.user where UserId=?";
				 PreparedStatement stmt=conn.prepareStatement(sql);
	             stmt.setInt(1, useri);
	             ResultSet rs=stmt.executeQuery(sql);
	             while(rs.next()) {
	            	 user[i].age=rs.getInt("Age");
	            	 user[i].gender=rs.getInt("Gender");
	            	 user[i].id=rs.getInt("UserId");
	            	 user[i].mailAccount=new BString(rs.getString("MailAccount"));
	            	 user[i].nickname=new BString(rs.getString("UserNickName"));
	            	 user[i].postCount=rs.getInt("PostCount");
	            	 
	             }
	             i++;
				 }
			
			}
			 
			 rs2.close();
			 stmt2.close();
			 conn.close();
		
		return user;
	}
	
	public MessageInf[] fetchMessage(int id,int count,long time) throws SQLException, NoSuchPostException {
		MessageInf[] messageinf=new MessageInf[count];
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select * from upartier.messageinf where PostId = ? order by Time desc limit ? ";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, id);
			stmt.setInt(2, count);
			ResultSet rs = stmt.executeQuery(sql);
			int i=0;
			 while(rs.next()) {
				 messageinf[0].postID=rs.getInt("PostId");
					messageinf[0].time=rs.getLong("Time");
					Blob b;
					//messageinf[0].type=new byte rs.getBlob("");
				    messageinf[0].userID=rs.getInt("UserId");
				 i++;
				 }
			 
			 rs.close();
			 stmt.close();
			 conn.close();
		
		
		return messageinf;
	}

		
	}



	


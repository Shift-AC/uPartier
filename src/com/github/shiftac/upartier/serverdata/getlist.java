package com.github.shiftac.upartier.serverdata;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import com.github.shiftac.upartier.data.*;

public class getlist {
	static final String url ="jdbc:mysql://localhost:3306/group4?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="group4";

	public static ArrayList<MessageInf> getpmlist(int postid) throws SQLException{
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select * from messageinf where PostId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, postid);
			ResultSet rs = stmt.executeQuery();	
			ArrayList<MessageInf> pmlist =new ArrayList<MessageInf>();
			while(rs.next()) {
				MessageInf minf=new MessageInf(rs.getString("Content"),rs.getInt("UserId"),rs.getInt("PostId"));	
				pmlist.add(minf);
			}
			return pmlist;
	}
	
	
	public static ArrayList<User> getpulist(int postid) throws SQLException, IOException, NoSuchUserException{
		Connection conn = null;
		String sql;
		ArrayList<User> pulist=new ArrayList<User>();
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select UserId from userpost where PostId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, postid);
			ResultSet rs = stmt.executeQuery();	
			while(rs.next()) {
				sql="select * from user where UserId=?";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, rs.getInt("UserId"));
				ResultSet rs2=stmt.executeQuery();
				while(rs2.next()) {
					User user=new User();
					user.age=rs2.getInt("Age");
					user.gender=rs2.getInt("Gender");
					user.id=rs2.getInt("UserId");
					user.mailAccount=new BString(rs2.getString("MailAccount"));
					user.myPosts= getupostlist(user.id);
					user.nickname=new BString(rs2.getString("NickName"));
					user.postCount=rs2.getInt("PostCount");
					user.profile=new Image(rs.getString("Image"));
				pulist.add(user);
				}
			}
		return pulist;
	}
	
	public static ArrayList<Post> getbpostlist(int blockid) throws SQLException, IOException, NoSuchUserException{
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select PostId from userpost where BlockId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, blockid);
			ResultSet rs = stmt.executeQuery();
			ArrayList<Post> postlist =new ArrayList<Post>();
			while(rs.next()) {
				Post post=new Post();
				post.id=rs.getInt("PostId");
				sql="select * from post where PostId=?";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, post.id);
				ResultSet rs2 = stmt.executeQuery();
				while(rs2.next()) {
					post.blockID=rs2.getInt("BlockId");
					post.label=new BString (rs2.getString("PostLabel"));
					post.name=new BString(rs2.getString("PostName"));
					post.note=new BString(rs2.getString("PostNote"));
					post.place=new BString(rs2.getString("PostPlace"));
					post.time=rs2.getLong("Time");
					post.userCount=rs2.getInt("UserCount");
					post.userID=rs.getInt("PostOwnerId");
					post.messages=getpmlist(post.id);
					new Fetch();
					post.postUser=Fetch.fetchProfile(post.userID);
					post.users=getpulist(post.id);
				}
				postlist.add(post);
				
			}
			
		return postlist;
		
	}
	
	public static ArrayList<Post> getupostlist(int userid) throws SQLException, IOException, NoSuchUserException{
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select PostId from userpost where UserId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			ResultSet rs = stmt.executeQuery();
			ArrayList<Post> postlist =new ArrayList<Post>();
			while(rs.next()) {
				Post post=new Post();
				post.id=rs.getInt("PostId");
				sql="select * from post where PostId=?";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, post.id);
				ResultSet rs2 = stmt.executeQuery();
				while(rs2.next()) {
					post.blockID=rs2.getInt("BlockId");
					post.label=new BString (rs2.getString("PostLabel"));
					post.name=new BString(rs2.getString("PostName"));
					post.note=new BString(rs2.getString("PostNote"));
					post.place=new BString(rs2.getString("PostPlace"));
					post.time=rs2.getLong("Time");
					post.userCount=rs2.getInt("UserCount");
					post.userID=rs.getInt("PostOwnerId");
					post.messages=getpmlist(post.id);
					new Fetch();
					post.postUser=Fetch.fetchProfile(post.userID);
					post.users=getpulist(post.id);
				}
				postlist.add(post);
				
			}
			
		return postlist;
	}
}

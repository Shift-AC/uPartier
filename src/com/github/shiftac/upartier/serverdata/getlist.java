package com.github.shiftac.upartier.serverdata;
import java.io.IOException;
import java.sql.*;
import java.util.*;
import com.github.shiftac.upartier.data.*;

public class getlist {
	static final String url ="jdbc:mysql://127.0.0.1:3306/upartier?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="tyy971012";

	public static ArrayList<MessageInf> getpmlist(int postid) throws SQLException{
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select * from upartier.messageinf where PostId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, postid);
			ResultSet rs = stmt.executeQuery(sql);	
			ArrayList<MessageInf> pmlist =new ArrayList<MessageInf>();
			while(rs.next()) {
				MessageInf minf=new MessageInf();
				minf.postID=rs.getInt("PostId");
				minf.time=rs.getLong("Time");
				minf.type=rs.getByte("Type");
				minf.userID=rs.getInt("UserId");	
				pmlist.add(minf);
			}
			return pmlist;
	}
	
	
	public static ArrayList<User> getpulist(int postid) throws SQLException, IOException{
		Connection conn = null;
		String sql;
		ArrayList<User> pulist=new ArrayList<User>();
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select UserId from upartier.userpost where PostId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, postid);
			ResultSet rs = stmt.executeQuery(sql);	
			while(rs.next()) {
				sql="select * from upartier.user where UserId=?";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, rs.getInt("UserId"));
				ResultSet rs2=stmt.executeQuery(sql);
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
	
	public static ArrayList<Post> getbpostlist(int blockid) throws SQLException, IOException{
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select PostId from upartier.userpost where BlockId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, blockid);
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<Post> postlist =new ArrayList<Post>();
			while(rs.next()) {
				Post post=new Post();
				post.id=rs.getInt("PostId");
				sql="select * from upartier.post where PostId=?";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, post.id);
				ResultSet rs2 = stmt.executeQuery(sql);
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
	
	public static ArrayList<Post> getupostlist(int userid) throws SQLException, IOException{
		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select PostId from upartier.userpost where UserId=?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<Post> postlist =new ArrayList<Post>();
			while(rs.next()) {
				Post post=new Post();
				post.id=rs.getInt("PostId");
				sql="select * from upartier.post where PostId=?";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, post.id);
				ResultSet rs2 = stmt.executeQuery(sql);
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

package com.github.shiftac.upartier.serverdata;
import java.io.IOException;
import java.sql.*;


import  com.github.shiftac.upartier.data.*;
public class Fetch {
	static final String url ="jdbc:mysql://127.0.0.1:3306/upartier?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="tyy971012";
	/**
     * Try to fetch all existing post blocks, the {@code Block} objects returned 
     * in this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws SQLException if SQLException occured when accessing database files
	 * @throws IOException 
     */
	static public Block[] fetchBlocks() throws SQLException, IOException {
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
				 new getlist();
				block[i].posts=getlist.getbpostlist(block[i].id);
				 i++;
				 }
			 
			 rs.close();
			 stmt.close();
			 conn.close();
			 return block;
		}
	
	/**
     * Try to fetch last {@code count} posts with id less than {@code id} for a
     * given block id, or fetch all posts if there're not so many. The {@code Post}
     * objects returned by this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws SQLException if SQLException occured when accessing database files.
     * @throws NoSuchBlockException if no such block exists.
     */
	static public Post[] fetchPostForBlock(int blockid, int postid, int count) throws SQLException, NoSuchBlockException {

		Connection conn = null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select * from upartier.post where BlockId = ? and PostId <? order by PostId desc limit ? ";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, blockid);
			stmt.setInt(2, postid);
			stmt.setInt(3, count);
			ResultSet rs = stmt.executeQuery(sql);
			Post[] post=new Post[count];
			int i=0;
			if(rs==null) {
				throw new NoSuchBlockException();
			}
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
	
	/**
     * Try to fetch last {@code count} posts with id less than {@code id} for a
     * given user id, or fetch all remaining posts if there're not so many. The 
     * {@code Post} objects returned by this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws SQLException if SQLException occured when accessing database files.
     * @throws NoSuchUserException if no such user exists.
	 * @throws IOException 
     */
	static public Post[] fetchPostForUser(int userid, int postid, int count) throws SQLException,NoSuchUserException, IOException {

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
			
			sql="select * from upartier.post where PostId=(select PostId from upartier.userpost where UserId = ? and PostId<? order by PostId desc) limit ?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			stmt.setInt(2, postid);
			stmt.setInt(3, count);
			ResultSet rs = stmt.executeQuery(sql);
			Post[] post=new Post[count];
			int i=0;
			 while(rs.next()) {
				 post[i].id=rs.getInt("PostId");
				 post[i].blockID=rs.getInt("BlockId");
				 post[i].label=new BString(rs.getString("PostLabel"));
				 post[i].name=new BString(rs.getString("PostName"));
				 post[i].note=new BString(rs.getString("PostNote"));
				 post[i].place=new BString(rs.getString("PostPlace"));
				 post[i].time=rs.getLong("Time");
				 post[i].userID=rs.getInt("PostOwnerId");
				 new getlist();
				 post[i].messages=getlist.getpmlist(post[i].id);
				 post[i].postUser=fetchProfile(post[i].userID);
				 post[i].userCount=rs.getInt("UserCount");
				 post[i].users=getlist.getpulist(post[i].id);
				 i++;
				 }
			 
			 rs.close();
			 stmt.close();
			 conn.close();
			 return post;
		}
	
	/**
     * Try to fetch user list for a given post id.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws SQLException if SQLException occured when accessing database files.
     * @throws NoSuchPostException if no such post exists.
	 * @throws IOException 
     */
	static public User[] fetchPostUserList(int id) throws SQLException,NoSuchPostException, IOException{
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
	            	 new getlist();
	            	 user[i].myPosts=getlist.getupostlist(user[i].id);
	            	 user[i].postCount=rs.getInt("PostCount");
	            	 user[i].profile=new Image(rs.getString("Image"));
	            	 
	            	 
	             }
	             i++;
				 }
			
			}
			 
			 rs2.close();
			 stmt2.close();
			 conn.close();
		
		return user;
	}
	
	/**
     * Try to fetch last {@code count} messages issued before {@code time} for
     * a given post id. The messages should be stored in {@code messages} in 
     * reverse order.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws SQLException if SQLException occured when accessing database files.
     * @throws NoSuchPostException if no such post exists.
	 * @throws PermissionException if the user hasn't join the post.
     */
	static public MessageInf[] fetchMessage(int id, int userID, int count, long time) throws SQLException, NoSuchPostException, PermissionException{
			MessageInf[] messageinf=new MessageInf[count];
			Connection conn = null;
			String sql;
			System.out.println("connecting to database....");
				conn = DriverManager.getConnection(url,USER,PASS);	
				System.out.println("Creating statement....");
				sql="select * from upartier.messageinf where PostId = ? and Time <? order by Time desc limit ? ";
				PreparedStatement stmt=conn.prepareStatement(sql);
				stmt.setInt(1, id);
				stmt.setLong(2, time);
				stmt.setInt(3, count);
				ResultSet rs = stmt.executeQuery(sql);
				if(rs==null) {throw new NoSuchPostException(); }
				else{
					int i=0;
			
					while(rs.next()) {
					 messageinf[i].postID=rs.getInt("PostId");
						messageinf[i].time=rs.getLong("Time");
						messageinf[i].type= rs.getByte("Type");
					    messageinf[i].userID=rs.getInt("UserId");
					   // messageinf[i].content=rs.getObject(arg0, arg1);
					 i++;
					 }
				}
				 rs.close();
				 stmt.close();
				 conn.close();
			
			return messageinf;
		}
		

		/**
	     * Try to issue a new post. The {@code id}, {@code time} field of the 
	     * parameter {@code Post} will be properly set on successful returns.
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
	     * @throws NoSuchUserException if no such user exists.
	     * @throws NoSuchBlockException if no such block exists.
	     */
		static public void issuePost(Post post)throws SQLException, NoSuchUserException, NoSuchBlockException {
			Connection conn = null;
			String sql;
			System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);
			System.out.println("Creating statement....");
			sql="insert into upartier.post(PostId,BlockId,PostName,Time,PostLabel,PostPlace,PostNote,UserCount,PostOwnerId) values(?,?,?,?,?,?,?,?,?) ";
			PreparedStatement stmt = conn.prepareStatement(sql);
			stmt.setInt(1, post.id);
			stmt.setInt(2, post.blockID);
			stmt.setString(3, post.name.toString());
			stmt.setLong(4, post.time);
			stmt.setString(5, post.label.toString());
			stmt.setString(6, post.place.toString());
			stmt.setString(7, post.note.toString());
			stmt.setInt(8, post.userCount);
			stmt.setInt(9, post.userID);
			//post.users
			//post.messages
			//post.users
		    stmt.executeQuery(sql);
		    for( User postuser:post.users)
		    {
		    	 sql="insert into upartier.userpost(UserId,PostId) value(?,?)";
		    	 PreparedStatement stmt1=conn.prepareStatement(sql);
		    	 stmt.setInt(1, postuser.id);
		    	 stmt.setInt(2, post.id);
		    	 stmt.execute(sql);
		    }
		   
		    
			 stmt.close();
			 conn.close();
			
		}
	
		
		/**
	     * Try to send a reply message under a given post id. Returns list of users of this
	     * post.The number of a Post User must be below 30  
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
	     * @throws NoSuchUserException if no such user exists.
	     * @throws NoSuchPostException if no such post exists.
	     * @throws PermissionException if current user can't send message on this 
	     * post.
	     */
		static public User[] sendMessage(int userid, int postid, MessageInf message) 
				throws SQLException, NoSuchUserException, NoSuchPostException, PermissionException{
			 User[] user=new User[30];
			 int i=0;
			 Connection conn = null;
				String sql;
				System.out.println("connecting to database....");
					conn = DriverManager.getConnection(url,USER,PASS);	
					System.out.println("Creating statement....");
					sql="select * from upartier.user where UserId = ?  ";
					PreparedStatement stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					ResultSet rs = stmt.executeQuery(sql);
					if(rs==null) {throw new NoSuchUserException(); }
					sql="select * from upartier.post where PostId = ? ";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, postid);
					rs=stmt.executeQuery(sql);
					if(rs==null) {throw new NoSuchPostException(); }
					sql="select * from upartier.userpost where UserId = ? and PostId = ?   ";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					stmt.setInt(2, postid);
				    rs = stmt.executeQuery(sql);
					if(rs==null) {throw new PermissionException(); }
					else { 
						while(rs.next()) {
						user[i].age=rs.getInt("Age");
		            	 user[i].gender=rs.getInt("Gender");
		            	 user[i].id=rs.getInt("UserId");
		            	 user[i].mailAccount=new BString(rs.getString("MailAccount"));
		            	 user[i].nickname=new BString(rs.getString("UserNickName"));
		            	 user[i].postCount=rs.getInt("PostCount");					
					}
		
						sql="insert into upartier.messageinf(PostId,UserId,Type,Time) values(?,?,?,?)";
						stmt = conn.prepareStatement(sql);
						stmt.setInt(1, message.postID);
						stmt.setInt(2, message.userID);
						stmt.setByte(3, message.type);
						stmt.setLong(4, message.time);
						stmt.execute(sql);
					    }
			 return user;
		}


		
		 /**
	     * Attempt to join a post, throw an error if after this operation
	     * current user doesn't belong to the post.
	     * 
	     * @throws SQLException if SQLException occured when accessing database files.
	     * @throws NoSuchUserException if no such user exists.
	     * @throws NoSuchPostException if no such post exists.
	     */
		static public void join(int userid,int postid) throws SQLException, NoSuchUserException, NoSuchPostException{
			 Connection conn = null;
				String sql;
				System.out.println("connecting to database....");
					conn = DriverManager.getConnection(url,USER,PASS);	
					System.out.println("Creating statement....");
					sql="insert into upartier.userpost(UserId,PostId) values(?,?)";
					PreparedStatement stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					stmt.setInt(2, postid);
					stmt.execute(sql);
					sql="select PostCount from upartier.user where UserId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					ResultSet rs = stmt.executeQuery(sql);
					int mypostcount =rs.getInt("PostCount");
					mypostcount=mypostcount+1;
					sql="update upartier.user set PostCount=? where UserId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, mypostcount);
					stmt.setInt(2, userid);
					stmt.execute(sql);
		}
	
		 /**
	     * Attempts to fetch user profile for a given user ID.
	     * <p>
	     * Current thread will <b>block</b> inside this call.
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
	     */
	   public static User fetchProfile(int id) throws SQLException{
	    	User user=new User();
	    	 Connection conn = null;
				String sql;
				System.out.println("connecting to database....");
					conn = DriverManager.getConnection(url,USER,PASS);	
					System.out.println("Creating statement....");
	    	 sql="select * from upartier.user where UserId=?";
			 PreparedStatement stmt=conn.prepareStatement(sql);
             stmt.setInt(1, id);
             ResultSet rs = stmt.executeQuery(sql);
             user.age=rs.getInt("Age");
        	 user.gender=rs.getInt("Gender");
        	 user.id=rs.getInt("UserId");
        	 user.mailAccount=new BString(rs.getString("MailAccount"));
        	 user.nickname=new BString(rs.getString("UserNickName"));
        	 user.postCount=rs.getInt("PostCount");
	    	
		   return user;
	    }
		
	   /**
	     * Attempts to fetch user profile who issued the specified post.
	     * <p>
	     * Current thread will <b>block</b> inside this call.
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
	     */
	   public static User fetchIssuerProfile(int id)throws SQLException{
	    	User user = new User();
	    	Connection conn = null;
			String sql;
			System.out.println("connecting to database....");
				conn = DriverManager.getConnection(url,USER,PASS);	
				System.out.println("Creating statement....");
    	 sql="select PostOwnerId from upartier.post where PostId=?";
		 PreparedStatement stmt=conn.prepareStatement(sql);
         stmt.setInt(1, id);
         ResultSet rs = stmt.executeQuery(sql);
         int ownerid=rs.getInt("PostOwnerId");
         sql="select * from upartier.user where UserId=?";
		 stmt=conn.prepareStatement(sql);
         stmt.setInt(1, ownerid);
         rs = stmt.executeQuery(sql);
         user.age=rs.getInt("Age");
    	 user.gender=rs.getInt("Gender");
    	 user.id=rs.getInt("UserId");
    	 user.mailAccount=new BString(rs.getString("MailAccount"));
    	 user.nickname=new BString(rs.getString("UserNickName"));
    	 user.postCount=rs.getInt("PostCount");    	
	    	return user;
	    	}
	   
	   
	   /**
	     * Attempts to modify user profile.
	     * <p>
	     * Current thread will <b>block</b> inside this call.
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
	     */
	    public static void renewProfile(User u) throws SQLException{
	    	Connection conn = null;
			String sql;
			System.out.println("connecting to database....");
				conn = DriverManager.getConnection(url,USER,PASS);	
				System.out.println("Creating statement....");
				sql="update upartier.user set Age=? Gender=? PostCount=? MailAccount=? NickName=? where UserId=?";
				PreparedStatement stmt=conn.prepareStatement(sql);
				stmt.setInt(1, u.age);
				stmt.setInt(2, u.gender);
				stmt.setInt(3, u.postCount);
				stmt.setString(4,u.mailAccount.toString());
				stmt.setString(5,u.nickname.toString());
				stmt.setInt(6, u.id);
				stmt.execute(sql);
	    }
	}





	


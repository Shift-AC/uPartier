package com.github.shiftac.upartier.serverdata;
import java.io.IOException;
import java.sql.*;


import  com.github.shiftac.upartier.data.*;
public class Fetch {
	static final String url ="jdbc:mysql://localhost:3306/group4?useSSL=false"; 
	static final String USER ="root";
	static final String PASS="group4";
	/**
     * Try to fetch all existing post blocks, the {@code Block} objects returned 
     * in this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws SQLException if SQLException occured when accessing database files
	 * @throws IOException 
	 * @throws NoSuchUserException 
     */
	static public Block[] fetchBlocks() throws SQLException, IOException, NoSuchUserException {
		Connection conn = null;
		Statement stmt=null;
		String sql;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql="select * from block order By BlockId desc limit 3";
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
			sql="select * from post where BlockId = ? and PostId <? order by PostId desc limit ? ";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, blockid);
			stmt.setInt(2, postid);
			stmt.setInt(3, count);
			ResultSet rs = stmt.executeQuery();
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
			sql2="select UserId from user where UserId=?";
			PreparedStatement stmt2=conn.prepareStatement(sql2);
			stmt2.setInt(1, userid);
			ResultSet rs2 = stmt2.executeQuery();
			if(rs2==null) 
			{NoSuchUserException e= new NoSuchUserException();
			  throw e;
			}
			
			sql="select * from post where PostId=(select PostId from userpost where UserId = ? and PostId<? order by PostId desc) limit ?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			stmt.setInt(2, postid);
			stmt.setInt(3, count);
			ResultSet rs = stmt.executeQuery();
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
	 * @throws NoSuchUserException 
     */
	static public User[] fetchPostUserList(int id) throws SQLException,NoSuchPostException, IOException, NoSuchUserException{
		User[] user=new User[20];
		
		Connection conn = null;
		String sql,sql2;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql2="select UserId from userpost where PostId=?";
			PreparedStatement stmt2=conn.prepareStatement(sql2);
			stmt2.setInt(1, id);
			ResultSet rs2 = stmt2.executeQuery();
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
	            sql="select * from user where UserId=?";
				 PreparedStatement stmt=conn.prepareStatement(sql);
	             stmt.setInt(1, useri);
	             ResultSet rs=stmt.executeQuery();
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
				sql="select * from messageinf where PostId = ? and Time <? order by Time desc limit ? ";
				PreparedStatement stmt=conn.prepareStatement(sql);
				stmt.setInt(1, id);
				stmt.setLong(2, time);
				stmt.setInt(3, count);
				ResultSet rs = stmt.executeQuery();
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
			sql="insert into post(PostId,BlockId,PostName,Time,PostLabel,PostPlace,PostNote,UserCount,PostOwnerId) values(?,?,?,?,?,?,?,?,?) ";
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
		    stmt.execute();
		    for( User postuser:post.users)
		    {
		    	 sql="insert into userpost(UserId,PostId) value(?,?)";
		    	 stmt=conn.prepareStatement(sql);
		    	 stmt.setInt(1, postuser.id);
		    	 stmt.setInt(2, post.id);
		    	 stmt.execute();
		    }
		    sql="insert into blockpost(BlockId,PostId) value(?,?)";
		    stmt=conn.prepareStatement(sql);
		    stmt.setInt(1, post.blockID);
		    stmt.setInt(2, post.id);
		    stmt.execute();
		    sql="select PostCount from user where UserId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, post.userID);
			ResultSet rs = stmt.executeQuery();
			int mypostcount =rs.getInt("PostCount");
			mypostcount=mypostcount+1;
		    sql="update user set PostCount=? where UserId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, mypostcount);
			stmt.setInt(2, post.userID);
			stmt.execute();
			sql="select PostCount from block where BlockId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, post.blockID);
			rs = stmt.executeQuery();
			mypostcount =rs.getInt("PostCount");
			mypostcount=mypostcount+1;
		    sql="update user set PostCount=? where BlockId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, mypostcount);
			stmt.setInt(2, post.blockID);
			stmt.execute();
		   
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
		 * @throws IOException 
	     */
		static public User[] sendMessage(int userid, int postid, MessageInf message) 
				throws SQLException, NoSuchUserException, NoSuchPostException, PermissionException, IOException{
			 User[] user=new User[30];
			 int i=0;
			 Connection conn = null;
				String sql;
				System.out.println("connecting to database....");
					conn = DriverManager.getConnection(url,USER,PASS);	
					System.out.println("Creating statement....");
					sql="select * from user where UserId = ?  ";
					PreparedStatement stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					ResultSet rs = stmt.executeQuery();
					if(rs==null) {throw new NoSuchUserException(); }
					sql="select * from post where PostId = ? ";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, postid);
					rs=stmt.executeQuery();
					if(rs==null) {throw new NoSuchPostException(); }
					sql="select * from userpost where UserId = ? and PostId = ?   ";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					stmt.setInt(2, postid);
				    rs = stmt.executeQuery();
					if(rs==null) {throw new PermissionException(); }
					else { 
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
		
						sql="insert into messageinf(PostId,UserId,Type,Time) values(?,?,?,?)";
						stmt = conn.prepareStatement(sql);
						stmt.setInt(1, message.postID);
						stmt.setInt(2, message.userID);
						stmt.setByte(3, message.type);
						stmt.setLong(4, message.time);
						stmt.execute();
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
		 * @throws IOException 
	     */
		static public void join(int userid,int postid) throws SQLException, NoSuchUserException, NoSuchPostException, IOException{
			 Connection conn = null;
				String sql;
				System.out.println("connecting to database....");
					conn = DriverManager.getConnection(url,USER,PASS);	
					System.out.println("Creating statement....");
					sql="insert into userpost(UserId,PostId) values(?,?)";
					PreparedStatement stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					stmt.setInt(2, postid);
					stmt.execute();
					sql="select PostCount from user where UserId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					ResultSet rs = stmt.executeQuery();
					int mypostcount =rs.getInt("PostCount");
					mypostcount=mypostcount+1;
					sql="update user set PostCount=? where UserId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, mypostcount);
					stmt.setInt(2, userid);
					stmt.execute();
					sql="select UserCount from post where PostId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, postid);
					rs = stmt.executeQuery();
					int myusercount =rs.getInt("UserCount");
					myusercount=myusercount+1;
					sql="update post set UserCount=? where PostId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, myusercount);
					stmt.setInt(2, postid);
					stmt.execute();
					/*sql="select * from upartier.post where PostId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, postid);
					ResultSet rs2 = stmt.executeQuery(sql);
					Post post=new Post();
					 new getlist();
					while(rs2.next()) {
						post.blockID=rs2.getInt("BlockId");
						post.label=new BString (rs2.getString("PostLabel"));
						post.name=new BString(rs2.getString("PostName"));
						post.note=new BString(rs2.getString("PostNote"));
						post.place=new BString(rs2.getString("PostPlace"));
						post.time=rs2.getLong("Time");
						post.userCount=rs2.getInt("UserCount");
						post.userID=rs.getInt("PostOwnerId");
						post.messages=getlist.getpmlist(post.id);
						new Fetch();
						post.postUser=Fetch.fetchProfile(post.userID);
						post.users=getlist.getpulist(post.id);
					}*/

		}
	
		 /**
	     * Attempts to fetch user profile for a given user ID.
	     * <p>
	     * Current thread will <b>block</b> inside this call.
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
		 * @throws IOException 
		 * @throws NoSuchUserException if there is no such userid
	     */
	   public static User fetchProfile(int id) throws SQLException, IOException, NoSuchUserException{
	    	User user=new User();
	    	 Connection conn = null;
				String sql;
				System.out.println("connecting to database....");
					conn = DriverManager.getConnection(url,USER,PASS);	
					System.out.println("Creating statement....");
	    	 sql="select * from user where UserId=?";
			 PreparedStatement stmt=conn.prepareStatement(sql);
             stmt.setInt(1, id);
             ResultSet rs = stmt.executeQuery();
             if(rs==null) {
            	 throw new NoSuchUserException();
             }
             else 
             {
            	 while(rs.next()) {
             user.age=rs.getInt("Age");
        	 user.gender=rs.getInt("Gender");
        	 user.id=rs.getInt("UserId");
        	 user.mailAccount=new BString(rs.getString("MailAccount"));
        	 user.nickname=new BString(rs.getString("UserNickName"));
        	 user.postCount=rs.getInt("PostCount");
        	 new getlist();
        	 user.myPosts=getlist.getupostlist(user.id);
        	 user.postCount=rs.getInt("PostCount");
        	 user.profile=new Image(rs.getString("Image"));
        	 }
            	 
	    	 return user;
	    	}
             
	    }
		
	   /**
	     * Attempts to fetch user profile who issued the specified post.
	     * <p>
	     * Current thread will <b>block</b> inside this call.
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
	 * @throws IOException 
	 * @throws NoSuchPostException if there is no such postid
	 * @throws NoSuchUserException 
	     */
	   public static User fetchIssuerProfile(int id)throws SQLException, IOException, NoSuchPostException, NoSuchUserException{
	    	User user = new User();
	    	Connection conn = null;
			String sql;
			System.out.println("connecting to database....");
				conn = DriverManager.getConnection(url,USER,PASS);	
				System.out.println("Creating statement....");
    	 sql="select PostOwnerId from post where PostId=?";
		 PreparedStatement stmt=conn.prepareStatement(sql);
         stmt.setInt(1, id);
         ResultSet rs = stmt.executeQuery();
         if(rs==null)
         {
        	 throw new NoSuchPostException();
         }
         else {
         int ownerid=rs.getInt("PostOwnerId");
         sql="select * from user where UserId=?";
		 stmt=conn.prepareStatement(sql);
         stmt.setInt(1, ownerid);
         rs = stmt.executeQuery();
         user.age=rs.getInt("Age");
    	 user.gender=rs.getInt("Gender");
    	 user.id=rs.getInt("UserId");
    	 user.mailAccount=new BString(rs.getString("MailAccount"));
    	 user.nickname=new BString(rs.getString("UserNickName"));
    	 user.postCount=rs.getInt("PostCount");    	
    	 new getlist();
    	 user.myPosts=getlist.getupostlist(user.id);
    	 user.postCount=rs.getInt("PostCount");
    	 user.profile=new Image(rs.getString("Image"));
	    	return user;
	    	  }
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
				sql="update user set Age=? Gender=? PostCount=? MailAccount=? NickName=?  Image=? where UserId=?";
				PreparedStatement stmt=conn.prepareStatement(sql);
				stmt.setInt(1, u.age);
				stmt.setInt(2, u.gender);
				stmt.setInt(3, u.postCount);
				stmt.setString(4,u.mailAccount.toString());
				stmt.setString(5,u.nickname.toString());
				stmt.setString(6, u.profile.name.toString());
				stmt.setInt(7, u.id);
				stmt.execute();
	    }
	}





	


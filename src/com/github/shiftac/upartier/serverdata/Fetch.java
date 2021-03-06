package com.github.shiftac.upartier.serverdata;
import java.io.File;
import java.io.IOException;
import java.sql.*;

import javax.imageio.stream.FileImageOutputStream;

import com.github.shiftac.upartier.LogManager;
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
			sql="select * from block order By BlockId desc";
			stmt=conn.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			int count=0;
			while(rs.next()) {
				count++;
			}
			Block[] block=new Block[count];
			int i=0;
			for(i=0;i<count;i++) {
				block[i]=new Block();
			}
			
			sql="select * from block order By BlockId desc";
			stmt=conn.createStatement();
			rs = stmt.executeQuery(sql);		
			
				i=0;
			 while(rs.next()) {
				 block[i].id=rs.getInt("BlockId");
				 String name=rs.getString("BlockName");
				 block[i].name=new BString(name);
				 block[i].postCount=rs.getInt("PostCount");
				// new getlist();
				//block[i].posts=getlist.getbpostlist(block[i].id);
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
			int i=0;
			if(!rs.first()) {
				throw new NoSuchBlockException();
			}
			rs.previous();
			int realcount=0;
			while((rs.next())&&(realcount<=count)) {
				realcount++;
			}
			Post[] post;
			if(realcount>=count) {
			post=new Post[count];
			for(int j=0;j<count;j++) {
				post[j]=new Post();
			}}
			else {
				post=new Post[realcount];
				for(int j=0;j<realcount;j++) {
					post[j]=new Post();
				}}
			
			sql="select * from post where BlockId = ? and PostId <? order by PostId desc limit ? ";
		    stmt=conn.prepareStatement(sql);
			stmt.setInt(1, blockid);
			stmt.setInt(2, postid);
			stmt.setInt(3, count);
		     rs = stmt.executeQuery();
			 while(rs.next()) {
				 post[i].id=rs.getInt("PostId");
				 post[i].blockID=rs.getInt("BlockId");
				 post[i].label=new BString(rs.getString("PostLabel"));
				 post[i].name=new BString(rs.getString("PostName"));
				 post[i].note=new BString(rs.getString("PostNote"));
				 post[i].place=new BString(rs.getString("PostPlace"));
				 post[i].time=rs.getLong("Time");
				 post[i].userCount=rs.getInt("UserCount");
				 //new getlist();
				//post[i].messages=getlist.getpmlist(post[i].id);
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
			if(!rs2.first()) 
			{NoSuchUserException e= new NoSuchUserException();
			  throw e;
			}
			
			sql="select * from post where PostId=any(select PostId from userpost where UserId = ? and PostId<? order by PostId desc) order by PostId desc  limit ?";
			PreparedStatement stmt=conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			stmt.setInt(2, postid);
			stmt.setInt(3, count);
			ResultSet rs = stmt.executeQuery();
			int realcount=0;
			while(rs.next()) {
				realcount++;
			}
			
			Post[] post;
			if(realcount<count) {
				post=new Post[realcount];
				for(int j=0;j<realcount;j++) {
					post[j]=new Post();
				}
				
			}
			else {
			post=new Post[count];
			for(int j=0;j<count;j++) {
				post[j]=new Post();
			}
			}
			
			sql="select * from post where PostId=any(select PostId from userpost where UserId = ? and PostId<? order by PostId desc)order by PostId desc limit ?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, userid);
			stmt.setInt(2, postid);
			stmt.setInt(3, count);
			rs = stmt.executeQuery();
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
				 //new getlist();
				// post[i].messages=getlist.getpmlist(post[i].id);
				 //post[i].postUser=fetchProfile(post[i].userID);
				 post[i].userCount=rs.getInt("UserCount");
				// post[i].users=getlist.getpulist(post[i].id);
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
		Connection conn = null;
		String sql,sql2;
		System.out.println("connecting to database....");
			conn = DriverManager.getConnection(url,USER,PASS);	
			System.out.println("Creating statement....");
			sql2="select UserId from userpost where PostId=? order by UserId desc";
			PreparedStatement stmt2=conn.prepareStatement(sql2);
			stmt2.setInt(1, id);
			ResultSet rs2 = stmt2.executeQuery();
			if(!rs2.first()) 
			{NoSuchPostException e= new NoSuchPostException();
			  throw e;
			}
			rs2.previous();
				int useri;
				int i=0;
		        int count=0;
		        while(rs2.next()) {
		        	count++;
		        }
		        User[] user=new User[count];
				for(int j=0;j<count;j++) {
					user[j]=new User();
				}
				
				sql2="select UserId from userpost where PostId=?";
			    stmt2=conn.prepareStatement(sql2);
				stmt2.setInt(1, id);
				rs2 = stmt2.executeQuery();
				
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
	            	 //new getlist();
	            	 //user[i].myPosts=getlist.getupostlist(user[i].id);
	            	 //user[i].postCount=rs.getInt("PostCount");
	            	 user[i].profile=new Image(rs.getString("Image"));
	            	 
	            	 
	             }
	             i++;
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
			
			Connection conn = null;
			String sql;
			System.out.println("connecting to database....");
				conn = DriverManager.getConnection(url,USER,PASS);	
				System.out.println("Creating statement....");
				sql="select * from messageinf where PostId =?";
				PreparedStatement stmt=conn.prepareStatement(sql);
				stmt.setInt(1, id);
				ResultSet rs=stmt.executeQuery();
				if(!rs.first()) {
					throw new NoSuchPostException();
				}
				sql="select * from messageinf where PostId = ? and UserId=? and Time <? order by MessageId desc limit ? ";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, id);
                stmt.setInt(2, userID);
				stmt.setLong(3, time);
				stmt.setInt(4, count);
				rs = stmt.executeQuery();
				if(!rs.first()) {throw new PermissionException(); }
				rs.previous();
				int realcount=0;
				while(rs.next()) {
					realcount++;
				}
				MessageInf[] messageinf;
				if(realcount<count) {
					messageinf=new MessageInf[realcount];
					for(int j=0;j<realcount;j++) {
						messageinf[j]=new MessageInf();
					}
				}
				else {
				messageinf=new MessageInf[count];
				for(int j=0;j<count;j++) {
					messageinf[j]=new MessageInf();
				}
				}
            	int i=0;
            	sql="select * from messageinf where PostId = ? and UserId=? and Time <? order by MessageId desc limit ? ";
				stmt=conn.prepareStatement(sql);
				stmt.setInt(1, id);
                stmt.setInt(2, userID);
				stmt.setLong(3, time);
				stmt.setInt(4, count);
				rs = stmt.executeQuery();
			   while(rs.next()) {
					 messageinf[i]=new MessageInf(rs.getString("Content"),rs.getInt("UserId"),rs.getInt("PostId"));
					 i++;
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
			sql="select * from block where BlockId=?";
			PreparedStatement stmt1=conn.prepareStatement(sql); 
			stmt1.setInt(1, post.blockID);
			ResultSet myrs=stmt1.executeQuery(); 
			if(!myrs.first()) {
				throw new NoSuchBlockException();
			}
			sql="select * from user where UserId=?";
			stmt1=conn.prepareStatement(sql);
			stmt1.setInt(1, post.userID);
			myrs=stmt1.executeQuery();
			if(!myrs.first()) {
				throw new NoSuchUserException();
			}
			sql="insert into post(BlockId,PostName,Time,PostLabel,PostPlace,PostNote,UserCount,PostOwnerId) values(?,?,?,?,?,?,?,?) ";
			PreparedStatement stmt = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
			//stmt.setInt(1, post.id);
			stmt.setInt(1, post.blockID);
			stmt.setString(2, post.name.toString());
			Long mytime=LogManager.calendar.getTimeInMillis();
			post.time=mytime;
			stmt.setLong(3, mytime);
			stmt.setString(4, post.label.toString());
			stmt.setString(5, post.place.toString());
			stmt.setString(6, post.note.toString());
			stmt.setInt(7, 1);
			post.userCount=1;
			stmt.setInt(8, post.userID);
			//post.users
			//post.messages
			//post.users
		    stmt.execute();
		    ResultSet rs1 = stmt.getGeneratedKeys(); //get result
		    int mypostid = 10000;
		    while(rs1.next()) {  
		    mypostid = rs1.getInt(1);//get ID  
		    }
		    post.id=mypostid;
		    post.userCount=1;
		    sql="insert into userpost(UserId,PostId) value(?,?)";
		    stmt=conn.prepareStatement(sql);
		    stmt.setInt(1, post.userID);
		    stmt.setInt(2, mypostid);
		    stmt.execute();
		    
		    sql="insert into blockpost(BlockId,PostId) value(?,?)";
		    stmt=conn.prepareStatement(sql);
		    stmt.setInt(1, post.blockID);
		    stmt.setInt(2, mypostid);
		    stmt.execute();
		    sql="select PostCount from user where UserId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, post.userID);
			ResultSet rs = stmt.executeQuery();
			while(rs.next()) {
			int mypostcount =rs.getInt("PostCount");
			mypostcount=mypostcount+1;
		    sql="update user set PostCount=? where UserId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, mypostcount);
			stmt.setInt(2, post.userID);
			stmt.executeUpdate();}
			sql="select PostCount from block where BlockId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, post.blockID);
			rs = stmt.executeQuery();
			while(rs.next()) {
			int mypostcount =rs.getInt("PostCount");
			mypostcount=mypostcount+1;
		    sql="update block set PostCount=? where BlockId=?";
			stmt=conn.prepareStatement(sql);
			stmt.setInt(1, mypostcount);
			stmt.setInt(2, post.blockID);
			stmt.executeUpdate();}
		   
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
					if(!rs.first()) {throw new NoSuchUserException(); }
					sql="select * from post where PostId = ? ";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, postid);
					rs=stmt.executeQuery();
					if(!rs.first()) {throw new NoSuchPostException(); }
					sql="select * from userpost where UserId = ? and PostId = ?   ";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, userid);
					stmt.setInt(2, postid);
				    rs = stmt.executeQuery();
				    if(!rs.first()) {throw new PermissionException(); }
				    rs.previous();
				    int count=0;
				    while(rs.next()) {
				    	count++;
				    }
				    User[] user=new User[count];
					 for(int j=0;j<count;j++) {
						 user[j]=new User();
					 }
					 sql="select UserId from userpost where PostId = ?   ";
					 stmt=conn.prepareStatement(sql);
					 stmt.setInt(1, postid);
					    rs = stmt.executeQuery();
					    while(rs.next()) {
					    int myuserid=rs.getInt("UserId");
					    sql="select * from user where UserId=?";
					    stmt=conn.prepareStatement(sql);
					    stmt.setInt(1, myuserid);
					    ResultSet myrs=stmt.executeQuery();	
						while(myrs.next()) {
						user[i].age=myrs.getInt("Age");
		            	 user[i].gender=myrs.getInt("Gender");
		            	 user[i].id=myrs.getInt("UserId");
		            	 user[i].mailAccount=new BString(myrs.getString("MailAccount"));
		            	 user[i].nickname=new BString(myrs.getString("UserNickName"));
		            	 user[i].postCount=myrs.getInt("PostCount");			
		            	 //new getlist();
		            	 //user[i].myPosts=getlist.getupostlist(user[i].id);
		            	 
		            	 user[i].profile=new Image(myrs.getString("Image"));
					}
						}
		
						sql="insert into messageinf(PostId,UserId,Type,Time,Content) values(?,?,?,?,?)";
						stmt = conn.prepareStatement(sql);
						stmt.setInt(1, message.postID);
						stmt.setInt(2, message.userID);
						stmt.setByte(3, message.type);
						stmt.setLong(4, message.time);
						stmt.setString(5, message.content.toString());
						stmt.execute();
					    
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
					  while(rs.next()) {
					int mypostcount =rs.getInt("PostCount");
					mypostcount=mypostcount+1;
					sql="update user set PostCount=? where UserId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, mypostcount);
					stmt.setInt(2, userid);
					stmt.execute();}
					sql="select UserCount from post where PostId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, postid);
					rs = stmt.executeQuery();
					  while(rs.next()) {
					int myusercount =rs.getInt("UserCount");
					myusercount=myusercount+1;
					sql="update post set UserCount=? where PostId=?";
					stmt=conn.prepareStatement(sql);
					stmt.setInt(1, myusercount);
					stmt.setInt(2, postid);
					stmt.execute();}
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
             if(!rs.first()) {
            	 throw new NoSuchUserException();
             }
             else 
             {
            	 rs.previous();
            	 while(rs.next()) {
             user.age=rs.getInt("Age");
        	 user.gender=rs.getInt("Gender");
        	 user.id=rs.getInt("UserId");
        	 user.mailAccount=new BString(rs.getString("MailAccount"));
        	 user.nickname=new BString(rs.getString("UserNickName"));
        	 user.postCount=rs.getInt("PostCount");
        	 new getlist();
        	 //user.myPosts=getlist.getupostlist(user.id);
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
         if(!rs.first())
         {
        	 throw new NoSuchPostException();
         }
         else {
        	 rs.previous();
        	 int ownerid = 0;
        	 while(rs.next()) {
          ownerid=rs.getInt("PostOwnerId");}
         sql="select * from user where UserId=?";
		 stmt=conn.prepareStatement(sql);
         stmt.setInt(1, ownerid);
         rs = stmt.executeQuery();
         while(rs.next()) {
         user.age=rs.getInt("Age");
    	 user.gender=rs.getInt("Gender");
    	 user.id=rs.getInt("UserId");
    	 user.mailAccount=new BString(rs.getString("MailAccount"));
    	 user.nickname=new BString(rs.getString("UserNickName"));
    	 user.postCount=rs.getInt("PostCount");    	
    	 //new getlist();
    	 //user.myPosts=getlist.getupostlist(user.id);
    	 //user.postCount=rs.getInt("PostCount");
    	 user.profile=new Image(rs.getString("Image"));}
	    	return user;
         }
	    	}
	   
	 
	   public static void saveimage(byte[] data,String path) throws IOException{
		    if(data.length<3||path.equals("")) return;//judge data
		    
		    FileImageOutputStream imageOutput = new FileImageOutputStream(new File(path));//open file
		    imageOutput.write(data, 0, data.length);//write byte
		    imageOutput.close();
		    System.out.println("Make Picture success,Please find image in " + path);
		    
		  }
	   
	   
	   /**
	     * Attempts to modify user profile.
	     * <p>
	     * Current thread will <b>block</b> inside this call.
	     *
	     * @throws SQLException if SQLException occured when accessing database files.
	 * @throws IOException 
	     */
	    public static void renewProfile(User u) throws SQLException, IOException{
	    	Connection conn = null;
			String sql;
			System.out.println("connecting to database....");
				conn = DriverManager.getConnection(url,USER,PASS);	
				System.out.println("Creating statement....");
				sql="update user set Age=?,Gender=?,PostCount=?,MailAccount=?,UserNickName=?,Image=? where UserId=?";
				PreparedStatement stmt=conn.prepareStatement(sql);
				String mypath="/home/shift/Pictures/"+Long.toString(LogManager.calendar.getTimeInMillis())+u.profile.name;
				saveimage(u.profile.payload,mypath);
				stmt.setInt(1, u.age);
				stmt.setInt(2, u.gender);
				stmt.setInt(3, u.postCount);
				stmt.setString(4,u.mailAccount.toString());
				stmt.setString(5,u.nickname.toString());
				stmt.setString(6,mypath);
				stmt.setInt(7, u.id);
				
				stmt.executeUpdate();
	    }
	}





	


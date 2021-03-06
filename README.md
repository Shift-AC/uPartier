# uPartier

__For UI developers: This document was deprecated! use `make javadoc` to view in-date API specification!__

## Common data structure

- For login operation:

    ```java
    class LoginInf
    {
        long id;
        BString passwd;
        boolean isNewUser;
    }
    ```

- For user profile fetch/modify:

    ```java
    
    class User
    {
        int id;
        int age;
        int gender;
        BString mailAccount;
        BString nickname;
        Image profile;
        int postCount;
        ArrayList<Post> myPosts;
    }
    ```

- For block fetch:

    ```java
    class Block
    {
        int id;
        BString name;
        int postCount;
        ArrayList<Post> posts;
    }
    ```

- For post fetch:

    ```java
    class Post
    {
        int id;
        int blockID;
        BString name;
        long time;
        BString label;
        BString place;
        BString note;
        User postUser;
        ArrayList<ByteArrayIO> messages;
        int userCount;
        ArrayList<User> users;
    }
    ```

- For file transfer:
    
    ```java
    class GeneralFile
    {
        byte type;
        String path;
    }

    class Image
    {
        String path;
    }
    ```

- For messages

    ```java
    class MessageInf
    {
        int userID;
        int postID;
        byte type;
        long time;
        ByteArrayIO content;
    }
    ```

### Cross-layer methods

- register/login, fetch/modify user profile

    API for server Net:
    ```java
    // class ?
    /**
     * Attempts to login(or register) use the given {@code LoginInf}. Also sets 
     * status field of users to {@code online} in database.
     * 
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchUserException if no such user exists or wrong password is given.
     */
    void login(LoginInf inf)
        throws IOException, NoSuchUserException;

    // class ?
    /**
     * Attempts to set the status field of user to {@code offline} in database.
     * 
     * @throws IOException if IOException occured when accessing database files.
     */
    void logout(int id)
        throws IOException;

    // class ?
    /**
     * Attempts to fetch user profile for a given user ID.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if IOException occured when accessing database files.
     */
    User fetchProfile(int id)
        throws IOException;

    // class ?
    /**
     * Attempts to fetch user profile who issued the specified post.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if IOException occured when accessing database files.
     */
    User fetchIssuerProfile(int postID)
        throws IOException;

    // class ?
    /**
     * Attempts to modify user profile.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if IOException occured when accessing database files.
     */
    void renewProfile(User profile)
        throws IOException;
    ```

- prefetch block

    API for Server Net:
    ```java
    // class ?
    /**
     * Try to fetch all existing post blocks, the {@code Block} objects returned 
     * in this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if IOException occured when accessing database files.
     */
    Block[] fetchBlocks()
        throws IOException;
    ```

- prefetch post

    API for Server Net:
    ```java
    // class ?
    /**
     * Try to fetch last {@code count} posts with id less than {@code id} for a
     * given user id, or fetch all remaining posts if there're not so many. The 
     * {@code Post} objects returned by this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchUserException if no such user exists.
     */
    Post[] fetchPostForUser(int id, int count, int id)
        throws IOException, NoSuchUserException;

    // class ?
    /**
     * Try to fetch last {@code count} posts with id less than {@code id} for a
     * given block id, or fetch all posts if there're not so many. The {@code Post}
     * objects returned by this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchBlockException if no such block exists.
     */
    Post[] fetchPostForBlock(int id, int count, int id)
        throws IOException, NoSuchBlockException;
    ```

- fetch posts


    API for Server Net
    ```java
    // class ?
    /**
     * Try to fetch user list for a given post id.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchPostException if no such post exists.
     */
    User[] fetchPostUserList(int id)
        throws IOException, NoSuchPostException;

    // class ?
    /**
     * Try to fetch last {@code count} messages issued before {@code time} for
     * a given post id. The messages should be stored in {@code messages} in 
     * reverse order.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchPostException if no such post exists.
	 * @throws PermissionException if the user hasn't join the post.
     */
    MessageInf[] fetchMessage(int id, int userID, int count, long time)
        throws IOException, NoSuchPostException;
    ```

- For modifying posts

    API for Server Net
    ```java
    // class ?
    /**
     * Try to issue a new post. The {@code id}, {@code time} field of the 
     * parameter {@code Post} will be properly set on successful returns.
     *
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSucBlockException if no such block exists.
     */
    void issuePost(Post post)
        throws IOException, NoSuchUserException, NoSuchBlockException;

    // class ?
    /**
     * Try to send a reply message under a given post id. Returns list of users of this
     * post.
     *
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSuchPostException if no such post exists.
     * @throws PermissionException if current user can't send message on this 
     * post.
     */
    User[] sendMessage(int userid, int postid, MessageInf message)
        throws IOException, NoSuchUserException, NoSuchPostException,
        PermissionException;

    // class ?
    /**
     * Attempt to join a post, throw an error if after this operation
     * current user doesn't belong to the post.
     * 
     * @throws IOException if IOException occured when accessing database files.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSuchPostException if no such post exists.
     */
    public void join(int userid, int postid)
        throws IOException, NoSuchUserException, NoSuchPostException
    ```
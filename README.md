# uPartier

## Common data structure

- `Prefetchable` interface

    ```java
    public interface Prefetchable
    {
        public void prefetch();
        public void fetchAll();
    }
    ```


- For login operation:

    ```java
    class LoginInf
    {
        long id;
        String passwd;
        boolean isNewUser;
    }
    ```

- For user profile fetch/renew:

    ```java
    interface Gender
    {
        public static final int male = 0;
        public static final int female = 1;
        public static final int unknown = 2;
    }
    
    class UserProfile
    {
        long id;
        int age;
        int gender;
        String mailAccount;
        String nickname;
        Image profile;
        ArrayList<Post> myPosts;
    }
    ```

- For block fetch:

    ```java
    class Block
    {
        int id;
        String name;
        ArrayList<Post> posts;
    }
    ```

- For post fetch:

    ```java
    class Post
    {
        int id;
        String name;
        long time;
        String label;
        String location;
        String note;
        User postUser;
        ArrayList<Message> messages;
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
    class Message
    {
        byte type;
        Object content;
    }
    ```

### Cross-layer methods

- `register/login`

    Item | Detail
    :---:|:-----:
    From | UI layer  
    Dest | Server Database  
    Type | Fetch
    Data layer API | Bypass
    Net layer API | `UserProfile Client.login(LoginInf inf)`
    
    API detail:
    ```java
    // class Client
    /**
     * Attempts to login(or register) use the given { @code LoginInf },
     * try to fetch { @code UserProfile } for the user if login succeeded.
     * 
     * @throws IOException if network exceptions occured.
     * @throws AuthenticateException if no such user exists or wrong 
     * password is given.
     *
     * @return 
     */
    UserProfile login(LoginInf inf)
        throws IOException, AuthenticateException
    {

    }
    ```


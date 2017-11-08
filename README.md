# uPartier

## Common data structure

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
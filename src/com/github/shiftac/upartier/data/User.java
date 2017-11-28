package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.ByteArrayIOList;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.app.Client;

/**
 * Information about a single user.
 * 
 * when transferring as bytes using ByteArrayIO:
 * <pre>
 * class User
 * {
 *     int id;
 *     int age;
 *     int postCount;
 *     byte gender;
 *     BString mailAccount;
 *     BString nickname;
 *     Image profile;
 * }
 * </pre>
 */
public class User implements ByteArrayIO, PacketGenerator
{
    public static interface Gender
    {
        public static final int male = 0;
        public static final int female = 1;
        public static final int unknown = 2;
    }

    public int id = 0;
    public int age = 0;
    public int gender = Gender.unknown;
    public BString mailAccount = null;
    public BString nickname = null;
    public Image profile = null;
    public int postCount = 0;
    public ArrayList<Post> myPosts = null;

    public void setMailAccount(String mail)
    {
        this.mailAccount.setContent(mail);
    }

    public void setNickname(String name)
    {
        this.nickname.setContent(name);
    }

    /**
     * Attempts to login(or register) use the given {@code LoginInf}, try to 
     * fetch {@code User} for the user if login succeeded.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists or wrong password is 
     * given.
     */
    public static User login(LoginInf inf)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        Client.client.init(inf);
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
        {
            User res = new User();
            res.read(pak);
            return res;
        }
        case PacketType.TYPE_SERVER_ACK:
        {            
            ACKInf res = new ACKInf();
            res.read(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRPERMISSION:
                throw new NoSuchUserException("User ID & password not match.");
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * Attempts to log out the current user.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists or wrong password is given.
     */
    public void logout()
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        Packet pak = new AES128Packet();
        ((AES128Packet)pak).setLen(8);
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_SERVER_ACK:
            ACKInf res = new ACKInf();
            res.read(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_SUCC:
                Client.client.terminate();
                return;
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRPERMISSION:
                throw new NoSuchUserException("Attempting to logout other's ID!");
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * Attempts to fetch user profile for a given user ID.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists.
     */
    public static User fetchProfile(int id)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        UserFetchInf inf = new UserFetchInf();
        inf.id = id;
        inf.type = UserFetchInf.ID;
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_USER_FETCH:
        {
            User res = new User();
            res.read(pak);
            return res;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf();
            res.read(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRUSER:
                throw new NoSuchUserException("User #" + id + " not found.");
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * Attempts to modify user profile.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't get reply from server after
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists.
     * @throws PermissionException if {@code this} is not current user.
     */
    public void modify()
        throws IOException, SocketTimeoutException, NoSuchUserException,
        PermissionException
    {
        Packet pak = this.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf();
            res.read(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_SUCC:
                return;
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRPERMISSION:
                throw new PermissionException("Modifying other's profile!");
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * Try to fetch last {@code count} posts with id less than {@code id} issued
     * by current user, or fetch all remaining posts if there're not so many. The 
     * {@code Post} objects returned by this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists.
     */
    public void fetchMyPosts(int count)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        PostFetchInf inf = new PostFetchInf();
        inf.count = count;
        if (myPosts == null)
        {
            inf.token = 2147483647;
        }
        else
        {
            inf.token = myPosts.get(myPosts.size() - 1).id;
        }
        inf.user = this.id;
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_POST_FETCH:
        {
            ByteArrayIOList<Post> res = new ByteArrayIOList<Post>();
            res.read(pak);
            synchronized (myPosts)
            {
                myPosts.addAll(Arrays.asList(res.arr));
            }
            break;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf();
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRUSER:
                throw new NoSuchUserException("User #" + id + " not found.");
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * Try to issue a new post. The {@code id}, {@code time}, 
     * {@code postUser} field of the parameter {@code Post} will be
     * properly set on successful returns.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSuchBlockException if no such block exists.
     */
    public void issue(Post post)
        throws IOException, NoSuchUserException, NoSuchBlockException,
        SocketTimeoutException
    {
        Packet pak = post.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_POST_MODIFY:
        {
            post.read(pak);
            if (post.userID == this.id)
            {
                synchronized (myPosts)
                {
                    if (myPosts == null)
                    {
                        myPosts = new ArrayList<Post>();
                    }
                    myPosts.add(0, post);
                }
                return;
            }
            else
            {
                throw new IOException(String.format(
                    "Mysterious userID %d!(expected %d)", 
                    this.id, post.userID));
            }
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf();
            res.read(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRBLOCK:
                throw new NoSuchBlockException("Block #" + post.blockID +
                    " not found.");
            // We don't expect this to happen here since the server instead
            // of us decides the ID of the user who's issuing this post.
            //case ACKInf.RET_ERRPERMISSION:
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * Try to send a reply message under a given post. This also modify 
     * {@code messages} field of the parameter {@code Post}. On successful 
     * returns {@code time} field of parameter {@code message} will be
     * properly set.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSuchPostException if no such post exists.
     * @throws PermissionException if current user can't send message on this 
     * post.
     */
    public void sendMessage(Post post, MessageInf message)
        throws IOException, NoSuchUserException, NoSuchPostException,
        SocketTimeoutException, PermissionException
    {
        message.postID = post.id;
        message.userID = this.id;
        Packet pak = message.toPacket();
        pak.type = PacketType.TYPE_MESSAGE_PUSH;
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf();
            res.read(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRPOST:
                throw new NoSuchPostException("Post #" + message.postID
                    + " not found.");
            case ACKInf.RET_ERRUSER:
                throw new NoSuchUserException("User #" + message.userID
                    + " not found.");
            case ACKInf.RET_ERRPERMISSION:
                throw new PermissionException(String.format(
                    "User #%d have no permission to issue message to post #%d!",
                    message.userID, message.postID));
            default:
                if (res.retval > 0)
                {
                    message.time = res.retval;
                    synchronized (post.messages)
                    {
                        if (post.messages == null)
                        {
                            post.messages = new ArrayList<MessageInf>();
                        }
                        // this is ugly and inefficient. fix this later.
                        post.messages.add(0, message);
                    }
                    return;
                }
                else
                {
                    throw new IOException("Server returning unknown ack value("
                        + res.retval + ")!");
                }
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    @Override
    public int getLength()
    {
        return 16 + mailAccount.getLength() + nickname.getLength() + 
            profile.getLength();
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_BYTE + SIZE_INT * 3);
        id = getInt(buf, off);
        age = getInt(buf, off += SIZE_INT);
        gender = buf[off += SIZE_INT];
        mailAccount.read(buf, off += SIZE_BYTE, 
            len -= SIZE_INT * 3 + SIZE_BYTE);
        nickname.read(buf, off += mailAccount.getLength(), 
            len -= mailAccount.getLength());
        profile.read(buf, off += nickname.getLength(), 
            len -= nickname.getLength());
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_BYTE + SIZE_INT * 3);
        setInt(buf, off, id);
        setInt(buf, off += SIZE_INT, age);
        buf[off += SIZE_INT] = (byte)gender;
        mailAccount.write(buf, off += SIZE_BYTE, 
            len -= SIZE_INT * 3 + SIZE_BYTE);
        nickname.write(buf, off += mailAccount.getLength(), 
            len -= mailAccount.getLength());
        profile.write(buf, off += nickname.getLength(), 
            len -= nickname.getLength());
    }

    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this, PacketType.TYPE_USER_MODIFY);
    }
}
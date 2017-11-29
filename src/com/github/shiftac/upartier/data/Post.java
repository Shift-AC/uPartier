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
 * Information about post.
 * 
 * when transferring as bytes using ByteArrayIO:
 * <pre>
 * class Post
 * {
 *     int id;
 *     int blockID;
 *     long time;
 *     int userCount;
 *     BString name;
 *     BString label;
 *     BString place;
 *     BString note;
 * }
 * </pre>
 */
public class Post implements ByteArrayIO, PacketGenerator
{
    public int id = 0;
    public int blockID = 0;
    public BString name = null;
    public long time = 0;
    public BString label = null;
    public BString place = null;
    public BString note = null;
    public User postUser = null;
    public ArrayList<MessageInf> messages = null;
    public int userCount = 0;
    public ArrayList<User> users = null;

    public Post() {}

    public Post(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    @Override
    public int getLength()
    {
        return SIZE_INT * 4 + SIZE_LONG +
            name.getLength() + label.getLength() + place.getLength() +
            note.getLength();
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT * 4 + SIZE_LONG);
        setInt(buf, off, id);
        setInt(buf, off += SIZE_INT, blockID);
        setLong(buf, off += SIZE_INT, time);
        setInt(buf, off += SIZE_LONG, userCount);
        name.write(buf, off += SIZE_INT, len -= SIZE_INT * 4 + SIZE_LONG);
        label.write(buf, off += name.getLength(), len -= name.getLength());
        place.write(buf, off += label.getLength(), len -= label.getLength());
        note.write(buf, off += place.getLength(), len -= place.getLength());
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT * 4 + SIZE_LONG);
        id = getInt(buf, off);
        blockID = getInt(buf, off += SIZE_INT);
        time = getLong(buf, off += SIZE_INT);
        userCount = getInt(buf, off += SIZE_LONG);
        name.read(buf, off += SIZE_INT, len -= SIZE_INT * 3 + SIZE_LONG);
        label.read(buf, off += name.getLength(), len -= name.getLength());
        place.read(buf, off += label.getLength(), len -= label.getLength());
        note.read(buf, off += place.getLength(), len -= place.getLength());
    }

    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this, PacketType.TYPE_POST_MODIFY);
    }

    /**
     * Attempts to fetch profile of user who issued current post.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSuchPostException if no such user exists.
     */
    public void fetchPostUserProfile()
        throws IOException, SocketTimeoutException, NoSuchUserException,
        NoSuchPostException
    {
        UserFetchInf inf = new UserFetchInf(UserFetchInf.POST_ISSUE, this.id);
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_USER_FETCH:
        {
            User res = new User(pak);
            this.postUser = res;
            return;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRPOST:
                throw new NoSuchPostException("Post #" + inf.id + 
                    " not found.");
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
     * Try to fetch user list for current post. 
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchPostException if no such post exists.
     */
    public void fetchUserList(int count)
        throws IOException, SocketTimeoutException, NoSuchPostException
    {
        UserFetchInf inf = new UserFetchInf(UserFetchInf.POST_LIST, this.id);
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_USER_FETCH:
        {
            ByteArrayIOList<User> res = new ByteArrayIOList<User>(pak);
            synchronized (users)
            {
                if (users == null)
                {
                    users = new ArrayList<User>();
                }
                users.addAll(Arrays.asList(res.arr));
            }
            break;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf(pak);
            switch ((int)res.retval)
            {
                case ACKInf.RET_ERRIO:
                    throw new IOException("Server IO exception.");
                case ACKInf.RET_ERRPOST:
                    throw new NoSuchPostException("Post #" + inf.id + 
                        " not found.");
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
     * Try to fetch {@code count} messages for current post, the messages will 
     * be stored in {@code messages} in reverse order.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchPostException if no such post exists.
     * @throws PermissionException if {@code user} is not current user or 
     * {@code user} hasn't join the post.
     */
    public void fetchMessage(User user, int count)
        throws IOException, SocketTimeoutException, NoSuchPostException,
        PermissionException
    {
        long token;
        synchronized (messages)
        {
            if (messages == null)
            {
                token = 2147483647;
            }
            else
            {
                token = messages.get(messages.size() - 1).time;
            }
        }
        MsgFetchInf inf = new MsgFetchInf(MsgFetchInf.POST, user.id, token,
            this.id, count);
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_MESSAGE_FETCH:
        {
            ByteArrayIOList<MessageInf> res = 
                new ByteArrayIOList<MessageInf>(pak);
            synchronized (messages)
            {
                if (messages == null)
                {
                    messages = new ArrayList<MessageInf>();
                }
                messages.addAll(Arrays.asList(res.arr));
            }
            return;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf(pak);
            switch ((int)res.retval)
            {
                case ACKInf.RET_ERRIO:
                    throw new IOException("Server IO exception.");
                case ACKInf.RET_ERRPOST:
                    throw new NoSuchPostException("Post #" + inf.id + 
                        " not found.");
                case ACKInf.RET_ERRPERMISSION:
                    throw new PermissionException("User #" + inf.user + 
                        " have no permission to get message on post #" +
                        inf.id + ".");
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
}
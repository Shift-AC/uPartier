package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;

/**
 * Information about post.
 * 
 * when transferring as bytes using ByteArrayIO:
 * <code>
 * struct Post
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
 * </code>
 */
public class Post implements ByteArrayIO, PacketGenerator
{
    int id = 0;
    int blockID = 0;
    BString name = null;
    long time = 0;
    BString label = null;
    BString place = null;
    BString note = null;
    User postUser = null;
    ArrayList<ByteArrayIO> messages = null;
    int userCount = 0;
    ArrayList<User> users = null;

    @Override
    public int getLength()
    {
        return SIZE_INT + SIZE_INT + SIZE_LONG + SIZE_INT +
            name.getLength() + label.getLength() + place.getLength() +
            note.getLength();
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT * 3 + SIZE_LONG);
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
        checkLen(len, SIZE_INT * 3 + SIZE_LONG);
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
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists.
     */
    void fetchPostUserProfile()
        throws IOException, SocketTimeoutException, NoSuchUserException
    {

    }

    /**
     * Try to fetch last { @code count } posts with time before { @code time } 
     * for current block, or fetch all remaining posts if there're not so 
     * many. The { @code Post } objects returned by this call will in 
     * <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchBlockException if no such block exists.
     */
    void fetchPosts(int count, long time)
        throws IOException, SocketTimeoutException, NoSuchBlockException
    {

    }

    /**
     * Try to fetch user list and last { @code Post.FETCH_COUNT } messages 
     * for current post. The messages will be stored in { @code messages} 
     * in reverse order.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchPostException if no such post exists.
     */
    void fetchBase()
        throws IOException, SocketTimeoutException, NoSuchPostException
    {

    }

    /**
     * Try to fetch { @code Post.FETCH_COUNT } messages for current post, 
     * the messages will be stored in { @code messages} in reverse order.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchPostException if no such post exists.
     */
    void fetchMessage()
        throws IOException, SocketTimeoutException, NoSuchPostException
    {

    }
}
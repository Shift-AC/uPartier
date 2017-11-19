package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.github.shiftac.upartier.network.ByteArrayIO;

public class Post implements ByteArrayIO
{
    int id = 0;
    int blockID = 0;
    String name = null;
    long time = 0;
    String label = null;
    String location = null;
    String note = null;
    User postUser = null;
    ArrayList<Message> messages = null;
    int userCount = 0;
    ArrayList<User> users = null;

    @Override
    public int getLength()
    {
        return 0;
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {

    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {

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
     * Try to fetch last { @code count } posts with time before { @code time } for 
     * current block, or fetch all remaining posts if there're not so many. The 
     * { @code Post } objects returned by this call will in <i>prefetched</i> state.
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
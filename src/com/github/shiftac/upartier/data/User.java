package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class User
{
    public static interface Gender
    {
        public static final int male = 0;
        public static final int female = 1;
        public static final int unknown = 2;
    }

    int id = 0;
    int age = 0;
    int gender = Gender.unknown;
    String mailAccount = null;
    String nickname = null;
    Image profile = null;
    int postCount = 0;
    ArrayList<Post> myPosts = null;

    // class com.github.shiftac.upartier.network.app.User
    /**
     * Attempts to login(or register) use the given { @code LoginInf }, try to 
     * fetch { @code User } for the user if login succeeded.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists or wrong password is given.
     */
    static User login(LoginInf inf)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        return null;
    }

    /**
     * Attempts to log out the current user.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists or wrong password is given.
     */
    void logout()
        throws IOException, SocketTimeoutException, NoSuchUserException
    {

    }

    /**
     * Attempts to fetch user profile for a given user ID.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists.
     */
    static User fetchProfile(int id)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        return null;
    }

    /**
     * Attempts to modify user profile.
     * <p>
     * Current thread will <b>block</b> inside this call.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't get reply from server after
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists.
     */
    void modify()
        throws IOException, SocketTimeoutException, NoSuchUserException
    {

    }

    /**
     * Try to fetch last { @code count } posts with time before { @code time } issued
     * by current user, or fetch all remaining posts if there're not so many. The 
     * { @code Post } objects returned by this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists.
     */
    void fetchMyPosts(int count, long time)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {

    }

    /**
     * Try to issue a new post. The { @code id }, { @code time }, { @code postUser }
     * field of the parameter { @code Post } will be properly set on successful returns.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSucBlockException if no such block exists.
     */
    void issue(Post post)
        throws IOException, NoSuchUserException, NoSuchBlockException,
        SocketTimeoutException
    {

    }

    /**
     * Try to send a reply message under a given post. This also modify 
     * { @code messages } field of the parameter { @code Post }. On successful returns
     * { @code time } field of parameter { @code message } will be properly set.
     *
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     * @throws NoSuchUserException if no such user exists.
     * @throws NoSuchPostException if no such post exists.
     */
    void sendMessage(Post post, Message message)
        throws IOException, NoSuchUserException, NoSuchPostException,
        SocketTimeoutException
    {

    }
}
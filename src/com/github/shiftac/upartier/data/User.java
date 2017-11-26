package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.app.Client;

/**
 * Information about a single user.
 * 
 * when transferring as bytes using ByteArrayIO:
 * <pre>
 * struct User
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

    int id = 0;
    int age = 0;
    int gender = Gender.unknown;
    BString mailAccount = null;
    BString nickname = null;
    Image profile = null;
    int postCount = 0;
    ArrayList<Post> myPosts = null;

    void setMailAccount(String mail)
    {
        this.mailAccount.setContent(mail);
    }

    void setNickname(String name)
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
    static User login(LoginInf inf)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        throw new IOException();
        //Client.client.issue(inf.toPacket());
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
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchUserException if no such user exists.
     */
    static User fetchProfile(int id)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        throw new NoSuchUserException();
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
    void modify()
        throws IOException, SocketTimeoutException, NoSuchUserException,
        PermissionException
    {
        throw new SocketTimeoutException();
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
    void fetchMyPosts(int count, int id)
        throws IOException, SocketTimeoutException, NoSuchUserException
    {
        throw new SocketTimeoutException();
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
     * @throws NoSucBlockException if no such block exists.
     */
    void issue(Post post)
        throws IOException, NoSuchUserException, NoSuchBlockException,
        SocketTimeoutException
    {
        throw new NoSuchBlockException();
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
    void sendMessage(Post post, MessageInf message)
        throws IOException, NoSuchUserException, NoSuchPostException,
        SocketTimeoutException, PermissionException
    {
        throw new SocketTimeoutException();
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
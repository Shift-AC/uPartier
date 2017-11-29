package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.Packet;

/**
 * When fetching post information, following situations are possible to occur:
 * <ol>
 * <li>Fetching posts from its ID(We think this is needed but it is not being 
 * used by now).</li>
 * <li>Fetching posts belong to a specified block.</li>
 * <li>Fetching posts posted by a specified user.</li>
 * </ol>
 * For 2. and 3., {@code count} will be valid and {@code token} will be id of 
 * last item, for 2., {@code id} will be block ID, for 3., {@code user} will
 * be user ID.
 */
public class PostFetchInf extends CountFetchInf
{
    public static final int ID = 0;
    public static final int BLOCK = 1;
    public static final int USER = 2;

    public PostFetchInf() {}

    public PostFetchInf(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    public PostFetchInf(int type, 
        int user, long token, int id, int count)
    {
        this.type = type;
        this.user = user;
        this.token = token;
        this.count = count;
        this.id = id;
    }

    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_POST_FETCH;
    }
}
package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.Packet;

/**
 * When fetching user information, following situations are possible to occur:
 * <ol>
 * <li>Fetching a user from its ID.</li>
 * <li>Fetching a user who issued a specified post.</li>
 * <li>Fetching a list of users belong to a post.</li>
 * </ol>
 */
public class UserFetchInf extends IDFetchInf
{
    public static final int ID = 0;
    public static final int POST_ISSUE = 1;
    public static final int POST_LIST = 2;

    public UserFetchInf(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    public UserFetchInf(int type, int id)
    {
        this.type = type;
        this.id = id;
    }

    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_USER_FETCH;
    }
}
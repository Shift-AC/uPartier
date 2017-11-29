package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.Packet;

/**
 * When fetching user information, following situations are possible to occur:
 * <p>
 * 1. Fetching a user from its ID.<br>
 * 2. Fetching a user who issued a specified post.<br>
 * 3. Fetching a list of users belong to a post.<br>
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
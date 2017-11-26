package com.github.shiftac.upartier.data;

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

    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_USER_FETCH;
    }
}
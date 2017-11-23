package com.github.shiftac.upartier.data;

/**
 * When fetching post information, following situations are possible to occur:
 * <p>
 * 1. Fetching posts from its ID(We think this is needed but it is not being 
 * used by now).<br>
 * 2. Fetching posts belong to a specified block.<br>
 * 2. Fetching posts posted by a specified user.<br>
 */
public class PostFetchInf extends FetchInf
{
    public static final int ID = 0;
    public static final int BLOCK = 1;
    public static final int USER = 2;

    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_POST_FETCH;
    }
}
package com.github.shiftac.upartier.data;

/**
 * When fetching post information, following situations are possible to occur:
 * <p>
 * 1. Fetching posts from its ID(We think this is needed but it is not being 
 * used by now).<br>
 * 2. Fetching posts belong to a specified block.<br>
 * 3. Fetching posts posted by a specified user.<br>
 * 
 * For 2. and 3., {@code count} will be valid and {@code token} will be id of 
 * last item, for 2., {@code user} will be block ID, for 3., {@code user} will
 * be user ID.
 */
public class PostFetchInf extends CountFetchInf
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
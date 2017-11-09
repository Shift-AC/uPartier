package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

public class Block
{
    int id = 0;
    String name = null;
    int postCount = 0;
    ArrayList<Post> myPosts = null;

    /**
     * Try to fetch all existing post blocks, the { @code Block } objects returned
     * in this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * { @code Client.NETWORK_TIMEOUT } milliseconds.
     */
    static Block[] fetchBlocks()
        throws IOException, SocketTimeoutException
    {
        return null;
    }


}
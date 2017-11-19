package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.ByteArrayIO;

/**
 *  Information about post block.
 *  
 *  when transferring as bytes using ByteArrayIO:
 *  <code>
 *  struct Block
 *  {
 *      int id;
 *      int postCount;
 *      BString name;
 *  }
 *  </code>
 */
public class Block implements ByteArrayIO
{
    int id = 0;
    BString name = null;
    int postCount = 0;
    ArrayList<Post> posts = null;

    /**
     *  Try to fetch all existing post blocks, the { @code Block } objects 
     *  returned in this call will in <i>prefetched</i> state.
     *  <p>
     *  Current thread will <b>block</b> inside this call.
     *  
     *  @throws IOException if network exceptions occured.
     *  @throws SocketTimeoutException if can't hear from server for
     *  { @code Client.NETWORK_TIMEOUT } milliseconds.
     */
    static Block[] fetchBlocks()
        throws IOException, SocketTimeoutException
    {
        return null;
    }

    /**
     *  @param name the name to set
     */
    public void setName(String name)
    {
        this.name.setContent(name);
    }

    @Override
    public int getLength()
    {
        return SIZE_INT + SIZE_INT + name.getLength();
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        setInt(buf, off, id);
        setInt(buf, off += 4, postCount);
        name.read(buf, off += 4, len -= SIZE_INT + SIZE_INT);
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT + SIZE_INT);
        id = getInt(buf, off);
        postCount = getInt(buf, off += 4);
        name.read(buf, off += 4, len -= SIZE_INT + SIZE_INT);
    }
}
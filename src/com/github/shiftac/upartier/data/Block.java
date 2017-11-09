package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import com.github.shiftac.upartier.network.ByteArrayIO;

public class Block implements ByteArrayIO
{
    int id = 0;
    String name = null;
    protected byte[] bname = null;
    int postCount = 0;
    ArrayList<Post> posts = null;

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

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name = name;
        bname = name.getBytes();
    }

    @Override
    public int getLength()
    {
        return 9 + bname.length;
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        Util.setInt(buf, off, id);
        buf[off += 4] = (int)bname.length;
        for (int i = 0; i < bname.length; ++i)
        {
            buf[++off] = bname[i];
        }
        Util.setInt(buf, ++off, postCount);
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, 5);
        id = Util.getInt(buf, off);
        int nlen = buf[off += 4];
        checkLen(len -= 5, nlen);
        setName(new String(buf, ++off, nlen));
        checkLen(len -= nlen, 4);
        postCount = Util.getInt(buf, off += nlen);
    }
}
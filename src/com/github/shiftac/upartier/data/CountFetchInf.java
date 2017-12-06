package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;

/**
 * When fetching posts and messages, the program is not expected to fetch all
 * items in a single request since they can use up the network bandwidth. In 
 * this situation client should specify the number of items they want to fetch
 * in a single request, and to let the server know about the range of data,
 * data like timestamp or id of last item must be transferred. Also, when 
 * fetching posts and messages, we need to ensure that current user has 
 * permission to access the data, so we need to transfer the ID of user.
 * <p>
 * class extending this class should explain the meanings of their 
 * {@code type}.
 * <p>
 * When transferring as byte array:
 * <pre>
 * class CountFetchInf
 * {
 *     byte type;
 *     byte[3] reserved;
 *     int id;
 *     int count;
 *     long token;
 *     int user;
 * }
 * </pre>
 */
public abstract class CountFetchInf implements ByteArrayIO, PacketGenerator
{
    public int type;
    public int id;
    public int count;
    public long token;
    public int user;

    @Override
    public String getInf()
    {
        return String.format("type=%d, id=%d, count=%d, token=%d, user=%d", 
            type, id, count, token, user);
    }

    @Override
    public int getLength()
    {
        return SIZE_INT * 4 + SIZE_LONG;
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        type = buf[off];
        id = getInt(buf, off += SIZE_INT);
        count = getInt(buf, off += SIZE_INT);
        token = getLong(buf, off += SIZE_INT);
        user = getInt(buf, off += SIZE_LONG);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        buf[off] = (byte)type;
        setInt(buf, off += SIZE_INT, id);
        setInt(buf, off += SIZE_INT, count);
        setLong(buf, off += SIZE_INT, token);
        setInt(buf, off += SIZE_LONG, user);
    }

    public abstract int getOperationType();

    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this, getOperationType());
    }
}
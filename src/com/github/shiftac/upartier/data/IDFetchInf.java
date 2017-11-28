package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;

/**
 * We notice that sometimes some kind of ID number can be enough for the 
 * server to know what to fetch. Transferring such a short message as long as 
 * 8 bytes is neat: we only need to transfer one AES encryped block. 
 * <p>
 * class extending this class should explain the meanings of their 
 * {@code type}.
 * <p>
 * When transferring as byte array:
 * <pre>
 * class IDFetchInf
 * {
 *     byte type;
 *     byte[3] reserved;
 *     int id;
 * }
 * </pre>
 */
public abstract class IDFetchInf implements ByteArrayIO, PacketGenerator
{
    public int type;
    public int id;

    @Override
    public int getLength()
    {
        return SIZE_INT + SIZE_INT;
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        type = buf[off];
        id = getInt(buf, off += SIZE_INT);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        buf[off] = (byte)type;
        setInt(buf, off += SIZE_INT, id);
    }

    public abstract int getOperationType();

    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this, getOperationType());
    }
}
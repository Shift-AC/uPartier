package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;

/**
 * We notice that some kind of ID number will be enough for the server to know
 * what to fetch. Transferring such a short message as long as 8 bytes is neat: 
 * we only need to transfer one AES encryped block. 
 * 
 * class extending this class should explain the meanings of their 
 * { @code type }.
 * 
 * When transferring as byte array:
 * <code>
 * struct FetchInf
 * {
 *     byte type;
 *     byte[3] reserved;
 *     int id;
 * }
 * </code>
 */
public abstract class FetchInf implements ByteArrayIO, PacketGenerator
{
    public int type;
    public int id;

    @Override
    public int getLength()
    {
        return 8;
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
        return new AES128Packet(this, getOperationType(), 0);
    }
}
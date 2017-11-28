package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;

public class PostJoinInf implements ByteArrayIO, PacketGenerator
{
    int postID = 0;
    int userID = 0;

    @Override
    public int getLength()
    {
        return SIZE_INT * 2;
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        postID = getInt(buf, off);
        userID = getInt(buf, off += SIZE_INT);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        setInt(buf, off, postID);
        setInt(buf, off += SIZE_INT, userID);
    }

    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this, PacketType.TYPE_POST_JOIN);
    }
}
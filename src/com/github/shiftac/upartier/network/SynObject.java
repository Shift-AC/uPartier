package com.github.shiftac.upartier.network;

import java.io.IOException;

public class SynObject implements ByteArrayIO
{
    public int id;
    public long mili;
    public int ip;

    @Override
    public int getLength()
    {
        return SIZE_INT + SIZE_LONG + SIZE_INT;
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        setInt(buf, off, id);
        setLong(buf, off += SIZE_INT, mili);
        setInt(buf, off += SIZE_LONG, ip);
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        id = getInt(buf, off);
        mili = getLong(buf, off += SIZE_INT);
        ip = getInt(buf, off += SIZE_LONG);
    }
}
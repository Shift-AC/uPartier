package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.ByteArrayIO;

public class LoginInf implements ByteArrayIO
{
    int id = 0;
    BString passwd = new BString();
    boolean isNewUser = false;

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT);
        id = getInt(buf, off);
        isNewUser = id < 0;
        id = id & 0x7FFFFFFF;
        passwd.read(buf, off += SIZE_INT, len -= SIZE_INT);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        int tid = isNewUser ? -id : id;
        setInt(buf, off, tid);
        passwd.write(buf, off += SIZE_INT, len -= SIZE_INT);
    }

    @Override
    public int getLength()
    {
        return SIZE_INT + passwd.getLength();
    }
}
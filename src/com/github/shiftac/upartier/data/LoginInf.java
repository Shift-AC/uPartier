package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.ByteArrayIO;

public class LoginInf implements ByteArrayIO
{
    int id = 0;
    String passwd = null;
    boolean isNewUser = false;

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        id = Util.getInt(buf, off);
        len = buf[off + 4];
        passwd = new String(buf, off + 5, len);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        int tid = isNewUser ? -id : id;
        Util.setInt(buf, off, tid);
        buf[off + 4] = (byte)passwd.length();
        off += 5;
        for (int i = 0; i < passwd.length(); ++i)
        {
            buf[off++] = (byte)(passwd.charAt(i));
        }
    }

    @Override
    public int getLength()
    {
        return 5 + passwd.length();
    }
}
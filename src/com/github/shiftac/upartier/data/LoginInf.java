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
        passwd = new String(buf, off + 4, len - 4);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        int tid = isNewUser ? -id : id;
        Util.setInt(buf, off, tid);
        for (int i = 0; i < passwd.length(); ++i)
        {
            buf[off++] = (byte)(passwd.charAt(i));
        }
    }

    @Override
    public int getLength()
    {
        if (passwd == null)
        {
            return 4;
        }
        else
        {
            return 4 + passwd.length();
        }
    }
}
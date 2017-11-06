package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.shiftac.upartier.Util;

public class RichMessage implements ByteArrayIO
{
    public static final byte IMAGE = 0;
    public static final byte AUDIO = 1;
    public static final byte VIDEO = 2;
    public static final byte FILE = 3;

    byte type;
    byte[] name;
    byte[] payload;

    public RichMessage(byte[] buf, int off)
    {
        read(buf, off, buf.length);
    }

    @Override
    public void read(byte[] buf, int off, int len)
    {
        type = buf[off++];
        int namelen = Util.getShort(buf, off);
        name = new byte[namelen];
        off += 2;
        for (int i = 0; i < namelen; ++i)
        {
            name[i] = buf[off++];
        }
        payload = new byte[buf.length - off];
        for (int i = 0; i < payload.length; ++i)
        {
            payload[i] = buf[off++];
        }
    }

    @Override
    public void write(byte[] buf, int off, int len)
    {
        buf[off++] = type;
        Util.setShort(buf, off, name.length);
        off += 2;
        for (int i = 0; i < name.length; ++i)
        {
            buf[off++] = name[i];
        }
        for (int i = 0; i < payload.length; ++i)
        {
            buf[off++] = payload[i];
        }
    }

    @Override
    public int getLength()
    {
        return 2 + name.length + payload.length;
    }

    public String getName()
    {
        return new String(name);
    }
}
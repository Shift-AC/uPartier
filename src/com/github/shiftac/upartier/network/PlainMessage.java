package com.github.shiftac.upartier.network;

import java.io.IOException;

public class PlainMessage implements ByteArrayIO
{
    byte[] sbuf;

    public PlainMessage(byte[] buf)
    {
        sbuf = new byte[buf.length];
        for (int i = 0; i < buf.length; ++i)
        {
            sbuf[i] = buf[i];
        }
    }

    public PlainMessage(String str)
    {
        setContent(str);
    }

    @Override
    public void read(byte[] buf, int off, int len) throws IOException
    {
        sbuf = new byte[buf.length - len];
        for (int i = 0; i < sbuf.length; ++i)
        {
            sbuf[i] = buf[off++];
        }
    }

    @Override
    public void write(byte[] buf, int off, int len) throws IOException
    {
        int elen = getLength();
        if (elen > len)
        {
            throw new IOException(String.format("Buffer size(%d) not enough, expected %d.", len, elen));
        }
        for (int i = 0; i < sbuf.length; ++i)
        {
            buf[off++] = sbuf[i];
        }
    }

    @Override
    public String toString()
    {
        return new String(sbuf);
    }

    @Override
    public int getLength()
    {
        return sbuf.length;
    }

    public void setContent(String str)
    {
        sbuf = str.getBytes();
    }
}
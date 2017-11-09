package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.ByteArrayIO;

public abstract class AbstractFile implements ByteArrayIO
{
    public String name = null;
    protected byte[] bname = null;
    public byte[] payload = null;

    abstract public int getType();

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        if (name == null)
        {
            return;
        }
        checkLen(len, getLength());
        Util.setShort(buf, off, bname.length);
        off += 2;
        for (int i = 0; i < bname.length; ++i)
        {
            buf[off++] = bname[i];
        }
        Util.setInt(buf, off, payload.length);
        off += 4;
        for (int i = 0; i < payload.length; ++i)
        {
            buf[off++] = payload[i];
        }
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, 2);
        int nlen = Util.getShort(buf, off);
        off += 2;
        checkLen(len -= 2, nlen);
        setName(new String(buf, off, nlen));
        off += nlen;
        checkLen(len -= nlen, 4);
        nlen = Util.getInt(buf, off);
        off += nlen;
        checkLen(len -= 4, nlen);
        payload = new byte[nlen];
        for (int i = 0; i < nlen; ++i)
        {
            payload[i] = buf[off++];
        }
    }

    public void setName(String name)
    {
        this.name = name;
        this.bname = name.getBytes();
    }

    public int getLength()
    {
        return 6 + bname.length + payload.length;
    }
}
package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.ByteArrayIO;

public abstract class AbstractFile implements ByteArrayIO
{
    public BString name = null;
    public byte[] payload = null;

    abstract public int getType();

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        name.write(buf, off, len);
        int nlen = name.getLength();
        checkLen(len -= nlen, SIZE_INT);
        setInt(buf, off += nlen, payload.length);
        memcpy(buf, off += SIZE_INT, payload, 0, payload.length);
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        name.write(buf, off, len);
        int nlen = name.getLength();
        checkLen(len -= nlen, SIZE_INT);
        int blen = getInt(buf, off += nlen);
        payload = new byte[blen];
        memcpy(payload, 0, buf, off += SIZE_INT, blen);
    }

    public void setName(String name)
    {
        this.name.setContent(name);
    }

    public int getLength()
    {
        return SIZE_INT + name.getLength() + payload.length;
    }
}
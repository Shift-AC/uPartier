package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.github.shiftac.upartier.network.ByteArrayIO;

public abstract class AbstractFile implements ByteArrayIO
{
    public String name = null;
    protected byte[] bname = null;
    public byte[] payload = null;

    abstract public int getType();
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        if (name == null)
        {
            return;
        }
        byte[] bname = name.getBytes();
        for (int i = 0; i < bname.length; ++i)
        {
            buf[off++] = bname[i];
        }
        for (int i = 0; i < bname.length; ++i)
        {
            buf[off++] = bname[i];
        }
    }

    public void read(InputStream is)
        throws IOException
    {
        int ava = is.available();
        if (ava == 0)
        {
            return;
        }
        payload = new byte[ava];
        is.read(payload);
    }
}
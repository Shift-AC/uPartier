package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public abstract class AbstractFile
{
    public String name = null;
    public byte[] payload = null;

    abstract public int getType();
    public void write(OutputStream os)
        throws IOException
    {
        os.write(payload);
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
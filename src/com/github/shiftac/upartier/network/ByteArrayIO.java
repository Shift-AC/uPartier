package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface ByteArrayIO
{
    public void read(byte[] buf, int off, int len) throws IOException;
    public void write(byte[] buf, int off, int len) throws IOException;
    public int getLength();
    public default byte[] toByteArray()
    {
        byte[] res = new byte[getLength()];
        try
        {
            write(res, 0, res.length);
        }
        catch (Exception e) {}
        return res;
    }
    public default void read(byte[] buf)
        throws IOException
    {
        read(buf, 0, buf.length);
    }
    public default void write(byte[] buf)
        throws IOException
    {
        write(buf, 0, buf.length);
    }
}
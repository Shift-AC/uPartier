package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.github.shiftac.upartier.network.ByteArrayIO;


/**
 * A multithread-safe String handler that implements ByteArrayIO and supports
 * efficient calls of getBytes().
 * 
 * When transferring as bytes:
 * <pre>
 * class BString
 * {
 *    dword mlen;
 *    byte[mlen] mybytes;
 * }
 * </pre>
 */
public class BString implements ByteArrayIO
{
    private byte[] mybytes = null;
    private String content = null;

    public BString()
    {
        setContent(new String());
    }

    public BString(byte[] bytes)
    {
        setContent(new String(bytes));
    }

    public BString(byte[] bytes, int offset, int length)
    {
        setContent(new String(bytes, offset, length));
    }

    public BString(byte[] bytes, int offset, int length, String charsetName)
        throws UnsupportedEncodingException
    {
        setContent(new String(bytes, offset, length, charsetName));
    }

    public BString(byte[] bytes, int offset, int length, Charset charset)
    {
        setContent(new String(bytes, offset, length, charset));
    }

    public BString(byte[] bytes, String charsetName)
        throws UnsupportedEncodingException
    {
        setContent(new String(bytes, charsetName));
    }

    public BString(byte[] bytes, Charset charset)
    {
        setContent(new String(bytes, charset));
    }

    public BString(char[] value)
    {
        setContent(new String(value));
    }

    public BString(char[] value, int offset, int count)
    {
        setContent(new String(value, offset, count));
    }

    public BString(int[] codePoints, int offset, int count)
    {
        setContent(new String(codePoints, offset, count));
    }

    public BString(String str)
    {
        setContent(str);
    }

    public BString(StringBuffer buffer)
    {
        setContent(new String(buffer));
    }

    public BString(StringBuilder builder)
    {
        setContent(new String(builder));
    }

    public void setContent(String str)
    {
        synchronized (this)
        {
            content = str;
            mybytes = str.getBytes();
        }
    }

    public byte[] getBytes()
    {
        synchronized (this)
        {
            return mybytes;
        }
    }

    @Override
    public String getInf()
    {
        return content;
    }

    @Override
    public int getLength()
    {
        synchronized (this)
        {
            if (mybytes == null)
            {
                return SIZE_INT;
            }
            else
            {
                return mybytes.length + SIZE_INT;
            }
        }
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        synchronized (this)
        {
            checkLen(len, SIZE_INT);
            int slen = getInt(buf, off);
            checkLen(len -= SIZE_INT, slen);
            setContent(new String(buf, off += SIZE_INT, slen));
        }
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        synchronized (this)
        {
            int mlen = getLength();
            checkLen(len, mlen);
            setInt(buf, off, mlen -= SIZE_INT);
            if (mybytes != null)
            {
                memcpy(buf, off += SIZE_INT, mybytes, 0, mlen);
            }
        }
    }

    @Override
    public String toString()
    {
        synchronized (this)
        {
            return content;
        }
    }
}
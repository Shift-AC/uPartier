package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

import com.github.shiftac.upartier.Util;
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

    public BString(String str)
    {
        setContent(str);
    }

    public BString(StringBuffer buffer)
    {
        this(buffer.toString());
    }

    public BString(StringBuilder builder)
    {
        this(builder.toString());
    }

    public void setContent(String str)
    {
        synchronized (this)
        {
            content = str;
            mybytes = str.getBytes(Util.tgtSet);
            //System.out.printf("content %s\n", new String(mybytes, Util.tgtSet));
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
            mybytes = new byte[slen];
            memcpy(mybytes, 0, buf, off += SIZE_INT, slen);
            content = new String(mybytes, Util.tgtSet);
            //System.out.printf("read: %s\n", content);
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

    public static void main(String[] args)
    {
        String str = new String("メッセージ");
        BString bstr = new BString(str);
        try
        {
            bstr.read(bstr.toByteArray());
            System.out.printf("res: %s\n", bstr);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
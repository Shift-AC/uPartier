package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class ByteArrayIOList<T extends ByteArrayIO> implements ByteArrayIO
{
    public T[] arr;
    private Class<T> cls;

    public ByteArrayIOList(Class<T> cls, T[] arr)
    {
        this.cls = cls;
        this.arr = arr;
    }

    public ByteArrayIOList(Class<T> cls, Packet pak)
        throws IOException
    {
        this.cls = cls;
        this.read(pak);
    }

    @Override
    public String getInf()
    {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format(
            "type=%s, len=%d", arr.toString(), arr.length));
        for (int i = 0; i < arr.length; ++i)
        {
            sb.append(String.format(" ->arr[%d]:%s", i, arr[i].getInf()));
        }
        return sb.toString();
    }

    @Override
    public int getLength()
    {
        int sum = 0;
        for (int i = 0; i < arr.length; ++i)
        {
            sum += arr[i].getLength();
        }
        return sum + SIZE_INT;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT);
        int larr = getInt(buf, off);
        off += SIZE_INT;
        len -= SIZE_INT;
        arr = (T[])Array.newInstance(cls, larr);
        for (int i = 0; i < larr; ++i)
        {
            try
            {
                arr[i] = cls.getDeclaredConstructor().newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
            arr[i].read(buf, off, len);
            int elen = arr[i].getLength();
            off += elen;
            len -= elen;
        }
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT);
        setInt(buf, off, arr.length);
        off += SIZE_INT;
        len -= SIZE_INT;
        for (int i = 0; i < arr.length; ++i)
        {
            int elen = arr[i].getLength();
            arr[i].write(buf, off, len);
            off += elen;
            len -= elen;
        }
    }
}
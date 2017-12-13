package com.github.shiftac.upartier.network;

import java.io.IOException;
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
        return String.format("type=%s, len=%d", arr.toString(), arr.length);
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
        ArrayList<T> tarr = new ArrayList<T>(larr);
        for (int i = 0; i < larr; ++i)
        {
            T ele = null;
            try
            {
                ele = cls.getDeclaredConstructor().newInstance();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                return;
            }
            ele.read(buf, off, len);
            int elen = ele.getLength();
            off += elen;
            len -= elen;
            tarr.set(i, ele);
        }
        arr = (T[])tarr.toArray();
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
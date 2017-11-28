package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.util.ArrayList;

public class ByteArrayIOList<T extends ByteArrayIO> implements ByteArrayIO
{
    public T[] arr;

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
            T ele = tarr.get(i);
            ele.read(buf, off, len);
            int elen = ele.getLength();
            off += elen;
            len -= elen;
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
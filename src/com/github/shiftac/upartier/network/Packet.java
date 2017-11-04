package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static com.github.shiftac.upartier.Util.*;

public class Packet
{
    static final int sleepInterval;
    static final int baseAttempt;
    static final int attemptPerKB;
    byte version = 0;
    byte type = 0;
    short subtype = 0;
    int len = 0;
    byte[] data = null;
    private byte[] buf = new byte[8];

    private static int getAttempt(int n)
    {
        return ((n >> 10) + 1) * attemptPerKB + baseAttempt;
    }

    private static int readN(InputStream is, byte[] buf, int off, int n)
        throws IOException
    {
        int on = n;
        int maxAttempt = getAttempt(n);

        for (int att = 0; att < maxAttempt; ++att)
        {
            int ava = is.available();
            if (ava == 0)
            {
                sleepIgnoreInterrupt(100);
            }
            else
            {
                ava = ava < n ? ava : n;
                ava = is.read(buf, off, ava);
                off += ava;
                if ((n -= ava) == 0)
                {
                    break;
                }
            }
        }
        return on - n;
    }

    private static void forceReadN(InputStream is, byte[] buf, int off, int n)
        throws IOException, NetworkTimeoutException
    {
        int x = readN(is, buf, off, n);
        if (x != 0)
        {
            throw new NetworkTimeoutException(x + " bytes remaining.");
        }
    }

    public void forceRead(InputStream is)
        throws IOException, NetworkTimeoutException
    {
        forceReadN(is, buf, 0, 8);
        version = buf[0];
        type = buf[1];
        subtype = ((short)buf[2] << 8) + ((short)buf[3] & 0xFF);
        len = ((int)buf[4] << 24) + (((int)buf[5] & 0xFF) << 16) + 
            (((int)buf[6] & 0xFF) << 8) + ((int)buf[7] & 0xFF);
        data = new byte[len];
        forceReadN(is, data, 0, len);
    }

    public void read(InputStream is)
        throws IOException
    {
        is.read(buf, 0, 8);
        version = buf[0];
        type = buf[1];
        subtype = ((short)buf[2] << 8) + ((short)buf[3] & 0xFF);
        len = ((int)buf[4] << 24) + (((int)buf[5] & 0xFF) << 16) + 
            (((int)buf[6] & 0xFF) << 8) + ((int)buf[7] & 0xFF);
        data = new byte[len];
        is.read(data, 0, len);
    }

    public void write(OutputStream os)
        throws IOException
    {
        buf[0] = version;
        buf[1] = type;
        buf[2] = (byte)(subtype >> 8);
        buf[3] = (byte)subtype;
        buf[4] = (byte)(len >> 24);
        buf[5] = (byte)(len >> 16);
        buf[6] = (byte)(len >> 8);
        buf[7] = (byte)len;
        os.write(buf);
        os.write(data);
    }

    static
    {
        try
        {
            sleepInterval = getIntConfig("/network/Packet/sleepInms");
            baseAttempt = getIntConfig("/network/Packet/baseAttempt");
            attemptPerKB = getIntConfig("/network/Packet/attemptPerKB");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
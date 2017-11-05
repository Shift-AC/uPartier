package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static com.github.shiftac.upartier.Util.*;

public abstract class Packet
{
    static final int sleepInterval;
    static final int baseAttempt;
    static final int attemptPerKB;
    public byte version = 0;
    public byte type = 0;
    public byte subtype = 0;
    public byte padding = 0;
    public int len = 0;
    public byte[] data = null;

    protected static int getAttempt(int n)
    {
        return ((n >> 10) + 1) * attemptPerKB + baseAttempt;
    }

    public void setLen(int len)
    {
        this.len = len;
        data = new byte[len];
    }

    protected static int doRead(
        InputStream is, byte[] buf, int off, int n, boolean force)
        throws IOException, NetworkTimeoutException
    {
        int on = n;
        int maxAttempt = force ? getAttempt(n) : 2147483647;

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
        if (force && n != 0)
        {
            throw new NetworkTimeoutException(n + " bytes remaining.");
        }
        return on;
    }

    protected void checkVersion()
        throws PacketFormatException
    {
        if (version != getVersion())
        {
            throw new PacketFormatException("Version(" + version + 
                ") not match, Expected "+ getVersion());
        }
    }

    public abstract void read(InputStream is, boolean force)
        throws IOException, PacketFormatException, NetworkTimeoutException;
    
    public abstract void write(OutputStream os)
        throws IOException, PacketFormatException;

    public abstract byte getVersion();

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
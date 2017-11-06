package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.github.shiftac.upartier.Util;

public abstract class Packet
{
    static int sleepInterval;
    static int baseAttempt;
    static int attemptPerKB;
    public byte version = 0;
    public byte type = 0;
    public byte padding = 0;
    public int sequence = 0;
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

    protected static int headerLen()
    {
        return 8;
    }

    protected void setHeader(byte[] buf)
    {
        version = (byte)(buf[0] >> (byte)5);
        type = (byte)(buf[0] & (byte)0x1F);
        padding = buf[1];
        sequence = ((int)buf[2] << 8) + (int)buf[3];
        len = ((int)buf[4] << 24) + (((int)buf[5] & 0xFF) << 16) + 
            (((int)buf[6] & 0xFF) << 8) + ((int)buf[7] & 0xFF);
    }

    protected void fillHeader(byte[] buf)
    {
        buf[0] = (byte)((version << (byte)5) | type);
        buf[1] = padding;
        buf[2] = (byte)(sequence >> 8);
        buf[3] = (byte)sequence;
        buf[4] = (byte)(len >> 24);
        buf[5] = (byte)(len >> 16);
        buf[6] = (byte)(len >> 8);
        buf[7] = (byte)len;
    }

    public String getInf()
    {
        return String.format("VER=%d, TYP=%d, PAD=%d, SEQ=%d, LEN=%d", 
            version, type, padding, sequence, len);
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
            if (force && ava == 0)
            {
                Util.sleepIgnoreInterrupt(100);
            }
            else 
            {
                ava = force ? (ava < n ? ava : n) : n;
                ava = is.read(buf, off, ava);
                if (ava == -1)
                {
                    throw new IOException("Connection closed.");
                }
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
        Util.log.logVerbose(String.format("Got %d bytes.", on - n), 2);
        return on - n;
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
            sleepInterval = Util.getIntConfig("/network/Packet/sleepInms");
            baseAttempt = Util.getIntConfig("/network/Packet/baseAttempt");
            attemptPerKB = Util.getIntConfig("/network/Packet/attemptPerKB");
        }
        catch (Exception e)
        {
            e.printStackTrace(Util.log.dest);
        }
    }
}
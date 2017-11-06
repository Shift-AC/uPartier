package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.github.shiftac.upartier.Util;

public class RawPacket extends Packet
{
    protected byte[] buf = new byte[headerLen()];

    public RawPacket() 
    {
        version = getVersion();
    }

    public RawPacket(ByteArrayIO payload)
    {
        version = getVersion();
        setLen(payload.getLength());
        try
        {
            payload.write(data, 0, data.length);
        }
        catch (Exception e) {}
    }

    @Override
    public void read(InputStream is, boolean force)
        throws IOException, PacketFormatException, NetworkTimeoutException
    {
        doRead(is, buf, 0, buf.length, force);
        setHeader(buf);
        data = new byte[len];
        doRead(is, data, 0, len, force);
        checkVersion();
    }

    @Override
    public void write(OutputStream os)
        throws IOException
    {
        fillHeader(buf);
        os.write(buf);
        os.write(data);
        os.flush();
    }

    @Override
    public byte getVersion()
    {
        return (byte)PacketType.VER_RAW;
    }
}
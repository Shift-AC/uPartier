package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.github.shiftac.upartier.Util;

public class RawPacket extends Packet
{
    protected byte[] buf = new byte[headerLen()];

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
        return PacketType.version("RawPacket");
    }
}
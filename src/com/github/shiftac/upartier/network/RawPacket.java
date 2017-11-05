package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static com.github.shiftac.upartier.Util.*;

public class RawPacket extends Packet
{
    protected byte[] buf = new byte[8];

    @Override
    public void read(InputStream is, boolean force)
        throws IOException, PacketFormatException, NetworkTimeoutException
    {
        doRead(is, buf, 0, 8, force);
        version = buf[0];
        type = buf[1];
        subtype = buf[2];
        padding = buf[3];
        len = ((int)buf[4] << 24) + (((int)buf[5] & 0xFF) << 16) + 
            (((int)buf[6] & 0xFF) << 8) + ((int)buf[7] & 0xFF);
        data = new byte[len];
        doRead(is, data, 0, len, force);
        checkVersion();
    }

    @Override
    public void write(OutputStream os)
        throws IOException
    {
        buf[0] = version;
        buf[1] = type;
        buf[2] = subtype;
        buf[3] = padding;
        buf[4] = (byte)(len >> 24);
        buf[5] = (byte)(len >> 16);
        buf[6] = (byte)(len >> 8);
        buf[7] = (byte)len;
        os.write(buf);
        os.write(data);
        os.flush();
    }

    @Override
    public byte getVersion()
    {
        return (byte)0;
    }
}
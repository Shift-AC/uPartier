package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import static com.github.shiftac.upartier.Util.*;

public class AES128Packet extends Packet
{
    protected byte[] buf = new byte[16];
    static AES128Key key = null;

    void decrypt(byte[] src, int off)
    {

    }

    void encrypt(byte[] src, int off)
    {
        
    }

    void fill(byte[] src, int off, int len)
    {
        padding = (byte)(16 - ((len + 8) & 0xF) & 0xF);
        setLen(len + padding);
        for (int i = 0; i < len; ++i)
        {
            data[i] = src[off + i];
        }
    }

    @Override
    public void read(InputStream is, boolean force)
        throws IOException, PacketFormatException, NetworkTimeoutException
    {
        doRead(is, buf, 0, 16, force);
        decrypt(buf, 0);
        version = buf[0];
        type = buf[1];
        subtype = buf[2];
        padding = buf[3];
        setLen(getInt(buf, 4));
        for (int i = 0; i < 8; ++i)
        {
            data[i] = buf[i + 8];
        }
        doRead(is, data, 8, len, force);
        checkVersion();
        decrypt(data, 0);
    }

    @Override
    public void write(OutputStream os)
        throws IOException, PacketFormatException
    {
        checkVersion();
        buf[0] = version;
        buf[1] = type;
        setShort(buf, 2, subtype);
        setInt(buf, 4, len);
        for (int i = 0; i < 8; ++i)
        {
            buf[i + 8] = data[i];
        }
        encrypt(buf, 0);
        encrypt(data, 8);
        os.write(buf);
        os.write(data, 8, data.length - 8);
        os.flush();
    }

    @Override
    public byte getVersion()
    {
        return (byte)1;
    }

    @Override
    protected void checkVersion()
        throws PacketFormatException
    {
        super.checkVersion();
        if ((len ^ 8) & 0xF)
        {
            throw new PacketFormatException("Length(" + len + ") not aligned.");
        }
    }
}
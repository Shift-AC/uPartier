package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.github.shiftac.upartier.Util;

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

    @Override
    public void setLen(int len)
    {
        padding = (byte)(16 - ((len + 8) & 0xF) & 0xF);
        super.setLen(len + padding);
    }

    void fill(byte[] src, int off, int len)
    {
        setLen(len);
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
        setLen(Util.getInt(buf, 4));
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
        buf[2] = subtype;
        buf[3] = padding;
        Util.setInt(buf, 4, len);
        for (int i = 0; i < 8; ++i)
        {
            buf[i + 8] = data[i];
        }
        byte[] tdata = new byte[data.length - 8];
        for (int i = 0; i < tdata.length; ++i)
        {
            tdata[i] = data[i + 8];
        }
        encrypt(buf, 0);
        encrypt(tdata, 0);
        os.write(buf);
        os.write(tdata, 8, tdata.length);
        os.flush();
    }

    @Override
    public byte getVersion()
    {
        return PacketType.version("AES128Packet");
    }

    @Override
    protected void checkVersion()
        throws PacketFormatException
    {
        super.checkVersion();
        if (((len ^ 8) & 0xF) != 0)
        {
            throw new PacketFormatException("Length(" + len + ") not aligned.");
        }
    }
}
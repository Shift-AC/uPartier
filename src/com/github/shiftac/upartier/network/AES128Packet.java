package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import com.github.shiftac.upartier.Util;

public class AES128Packet extends Packet
{
    protected byte[] buf = new byte[16];
    static AES128Key key = null;

    static synchronized AES128Key accessKey(
        AES128Key nkey, boolean read)
    {
        if (read)
        {
            return key;
        }
        else
        {
            return key = nkey;
        }
    }

    public static void setKey(AES128Key nkey)
    {
        accessKey(nkey, false);
    }

    public static AES128Key getKey()
    {
        return accessKey(null, true);
    }

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
        setHeader(buf);
        int hlen = headerLen();
        int m = 16 - hlen;
        for (int i = 0; i < m; ++i)
        {
            data[i] = buf[i + hlen];
        }
        doRead(is, data, m, len + hlen - 16 - m, force);
        checkVersion();
        decrypt(data, 0);
    }

    @Override
    public void write(OutputStream os)
        throws IOException, PacketFormatException
    {
        checkVersion();
        fillHeader(buf);
        int hlen = headerLen();
        int m = 16 - hlen;
        for (int i = 0; i < m; ++i)
        {
            buf[i + hlen] = data[i];
        }
        byte[] tdata = new byte[data.length - m];
        for (int i = 0; i < tdata.length; ++i)
        {
            tdata[i] = data[i + m];
        }
        encrypt(buf, 0);
        encrypt(tdata, 0);
        os.write(buf);
        os.write(tdata, 0, tdata.length);
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
        if (((len + headerLen()) & 0xF) != 0)
        {
            throw new PacketFormatException("Length(" + len + ") not aligned.");
        }
    }
}
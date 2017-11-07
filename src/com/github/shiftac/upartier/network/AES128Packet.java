package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.Key;
import java.util.concurrent.atomic.AtomicInteger;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import com.github.shiftac.upartier.Util;

public class AES128Packet extends Packet
{
    protected byte[] buf = new byte[16];
    static AES128Key key = null;
    static AtomicInteger nowSeq = new AtomicInteger(0);

    public AES128Packet() 
    {
        version = getVersion();
        sequence = nowSeq.addAndGet(1) & 0xFFFF;
    }

    public AES128Packet(ByteArrayIO payload)
    {
        version = getVersion();
        sequence = nowSeq.addAndGet(1) & 0xFFFF;
        setLen(payload.getLength());
        try
        {
            payload.write(data, 0, data.length);
        }
        catch (Exception e) {}
    }

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

    // contains code from http://blog.csdn.net/fishmai/article/details/52398532
    // also encrypt(), AES128Key.AES128Key().
    void decrypt(byte[] src, int off)
    {
        try 
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key.key, key.spec);
            cipher.doFinal(src, off, src.length - off, src, off);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(Util.log.dest);
        }
    }

    void encrypt(byte[] src, int off)
    {
        try 
        {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key.key, key.spec);
            cipher.doFinal(src, off, src.length - off, src, off);
        } 
        catch (Exception e) 
        {
            e.printStackTrace(Util.log.dest);
        } 
    }

    @Override
    public void setLen(int len)
    {
        padding = (byte)(16 - ((len + headerLen()) & 0xF) & 0xF);
        data = new byte[len + padding];
        this.len = len;
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
        Util.log.logVerbose(getInf(), 3);
        setLen(len);
        int hlen = headerLen();
        int m = 16 - hlen;
        for (int i = 0; i < m; ++i)
        {
            data[i] = buf[i + hlen];
        }
        if (len < m)
        {
            checkVersion();
            return;
        }
        doRead(is, data, m, len + padding - m, force);
        checkVersion();
        decrypt(data, m);
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
        os.write(tdata);
        os.flush();
    }

    @Override
    public byte getVersion()
    {
        return (byte)PacketType.VER_AES128;
    }

    @Override
    protected void checkVersion()
        throws PacketFormatException
    {
        super.checkVersion();
        if (((len + padding + headerLen()) & 0xF) != 0)
        {
            throw new PacketFormatException("Length(" + len + ") not aligned.");
        }
    }
}
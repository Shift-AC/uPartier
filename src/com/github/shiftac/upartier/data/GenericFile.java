package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.Packet;

public class GenericFile implements ByteArrayIO
{
    public BString name = new BString();
    public byte[] payload = null;
    protected int type = ContentTypes.GENERAL;

    public GenericFile() {}

    public GenericFile(String name)
        throws IOException
    {
        Path path = Paths.get(name);
        this.payload = Files.readAllBytes(path);
        this.name = new BString(name);
    }

    public GenericFile(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    @Override
    public String getInf()
    {
        return String.format("type=%d, name=%s, len=%d", 
            type, name.toString(), payload.length);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        name.write(buf, off, len);
        int nlen = name.getLength();
        checkLen(len -= nlen, SIZE_INT + SIZE_INT + payload.length);
        setInt(buf, off += nlen, payload.length);
        setInt(buf, off += SIZE_INT, type);
        memcpy(buf, off += SIZE_INT, payload, 0, payload.length);
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        name.write(buf, off, len);
        int nlen = name.getLength();
        checkLen(len -= nlen, SIZE_INT + SIZE_INT);
        int blen = getInt(buf, off += nlen);
        type = getInt(buf, off += SIZE_INT);
        checkLen(len -= SIZE_INT + SIZE_INT, blen);
        payload = new byte[blen];
        memcpy(payload, 0, buf, off += SIZE_INT, blen);
    }

    public void setName(String name)
    {
        this.name.setContent(name);
    }

    @Override
    public int getLength()
    {
        int res = SIZE_INT + SIZE_INT + name.getLength();
        if (payload != null)
        {
            res += payload.length;
        }
        return res;
    }
}
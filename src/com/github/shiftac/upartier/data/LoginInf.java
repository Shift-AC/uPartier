package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.Packet;

public class LoginInf implements ByteArrayIO, PacketGenerator
{
    public int id = 0;
    public BString passwd = new BString();
    public boolean isNewUser = false;

    public LoginInf() {}

    public LoginInf(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    public LoginInf(int id, String passwd, boolean isNewUser)
    {
        this.id = id;
        this.passwd.setContent(passwd);
        this.isNewUser = isNewUser;
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT);
        id = getInt(buf, off);
        isNewUser = id < 0;
        id = id < 0 ? -id : id;
        passwd.read(buf, off += SIZE_INT, len -= SIZE_INT);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT);
        int tid = isNewUser ? -id : id;
        setInt(buf, off, tid);
        passwd.write(buf, off += SIZE_INT, len -= SIZE_INT);
    }

    @Override
    public int getLength()
    {
        return SIZE_INT + passwd.getLength();
    }

    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this, PacketType.TYPE_LOGIN);
    }

    @Override
    public String getInf()
    {
        return String.format("id=%d, passwd=%s, isNewUser=%b", id, passwd,
            isNewUser);
    }
}

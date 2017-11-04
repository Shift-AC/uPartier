package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.network.Encryptor;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;

public class WorkerThread extends Thread
{
    protected Socket s = null;
    protected InputStream is = null;
    protected OutputStream os = null;
    protected AES128Key key = null;
    static protected Encryptor encryptor = new AES128Encryptor();

    public WorkerThread(Socket s) 
        throws IOException
    {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
        key = new AES128Key(s);
    }

    Packet receivePacket()
        throws PacketFormatException
    {
        Packet pak = new Packet();
        pak.read(is);
        return encryptor.decrypt(packet, key);
    }

    void sendPacket(Packet pak)
    {
        encryptor.encrypt(packet, key).write(os);
    }

    @Override
    public void run()
    {
        
    }
}
package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.github.shiftac.upartier.network.NetworkTimeoutException;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;

public abstract class AbstractWorker
{
    protected Socket s = null;
    protected InputStream is = null;
    protected OutputStream os = null;
    protected static Class<? extends Object> cpak = null;
    public ConcurrentLinkedDeque<Packet> sendQueue = 
        new ConcurrentLinkedDeque<Packet>();
    public ConcurrentLinkedDeque<Packet> recvQueue = 
        new ConcurrentLinkedDeque<Packet>();

    public AbstractWorker(Socket s) 
        throws IOException
    {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    // used to distribute information about encrypt key.
    // run() should call this function before any other operations.
    // return 0 on normal situations.
    protected abstract int synchronize();

    public abstract void start();

    public abstract boolean isAlive();

    public void issue(Packet pak)
    {
        sendQueue.add(pak);
    }

    protected void send(Packet pak)
         throws IOException, PacketFormatException
    {
        pak.write(os);
    }

    protected Packet recv(boolean force)
        throws IOException, PacketFormatException, NetworkTimeoutException
    {
        Packet pak = null;
        try
        {
            pak = (Packet)cpak.getDeclaredConstructor().newInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        pak.read(is, force);
        return pak;
    }
}
package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.network.NetworkTimeoutException;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;

public abstract class AbstractWorkerThread extends Thread
{
    protected Socket s = null;
    protected InputStream is = null;
    protected OutputStream os = null;
    protected static Class cpak = null;

    public WorkerThread(Socket s) 
        throws IOException
    {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    // used to distribute information about encrypt key.
    // run() should call this function before any other operations.
    // return 0 on normal situations.
    abstract int synchronize();

    public void send(Packet pak)
         throws IOException, PacketFormatException
    {
        pak.write(os);
    }

    public Packet recv(boolean force)
        throws IOException, PacketFormatException, NetworkTimeoutException
    {
        Packet pak = cpak.newInstance();
        pak.read(is, force);
        return pak;
    }
}
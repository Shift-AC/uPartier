package com.github.shiftac.upartier.network.app;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.shiftac.upartier.data.LoginInf;
import com.github.shiftac.upartier.data.PacketType;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;

public class Client extends AbstractClient
{
    public static final Client client;
    public LoginInf inf;

    protected AtomicBoolean bufLock = new AtomicBoolean(false);
    public Thread[] waitBuf;
    public Packet[] recvBuf;

    private Client()
    {
        super();
        waitBuf = new Thread[256];
        recvBuf = new Packet[256];
    }

    public void init(LoginInf inf)
        throws IOException
    {
        synchronized (this)
        {
            this.inf = inf;
        }
        restart();
    }

    /**
     * Issue a packet and then enter {@code wait()} mode, current thread
     * will be later {@code notify()}ed when reply comes.
     */
    public Packet issueWait(Packet pak)
        throws SocketTimeoutException
    {
        synchronized (started)
        {
            if (started.get() == false)
            {
                throw new IllegalStateException("Client not started!");
            }
        }
        int seq = pak.sequence;
        Thread current = Thread.currentThread();
        synchronized (this.bufLock)
        {
            waitBuf[seq] = current;
        }

        super.issue(pak);

        synchronized (current)
        {
            try
            {
                current.wait();
            }
            catch (Exception e) {}
        }

        Packet res;
        synchronized (this.bufLock)
        {
            res = recvBuf[seq];
        }
        return res;
    }
    
    @Override
    protected void parseOut(Packet pak)
        throws IOException, PacketFormatException
    {
        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
        case PacketType.TYPE_LOGOUT:
        case PacketType.TYPE_USER_FETCH:
        case PacketType.TYPE_POST_FETCH:
        case PacketType.TYPE_BLOCK_FETCH:
        case PacketType.TYPE_USER_MODIFY:
        case PacketType.TYPE_POST_MODIFY:
        case PacketType.TYPE_MESSAGE_FETCH:
            pak.write(os);
            break;
        default:
            throw new PacketFormatException("Invalid packet type " + pak.type);
        }
        
    }

    @Override
    protected void parseIn(Packet pak)
        throws IOException, PacketFormatException
    {
        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
        case PacketType.TYPE_USER_FETCH:
        case PacketType.TYPE_POST_FETCH:
        case PacketType.TYPE_MESSAGE_FETCH:
        case PacketType.TYPE_SERVER_ACK:
            int ack = pak.ack;
            Thread waiting;
            synchronized (this.bufLock)
            {
                recvBuf[ack] = pak;
                waiting = waitBuf[ack];
                waitBuf[ack] = null;
            }
            synchronized (waiting)
            {
                waiting.notify();
            }
            break;
        case PacketType.TYPE_MESSAGE_PUSH:
        case PacketType.TYPE_LOGOUT:
            // someone should provide me a way to notify the app that 
            // theres's an incoming message.
            break;
        default:
            throw new PacketFormatException("Invalid packet type " + pak.type);
        }
    }

    @Override
    protected int synchronize()
    {
        int syn = super.synchronize();
        if (syn != 0)
        {
            return syn;
        }

        try
        {
            parseOut(new AES128Packet(inf));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    static
    {
        client = new Client();
    }
}
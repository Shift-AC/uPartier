package com.github.shiftac.upartier.network;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.SimpleWaitThread;
import com.github.shiftac.upartier.Util;

public abstract class AbstractWorker
{
    protected Socket s = null;
    protected InputStream is = null;
    protected OutputStream os = null;
    protected static Class<? extends Object> cpak = null;
    protected ConcurrentLinkedDeque<Packet> sendQueue = 
        new ConcurrentLinkedDeque<Packet>();

    protected SimpleWaitThread ot = new SimpleWaitThread()
    {
        @Override
        public void run()
        {
            if (synchronize() == 1)
            {
                try
                {
                    s.close();
                }
                catch (Exception e) 
                {
                    Util.log.logWarning("Exception when closing socket!");
                    e.printStackTrace(Util.log.dest);
                }
                return;
            }
            it.start();
            while (true)
            {
                try
                {
                    while (!sendQueue.isEmpty())
                    {
                        parseOut(sendQueue.remove());
                    }
                    Util.log.logVerbose("Sending queue is empty.", 1);
                    synchronized (this.wait)
                    {
                        if (!it.isAlive())
                        {
                            break;
                        }
                    }
                    Util.log.logVerbose(
                        "Recv thread still working, wait for packet issue.", 
                        1);
                    doWait();
                }
                catch (IOException ie)
                {
                    ie.printStackTrace(Util.log.dest);
                    break;
                }
                catch (Exception e)
                {
                    e.printStackTrace(Util.log.dest);
                }
            }
            Util.joinIgnoreInterrupt(it);
            try
            {
                s.close();
                Util.log.logMessage(String.format(
                    "Worker for %s:%d terminated.", s.getInetAddress(), 
                    s.getPort()));
            }
            catch (Exception e)
            {
                Util.log.logWarning("Exception when closing socket!");
                e.printStackTrace(Util.log.dest);
            }
        }
    };

    protected SimpleWaitThread it = new SimpleWaitThread()
    {
        @Override
        public void run()
        {
            try
            {
                AES128Packet pak = new AES128Packet();
                while (true)
                {
                    pak.read(is, false);
                    parseIn(pak);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace(Util.log.dest);
            }
            ot.doNotify();
        }
    };
    
    public AbstractWorker() {}

    public abstract void init(Socket s) 
        throws IOException;

    // used to distribute information about encrypt key.
    // run() should call this function before any other operations.
    // return 0 on normal situations.
    protected abstract int synchronize();

    protected abstract void parseIn(Packet pak)
        throws IOException, PacketFormatException;

    protected abstract void parseOut(Packet pak)
        throws IOException, PacketFormatException;

    public void start()
    {
        ot.start();
    }

    public boolean isAlive()
    {
        return ot.isAlive() || it.isAlive();
    }

    public void issue(Packet pak)
    {
        sendQueue.add(pak);
        Util.log.logVerbose(String.format(
            "Packet #%d issued. Protocol inf:(%s)", pak.sequence, 
                pak.getInf()), 2);
        ot.doNotify();
    }

    protected void send(Packet pak)
         throws IOException, PacketFormatException
    {
        pak.write(os);
        Util.log.logVerbose(String.format(
            "Packet #%d sent.", pak.sequence), 2);
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
            e.printStackTrace(Util.log.dest);
        }
        pak.read(is, force);
        Util.log.logVerbose(String.format(
            "Packet received. Protocol inf:(%s)", pak.getInf()), 2);
        return pak;
    }

    public void terminate()
    {
        try
        {
            Socket ts = s;
            s = null;
            ts.close();
        }
        catch (Exception e) {}
        Util.joinIgnoreInterrupt(ot);
    }
}
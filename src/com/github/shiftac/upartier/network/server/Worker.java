package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.SimpleWaitThread;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.Util;

public class Worker extends AbstractWorker
{
    protected AES128Key key = null;
    protected static Class<? extends Object> cpak = null;
    int ip = 0;
    int userID = 0;
    long timestamp = 0;
    protected SimpleWaitThread ot = new SimpleWaitThread()
    {
        public void parse(Packet pak)
            throws IOException
        {
            Util.log.logMessage("Parsing send package #" + pak.sequence);

        }

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
                        parse(sendQueue.remove());
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
        public void parse(Packet pak)
        {
            
        }

        @Override
        public void run()
        {
            try
            {
                AES128Packet pak = new AES128Packet();
                while (true)
                {
                    pak.read(is, false);
                    parse(pak);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace(Util.log.dest);
            }
            ot.doNotify();
        }
    };

    public Worker(Socket s)
        throws IOException
    {
        super(s);
        byte[] saddr = s.getInetAddress().getAddress();
        ip = Util.getInt(saddr, 0);
    }

    @Override
    public void issue(Packet pak)
    {
        super.issue(pak);
        ot.doNotify();
    }

    protected int synchronize()
    {
        Util.log.logVerbose("Synchronizing with client...", 1);
        try
        {
            byte[] buf = new byte[16];
            is.read(buf);
            userID = Util.getInt(buf, 0);
            timestamp = Util.getLong(buf, 4);
            Util.log.logVerbose(String.format(
                "Got userID=%d, timestamp=%d", userID, timestamp), 2);
            Util.setInt(buf, 12, ip);
            Util.log.logVerbose(String.format("Set ip=%d", ip), 2);
            os.write(buf);
            os.flush();
            is.read(buf);
            int tid = Util.getInt(buf, 0);
            long tts = Util.getLong(buf, 4);
            int tip = Util.getInt(buf, 12);
            Util.log.logVerbose(String.format(
                "Got userID=%d, timestamp=%d, ip=%d", tid, tts, tip), 2);
            if (userID != tid || timestamp != tts || ip != tip)
            {
                Util.log.logError("Synchronization id not match!");
                s.close();
                throw new IOException("Synchronization failed.");
            }
            os.write(0);
            Util.log.logMessage("Synchronize completed.");
            key = new AES128Key(ip, userID, timestamp);
            AES128Packet.setKey(key);
        }
        catch (Exception e)
        {
            Util.log.logError("Exception in synchronization process!");
            e.printStackTrace(Util.log.dest);
            return 1;
        }
        return 0;
    }

    @Override
    public void start()
    {
        ot.start();
    }

    @Override
    public boolean isAlive()
    {
        return it.isAlive() || ot.isAlive();
    }

    static 
    {
        try
        {
            cpak = Class.forName(
                "com.github.shiftac.upartier.network.AES128Packet");
        }
        catch (Exception e)
        {
            Util.errorExit("Can't initialize packet class.", e);
        }
    }
}
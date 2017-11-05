package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.network.AES128Key;
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
    protected Thread ot = new Thread()
    {
        public void parse(Packet pak)
        {

        }

        @Override
        public void run()
        {
            if (synchronize() == 1)
            {
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
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    protected Thread it = new Thread()
    {
        public void parse(Packet pak)
        {
            
        }

        @Override
        public void run()
        {
            while (true)
            {
                try
                {
                    while (!recvQueue.isEmpty())
                    {
                        parse(recvQueue.remove());
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    };

    public Worker(Socket s)
        throws IOException
    {
        super(s);
        byte[] saddr = s.getInetAddress().getAddress();
        ip = Util.getInt(saddr, 0);
    }

    protected int synchronize()
    {
        try
        {
            byte[] buf = new byte[16];
            is.read(buf);
            userID = Util.getInt(buf, 0);
            timestamp = Util.getLong(buf, 4);
            Util.setInt(buf, 12, ip);
            os.write(buf);
            os.flush();
            is.read(buf);
            if (userID != Util.getInt(buf, 0) || timestamp != Util.getLong(buf, 4))
            {
                s.close();
                throw new IOException("Synchronization failed.");
            }
            key = new AES128Key(ip, userID, timestamp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public void start()
    {
        it.start();
    }

    @Override
    public boolean isAlive()
    {
        return it.isAlive() && ot.isAlive();
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
            Util.errorExit(e);
        }
    }
}
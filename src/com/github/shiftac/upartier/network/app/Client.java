package com.github.shiftac.upartier.network.app;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.SimpleWaitThread;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.Util;

/** 
 * Provides interfaces for sending/receving data as a client. 
 * This class is a stand-alone thread working on the Android devices and it 
 * acts as a network interface.
 * <p>
 * In our application layer model, <code>Client</code> lays on the bottom of 
 * <i> network</i> layer. It implements sending/receving operations for general
 * usages, do encryption/decryption work, while other <i>network</i> layer
 * classes simply call interfaces it provides to complete the communication 
 * work. 
 * 
 * @author ShiftAC
 * @see com.github.shiftac.upartier.network.server.Server
 */
public class Client
{
    private static Client client = null;
    private int id = 0;
    private long mili = 0;
    private int ip = 0;
    private Socket s = null;
    private InputStream is = null;
    private OutputStream os = null;
    private AES128Key key = null;
    public ConcurrentLinkedDeque<Packet> sendQueue = 
        new ConcurrentLinkedDeque<Packet>();
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
                Util.log.logMessage(String.format("Session terminated."));
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
            try
            {
                if (s != null)
                {
                    s.close();
                }
            }
            catch (Exception e)
            {
                Util.log.logWarning("Exception when closing socket!");
                e.printStackTrace(Util.log.dest);
            }
            Util.joinIgnoreInterrupt(it);
            Util.log.logMessage(String.format("Session terminated."));
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

    public static synchronized void startClient(int userID)
    {
        if (client == null)
        {
            client = new Client(userID);
            client.start();
        }
        else if (client.id != userID)
        {
            try
            {
                Socket s = client.s;
                client.s = null;
                s.close();
            }
            catch (Exception e) {}
            Util.joinIgnoreInterrupt(client.ot);
            client = new Client(userID);
            client.start();
        }
    }

    private Client(int userID)
    {
        id = userID;
    }

    private int synchronize()
    {
        Util.log.logVerbose("Synchronizing with server...", 1);
        try
        {
            byte[] buf = new byte[16];
            mili = LogManager.calendar.getTimeInMillis();
            Util.setInt(buf, 0, id);
            Util.setLong(buf, 4, mili);
            Util.log.logVerbose(String.format(
                "Set userID=%d, timestamp=%d", id, mili), 2);
            String host = Util.getStringConfig("/network/server/Server/host");
            int port = Util.getIntConfig("/network/server/Server/port");
            SocketChannel sc = SocketChannel.open();
            sc.connect(new InetSocketAddress(host, port));
            s = sc.socket();
            s.getChannel().setOption(
                StandardSocketOptions.SO_KEEPALIVE, true);
            is = s.getInputStream();
            os = s.getOutputStream();
            os.write(buf);
            os.flush();
            is.read(buf);
            int tid = Util.getInt(buf, 0);
            long tts = Util.getLong(buf, 4);
            int tip = Util.getInt(buf, 12);
            Util.log.logVerbose(String.format(
                "Got userID=%d, timestamp=%d, ip=%d", tid, tts, tip), 2);
            if (id != tid || mili != tts)
            {
                s.close();
                throw new IOException("Synchronization failed.");
            }
            ip = tip;
            os.write(buf);
            os.flush();
            is.read();
            Util.log.logMessage("Synchronize completed.");
            key = new AES128Key(ip, id, mili);
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

    public void start()
    {
        ot.start();
    }
}
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
import com.github.shiftac.upartier.network.AbstractWorker;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.network.PacketType;
import com.github.shiftac.upartier.network.PlainMessage;
import com.github.shiftac.upartier.Util;

public abstract class AbstractClient extends AbstractWorker
{
    protected int id = 0;
    protected long mili = 0;
    protected int ip = 0;
    protected AES128Key key = null;

    public AbstractClient(int userID)
    {
        super();
        id = userID;
    }

    @Override
    public void init(Socket s)
        throws IOException
    {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
    }

    @Override
    protected int synchronize()
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
            init(new Socket(host, port));
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
}
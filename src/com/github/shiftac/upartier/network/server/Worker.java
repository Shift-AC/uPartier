package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.SimpleWaitThread;
import com.github.shiftac.upartier.network.AbstractWorker;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.network.PacketType;
import com.github.shiftac.upartier.network.PlainMessage;

import com.github.shiftac.upartier.Util;

public abstract class Worker extends AbstractWorker
{
    protected AES128Key key = null;
    protected int ip = 0;
    protected int userID = 0;
    protected long timestamp = 0;

    @Override
    public void init(Socket s)
        throws IOException
    {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
        byte[] saddr = s.getInetAddress().getAddress();
        ip = Util.getInt(saddr, 0);
        Util.log.logMessage(String.format(
            "Worker initialized for request from %s:%d", 
            s.getInetAddress().toString(), s.getPort()));
    }

    @Override
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
}
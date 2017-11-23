package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.SimpleWaitThread;
import com.github.shiftac.upartier.network.AbstractWorker;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.network.PacketVersion;
import com.github.shiftac.upartier.network.PlainMessage;
import com.github.shiftac.upartier.network.SynObject;

import com.github.shiftac.upartier.Util;

public abstract class Worker extends AbstractWorker
{
    protected AES128Key key = null;
    protected SynObject obj = new SynObject();

    @Override
    public void init(Socket s)
        throws IOException
    {
        this.s = s;
        is = s.getInputStream();
        os = s.getOutputStream();
        byte[] saddr = s.getInetAddress().getAddress();
        obj.ip = obj.getInt(saddr, 0);
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
            obj.id = obj.getInt(buf, 0);
            obj.mili = obj.getLong(buf, ByteArrayIO.SIZE_INT);
            Util.log.logVerbose(String.format(
                "Got userID=%d, timestamp=%d", obj.id, obj.mili), 2);
            obj.setInt(buf, ByteArrayIO.SIZE_INT + ByteArrayIO.SIZE_LONG,
                obj.ip);
            Util.log.logVerbose(String.format("Set ip=%d", obj.ip), 2);
            os.write(buf);
            os.flush();
            is.read(buf);
            SynObject tobj = new SynObject();
            tobj.read(buf);
            Util.log.logVerbose(String.format(
                "Got userID=%d, timestamp=%d, ip=%d", tobj.id, tobj.mili,
                tobj.ip), 2);
            if (obj.id != tobj.id || obj.mili != tobj.mili || 
                obj.ip != tobj.ip)
            {
                Util.log.logError("Synchronization id not match!");
                s.close();
                throw new IOException("Synchronization failed.");
            }
            os.write(0);
            Util.log.logMessage("Synchronize completed.");
            key = new AES128Key(obj.ip, obj.id, obj.mili);
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
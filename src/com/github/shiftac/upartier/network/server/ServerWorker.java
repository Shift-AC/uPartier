package com.github.shiftac.upartier.network.server;

import java.io.IOException;
import java.net.Socket;

import com.github.shiftac.upartier.network.AbstractWorker;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.SynObject;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.data.LoginInf;

public abstract class ServerWorker extends AbstractWorker
{
    protected AES128Key key = null;
    protected SynObject obj = new SynObject();
    protected LoginInf current = null;
    protected WorkerManager manager = null;
    protected long seq = -1;

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
            "Server Worker initialized for request from %s:%d", 
            s.getInetAddress().toString(), s.getPort()));
    }

    public void init(Socket s, WorkerManager manager, long seq)
        throws IOException
    {
        init(s);
        this.manager = manager;
        this.seq = seq;
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
            Util.log.logVerbose("Got SynObject " + obj.getInf(), 2);
            obj.setInt(buf, ByteArrayIO.SIZE_INT + ByteArrayIO.SIZE_LONG,
                obj.ip);
            Util.log.logVerbose(String.format("Set ip=%d", obj.ip), 2);
            os.write(buf);
            os.flush();
            is.read(buf);
            SynObject tobj = new SynObject();
            tobj.read(buf);
            Util.log.logVerbose("Got SynObject " + obj.getInf(), 2);
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

    protected void notifyClient() {}

    public void endSession()
    {
        synchronized (current)
        {
            if (current == null)
            {
                return;
            }

            notifyClient();

            current = null;
        }
    }
}
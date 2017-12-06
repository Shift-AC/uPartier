package com.github.shiftac.upartier.network.app;

import java.io.IOException;
import java.net.Socket;
import java.util.Random;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.AbstractWorker;
import com.github.shiftac.upartier.network.SynObject;
import com.github.shiftac.upartier.Util;

public abstract class AbstractClient extends AbstractWorker
{
    protected SynObject obj = new SynObject();
    protected AES128Key key = null;

    public AbstractClient() 
    {
        Random rand = new Random(
            LogManager.calendar.getTimeInMillis());

        obj.id = rand.nextInt();
    }

    public AbstractClient(int userID)
    {
        super();
        obj.id = userID;
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
            obj.mili = LogManager.calendar.getTimeInMillis();
            long mili = obj.mili;
            byte[] buf = obj.toByteArray();
            Util.log.logVerbose("Got SynObject " + obj.getInf(), 2);
            String host = Util.getStringConfig("/network/server/Server/host");
            int port = Util.getIntConfig("/network/server/Server/port");
            init(new Socket(host, port));
            os.write(buf);
            os.flush();
            is.read(buf);
            obj.read(buf);
            Util.log.logVerbose("Got SynObject " + obj.getInf(), 2);
            if (obj.id != obj.id || mili != obj.mili)
            {
                s.close();
                throw new IOException("Synchronization failed.");
            }
            os.write(buf);
            os.flush();
            is.read();
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
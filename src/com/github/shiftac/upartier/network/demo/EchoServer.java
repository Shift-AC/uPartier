package com.github.shiftac.upartier.network.demo;

import java.io.IOException;
import com.github.shiftac.upartier.network.server.AbstractServer;
import com.github.shiftac.upartier.network.server.WorkerManager;
import com.github.shiftac.upartier.Util;

public final class EchoServer extends AbstractServer
{
    public EchoServer()
        throws IOException
    {
        super();
    }

    @Override
    protected void initManager()
    {
        Class<? extends Object> c = getClass();
        int maxWorker = Util.getIntConfig(c, "maxWorker");
        try
        {
            manager = new WorkerManager(maxWorker, 
                "com.github.shiftac.upartier.network.demo.EchoWorker");
        }
        catch (Exception e) {}
    }

    public static void main(String[] args)
    {
        try
        {
            AbstractServer s = new EchoServer();
            s.start();
            s.join();
        }
        catch (Exception e)
        {
            e.printStackTrace(Util.log.dest);
        }
    }
}
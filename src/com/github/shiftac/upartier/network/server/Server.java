package com.github.shiftac.upartier.network.server;

import java.io.IOException;

import com.github.shiftac.upartier.Util;

public class Server extends AbstractServer
{
    public Server()
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
                "com.github.shiftac.upartier.network.server.Worker");
        }
        catch (Exception e) {}
    }

    public static void main(String[] args)
    {
        try
        {
            Server s = new Server();
            s.start();
            s.join();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
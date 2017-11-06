package com.github.shiftac.upartier.network.server;

import java.net.Socket;
import com.github.shiftac.upartier.Util;

public class WorkerManager
{
    private AbstractWorker[] pool;
    private int probeGap = Util.getIntConfig(getClass(), "probeGapInms");

    public WorkerManager(int maxWorker)
    {
        pool = new AbstractWorker[maxWorker];
    }

    public AbstractWorker delegate(Socket s)
    {
        int rec = 0;
        try
        {
            for (int i = 0; i < pool.length; ++i)
            {
                if (pool[i] == null || !pool[i].isAlive())
                {
                    rec = i;
                    synchronized(this)
                    {
                        pool[i] = new Worker(s);
                        pool[i].start();
                    }
                    Util.log.logVerbose(String.format(
                        "Worker #%d now working..., ", i), 1);
                    return pool[i];
                }
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace(Util.log.dest);
            pool[rec] = null;
        }
        return null;
    }
}
package com.github.shiftac.upartier.network.server;

import java.net.Socket;
import static com.github.shiftac.upartier.Util.*;

public class WorkerManager
{
    private AbstractWorkerThread[] pool;
    private int probeGap = getIntConfig(getClass(), "probeGapInms");

    public WorkerManager(int maxWorker)
    {
        pool = new AbstractWorkerThread[maxWorker];
    }

    public AbstractWorkerThread delegate(Socket s)
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
                        pool[i] = new WorkerThread(s);
                        pool[i].start();
                        return pool[i];
                    }
                }
            }
        }
        catch (Exception e) 
        {
            e.printStackTrace();
            pool[rec] = null;
        }
        return null;
    }
}
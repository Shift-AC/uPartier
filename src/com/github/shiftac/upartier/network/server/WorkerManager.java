package com.github.shiftac.upartier.network.server;

import java.net.Socket;
import java.util.concurrent.ConcurrentHashMap;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.Packet;

public class WorkerManager
{
    private ServerWorker[] pool;
    //private int probeGap = Util.getIntConfig(getClass(), "probeGapInms");
    public Class<? extends ServerWorker> cworker = null;
    private long seq = 0;
    private ConcurrentHashMap<Integer, ServerWorker> idMap = 
        new ConcurrentHashMap<Integer, ServerWorker>();

    @SuppressWarnings("unchecked")
    public WorkerManager(int maxWorker, String name)
        throws ClassNotFoundException
    {
        pool = new ServerWorker[maxWorker];
        cworker = (Class<? extends ServerWorker>)(Class.forName(name));
    }

    public void setIDPos(ServerWorker worker, int id)
    {
        ServerWorker x = idMap.get(id);
        if (x != null)
        {
            x.endSession();
        }

        idMap.put(id, worker);
    }

    public void removeIDPos(int id)
    {
        idMap.remove(id);
    }

    public void broadcast(Packet pak, int[] ids, int source)
    {
        Util.log.logMessage("Broadcasting...");
        for (int i = 0; i < ids.length; ++i)
        {
            if (ids[i] == source)
            {
                continue;
            }
            ServerWorker worker = idMap.get(ids[i]);
            Util.log.logMessage("Sending to worker " + i);
            worker.issue(pak);
        }
    }

    public ServerWorker delegate(Socket s)
    {
        int rec = 0;
        try
        {
            for (int i = 0; i < pool.length; ++i)
            {
                if (pool[i] == null || !pool[i].isAlive())
                {
                    rec = i;
                    synchronized (this)
                    {
                        pool[i] = 
                            cworker.getDeclaredConstructor().newInstance();
                        pool[i].init(s, this, seq++);
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
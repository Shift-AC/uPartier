package com.github.shiftac.upartier.network.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import com.github.shiftac.upartier.Util;

public abstract class AbstractServer extends Thread
{
    protected ServerSocket ss = null;
    protected WorkerManager manager = null;

    protected void listen(int port)
        throws IOException
    {
        ss = new ServerSocket(port);
    }

    protected abstract void initManager();

    public AbstractServer()
        throws IOException
    {
        initManager();
    }

    protected void refuse(Socket s)
        throws IOException
    {
        s.close();
    }

    @Override
    public void run()
    {
        Util.log.logMessage("Server starting...");
        int port = Util.getIntConfig("/network/server/Server/port");
        try
        {
            listen(port);    
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return;
        }
        Util.log.logMessage("Server listening on port " + port + ".");
        while (true)
        {
            try
            {
                Socket s = ss.accept();
                s.setKeepAlive(true);
                Util.log.logMessage(String.format(
                    "Accepted request from %s:%d", 
                    s.getInetAddress().toString(), s.getPort()));
                if (manager.delegate(s) == null)
                {
                    Util.log.logWarning("Can't allocate worker, refuse.");
                    refuse(s);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace(Util.log.dest);
            }
        }
    }
}
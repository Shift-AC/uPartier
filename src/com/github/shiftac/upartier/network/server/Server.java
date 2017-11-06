package com.github.shiftac.upartier.network.server;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.StandardSocketOptions; 
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.Util;

/** 
 * Provides interfaces for sending/receving data as a server. 
 * This class is a stand-alone thread working on the server and it acts as a
 * network interface.
 * <p>
 * In our application layer model, <code>Server</code> lays on the bottom of 
 * <i> network</i> layer. It implements sending/receving operations for general
 * usages, do encryption/decryption work, while other <i>network</i> layer
 * classes simply call interfaces it provides to complete the communication 
 * work. 
 * <p>
 * This class is a bit complicated than <code>com.github.shiftac.
 * upartier.app.network.Client</code>, because <code>Server</code> is a 
 * <i>global</i> singleton, it deals with all network requests concurrently,
 * while each <i>uPartier</i> instance has a <code>Client</code> instance to
 * communicate with the <code>Server</code>.
 * 
 * @see com.github.shiftac.upartier.app.network.Client
 * @author ShiftAC
 * @since 1.0
 */
public class Server extends Thread
{
    private static Server server = null;
    private ServerSocket ss = null;
    private WorkerManager manager = null;
    public ConcurrentLinkedDeque<Packet> msgQueue = 
        new ConcurrentLinkedDeque<Packet>();

    public static Server getInstance()
    {
        return server;
    }

    private void listen(int port)
        throws IOException
    {
        ServerSocketChannel ssc = ServerSocketChannel.open();  
        ssc.socket().bind(new InetSocketAddress(port));
        ssc.socket().setReuseAddress(true);
        ss = ssc.socket();
        //ss.getChannel().setOption(StandardSocketOptions.SO_KEEPALIVE, true);
        //ssc.configureBlocking(false);
    }

    private Server()
        throws IOException
    {
        Class<? extends Object> c = getClass();
        int port = Util.getIntConfig(c, "port");
        Util.log.logMessage("Server listening on port " + port + ".");
        listen(port);
        int maxWorker = Util.getIntConfig(c, "maxWorker");
        manager= new WorkerManager(maxWorker);
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
        while (true)
        {
            try
            {
                Socket s = ss.accept();
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

    static
    {
        try
        {
            server = new Server();
            server.start();
        }
        catch (Exception e)
        {
            Util.errorExit("Can't start server.", e);
        }
    }
}
package com.github.shiftac.upartier.server.network;

import java.io.IOException;
import java.net.ServerSocket;
import static com.github.shiftac.upartier.Util.*;

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
public class Server
{
    static
    {
        try
        {
            server = new Server();
        }
        catch (Exception e)
        {
            errorExit(e);
        }
    }

    private static Server server;
    private ServerSocket ss;

    public static Server getInstance()
    {
        return server;
    }

    private Server()
        throws IOException
    {
        initServerSocket();
    }

    private void initServerSocket()
        throws IOException
    {
        ss = new ServerSocket((int)getLongConfig(
            "/server/network/server/port"));
    }
}
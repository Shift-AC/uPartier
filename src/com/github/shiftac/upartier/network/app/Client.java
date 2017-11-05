package com.github.shiftac.upartier.network.app;

import static com.github.shiftac.upartier.Util.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.network.AES128Encryptor;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.Encryptor;

/** 
 * Provides interfaces for sending/receving data as a client. 
 * This class is a stand-alone thread working on the Android devices and it 
 * acts as a network interface.
 * <p>
 * In our application layer model, <code>Client</code> lays on the bottom of 
 * <i> network</i> layer. It implements sending/receving operations for general
 * usages, do encryption/decryption work, while other <i>network</i> layer
 * classes simply call interfaces it provides to complete the communication 
 * work. 
 * 
 * @author ShiftAC
 * @see com.github.shiftac.upartier.network.server.Server
 */
public class Client extends Thread
{
    private static Client client = null;
    private int id = 0;
    private long mili = 0;
    private int ip = 0;
    private Socket s = null;
    private InputStream is = null;
    private OutputStream os = null;
    private Encryptor encryptor = new AES128Encryptor();
    private AES128Key key = null;

    public static synchronized void startClient(int userID)
    {
        if (client == null)
        {
            client = new Client(userID);
            client.start();
        }
    }

    private Client(int userID)
    {
        id = userID;
    }

    private int synchronize()
    {
        try
        {
            byte[] buf = new byte[16];
            mili = LogManager.calendar.getTimeInMillis();
            setInt(buf, 0, id);
            setLong(buf, 4, mili);
            String host = getStringConfig("/network/server/Server/host");
            int port = getStringConfig("/network/server/Server/port");
            s = new Socket(host, port);
            is = s.getInputStream();
            os = s.getOutputStream();
            os.write(buf);
            os.flush();
            is.read(buf);
            if (id != getInt(buf, 0) || mili != getLong(buf, 4))
            {
                s.close();
                throw new IOException("Synchronization failed.");
            }
            ip = getInt(buf, 12);
            os.write(buf);
            os.flush();
            key = new AES128Key(ip, id, timestamp);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    @Override
    public void run()
    {
        synchronize();
    }
}
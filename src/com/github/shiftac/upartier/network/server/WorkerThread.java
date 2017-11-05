package com.github.shiftac.upartier.network.server;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.network.AES128Encryptor;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.Encryptor;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import static com.github.shiftac.upartier.Util.*;

public class WorkerThread extends AbstractWorkerThread
{
    protected AES128Key key = null;
    protected static Class cpak = Class.forName(
        "com.github.shiftac.upartier.network.AES128Packet");
    int ip = 0;
    int userID = 0;
    long timestamp = 0;

    public WorkerThread(Socket s)
    {
        super(s);
        encryptor = stdEncryptor;
        byte[] saddr = s.getInetAddress().getAddress();
        ip = getInt(saddr, 0);
    }

    int synchronize()
    {
        try
        {
            byte[] buf = new byte[16];
            is.read(buf);
            userID = getInt(buf, 0);
            timestamp = getLong(buf, 4);
            setInt(buf, 12, ip);
            os.write(buf);
            os.flush();
            is.read(buf);
            if (userID != getInt(buf, 0) || timestamp != getLong(buf, 4))
            {
                s.close();
                throw new IOException("Synchronization failed.");
            }
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
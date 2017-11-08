package com.github.shiftac.upartier.network.demo;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.StandardSocketOptions;
import java.nio.channels.SocketChannel;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.ConcurrentLinkedDeque;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.SimpleWaitThread;
import com.github.shiftac.upartier.network.AES128Key;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.AbstractWorker;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.network.PacketType;
import com.github.shiftac.upartier.network.PlainMessage;
import com.github.shiftac.upartier.network.app.AbstractClient;
import com.github.shiftac.upartier.Util;

public final class EchoClient extends AbstractClient
{
    public EchoClient(int userID)
    {
        super(userID);
    }

    @Override
    protected void parseOut(Packet pak)
        throws IOException, PacketFormatException
    {
        Util.log.logMessage("Parsing send package #" + pak.sequence);
        
        if (pak.type != (byte)(
            PacketType.TYPE_CTRL | PacketType.CTRL_LOCAL))
        {
            pak.write(os);
            Util.log.logMessage("Package #" + pak.sequence + " sent.");    
        }
        else
        {
            Util.log.logMessage("Parsing local control package...");
        }
    }

    @Override
    protected void parseIn(Packet pak)
        throws IOException, PacketFormatException
    {
        Util.log.logMessage("Parsing incoming package...");
        switch (pak.type)
        {
        case PacketType.TYPE_PUSH | PacketType.DATA_MESSAGE_PLAIN:
            PlainMessage msg = new PlainMessage(pak.data);
            System.out.printf("Server says: %s\n", msg.toString());
            break;
        default:
            Util.log.logWarning(
                "Unrecognized package. Inf:" + pak.getInf());
        }
    }
}
package com.github.shiftac.upartier.network.demo;

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
import com.github.shiftac.upartier.network.PacketParser;
import com.github.shiftac.upartier.network.server.AbstractServer;
import com.github.shiftac.upartier.network.server.WorkerManager;
import com.github.shiftac.upartier.Util;

public final class EchoServer extends AbstractServer
{
    public EchoServer()
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
                "com.github.shiftac.upartier.network.demo.EchoWorker");
        }
        catch (Exception e) {}
    }
}
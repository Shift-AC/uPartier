package com.github.shiftac.upartier.network.server;

import java.io.IOException;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.data.ACKInf;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.server.ServerWorker;

@FunctionalInterface
public interface PacketParser
{
    public default void parse(ServerWorker worker, Packet pak, boolean login,
        ByteArrayIO obj)
    {
        Util.log.logVerbose("Parsing packet from worker #" + worker.seq + ".");
        if (!checkState(worker, pak, login))
        {
            Util.log.logWarning(
                "State check failed, expected login = " + login);
            return;
        }
        if (!generateObject(worker, pak, obj))
        {
            Util.log.logWarning("Can't generate " + 
                obj.getClass().getName() + ".");
            return;
        }
        parseObject(worker, obj, pak.sequence);
    }

    // checkstate->generate object->parse object
    public default boolean checkState(ServerWorker worker, Packet pak, 
        boolean login)
    {
        boolean cur = worker.loginState();
        if (cur != login)
        {
            worker.issueAck(new ACKInf(ACKInf.RET_ERRIO).toPacket(), 
                pak.sequence);
            return false;
        }
        return true;
    }

    public default boolean generateObject(ServerWorker worker, Packet pak, 
        ByteArrayIO obj)
    {
        try
        {
            obj.read(pak);
            return true;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            worker.issueAck(new ACKInf(ACKInf.RET_ERRIO).toPacket(), 
                pak.sequence);
            return false;
        }
    }

    public void parseObject(ServerWorker worker, ByteArrayIO obj, byte seq);
}
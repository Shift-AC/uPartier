package com.github.shiftac.upartier.network.demo;

import java.io.IOException;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.network.PlainMessage;
import com.github.shiftac.upartier.network.server.ServerWorker;
import com.github.shiftac.upartier.Util;

public final class EchoWorker extends ServerWorker
{
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
            System.out.printf("Client #%d says: %s\n", obj.id, 
                new PlainMessage(pak.data).toString());
            break;
        case PacketType.TYPE_TRIGGER | PacketType.DATA_MESSAGE_PLAIN:
            PlainMessage msg = new PlainMessage(pak.data);
            System.out.printf("Client #%d(trigger) says: %s\n", obj.id, 
                msg.toString());
            AES128Packet tpak = new AES128Packet(msg);
            tpak.type = 
                PacketType.TYPE_PUSH | PacketType.DATA_MESSAGE_PLAIN;
            issue(tpak);
            break;
        default:
            Util.log.logWarning(
                "Unrecognized package. Inf:" + pak.getInf());
        }
    }
}
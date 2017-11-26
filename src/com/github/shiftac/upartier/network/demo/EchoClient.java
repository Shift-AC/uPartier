package com.github.shiftac.upartier.network.demo;

import java.io.IOException;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
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
package com.github.shiftac.upartier.network.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import com.github.shiftac.upartier.network.AES128Packet;
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

    public static void main(String[] args)
    {
        try
        {
            AbstractClient client = new EchoClient(10);
            client.start();
            while (true)
            {
                BufferedReader is = new BufferedReader(
                    new InputStreamReader(System.in));
                String line = is.readLine();
                AES128Packet pak;
                if (line.charAt(0) == ' ')
                {
                    pak = new AES128Packet(new PlainMessage(line.substring(1)));
                    pak.type = PacketType.DATA_MESSAGE_PLAIN | 
                        PacketType.TYPE_TRIGGER;
                }
                else
                {
                    pak = new AES128Packet(new PlainMessage(line));
                    pak.type = PacketType.DATA_MESSAGE_PLAIN | 
                        PacketType.TYPE_PUSH;
                }
                client.issue(pak);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(Util.log.dest);
        }
    }
}
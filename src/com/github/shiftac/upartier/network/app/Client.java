package com.github.shiftac.upartier.network.app;

import java.io.IOException;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;

public class Client extends AbstractClient
{
    public static final Client client;

    private Client()
    {
        super();
    }

    private Client(int userID)
    {
        super(userID);
    }

    @Override
    protected void parseOut(Packet pak)
        throws IOException, PacketFormatException
    {
        
    }

    @Override
    protected void parseIn(Packet pak)
        throws IOException, PacketFormatException
    {

    }

    @Override
    protected int synchronize()
    {
        int syn = super.synchronize();
        if (syn != 0)
        {
            return syn;
        }

        parseOut(new LoginPacket());
    }

    static
    {
        client = new Client();
    }
}
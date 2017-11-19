package com.github.shiftac.upartier.network.app;

import java.io.IOException;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.data.LoginInf;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;

public class Client extends AbstractClient
{
    public static final Client client;
    public LoginInf inf;

    private Client()
    {
        super();
    }

    public static void init(LoginInf inf)
    {
        synchronized(client)
        {
            client.inf = inf;
        }
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

        try
        {
            parseOut(new AES128Packet(inf));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return 1;
        }
        return 0;
    }

    static
    {
        client = new Client();
    }
}
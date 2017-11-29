package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.Packet;

public class Audio extends GenericFile
{
    public Audio() 
    {
        type = ContentTypes.AUDIO;
    }
    
    public Audio(Packet pak)
        throws IOException
    {
        super(pak);
        type = ContentTypes.AUDIO;
    }

    public Audio(String name)
        throws IOException
    {
        super(name);
        type = ContentTypes.AUDIO;
    }
}
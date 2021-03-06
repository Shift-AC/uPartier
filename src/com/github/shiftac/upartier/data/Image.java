package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.Packet;

public class Image extends GenericFile
{
    public Image() 
    {
        type = ContentTypes.IMAGE;
    }

    public Image(Packet pak)
        throws IOException
    {
        super(pak);
        type = ContentTypes.IMAGE;
    }

    public Image(String name)
        throws IOException
    {
        super(name);
        type = ContentTypes.IMAGE;
    }
}
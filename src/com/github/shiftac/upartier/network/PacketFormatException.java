package com.github.shiftac.upartier.network;

public class PacketFormatException extends Exception
{
    public static final long serialVersionUID = Util.version;

    public PacketFormatException()
    {
        super();
    }

    public PacketFormatException(String str)
    {
        super(str);
    }
}
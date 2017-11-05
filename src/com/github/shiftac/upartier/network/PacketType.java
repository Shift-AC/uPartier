package com.github.shiftac.upartier.network;

import java.util.HashMap;

public abstract class PacketType
{
    private static HashMap<String, Byte> map = new HashMap<String, Byte>();
    public static byte version(String name)
    {
        return map.get(name).byteValue();
    }

    static
    {
        map.put("RawPacket", (byte)0);
        map.put("AES128Packet", (byte)1);
        //map.put("LocalPacket", new Byte(2));
    }
}
package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.network.AES128Packet;

public interface PacketGenerator
{
    public AES128Packet toPacket();
}
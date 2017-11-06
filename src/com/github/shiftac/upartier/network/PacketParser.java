package com.github.shiftac.upartier.network;

import java.io.IOException;

@FunctionalInterface
public interface PacketParser
{
    public void parse(Packet pak)
        throws IOException, PacketFormatException;
}
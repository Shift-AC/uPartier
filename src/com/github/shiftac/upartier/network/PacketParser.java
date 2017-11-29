package com.github.shiftac.upartier.network;

import java.io.IOException;

@FunctionalInterface
public interface PacketParser
{
    public void parse(AbstractWorker worker, Packet pak)
        throws IOException, PacketFormatException;
}
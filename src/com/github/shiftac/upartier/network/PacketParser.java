package com.github.shiftac.upartier.network;

import java.io.IOException;

import com.github.shiftac.upartier.network.server.ServerWorker;

@FunctionalInterface
public interface PacketParser
{
    public void parse(ServerWorker worker, Packet pak)
        throws IOException, PacketFormatException;
}
package com.github.shiftac.upartier.network;

public interface Encryptor
{
    public Packet encrypt(Packet packet, Object key);
    public Packet decrypt(Packet packet, Object key) 
        throws PacketFormatException;
}
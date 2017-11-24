package com.github.shiftac.upartier.network;

import com.github.shiftac.upartier.network.demo.PacketType;

/**
 * PacketVersion interface defines the way we translate data from 
 * network into byte array that can be later {@code read()} by
 * {@code ByteArrayIO} objects. To fully implement the protocol,
 * user may later implement PacketType interface to define the way 
 * we explain the raw bytes(which class should we use to call read()?)
 * 
 * @see PacketType 
 */
public interface PacketVersion
{
    public static final int VER_RAW = 0x0;
    public static final int VER_AES128 = 0x1;
}
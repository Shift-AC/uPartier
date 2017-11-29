package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.Packet;

/**
 * When fetching block information, following situations are possible to occur:
 * <p>
 * 1. Fetching a block from its ID.<br>
 * 2. Fetching all blocks.<br>
 */
public class BlockFetchInf extends IDFetchInf
{
    public static final int ID = 0;
    public static final int ALL = 1;
    
    public BlockFetchInf(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    public BlockFetchInf(int type, int id)
    {
        this.type = type;
        this.id = id;
    }

    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_BLOCK_FETCH;
    }
}
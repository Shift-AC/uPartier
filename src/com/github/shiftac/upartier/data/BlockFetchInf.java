package com.github.shiftac.upartier.data;

/**
 * When fetching block information, following situations are possible to occur:
 * <p>
 * 1. Fetching a block from its ID.(not currently used)<br>
 * 2. Fetching all blocks.<br>
 */
public class BlockFetchInf extends IDFetchInf
{
    public static final int ID = 0;
    public static final int ALL = 1;
    
    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_BLOCK_FETCH;
    }
}
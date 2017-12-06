package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.Packet;

/**
 * When fetching messages, following situations are possible to occur:
 * <ol>
 * <li>Fetching messages from a specified post.</li>
 * </ol>
 * {@code count} will be valid and {@code token} will be timestamp of last item.
 */
public class MsgFetchInf extends CountFetchInf
{
    public static final int POST = 0;

    public MsgFetchInf() {}

    public MsgFetchInf(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    public MsgFetchInf(int type, 
        int user, long token, int id, int count)
    {
        this.type = type;
        this.user = user;
        this.token = token;
        this.count = count;
        this.id = id;
    }

    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_MESSAGE_FETCH;
    }
}
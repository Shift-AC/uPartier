package com.github.shiftac.upartier.data;

/**
 * When fetching messages, following situations are possible to occur:
 * <p>
 * 1. Fetching messages from a specified post.
 * <p>
 * {@code count} will be valid and {@code token} will be timestamp of last item.
 */
public class MsgFetchInf extends CountFetchInf
{
    public static final int POST = 0;

    @Override 
    public int getOperationType()
    {
        return PacketType.TYPE_MESSAGE_FETCH;
    }
}
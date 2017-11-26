package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.network.PacketVersion;

/**
 * Protocol implement for real uPartier program.
 */
public interface PacketType extends PacketVersion
{
    /**
     * Client: {@link LoginInf} is transferred.<br>
     * Server: {@link User} is transferred.
     */
    public static final int TYPE_LOGIN = 0x0;
    /**
     * Nothing is transferred.
     */
    public static final int TYPE_LOGOUT = 0x1;
    /**
     * Client: {@link UserFetchInf} is transferred.<br>
     * Server: (On success){@link User} is transferred.
     */
    public static final int TYPE_USER_FETCH = 0x2;
    /**
     * Client: {@link User} is transferred.
     */
    public static final int TYPE_USER_MODIFY = 0x3;
    /**
     * Client: {@link PostFetchInf} is transferred.<br>
     * Server: (On success){@link Post} is transferred.
     */
    public static final int TYPE_POST_FETCH = 0x4;
    /**
     * Client: {@link Post} is transferred.
     */
    public static final int TYPE_POST_MODIFY = 0x5;
    /**
     * Client: {@link BlockFetchInf} is transferred.<br>
     * Server: (On success){@link Block} is transferred.
     */
    public static final int TYPE_BLOCK_FETCH = 0x6;
    /**
     * Client: {@link Block} is transferred.
     * <p>
     * Note: we don't really want users to modify a block so no one is going to
     * handle a {@code TYPE_BLOCK_MODIFY} packet now.
     */
    public static final int TYPE_BLOCK_MODIFY = 0x7;
    /**
     * Client: {@link MsgFetchInf} id transferred.<br>
     * Server: (On success){@link MessageInf} is transferred.
     */
    public static final int TYPE_MESSAGE_FETCH = 0x8;    
    /**
     * Server: {@link MessageInf} is transferred.
     */
    public static final int TYPE_MESSAGE_PURGE = 0x9;

    /**
     * Server: {@link AckInf} is transferred.
     */
    public static final int TYPE_SERVER_ACK = 0x1F;

}
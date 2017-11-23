package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.network.PacketVersion;

/**
 * Protocol implement for real uPartier program.
 */
public interface PacketType extends PacketVersion
{
    /**
     * { @link LoginInf } is transferred.
     */
    public static final int TYPE_LOGIN = 0x0;
    /**
     * Nothing is transferred.
     */
    public static final int TYPE_LOGOUT = 0x1;
    /**
     * { @link UserFetchInf } is transferred.
     */
    public static final int TYPE_USER_FETCH = 0x2;
    /**
     * { @link User } is transferred.
     */
    public static final int TYPE_USER_MODIFY = 0x3;
    /**
     * { @link PostFetchInf } is transferred.
     */
    public static final int TYPE_POST_FETCH = 0x4;
    /**
     * { @link Post } is transferred.
     */
    public static final int TYPE_POST_MODIFY = 0x5;
    /**
     * { @link BlockFetchInf } is transferred.
     */
    public static final int TYPE_BLOCK_FETCH = 0x6;
    /**
     * { @link Block } is transferred.
     * 
     * Note: we don't really want users to modify a block so there's
     * no one in charge to handle a { @code TYPE_BLOCK_MODIFY } packet.
     */
    public static final int TYPE_BLOCK_MODIFY = 0x7;
}
package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.network.PacketVersion;

/**
 * Protocol implement for real uPartier program.
 * <p>
 * We use <b>only</b> packets to transfer information between client and
 * server. For each possible situations that the programs should talk to
 * each other, we define the actions & return values as below:
 * <p>
 * <pre>
 * 1. client login:
 *   client->server: TYPE_LOGIN, transfer LoginInf
 *   server->client:
 *     success: TYPE_LOGIN, transfer User
 *     permission denied: TYPE_SERVER_ACK, transfer ACKInf, return 
 *       RET_ERRPERMISSION
 *     IO error: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO
 * 2. client fetch user profile: 
 *   client->server: TYPE_USER_FETCH, transfer UserFetchInf, type =
 *     fetch for ID: ID
 *     fetch user issued a post: POST_ISSUE
 *     fetch user list belong to a post: POST_LIST
 *   server->client:
 *     success: TYPE_USER_FETCH, if type == ID || POST_ISSUE: transfer
 *       User, if type == POST_LIST: transfer ByteArrayIOList<User>
 *     IO error: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO
 *     no such user(type == ID only): TYPE_SERVER_ACK, transfer ACKInf,
 *       return RET_ERRUSER
 *     no such post(type == POST_* only): TYPE_SERVER_ACK, transfer ACKInf,
 *       return RET_ERRPOST
 * 3. client fetch block:
 *   client->server: TYPE_BLOCK_FETCH, transfer BlockFetchInf, type = 
 *     fetch for ID: ID
 *     fetch all blocks: ALL
 *   server->client:
 *     success: TYPE_BLOCK_FETCH, if type == ID, transfer Block, if type ==
 *     ALL, transfer ByteArrayIOList<Block>
 *     IO error: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO
 *     no such block(type == ID only): TYPE_SERVER_ACK, transfer ACKInf,
 *     return RET_ERRBLOCK
 * 4. client fetch post:
 *   client->server: TYPE_POST_FETCH, transfer PostFetchInf, type = 
 *     fetch for ID: ID
 *     fetch posts belong to a block: BLOCK
 *     fetch posts belong to a user: USER
 *   server->client: 
 *     success: TYPE_POST_FETCH, if type == ID, transfer Post, if type ==
 *     BLOCK || USER, transfer ByteArrayIOList<Post>
 *     IO error: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO
 *     no such post(type == ID only): TYPE_SERVER_ACK, transfer ACKInf, 
 *     return RET_ERRPOST
 *     no such user(type == USER only): TYPE_SERVER_ACK, transfer ACKInf,
 *     return RET_ERRUSER
 *     no such block(type == BLOCK only): TYPE_SERVER_ACK, transfer ACKInf,
 *     return RET_ERRBLOCK
 * 5. client logout
 *   client->server: TYPE_LOGOUT, transfer nothing.
 *   server->client:
 *     success: TYPE_SERVER_ACK, transfer ACKInf, return RET_SUCC
 *     IO error: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO
 *     not current ID: TYPE_SERVER_ACK, transfer ACKInf, return 
 *     RET_ERRPERMISSION
 * 6. client issue post
 *   client->server: TYPE_POST_MODIFY, transfer Post
 *   server->client: 
 *     success: TYPE_SERVER_ACK, transfer ACKInf, return RET_SUCC
 *     IO error: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO
 *     no such block: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRBLOCK
 *     not current user: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRUSER
 * 7. client issue message
 *   client->server: TYPE_MESSAGE_PUSH, transfer MessageInf
 *   server->client:
 *     success: TYPE_SERVER_ACK, transfer ACKInf, return timestamp of message
 *     IO error: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO
 *     no such post: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPOST
 *     no such user: TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRUSER
 *     permission denied: TYPE_SERVER_ACK, transfer ACKInf, return 
 *     RET_ERRPERMISSION
 * 8. server push message
 *   server->client: TYPE_MESSAGE_PUSH, transfer MessageInf
 * </pre>
 */
public interface PacketType extends PacketVersion
{
    /**
     * Client: {@link LoginInf} is transferred.<br>
     * Server: {@link User} is transferred.
     */
    public static final int TYPE_LOGIN = 0x0;
    /**
     * Client: Nothing is transferred.
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
     * Client: {@link MessageInf} is transferred.
     * Server: {@link MessageInf} is transferred.
     */
    public static final int TYPE_MESSAGE_PUSH = 0x9;

    /**
     * Server: {@link AckInf} is transferred.
     */
    public static final int TYPE_SERVER_ACK = 0x1F;

}
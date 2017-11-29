package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.network.PacketVersion;

/**
 * <p>Protocol implement for real uPartier program.</p>
 * <p>We use <strong>only</strong> packets to transfer information between client and
 * server. For each possible situations that the programs should talk to
 * each other, we define the actions and return values as below:</p>
 * <ul>
 * <li>Client login:
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_LOGIN, transfer LoginInf</li>
 *   <li>Server to Client:
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_LOGIN, transfer User</li>
 *     <li>Permission denied:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPERMISSION</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client fetch user profile:
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_USER_FETCH, transfer UserFetchInf, type =
 *     <ul>
 *     <li>Fetch for ID: ID</li>
 *     <li>Fetch user issued a post: POST_ISSUE</li>
 *     <li>Fetch user list belong to a post: POST_LIST</li>
 *     </ul>
 *   </li>
 *   <li>Server to Client:
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_USER_FETCH,<br>
 *       if type == ID or POST_ISSUE: transfer User,<br>
 *       if type == POST_LIST: transfer ByteArrayIOList&lt;User&gt;</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>No such user(type == ID only):<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRUSER</li>
 *     <li>No such post(type == POST_* only):<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPOST</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client fetch block:
 *   <ul>
 *   <li>Client to Server:<br> 
 *     TYPE_BLOCK_FETCH, transfer BlockFetchInf, type =
 *     <ul>
 *     <li>Fetch for ID: ID</li>
 *     <li>Fetch all blocks: ALL</li>
 *     </ul>
 *   </li>
 *   <li>Server to Client:
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_BLOCK_FETCH,<br>
 *       if type == ID, transfer Block,<br>
 *       if type == ALL, transfer ByteArrayIOList&lt;Block&gt;</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>No such block(type == ID only):<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRBLOCK</li>
 *   </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client fetch post:
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_POST_FETCH, transfer PostFetchInf, type =
 *     <ul>
 *     <li>Fetch for ID: ID</li>
 *     <li>Fetch posts belong to a block: BLOCK</li>
 *     <li>Fetch posts belong to a user: USER</li>
 *     </ul>
 *   </li>
 *   <li>Server to Client:
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_POST_FETCH,<br>
 *       if type == ID, transfer Post,<br>
 *       if type == BLOCK or USER, transfer ByteArrayIOList&lt;Post&gt;</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>No such post(type == ID only):<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPOST</li>
 *     <li>No such user(type == USER only):<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRUSER</li>
 *     <li>No such block(type == BLOCK only):<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRBLOCK</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client logout
 * <ul>
 * <li>Client to Server:<br>
 *   TYPE_LOGOUT, transfer id.</li>
 * <li>Server to Client:
 *   <ul>
 *   <li>Success:<br>
 *     TYPE_SERVER_ACK, transfer ACKInf, return RET_SUCC</li>
 *   <li>IO error:<br>
 *     TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *   <li>Not current user:<br>
 *     TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPERMISSION</li>
 *   </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client issue post
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_POST_MODIFY, transfer Post</li>
 *   <li>Server to Client:
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_POST_MODIFY, transfer Post</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>No such block:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRBLOCK</li>
 *     <li>Not current user:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPERMISSION</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client join post
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_POST_JOIN, transfer PostJoinInf</li>
 *   <li>Server to Client:<br>
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_SUCC</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>No such post:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPOST</li>
 *     <li>Not current user:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPERMISSION</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client fetch message
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_MESSAGE_FETCH, transfer MsgJoinInf</li>
 *   <li>Server to Client:<br>
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_MESSAGE_FETCH, transfer ByteArrayIOList&lt;MessageInf&gt;</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>No such post:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPOST</li>
 *     <li>Permission denyed:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPERMISSION</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client modify user profile
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_USER_MODIFY, transfer User</li>
 *   <li>Server to Client:
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_SUCC</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>Not current user:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPERMISSION</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Client issue message
 *   <ul>
 *   <li>Client to Server:<br>
 *     TYPE_MESSAGE_PUSH, transfer MessageInf</li>
 *   <li>Server to Client:
 *     <ul>
 *     <li>Success:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return timestamp of message</li>
 *     <li>IO error:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRIO</li>
 *     <li>No such post:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPOST</li>
 *     <li>No such user:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRUSER</li>
 *     <li>Permission denied:<br>
 *       TYPE_SERVER_ACK, transfer ACKInf, return RET_ERRPERMISSION</li>
 *     </ul>
 *   </li>
 *   </ul>
 * </li>
 * <li>Server push message
 *   <ul>
 *   <li>Server to Client:<br>
 *     TYPE_MESSAGE_PUSH, transfer MessageInf</li>
 *   </ul>
 * </li>
 * </ul>
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
     * Client: {@link MessageInf} is transferred.<br>
     * Server: {@link MessageInf} is transferred.
     */
    public static final int TYPE_MESSAGE_PUSH = 0x9;
    /**
     * Client: {@link PostJoinInf} is transferred.<br>
     * Server: {@link ACKInf} is transferred.
     */
    public static final int TYPE_POST_JOIN = 0xA;

    /**
     * Server: {@link ACKInf} is transferred.
     */
    public static final int TYPE_SERVER_ACK = 0x1F;

}
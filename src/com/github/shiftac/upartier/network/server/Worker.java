package com.github.shiftac.upartier.network.server;

import java.io.IOException;
import java.sql.SQLException;

import com.github.shiftac.upartier.data.ACKInf;
import com.github.shiftac.upartier.data.LoginInf;
import com.github.shiftac.upartier.data.NoSuchUserException;
import com.github.shiftac.upartier.data.PacketType;
import com.github.shiftac.upartier.data.User;
import com.github.shiftac.upartier.data.UserFetchInf;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.network.PacketParser;
import com.github.shiftac.upartier.serverdata.log;

public class Worker extends ServerWorker
{
    static public final PacketParser loginHandler = (wk, pak) ->
    {
        LoginInf inf = null;
        Packet res = null;
        try
        {
            inf = new LoginInf(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        try
        {
            User user = null;
            log.login(inf);
            
            // make javac happy...
            // fetch user here, then remove this
            user = new User();

            res = user.toPacket();
        }
        catch (SQLException sqle)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchUserException nsue)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRUSER);
            res = ack.toPacket();
        }
        wk.issue(res);
    };

    static public final PacketParser logoutHandler = (wk, pak) ->
    {

    };

    static public final PacketParser userFetchHandler = (wk, pak) ->
    {
        UserFetchInf inf = null;
        Packet res = null;
        try
        {
            inf = new UserFetchInf(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }

        wk.issue(res);
    };

    static public final PacketParser postFetchHandler = (wk, pak) ->
    {

    };

    static public final PacketParser blockFetchHandler = (wk, pak) ->
    {

    };

    static public final PacketParser msgFetchHandler = (wk, pak) ->
    {

    };

    static public final PacketParser userModifyHandler = (wk, pak) ->
    {

    };

    static public final PacketParser postModifyHandler = (wk, pak) ->
    {

    };

    static public final PacketParser msgPushHandler = (wk, pak) ->
    {

    };

    static public final PacketParser postJoinHandler = (wk, pak) ->
    {

    };

    @Override
    protected void parseOut(Packet pak)
        throws IOException, PacketFormatException
    {
        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
        case PacketType.TYPE_USER_FETCH:
        case PacketType.TYPE_POST_FETCH:
        case PacketType.TYPE_SERVER_ACK:
        case PacketType.TYPE_MESSAGE_FETCH:
        case PacketType.TYPE_MESSAGE_PUSH:
            pak.write(os);
            break;
        default:
            throw new PacketFormatException("Invalid packet type " + pak.type);
        }
    }

    @Override
    protected void parseIn(Packet pak)
        throws IOException, PacketFormatException
    {
        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
            loginHandler.parse(this, pak);
            break;
        case PacketType.TYPE_LOGOUT:
            logoutHandler.parse(this, pak);
            break;
        case PacketType.TYPE_USER_FETCH:
            userFetchHandler.parse(this, pak);
            break;
        case PacketType.TYPE_POST_FETCH:
            postFetchHandler.parse(this, pak);
            break;
        case PacketType.TYPE_BLOCK_FETCH:
            blockFetchHandler.parse(this, pak);
            break;
        case PacketType.TYPE_MESSAGE_FETCH:
            msgFetchHandler.parse(this, pak);
            break;
        case PacketType.TYPE_MESSAGE_PUSH:
            msgPushHandler.parse(this, pak);
            break;
        case PacketType.TYPE_USER_MODIFY:
            userModifyHandler.parse(this, pak);
            break;
        case PacketType.TYPE_POST_MODIFY:
            postModifyHandler.parse(this, pak);
            break;
        case PacketType.TYPE_POST_JOIN:
            postJoinHandler.parse(this, pak);
            break;
        default:
            throw new PacketFormatException("Invalid packet type " + pak.type);
        }
    }
}
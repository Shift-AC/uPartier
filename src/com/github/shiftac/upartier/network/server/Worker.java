package com.github.shiftac.upartier.network.server;

import java.io.IOException;
import java.sql.SQLException;

// We use this ugly import policy here because this class needs to use almost
// everything in com.github.shiftac.upartier.data.
import com.github.shiftac.upartier.data.*;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.ByteArrayIOList;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;
import com.github.shiftac.upartier.network.PacketParser;
import com.github.shiftac.upartier.serverdata.Fetch;
import com.github.shiftac.upartier.serverdata.Log;

public class Worker extends ServerWorker
{
    static public final PacketParser loginHandler = (wk, pak) ->
    {
        synchronized (wk.current)
        {
            if (wk.current != null)
            {
                wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            }
        }
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
            wk.issue(res);
            return;
        }
        try
        {
            User user = null;
            Log.login(inf);
            synchronized (wk.current)
            {
                wk.current = inf;
            }
            
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
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        try
        {
            synchronized (wk.current)
            {
                Log.logout(wk.current.id);
                wk.current = null;
            }
        }
        catch (SQLException e)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
        }
    };

    static public final PacketParser userFetchHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
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
            wk.issue(res);
            return;
        }

        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            case UserFetchInf.ID:
                // fetch user here and remove this
                tmp = new User();
                break;    
            case UserFetchInf.POST_LIST:
                tmp = new ByteArrayIOList<User>(
                    Fetch.fetchPostUserList(inf.id));
                break;
            case UserFetchInf.POST_ISSUE:
                // fetch user here and remove this
                tmp = new User();
                break;    
            default:
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_POST_FETCH;
        }
        catch (SQLException sqle)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        }

        wk.issue(res);
    };

    static public final PacketParser postFetchHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        PostFetchInf inf = null;
        Packet res = null;
        try
        {
            inf = new PostFetchInf(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
            wk.issue(res);
            return;
        }

        synchronized (wk.current)
        {
            cur = wk.current.id != inf.user;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            //case PostFetchInf.ID:
            case PostFetchInf.BLOCK:
                tmp = new ByteArrayIOList<Post>(
                    Fetch.fetchPostForBlock(inf.id, (int)inf.token, inf.count));
                break;    
            case PostFetchInf.USER:
                tmp = new ByteArrayIOList<Post>(
                    Fetch.fetchPostForUser(inf.user, (int)inf.token, 
                    inf.count));
                break;
            default:
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_POST_FETCH;
        }
        catch (SQLException sqle)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchBlockException nsbe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRBLOCK);
            res = ack.toPacket();
        }
        catch (NoSuchUserException nsue)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRUSER);
            res = ack.toPacket();
        }

        wk.issue(res);
    };

    static public final PacketParser blockFetchHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        BlockFetchInf inf = null;
        Packet res = null;
        try
        {
            inf = new BlockFetchInf(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
            wk.issue(res);
            return;
        }
        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            //case BlockFetchInf.ID:
            case BlockFetchInf.ALL:
                tmp = new ByteArrayIOList<Block>(Fetch.fetchBlocks());
                break;
            default:
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_POST_FETCH;
        }
        catch (SQLException sqle)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }

        wk.issue(res);
    };

    static public final PacketParser msgFetchHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        MsgFetchInf inf = null;
        Packet res = null;
        try
        {
            inf = new MsgFetchInf(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
            wk.issue(res);
            return;
        }
        synchronized (wk.current)
        {
            cur = wk.current.id != inf.user;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            case MsgFetchInf.POST:
                tmp = new ByteArrayIOList<MessageInf>(Fetch.fetchMessage(
                    inf.id, inf.user, inf.count, inf.token));
                break;
            default:
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_POST_FETCH;
        }
        catch (SQLException sqle)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        }
        catch (PermissionException pe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPERMISSION);
            res = ack.toPacket();
        }

        wk.issue(res);
    };

    static public final PacketParser userModifyHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        User user = null;
        Packet res = null;
        try
        {
            user = new User(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
            wk.issue(res);
            return;
        }
        synchronized (wk.current)
        {
            cur = wk.current.id != user.id;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        //try
        //{
            // modify user here

            ACKInf tmp = new ACKInf(ACKInf.RET_SUCC);
            res = tmp.toPacket();
        //}
        /*
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
        catch (PermissionException pe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPERMISSION);
            res = ack.toPacket();
        }*/

        wk.issue(res);
    };

    static public final PacketParser postModifyHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        Post post = null;
        Packet res = null;
        try
        {
            post = new Post(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
            wk.issue(res);
            return;
        }
        synchronized (wk.current)
        {
            cur = wk.current.id != post.userID;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        try
        {
            Fetch.issuePost(post);

            res = post.toPacket();
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
        catch (NoSuchBlockException nsbe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRBLOCK);
            res = ack.toPacket();
        }

        wk.issue(res);
    };

    static public final PacketParser msgPushHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        MessageInf inf = null;
        Packet res = null;
        try
        {
            inf = new MessageInf(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
            wk.issue(res);
            return;
        }
        synchronized (wk.current)
        {
            cur = wk.current.id != inf.userID;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        //try
        //{
            // push message here

            ACKInf ack = new ACKInf(inf.time);
            res = ack.toPacket();
        //}
        /*
        catch (SQLException sqle)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        }
        catch (PermissionException pe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPERMISSION);
            res = ack.toPacket();
        }*/

        wk.issue(res);
    };

    static public final PacketParser postJoinHandler = (wk, pak) ->
    {
        boolean cur = false;
        synchronized (wk.current)
        {
            cur = wk.current == null;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        PostJoinInf inf = null;
        Packet res = null;
        try
        {
            inf = new PostJoinInf(pak);
        }
        catch (IOException e)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
            wk.issue(res);
            return;
        }
        synchronized (wk.current)
        {
            cur = wk.current.id != inf.userID;
        }
        if (cur)
        {
            wk.issue(new ACKInf(ACKInf.RET_ERRIO).toPacket());
            return;
        }
        //try
        //{
            // join post here

            ACKInf ack = new ACKInf(ACKInf.RET_SUCC);
            res = ack.toPacket();
        //}
        /*
        catch (SQLException sqle)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        }
        catch (PermissionException pe)
        {
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPERMISSION);
            res = ack.toPacket();
        }*/

        wk.issue(res);
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
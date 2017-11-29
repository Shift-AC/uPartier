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
import com.github.shiftac.upartier.serverdata.Fetch;
import com.github.shiftac.upartier.serverdata.Log;

public class Worker extends ServerWorker
{
    static public final PacketParser loginHandler = (wk, pak, obj) ->
    {
        LoginInf inf = (LoginInf)obj;
        Packet res = null;
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

    static public final PacketParser logoutHandler = new PacketParser()
    {
        public boolean generateObject(ServerWorker wk, Packet pak, 
            ByteArrayIO obj)
        {
            return true;
        }

        public void parseObject(ServerWorker wk, Packet pak, 
            ByteArrayIO obj)
        {
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
        }
    };

    static public final PacketParser userFetchHandler = (wk, pak, obj) ->
    {
        UserFetchInf inf = (UserFetchInf)obj;
        Packet res = null;
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

    static public final PacketParser postFetchHandler = (wk, pak, obj) ->
    {
        PostFetchInf inf = (PostFetchInf)obj;
        Packet res = null;
        boolean cur = false;
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

    static public final PacketParser blockFetchHandler = (wk, pak, obj) ->
    {
        BlockFetchInf inf = (BlockFetchInf)obj;
        Packet res = null;
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

    static public final PacketParser msgFetchHandler = (wk, pak, obj) ->
    {
        MsgFetchInf inf = (MsgFetchInf)obj;
        Packet res = null;
        boolean cur;
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

    static public final PacketParser userModifyHandler = (wk, pak, obj) ->
    {
        User user = (User)obj;
        Packet res = null;
        boolean cur = false;
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

    static public final PacketParser postModifyHandler = (wk, pak, obj) ->
    {
        Post post = (Post)obj;
        Packet res = null;
        boolean cur = false;
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

    static public final PacketParser msgPushHandler = (wk, pak, obj) ->
    {
        MessageInf inf = (MessageInf)obj;
        Packet res = null;
        boolean cur = false;
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

    static public final PacketParser postJoinHandler = (wk, pak, obj) ->
    {
        PostJoinInf inf = (PostJoinInf)obj;
        Packet res = null;
        boolean cur = false;
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
            loginHandler.parse(this, pak, false, new LoginInf());
            break;
        case PacketType.TYPE_LOGOUT:
            logoutHandler.parse(this, pak, true, null);
            break;
        case PacketType.TYPE_USER_FETCH:
            userFetchHandler.parse(this, pak, true, new UserFetchInf());
            break;
        case PacketType.TYPE_POST_FETCH:
            postFetchHandler.parse(this, pak, true, new PostFetchInf());
            break;
        case PacketType.TYPE_BLOCK_FETCH:
            blockFetchHandler.parse(this, pak, true, new BlockFetchInf());
            break;
        case PacketType.TYPE_MESSAGE_FETCH:
            msgFetchHandler.parse(this, pak, true, new MsgFetchInf());
            break;
        case PacketType.TYPE_MESSAGE_PUSH:
            msgPushHandler.parse(this, pak, true, new MessageInf());
            break;
        case PacketType.TYPE_USER_MODIFY:
            userModifyHandler.parse(this, pak, true, new User());
            break;
        case PacketType.TYPE_POST_MODIFY:
            postModifyHandler.parse(this, pak, true, new Post());
            break;
        case PacketType.TYPE_POST_JOIN:
            postJoinHandler.parse(this, pak, true, new PostJoinInf());
            break;
        default:
            throw new PacketFormatException("Invalid packet type " + pak.type);
        }
    }
}
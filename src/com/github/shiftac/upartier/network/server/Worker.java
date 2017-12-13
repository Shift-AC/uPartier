package com.github.shiftac.upartier.network.server;

import java.io.IOException;
import java.sql.SQLException;

import com.github.shiftac.upartier.Util;
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
    static public final PacketParser loginHandler = (wk, obj, seq) ->
    {
        LoginInf inf = (LoginInf)obj;
        Packet res = null;

        Util.log.logVerbose("Got login inf: " + inf.getInf());
        try
        {
            User user = null;
            Log.login(inf);
            wk.manager.setIDPos(wk, inf.id);
            synchronized (wk.current)
            {
                wk.current = inf;
            }
            
            user = Fetch.fetchProfile(inf.id);

            Util.log.logVerbose("Success. Returning: " + user.getInf());
            res = user.toPacket();
            res.type = PacketType.TYPE_LOGIN;
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            Util.log.logVerbose("IOException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchUserException nsue)
        {
            nsue.printStackTrace();
            Util.log.logVerbose("NoSuchUserException, return ERRUSER");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRUSER);
            res = ack.toPacket();
        }
        wk.issueAck(res, seq);
    };

    static public final PacketParser logoutHandler = new PacketParser()
    {
        public boolean generateObject(ServerWorker wk, Packet pak, 
            ByteArrayIO obj)
        {
            return true;
        }

        public void parseObject(ServerWorker wk, ByteArrayIO obj, byte seq)
        {
            try
            {
                synchronized (wk.current)
                {
                    Log.logout(wk.current.id);
                    wk.current = dumbLoginInf;
                    wk.manager.removeIDPos(wk.current.id);
                }
            }
            catch (SQLException e)
            {
                Util.log.logVerbose("SQLException, return ERRIO");
                wk.issueAck(new ACKInf(ACKInf.RET_ERRIO).toPacket(), seq);
            }
            Util.log.logVerbose("Success. Return RET_SUCC.");
            wk.issueAck(new ACKInf(ACKInf.RET_SUCC).toPacket(), seq);
        }
    };

    static public final PacketParser userFetchHandler = (wk, obj, seq) ->
    {
        UserFetchInf inf = (UserFetchInf)obj;
        Packet res = null;

        Util.log.logVerbose(String.format(
            "Got UserFetchInf: %s", inf.getInf()));
        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            case UserFetchInf.ID:
                tmp = Fetch.fetchProfile(inf.id);
                break;    
            case UserFetchInf.POST_LIST:
                tmp = new ByteArrayIOList<User>(Util.clsUser,
                    Fetch.fetchPostUserList(inf.id));
                break;
            case UserFetchInf.POST_ISSUE:
                tmp = Fetch.fetchIssuerProfile(inf.id);
                break;    
            default:
                Util.log.logVerbose("Unrecognized type, ignore.");
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_USER_FETCH;

            Util.log.logVerbose("Success. Returning: " + tmp.getInf());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            Util.log.logVerbose("IOException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            nspe.printStackTrace();
            Util.log.logVerbose("NoSuchPostException, return ERRPOST");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        } catch (NoSuchUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        wk.issueAck(res, seq);
    };

    static public final PacketParser postFetchHandler = (wk, obj, seq) ->
    {
        PostFetchInf inf = (PostFetchInf)obj;
        Packet res = null;
        
        Util.log.logVerbose(String.format(
            "Got PostFetchInf: %s", inf.getInf()));
        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            //case PostFetchInf.ID:
            case PostFetchInf.BLOCK:
                tmp = new ByteArrayIOList<Post>(Util.clsPost,
                    Fetch.fetchPostForBlock(inf.id, (int)inf.token, inf.count));
                break;    
            case PostFetchInf.USER:
                tmp = new ByteArrayIOList<Post>(Util.clsPost,
                    Fetch.fetchPostForUser(inf.user, (int)inf.token, 
                    inf.count));
                break;
            default:
                Util.log.logVerbose("Unrecognized type, ignore.");
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_POST_FETCH;
            
            Util.log.logVerbose("Success. Returning: " + tmp.getInf());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            Util.log.logVerbose("IOException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchBlockException nsbe)
        {
            nsbe.printStackTrace();
            Util.log.logVerbose("NoSuchBlockException, return ERRBLOCK");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRBLOCK);
            res = ack.toPacket();
        }
        catch (NoSuchUserException nsue)
        {
            nsue.printStackTrace();
            Util.log.logVerbose("NoSuchUserException, return ERRUSER");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRUSER);
            res = ack.toPacket();
        }

        wk.issueAck(res, seq);
    };

    static public final PacketParser blockFetchHandler = (wk, obj, seq) ->
    {
        BlockFetchInf inf = (BlockFetchInf)obj;
        Packet res = null;

        Util.log.logVerbose(String.format(
            "Got BlockFetchInf: %s", inf.getInf()));
        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            //case BlockFetchInf.ID:
            case BlockFetchInf.ALL:
                tmp = new ByteArrayIOList<Block>(Util.clsBlock, 
                    Fetch.fetchBlocks());
                break;
            default:
                Util.log.logVerbose("Unrecognized type, ignore.");
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_BLOCK_FETCH;
            
            Util.log.logVerbose("Success. Returning: " + tmp.getInf());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            Util.log.logVerbose("IOException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        } catch (NoSuchUserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

        wk.issueAck(res, seq);
    };

    static public final PacketParser msgFetchHandler = (wk, obj, seq) ->
    {
        MsgFetchInf inf = (MsgFetchInf)obj;
        Packet res = null;

        Util.log.logVerbose(String.format(
            "Got MsgFetchInf: %s", inf.getInf()));
        if (!checkUser(wk, inf.user))
        {
            return;
        }
        try
        {
            ByteArrayIO tmp;
            switch (inf.type)
            {
            case MsgFetchInf.POST:
                tmp = new ByteArrayIOList<MessageInf>(Util.clsMessageInf,
                    Fetch.fetchMessage(inf.id, inf.user, inf.count, inf.token));
                break;
            default:
                Util.log.logVerbose("Unrecognized type, ignore.");
                return;
            }
            res = new AES128Packet(tmp);
            res.type = PacketType.TYPE_MESSAGE_FETCH;
            
            Util.log.logVerbose("Success. Returning: " + tmp.getInf());
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            nspe.printStackTrace();
            Util.log.logVerbose("NoSuchPostException, return ERRPOST");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        }
        catch (PermissionException pe)
        {
            pe.printStackTrace();
            Util.log.logVerbose("PermissionException, return ERRPERMISSION");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPERMISSION);
            res = ack.toPacket();
        }

        wk.issueAck(res, seq);
    };

    static public final PacketParser userModifyHandler = (wk, obj, seq) ->
    {
        User user = (User)obj;
        Packet res = null;

        Util.log.logVerbose(String.format(
            "Got User: %s", user.getInf()));
        if (!checkUser(wk, user.id))
        {
            return;
        }
        try
        {
            Fetch.renewProfile(user);

            ACKInf tmp = new ACKInf(ACKInf.RET_SUCC);
            res = tmp.toPacket();
            
            Util.log.logVerbose("Success. Returning: " + tmp.getInf());
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }

        wk.issueAck(res, seq);
    };

    static public final PacketParser postModifyHandler = (wk, obj, seq) ->
    {
        Post post = (Post)obj;
        Packet res = null;

        Util.log.logVerbose(String.format(
            "Got Post: %s", post.getInf()));
        if (!checkUser(wk, post.userID))
        {
            return;
        }
        try
        {
            Fetch.issuePost(post);

            res = post.toPacket();
            
            Util.log.logVerbose("Success. Returning: " + post.getInf());
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchUserException nsue)
        {
            nsue.printStackTrace();
            Util.log.logVerbose("NoSuchUserException, return ERRUSER");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRUSER);
            res = ack.toPacket();
        }
        catch (NoSuchBlockException nsbe)
        {
            nsbe.printStackTrace();
            Util.log.logVerbose("NoSuchBlockException, return ERRBLOCK");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRBLOCK);
            res = ack.toPacket();
        }

        wk.issueAck(res, seq);
    };

    static public final PacketParser msgPushHandler = (wk, obj, seq) ->
    {
        MessageInf inf = (MessageInf)obj;
        Packet res = null;

        Util.log.logVerbose(String.format(
            "Got MessageInf: %s", inf.getInf()));
        if (!checkUser(wk, inf.userID))
        {
            return;
        }
        try
        {
            ACKInf ack = new ACKInf(inf.time);
            res = ack.toPacket();
            Fetch.sendMessage(inf.userID, inf.postID, inf);
            // let database implement a function returns only id array.
            User[] users = Fetch.fetchPostUserList(inf.postID);
            int[] ids = new int[users.length];
            for (int i = 0; i < users.length; ++i)
            {
                ids[i] = users[i].id;
            }
            Packet pak = inf.toPacket();
            pak.type = PacketType.TYPE_MESSAGE_PUSH;
            pak.ack = seq;
            wk.manager.broadcast(pak, ids, inf.userID);
            
            Util.log.logVerbose("Success. Returning: " + ack.getInf());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            Util.log.logVerbose("IOException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            nspe.printStackTrace();
            Util.log.logVerbose("NoSuchPostException, return ERRPOST");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        }
        catch (NoSuchUserException nsue)
        {
            nsue.printStackTrace();
            Util.log.logVerbose("NoSuchUserException, return ERRUSER");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRUSER);
            res = ack.toPacket();
        }
        catch (PermissionException pe)
        {
            pe.printStackTrace();
            Util.log.logVerbose("PermissionException, return ERRPERMISSION");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPERMISSION);
            res = ack.toPacket();
        }

        wk.issueAck(res, seq);
    };

    static public final PacketParser postJoinHandler = (wk, obj, seq) ->
    {
        PostJoinInf inf = (PostJoinInf)obj;
        Packet res = null;

        Util.log.logVerbose(String.format(
            "Got PostJoinInf: %s", inf.getInf()));
        if (!checkUser(wk, inf.userID))
        {
            return;
        }
        try
        {
            Fetch.join(inf.userID, inf.postID);

            ACKInf ack = new ACKInf(ACKInf.RET_SUCC);
            res = ack.toPacket();
            
            Util.log.logVerbose("Success. Returning: " + ack.getInf());
        }
        catch (IOException ioe)
        {
            ioe.printStackTrace();
            Util.log.logVerbose("IOException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (SQLException sqle)
        {
            sqle.printStackTrace();
            Util.log.logVerbose("SQLException, return ERRIO");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRIO);
            res = ack.toPacket();
        }
        catch (NoSuchUserException nsue)
        {
            nsue.printStackTrace();
            Util.log.logVerbose("NoSuchUserException, return ERRUSER");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRUSER);
            res = ack.toPacket();
        }
        catch (NoSuchPostException nspe)
        {
            nspe.printStackTrace();
            Util.log.logVerbose("NoSuchPostException, return ERRPOST");
            ACKInf ack = new ACKInf(ACKInf.RET_ERRPOST);
            res = ack.toPacket();
        }

        wk.issueAck(res, seq);
    };

    static boolean checkUser(ServerWorker wk, int id)
    {
        int cid;
        synchronized (wk.current)
        {
            cid = wk.current.id;
        }
        if (cid != id)
        {
            Util.log.logMessage(String.format(
                "User id(%d) mot match(expected %d), refuse.",
                id, cid));
        }
        return cid == id;
    }

    @Override
    protected void parseOut(Packet pak)
        throws IOException, PacketFormatException
    {
        Util.log.logMessage(String.format(
            "Parsing send package #%d with type=%d, ack=%d", 
            pak.sequence, pak.type, pak.ack));
        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
        case PacketType.TYPE_LOGOUT:
        case PacketType.TYPE_BLOCK_FETCH:
        case PacketType.TYPE_USER_FETCH:
        case PacketType.TYPE_POST_FETCH:
        case PacketType.TYPE_SERVER_ACK:
        case PacketType.TYPE_MESSAGE_FETCH:
        case PacketType.TYPE_MESSAGE_PUSH:
        case PacketType.TYPE_POST_MODIFY:
            pak.write(os);
            Util.log.logMessage("Package #" + pak.sequence + " sent.");   
            break;
        default:
            throw new PacketFormatException("Invalid packet type " + pak.type);
        }
    }

    @Override
    protected void parseIn(Packet pak)
        throws IOException, PacketFormatException
    {
        Util.log.logMessage(String.format(
            "Parsing incoming package with type=%d, seq=%d",
            pak.type, pak.sequence));
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

    @Override
    protected void notifyClient() 
    {
        Packet pak = new AES128Packet();
        pak.type = PacketType.TYPE_LOGOUT;
        pak.setLen(8);
        issue(pak);
    }
}
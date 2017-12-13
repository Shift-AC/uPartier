package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.ByteArrayIOList;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.app.Client;

/**
 * Information about post block.
 * 
 * when transferring as bytes using ByteArrayIO:
 * <pre>
 * class Block
 * {
 *     int id;
 *     int postCount;
 *     BString name;
 * }
 * </pre>
 */
public class Block implements ByteArrayIO, PacketGenerator
{
    public int id = 0;
    public BString name = new BString();
    public int postCount = 0;
    public AtomicBoolean postsLock = new AtomicBoolean(false);
    public ArrayList<Post> posts = new ArrayList<Post>();

    public Block() {}

    public Block(Packet pak)
        throws IOException
    {
        this.read(pak);
    }

    @Override
    public String getInf()
    {
        return String.format("id=%d, name=%s, portCount=%d", 
            id, name.toString(), postCount);
    }

    /**
     * Try to fetch all existing post blocks, the {@code Block} objects 
     * returned in this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     */
    public static Block[] fetchBlocks()
        throws IOException, SocketTimeoutException
    {
        BlockFetchInf inf = new BlockFetchInf(BlockFetchInf.ALL, 0);
        Packet pak = inf.toPacket();
        Util.log.logVerbose("Fetching block: " + inf.getInf());
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_BLOCK_FETCH:
        {
            ByteArrayIOList<Block> res = new ByteArrayIOList<Block>(
                Util.clsBlock, pak);
            Util.log.logVerbose("Success. result:");
            for (int i = 0; i < res.arr.length; ++i)
            {
                Util.log.logVerbose("  ->" + res.arr[i].getInf());
            }
            return res.arr;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * Try to fetch last {@code count} posts with id less then {@code id} for 
     * current block, or fetch all remaining posts if there're not so many. The 
     * {@code Post} objects returned by this call will in <i>prefetched</i> state.
     * <p>
     * Current thread will <b>block</b> inside this call.
     * 
     * @throws IOException if network exceptions occured.
     * @throws SocketTimeoutException if can't hear from server for
     * {@code Client.NETWORK_TIMEOUT} milliseconds.
     * @throws NoSuchBlockException if no such block exists.
     */
    public void fetchPosts(int count)
        throws IOException, SocketTimeoutException, NoSuchBlockException
    {
        long token;
        synchronized (postsLock)
        {
            if (posts.size() == 0)
            {
                token = 2147483647;
            }
            else
            {
                token = posts.get(posts.size() - 1).id;
            }
        }
        PostFetchInf inf = new PostFetchInf(PostFetchInf.BLOCK, 0, token,
            id, count);
        Packet pak = inf.toPacket();
        Util.log.logVerbose("Fetching posts: " + inf.getInf());
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_POST_FETCH:
        {
            ByteArrayIOList<Post> res = new ByteArrayIOList<Post>(
                Util.clsPost, pak);
            res.read(pak);
            synchronized (postsLock)
            {
                if (posts.size() == 0)
                {
                    posts = new ArrayList<Post>();
                }
                posts.addAll(Arrays.asList(res.arr));
            }
            Util.log.logVerbose("Success. result:");
            for (int i = 0; i < res.arr.length; ++i)
            {
                Util.log.logVerbose("  ->" + res.arr[i].getInf());
            }
            return;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf(pak);
            res.read(pak);
            switch ((int)res.retval)
            {
            case ACKInf.RET_ERRIO:
                throw new IOException("Server IO exception.");
            case ACKInf.RET_ERRBLOCK:
                throw new NoSuchBlockException("Block #" + inf.user +
                    " not found.");
            default:
                throw new IOException("Server returning unknown ack value("
                    + res.retval + ")!");
            }
        }
        default:
            throw new IOException("Server returning unknown packet("
                + pak.type + ")!");
        }
    }

    /**
     * @param name the name to set
     */
    public void setName(String name)
    {
        this.name.setContent(name);
    }

    @Override
    public int getLength()
    {
        return SIZE_INT + SIZE_INT + name.getLength();
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT + SIZE_INT);
        setInt(buf, off, id);
        setInt(buf, off += SIZE_INT, postCount);
        name.write(buf, off += SIZE_INT, len -= SIZE_INT + SIZE_INT);
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT + SIZE_INT);
        id = getInt(buf, off);
        postCount = getInt(buf, off += SIZE_INT);
        name.read(buf, off += SIZE_INT, len -= SIZE_INT + SIZE_INT);
    }

    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this, PacketType.TYPE_BLOCK_MODIFY);
    }
}
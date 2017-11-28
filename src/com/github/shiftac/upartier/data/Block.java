package com.github.shiftac.upartier.data;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;

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
    public BString name = null;
    public int postCount = 0;
    public ArrayList<Post> posts = null;

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
        BlockFetchInf inf = new BlockFetchInf();
        inf.type = BlockFetchInf.ALL;
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_BLOCK_FETCH:
        {
            ByteArrayIOList<Block> res = new ByteArrayIOList<Block>();
            res.read(pak);
            return res.arr;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf();
            res.read(pak);
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
        PostFetchInf inf = new PostFetchInf();
        inf.type = PostFetchInf.BLOCK;
        inf.count = count;
        synchronized (posts)
        {
            if (posts == null)
            {
                inf.token = 2147483647;
            }
            else
            {
                inf.token = posts.get(posts.size() - 1).id;
            }
        }
        inf.user = id;
        Packet pak = inf.toPacket();
        pak = Client.client.issueWait(pak);
        switch (pak.type)
        {
        case PacketType.TYPE_POST_FETCH:
        {
            ByteArrayIOList<Post> res = new ByteArrayIOList<Post>();
            res.read(pak);
            synchronized (posts)
            {
                if (posts == null)
                {
                    posts = new ArrayList<Post>();
                }
                posts.addAll(Arrays.asList(res.arr));
            }
            return;
        }
        case PacketType.TYPE_SERVER_ACK:
        {
            ACKInf res = new ACKInf();
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
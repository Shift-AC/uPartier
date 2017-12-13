package com.github.shiftac.upartier.network.app;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.data.Block;
import com.github.shiftac.upartier.data.Image;
import com.github.shiftac.upartier.data.LoginInf;
import com.github.shiftac.upartier.data.MessageInf;
import com.github.shiftac.upartier.data.PacketType;
import com.github.shiftac.upartier.data.Post;
import com.github.shiftac.upartier.data.User;
import com.github.shiftac.upartier.network.Packet;
import com.github.shiftac.upartier.network.PacketFormatException;

public class Client extends AbstractClient
{
    public static final Client client;
    public LoginInf inf;

    protected AtomicBoolean bufLock = new AtomicBoolean(false);
    public Thread[] waitBuf;
    public Packet[] recvBuf;

    private Client()
    {
        super();
        waitBuf = new Thread[256];
        recvBuf = new Packet[256];
    }

    public void init(LoginInf inf)
        throws IOException
    {
        synchronized (this)
        {
            this.inf = inf;
        }
        restart();
    }

    /**
     * Issue a packet and then enter {@code wait()} mode, current thread
     * will be later {@code notify()}ed when reply comes.
     */
    public Packet issueWait(Packet pak)
        throws SocketTimeoutException
    {
        synchronized (started)
        {
            if (started.get() == false)
            {
                throw new IllegalStateException("Client not started!");
            }
        }
        int seq = pak.sequence;
        Thread current = Thread.currentThread();
        Util.log.logVerbose("Waiting ACK for packet #" + seq);
        synchronized (this.bufLock)
        {
            waitBuf[seq] = current;
        }

        super.issue(pak);

        synchronized (current)
        {
            try
            {
                current.wait();
            }
            catch (Exception e) {}
        }

        Packet res;
        synchronized (this.bufLock)
        {
            res = recvBuf[seq];
        }
        Util.log.logVerbose("Got ACK for packet #" + seq);
        return res;
    }
    
    @Override
    protected void parseOut(Packet pak)
        throws IOException, PacketFormatException
    {
        Util.log.logMessage(String.format(
            "Parsing send package #%d with type=%d", pak.sequence, pak.type));

        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
        case PacketType.TYPE_LOGOUT:
        case PacketType.TYPE_USER_FETCH:
        case PacketType.TYPE_POST_FETCH:
        case PacketType.TYPE_BLOCK_FETCH:
        case PacketType.TYPE_USER_MODIFY:
        case PacketType.TYPE_POST_MODIFY:
        case PacketType.TYPE_MESSAGE_FETCH:
        case PacketType.TYPE_MESSAGE_PUSH:
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
        Util.log.logMessage("Parsing incoming package with type=" + pak.type);
        switch (pak.type)
        {
        case PacketType.TYPE_LOGIN:
        case PacketType.TYPE_USER_FETCH:
        case PacketType.TYPE_BLOCK_FETCH:
        case PacketType.TYPE_POST_FETCH:
        case PacketType.TYPE_MESSAGE_FETCH:
        case PacketType.TYPE_POST_MODIFY:
        case PacketType.TYPE_SERVER_ACK:
            int ack = pak.ack;
            Thread waiting;
            Util.log.logVerbose(String.format("Got ACK for pending packet #%d.",
                pak.ack));
            synchronized (this.bufLock)
            {
                recvBuf[ack] = pak;
                waiting = waitBuf[ack];
                waitBuf[ack] = null;
            }
            synchronized (waiting)
            {
                waiting.notify();
            }
            break;
        case PacketType.TYPE_MESSAGE_PUSH:
            // someone should provide me a way to notify the app that 
            // theres's an incoming message.
            // callback function specification:
            // class ?
            // /**
            //  * Parse and try to display an incoming message that someone 
            //  * issued in a post.
            //  * 
            //  * This method should <b>never</b> throw an exception, instead,
            //  * it should try to distinguish the type of exception happening
            //  * and generate a report for the user to know about.
            //  */
            // static void parseIncomingMsg(MessageInf inf)
            try
            {
                MessageInf inf = new MessageInf(pak);
                Util.log.logMessage(String.format(
                    "User #%d issued a message in post #%d with type=%d",
                    inf.userID, inf.postID, inf.type));
                Post.parseIncomingMessage(inf);
            }
            catch (IOException ioe)
            {
                Util.log.logWarning("Server sending unrecognizable message.");
            }
            break;
        case PacketType.TYPE_LOGOUT:
            break;
        default:
            throw new PacketFormatException("Invalid packet type " + pak.type);
        }
    }

    static
    {
        client = new Client();
    }

    private static final String usage = 
        "General: [operator] [parameters...]\n" +
        "  b: fetch blocks\n" +
        "  f [postID]: fetch user list for post with id=postID\n" +
        "  g [postID] [count]: get [count] messages for post with id=postID\n" +
        "  h: show this message\n" +
        "  i [postName]: issue a post\n" +
        "  j [postID]: join post with id=postID\n" +
        "  l [userID] [passwd]: login\n" +
        "  m [count]: fetch [count] posts issued by me\n" +
        "  o: logout\n" +
        "  p [blockID] [count]: get count posts for block with id=blockID\n" +
        "  r [nickname]: modify my nickname\n" +
        "  s [postID] [content]: send a message in post with id=postID\n" +
        "  u [userID]: fetch user with id=userID\n";

    public static void main(String[] args)
    {
        User currentuser = null;
        TreeMap<Integer, Block> blockCache = new TreeMap<Integer, Block>();
        TreeMap<Integer, Post> postCache = new TreeMap<Integer, Post>();
        TreeMap<Integer, User> userCache = new TreeMap<Integer, User>();
        BufferedReader is = null;
        Util.log.logMessage("Terminal client initialized.");
        try
        {
            is = new BufferedReader(
               new InputStreamReader(System.in));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            System.exit(0);
        }
        while (true)
        {
            try
            {
                if (currentuser != null && !client.isAlive())
                {
                    System.out.println("Client closed unexpectedly!");
                    break;
                }
                String line = is.readLine();
                if (line == null || line.length() == 0)
                {
                    continue;
                }

                char operator = line.charAt(0);

                switch (operator)
                {
                case 'b':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    Block[] arr = Block.fetchBlocks();
                    for (int i = 0; i < arr.length; ++i)
                    {
                        blockCache.put(arr[i].id, arr[i]);
                    }
                    break;
                }
                case 'f':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    int num = Integer.parseInt(line.substring(2));
                    Post post = postCache.get(num);
                    if (post == null)
                    {
                        System.out.println("Fetch this post first.");
                        break;
                    }
                    post.fetchUserList();
                    break;
                }
                case 'g':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    String[] largs = line.substring(2).split(" ");
                    int num = Integer.parseInt(largs[0]);
                    Post post = postCache.get(num);
                    if (post == null)
                    {
                        System.out.println("Fetch this post first.");
                        break;
                    }
                    post.fetchMessage(currentuser, Integer.parseInt(largs[1]));

                    break;
                }
                case 'h':
                    System.out.println(usage);
                    break;
                case 'i':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    Post post = new Post();
                    post.blockID = 1;
                    post.name.setContent(line.substring(2));
                    currentuser.issue(post);
                    postCache.put(post.id, post);
                    break;
                }
                case 'j':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    int num = Integer.parseInt(line.substring(2));
                    Post post = postCache.get(num);
                    if (post == null)
                    {
                        System.out.println("Fetch this post first.");
                        break;
                    }
                    currentuser.join(post);
                    break;
                }
                case 'l':
                {
                    String[] largs = line.substring(2).split(" ");
                    LoginInf inf = new LoginInf(Integer.parseInt(largs[0]),
                        largs[1], false);
                    currentuser = User.login(inf);
                    break;
                }
                case 'm':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    int base = currentuser.myPosts.size();
                    int num = Integer.parseInt(line.substring(2));
                    currentuser.fetchMyPosts(num);
                    num = num < currentuser.myPosts.size() - base ? num :
                        currentuser.myPosts.size() - base;
                    for (int i = 0; i < num; ++i)
                    {
                        Post post = currentuser.myPosts.get(base + i);
                        postCache.put(post.id, post);
                    }
                    break;
                }
                case 'o':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    currentuser.logout();
                    currentuser = null;
                    break;
                }
                case 'p':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    String[] largs = line.substring(2).split(" ");
                    int num = Integer.parseInt(largs[0]);
                    Block block = blockCache.get(num);
                    if (block == null)
                    {
                        System.out.println("Fetch this block first.");
                        break;
                    }
                    int base = block.posts.size();
                    num = Integer.parseInt(largs[1]);
                    block.fetchPosts(num);
                    num = num < block.posts.size() - base ? num :
                        block.posts.size() - base;
                    for (int i = 0; i < num; ++i)
                    {
                        Post post = block.posts.get(base + i);
                        postCache.put(post.id, post);
                    }
                    break;
                }
                case 'r':
                {
                    if (currentuser == null)
                    {
                        System.out.println("Login needed for this operation.");
                        break;
                    }
                    currentuser.nickname.setContent(line.substring(2));
                    currentuser.profile = new Image("resource/icon.jpg");
                    currentuser.modify();
                    break;
                }
                case 's':
                {
                    int spi = line.substring(2).indexOf(' ') + 3;
                    int postID = Integer.parseInt(line.substring(2, spi - 1));
                    Post post = postCache.get(postID);
                    if (post == null)
                    {
                        System.out.println("Fetch this post first.");
                        break;
                    }
                    MessageInf inf = new MessageInf(line.substring(spi), 
                        currentuser.id, postID);
                    currentuser.sendMessage(post, inf);
                    break;
                }
                case 'u':
                {
                    User user = User.fetchProfile(Integer.parseInt(
                        line.substring(2)));
                    userCache.put(user.id, user);
                    break;
                }
                default:
                    System.out.println(
                        "Unknown operator" + operator +", type h for help.");
                    break;
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }

            System.out.println("Completed.");
        }
    }
}
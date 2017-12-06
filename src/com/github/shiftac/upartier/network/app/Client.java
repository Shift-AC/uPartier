package com.github.shiftac.upartier.network.app;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import com.github.shiftac.upartier.Util;
import com.github.shiftac.upartier.data.ACKInf;
import com.github.shiftac.upartier.data.Block;
import com.github.shiftac.upartier.data.LoginInf;
import com.github.shiftac.upartier.data.MessageInf;
import com.github.shiftac.upartier.data.PacketType;
import com.github.shiftac.upartier.data.Post;
import com.github.shiftac.upartier.data.User;
import com.github.shiftac.upartier.network.AES128Packet;
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
        case PacketType.TYPE_POST_FETCH:
        case PacketType.TYPE_MESSAGE_FETCH:
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
                    inf.postID, inf.userID, inf.type));
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
        "  h: show this message\n" +
        "  l [userID] [passwd]: login\n" +
        "  o: logout\n";

    public static void main(String[] args)
    {
        User currentuser;
        TreeMap<Integer, Block> blockCache = new TreeMap<Integer, Block>();
        TreeMap<Integer, Post> postCache = new TreeMap<Integer, Post>();
        TreeMap<Integer, User> userCache = new TreeMap<Integer, User>();
        try
        {
            client.start();
            while (true)
            {
                BufferedReader is = new BufferedReader(
                    new InputStreamReader(System.in));
                String line = is.readLine();
                if (!client.isAlive())
                {
                    throw new Exception(
                        "Client closed unexpectedly!");
                }
                if (line == null || line.length() == 0)
                {
                    continue;
                }

                Packet pak = null;
                char operator = line.charAt(0);

                switch (operator)
                {
                case 'l':
                    String[] largs = line.substring(2).split(" ");
                    LoginInf inf = new LoginInf(Integer.parseInt(largs[0]),
                        largs[1], false);
                    User user = User.login(inf);
                    System.out.println(
                        "Login succeed, inf " + user.getInf());
                    break;
                case 'h':
                    System.out.println(usage);
                    break;
                case 'o':
                    pak = new AES128Packet();
                    pak.setLen(8);
                    pak.type = PacketType.TYPE_LOGOUT;
                    break;
                case 'u':
                    
                    break;
                default:
                    break;
                }

                if (pak != null)
                {
                    pak = client.issueWait(pak);
                    switch (pak.type)
                    {
                    case PacketType.TYPE_LOGIN:
                        User inf = new User(pak);
                        System.out.println(
                            "Login succeed, inf " + inf.getInf());
                        break;
                    case PacketType.TYPE_SERVER_ACK:
                        ACKInf ack = new ACKInf(pak);
                        System.out.println("Server ack, inf " + ack.getInf());
                    }
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
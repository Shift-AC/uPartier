package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.LogManager;
import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.ByteArrayIO;
import com.github.shiftac.upartier.network.Packet;

/**
 * Information about the message transferred.
 * <p>
 * When transferring as byte array:
 * <pre>
 * class MessageInf
 * {
 *     int userID;
 *     int postID;
 *     long type :8;
 *     long time :56;
 * }
 * </pre>
 */
public class MessageInf implements ByteArrayIO, PacketGenerator
{
    public static final int TYPE_TEXT = 0;
    public static final int TYPE_IMAGE = 1;
    public static final int TYPE_AUDIO = 2;
    public static final int TYPE_FILE = 3;

    public int userID = 0;
    public int postID = 0;
    public byte type = 0;
    public long time = 0;
    public ByteArrayIO content = null;

    public MessageInf() {}

    public MessageInf(Packet pak)
        throws IOException
    {
        this.read(pak);
    }
    
    @Override
    public int getLength()
    {
        return SIZE_INT * 2 + SIZE_LONG + content.getLength();
    }

    @Override
    public String getInf()
    {
        return String.format(
            "userID=%d, postID=%d, type=%d, time=%d, content.inf=%s",
            userID, postID, type, time, content.getInf());
    }

    /**
     * Construct a text message, the {@code time} field will be set to current
     * time.
     */
    public MessageInf(String content, int userID, int postID)
    {
        this.content = new BString(new String(content));
        this.userID = userID;
        this.postID = postID;
        this.type = TYPE_TEXT;
        this.time = LogManager.calendar.getTimeInMillis();
    }

    /**
     * Construct a image message, the {@code time} field will be set to current
     * time.
     */
    public MessageInf(Image content, int userID, int postID)
    {
        this.content = content;
        this.userID = userID;
        this.postID = postID;
        this.type = TYPE_TEXT;
        this.time = LogManager.calendar.getTimeInMillis();
    }

    /**
     * Construct a audio message, the {@code time} field will be set to current
     * time.
     */
    public MessageInf(Audio content, int userID, int postID)
    {
        this.content = content;
        this.userID = userID;
        this.postID = postID;
        this.type = TYPE_TEXT;
        this.time = LogManager.calendar.getTimeInMillis();
    }

    /**
     * Construct a file message, the {@code time} field will be set to current
     * time.
     */
    public MessageInf(GenericFile content, int userID, int postID)
    {
        this.content = content;
        this.userID = userID;
        this.postID = postID;
        this.type = TYPE_TEXT;
        this.time = LogManager.calendar.getTimeInMillis();
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT * 2 + SIZE_LONG);
        userID = getInt(buf, off);
        postID = getInt(buf, off += 4);
        type = buf[off += 4];
        time = getNumber(buf, ++off, SIZE_LONG - SIZE_BYTE);
        switch (type)
        {
        case TYPE_TEXT:
            content = new BString();
            break;
        case TYPE_IMAGE:
            content = new Image();
            break;
        case TYPE_AUDIO:
            content = new Audio();
            break;
        case TYPE_FILE:
            content = new GenericFile();
            break;
        default:
            throw new IOException("Unrecognized message type " + type);
        }
        content.read(buf, off += SIZE_LONG - SIZE_BYTE, 
            len -= SIZE_INT * 2 + SIZE_LONG);
        //System.err.printf("ct: %s\n", content.toString());
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, SIZE_INT * 2 + SIZE_LONG);
        setInt(buf, off, userID);
        setInt(buf, off += 4, postID);
        buf[off += 4] = type;
        setNumber(buf, ++off, SIZE_LONG - SIZE_BYTE, time);
        content.write(buf, off += SIZE_LONG - SIZE_BYTE, 
            len -= SIZE_INT * 2 + SIZE_LONG);
    }

    /**
     * Notice: user should set type field of packet manually.
     */
    @Override
    public AES128Packet toPacket()
    {
        return new AES128Packet(this);
    }
}
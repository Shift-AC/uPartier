package com.github.shiftac.upartier.data;

import java.io.IOException;

import com.github.shiftac.upartier.network.ByteArrayIO;

/**
 * We notice that if the server wants to send an ACK, there's either a pending
 * modify operation, or a failed pending fetch operation. Anyway we don't need
 * to tranfer any sort of data other than a single return value. This meets our
 * expectation: transferring a message as long as 8 bytes is of lowest cost.
 * <p>
 * When transferring as byte array:
 * <pre>
 * struct ACKInf
 * {
 *     int retval;
 *     byte[4] reserved;
 * }
 * </pre> 
 */
public class ACKInf implements ByteArrayIO
{
    public static int RET_SUCC = 0;
    public static int RET_ERRDATABASE = 1;
    public static int RET_ERRUSER = 2;
    public static int RET_ERRPOST = 3;
    public static int RET_ERRBLOCK = 4;
    public static int RET_ERRPERMISSION = 5;

    int retval;

    @Override
    public int getLength()
    {
        return 8;
    }

    @Override
    public void read(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        retval = getInt(buf, off);
    }

    @Override
    public void write(byte[] buf, int off, int len)
        throws IOException
    {
        checkLen(len, getLength());
        setInt(buf, off, retval);
    }
}
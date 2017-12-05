package com.github.shiftac.upartier.network;

import java.io.IOException;
import com.github.shiftac.upartier.data.User;

/**
 * We recommend that every class that implements {@code ByteArrayIO}
 * give a comment about how its data is serialized when writing to a byte
 * array. We use a C struct-like grammar to do that, while we assume there's
 * no aligning constraints and thus we won't get any padding bytes. An example
 * is: 
 * 
 * <pre>
 * class User
 * {
 *     int id;
 *     int age;
 *     byte gender;
 *     short mlen;
 *     byte[mlen] mailAccount;
 *     byte nlen;
 *     byte[nlen] nickname;
 *     Image profile;
 *     int postCount;
 * }
 * </pre>
 * 
 * Fields are listed using their types and names. For fields that correspond
 * to a member in the object itself, their names should <b>exactly</b> match
 * the names of the members. For fields that are not, their names should 
 * <b>never</b> be the same of a mamber. We give a possible list of types here:
 * <ul>
 * <li>{@code byte}: 8-bit integer;</li>
 * <li>{@code short}: 16-bit integer;</li>
 * <li>{@code int}: 32-bit integer;</li>
 * <li>{@code long}: 64-bit integer;</li>
 * <li>{@code type[len]}: Array with length {@code == len}. {@code len}
 * can either be a integer value defined previously or a constant.</li>
 * <li>{@code type[]}: Array with unknown length. Length of it can be 
 * decided when calling {@code read()} with {@code len} specified: its 
 * length is {@code len -} buffer length used by previous fields. This field
 * can only be the last field of a object.</li>
 * <li>{@code Classname}: An object that implements ByteArrayIO.</li>
 * <li>Bitfield: we use Big-Endian represent here.</li>
 * </ul>
 * 
 * @see User
 * @see PlainMessage
 */
public interface ByteArrayIO
{
    public static final int SIZE_BYTE = 1;
    public static final int SIZE_SHORT = 2;
    public static final int SIZE_INT = 4;
    public static final int SIZE_LONG = 8;

    public void read(byte[] buf, int off, int len) throws IOException;
    public void write(byte[] buf, int off, int len) throws IOException;

    /**
     * Note: this method should only be called after the object is properly
     * initialized, but once initialized, this method should return the
     * number of bytes it will consume when {@code write()} is called(or 
     * number of bytes it comsumed when {@code read()} was called 
     * previously).
     */
    public int getLength();
    public String getInf();
    public default byte[] toByteArray()
    {
        byte[] res = new byte[getLength()];
        try
        {
            write(res, 0, res.length);
        }
        catch (Exception e) {}
        return res;
    }

    public default void read(byte[] buf)
        throws IOException
    {
        read(buf, 0, buf.length);
    }

    public default void read(Packet pak)
        throws IOException
    {
        read(pak.data);
    }

    public default void write(byte[] buf)
        throws IOException
    {
        write(buf, 0, buf.length);
    }

    public default void checkLen(int len, int need)
        throws IOException
    {
        if (need < len)
        {
            throw new IOException(String.format(
                "No enough buffer space(%d), expected %d.", len, need));
        }
    }

    public default long getNumber(byte[] buf, int off, int len)
    {
        long res = 0;
        for (int i = 0; i < len; ++i)
        {
            res <<= 8;
            res |= (long)buf[off + i] & 0xFF;
        }
        return res;
    }

    public default long getLong(byte[] buf, int off)
    {
        return getNumber(buf, off, 8);
    }

    public default int getInt(byte[] buf, int off)
    {
        return (int)getNumber(buf, off, 4);
    }

    public default short getShort(byte[] buf, int off)
    {
        return (short)getNumber(buf, off, 2);
    }

    public default void setNumber(byte[] buf, int off, int len, long val)
    {
        for (int i = len - 1; i > -1; --i)
        {
            buf[off + i] = (byte)val;
            val >>= 8;
        }
    }

    public default void setLong(byte[] buf, int off, long val)
    {
        setNumber(buf, off, 8, val);
    }

    public default void setInt(byte[] buf, int off, long val)
    {
        setNumber(buf, off, 4, val);
    }

    public default void setShort(byte[] buf, int off, long val)
    {
        setNumber(buf, off, 2, val);
    }

    public default void memcpy(byte[] dst, int doff,
        byte[] src, int soff, int len)
    {
        while (len-- != 0)
        {
            dst[doff++] = src[soff++];
        }
    }
}
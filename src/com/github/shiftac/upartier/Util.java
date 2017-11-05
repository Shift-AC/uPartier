package com.github.shiftac.upartier;

import java.io.FileInputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONTokener;

public class Util
{
    static
    {
        try
        {
            config = new JSONObject(new JSONTokener(new FileInputStream("config/config.json")));
        }
        catch (Exception e)
        {
            errorExit(e);
        }
    }

    public static final long version = 0x0000000000000001;

    private static JSONObject config;
    public static LogManager log = new LogManager(System.err);

    private static Object getConfig(String name)
        throws JSONPointerException
    {
        return (new JSONPointer(name)).queryFrom(config);
    }

    public static int getIntConfig(String name)
        throws JSONPointerException
    {
        return (int)(((Long)getConfig(name)).longValue());
    }

    public static long getLongConfig(String name)
        throws JSONPointerException
    {
        return ((Long)getConfig(name)).longValue();
    }

    public static double getDoubleConfig(String name)
        throws JSONPointerException
    {
        return ((Double)getConfig(name)).doubleValue();
    }

    public static boolean getBoolConfig(String name)
        throws JSONPointerException
    {
        return ((Boolean)getConfig(name)).booleanValue();
    }

    public static String getStringConfig(String name)
        throws JSONPointerException
    {
        return (String)getConfig(name);
    }

    public static JSONArray getArrayConfig(String name)
        throws JSONPointerException
    {
        return (JSONArray)getConfig(name);
    }

    public static JSONObject getObjectConfig(String name)
        throws JSONPointerException
    {
        return (JSONObject)getConfig(name);
    }

    private static Object getConfig(Class c, String name)
        throws JSONPointerException
    {
        String cname = c.getName();
        int pos = cname.indexOf("upartier.");
        if (pos == -1)
        {
            return null;
        }
        cname = cname.substring(pos + "upartier.".length());
        cname.replace('.', '/');
        return getConfig("/" + cname + "/" + name);
    }

    public static int getIntConfig(Class c, String name)
        throws JSONPointerException
    {
        return (int)(((Long)getConfig(c, name)).longValue());
    }

    public static long getLongConfig(Class c, String name)
        throws JSONPointerException
    {
        return ((Long)getConfig(c, name)).longValue();
    }

    public static double getDoubleConfig(Class c, String name)
        throws JSONPointerException
    {
        return ((Double)getConfig(c, name)).doubleValue();
    }

    public static boolean getBoolConfig(Class c, String name)
        throws JSONPointerException
    {
        return ((Boolean)getConfig(c, name)).booleanValue();
    }

    public static String getStringConfig(Class c, String name)
        throws JSONPointerException
    {
        return (String)getConfig(c, name);
    }

    public static JSONArray getArrayConfig(Class c, String name)
        throws JSONPointerException
    {
        return (JSONArray)getConfig(c, name);
    }

    public static JSONObject getObjectConfig(Class c, String name)
        throws JSONPointerException
    {
        return (JSONObject)getConfig(c, name);
    }

    public static void errorExit(Exception e)
    {
        e.printStackTrace();
        System.exit(1);
    }

    public static void sleepIgnoreInterrupt(long mili)
    {
        try
        {
            Thread.sleep(mili);
        }
        catch (Exception e) {}
    }

    public long getNumber(byte[] buf, int off, int len)
    {
        long res = 0;
        for (int i = 0; i < len; ++i)
        {
            res <<= 8;
            res |= (long)buf[off + i] & 0xFF;
        }
        return res;
    }

    public long getLong(byte[] buf, int off)
    {
        return getNumber(buf, off, 8);
    }

    public int getInt(byte[] buf, int off)
    {
        return (int)getNumber(buf, off, 4);
    }

    public short getShort(byte[] buf, int off)
    {
        return (short)getNumber(buf, off, 2);
    }

    public void setNumber(byte[] buf, int off, int len, long val)
    {
        for (int i = len - 1; i > -1; --i)
        {
            buf[off + i] = (byte)val;
            val >>= 8;
        }
    }

    public void setLong(byte[] buf, int off, long val)
    {
        setNumber(buf, off, 8, val);
    }

    public void setInt(byte[] buf, int off, long val)
    {
        setNumber(buf, off, 4, val);
    }

    public void setShort(byte[] buf, int off, long val)
    {
        setNumber(buf, off, 2, val);
    }
}
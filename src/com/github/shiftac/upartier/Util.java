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

    private static JSONObject config;

    private static Object getConfig(String name)
        throws JSONPointerException
    {
        return (new JSONPointer(name)).queryFrom(config);
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

    public static void errorExit(Exception e)
    {
        e.printStackTrace();
        System.exit(1);
    }
}
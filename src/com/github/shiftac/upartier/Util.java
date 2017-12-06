package com.github.shiftac.upartier;

//import java.io.FileInputStream;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONTokener;

public class Util
{
    public static final long version = 0x0000000000000001;

    private static String configString = 
        "{\n" +
        "    \"network\":\n" +
        "    {\n" +
        "        \"server\":\n" +
        "        {\n" +
        "            \"Server\":\n" +
        "            {\n" +
        "                \"host\": \"162.105.175.115\",\n" +
        "                \"port\": 2333,\n" +
        "                \"maxWorker\": 32\n" +
        "            },\n" +
        "            \"WorkerManager\":\n" +
        "            {\n" +
        "                \"probeGapInms\": 100\n" +
        "            }\n" +
        "        },\n" +
        "        \"demo\":\n" +
        "        {\n" +
        "            \"EchoServer\":\n" +
        "            {\n" +
        "                \"maxWorker\": 32\n" +
        "            }\n" +
        "        },\n" +
        "        \"Packet\":\n" +
        "        {\n" +
        "            \"attemptPerKB\": 1,\n" +
        "            \"baseAttempt\": 10,\n" +
        "            \"sleepInms\": 100\n" +
        "        }\n" +
        "    }\n" +
        "}";
    private static JSONObject config;
    public static LogManager log = new LogManager(System.err, 10, true);

    static
    {
        try
        {
            //config = new JSONObject(new JSONTokener(new FileInputStream(
            //    "config/config.json")));
            config = new JSONObject(new JSONTokener(configString));
        }
        catch (Exception e)
        {
            errorExit("Can't initialize config manager.", e);
        }
    }
    
    private static Object getConfig(String name)
        throws JSONPointerException
    {
        Util.log.logVerbose(String.format("Getting config %s...", name), 3);
        return (new JSONPointer(name)).queryFrom(config);
    }

    public static int getIntConfig(String name)
        throws JSONPointerException
    {
        return ((Integer)getConfig(name)).intValue();
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

    private static Object getConfig(Class<? extends Object> c, String name)
        throws JSONPointerException
    {
        String cname = c.getName();
        int pos = cname.indexOf("upartier.");
        if (pos == -1)
        {
            return null;
        }
        cname = cname.substring(pos + "upartier.".length());
        cname = cname.replace('.', '/');
        return getConfig("/" + cname + "/" + name);
    }

    public static int getIntConfig(Class<? extends Object> c, String name)
        throws JSONPointerException
    {
        return ((Integer)getConfig(c, name)).intValue();
    }

    public static double getDoubleConfig(Class<? extends Object> c, String name)
        throws JSONPointerException
    {
        return ((Double)getConfig(c, name)).doubleValue();
    }

    public static boolean getBoolConfig(Class<? extends Object> c, String name)
        throws JSONPointerException
    {
        return ((Boolean)getConfig(c, name)).booleanValue();
    }

    public static String getStringConfig(Class<? extends Object> c, String name)
        throws JSONPointerException
    {
        return (String)getConfig(c, name);
    }

    public static JSONArray getArrayConfig(Class<? extends Object> c, String name)
        throws JSONPointerException
    {
        return (JSONArray)getConfig(c, name);
    }

    public static JSONObject getObjectConfig(Class<? extends Object> c, String name)
        throws JSONPointerException
    {
        return (JSONObject)getConfig(c, name);
    }

    public static void errorExit(String cause, Exception e)
    {
        e.printStackTrace(Util.log.dest);
        log.logFatal(cause, 1);
    }

    public static void sleepIgnoreInterrupt(long mili)
    {
        try
        {
            Thread.sleep(mili);
        }
        catch (Exception e) {}
    }

    public static void joinIgnoreInterrupt(Thread t)
    {
        while (true)
        {
            try
            {
                t.join();
            }
            catch (Exception e)
            {
                continue;
            }
            break;
        }
    }
}
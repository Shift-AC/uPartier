package com.github.shiftac.upartier;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LogManager
{
    PrintStream dest;
    private long start;
    private long startMili;

    private static final String[] typePrefix = 
    {
        "[ Verbose ]",
        "[ Message ]",
        "[ Warning ]",
        "[  Error  ]",
        "[  FATAL  ]"
    };
    private static final int LOG_VERBOSE = 0;
    private static final int LOG_MESSAGE = 1;
    private static final int LOG_WARNING = 2;
    private static final int LOG_ERROR = 3;
    private static final int LOG_FATAL = 4;
    public static Calendar calendar = new GregorianCalendar(); 

    public int verbose = 0;

    public boolean timestamp = false;

    public LogManager(OutputStream os)
    {
        long mili = calendar.getTimeInMillis();
        dest = new PrintStream(os);
        start = mili / 1000;
        startMili = mili % 1000;
    }

    public void logVerbose(String msg, int level)
    {
        if (verbose >= level)
        {
            log(msg, LOG_VERBOSE);
        }
    }

    public void logMessage(String msg, int level)
    {
        log(msg, LOG_MESSAGE);
    }
    
    public void logWarning(String msg, int level)
    {
        log(msg, LOG_WARNING);
    }
    
    public void logError(String msg, int level)
    {
        log(msg, LOG_ERROR);
    }
    
    public void logFatal(String msg, int level)
    {
        log(msg, LOG_FATAL);
        System.exit(1);
    }

    protected String generateLine(int type, String msg)
    {
        StringBuilder prefix = new StringBuilder();
        prefix.append(typePrefix[type]);
        if (timestamp)
        {
            long mili = calendar.getTimeInMillis();
            long sec = mili / 1000;
            mili = mili % 1000;
            prefix.append(String.format("(%7d.%03d)", sec, mili));
        }
        prefix.append(": ");
        prefix.append(msg);
        return prefix.toString();
    }

    public void log(String msg, int type)
    {
        dest.println(generateLine(type, msg));
        dest.flush();
    }
}
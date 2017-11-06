package com.github.shiftac.upartier;

import java.io.OutputStream;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class LogManager
{
    public PrintStream dest;
    private long start;

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

    public final boolean timestamp;

    public LogManager(OutputStream os, int verbose, boolean time)
    {
        timestamp = time;
        this.verbose = verbose;
        dest = new PrintStream(os);
        if (time)
        {
            initTimestamp();
        }
    }

    private void initTimestamp()
    {
        start = calendar.getTimeInMillis();
        long ssec = start / 1000;
        long smili = start % 1000;
        Date date = new Date(start);
        SimpleDateFormat format = new SimpleDateFormat(
            "yyyy.MM.dd-HH:mm:ss z");
        logMessage(String.format("LogManager initialized at %d.%d(%s).", 
            ssec, smili, format.format(date)));
    }

    public void logVerbose(String msg, int level)
    {
        if (verbose >= level)
        {
            log(msg, LOG_VERBOSE);
        }
    }

    public void logMessage(String msg)
    {
        log(msg, LOG_MESSAGE);
    }
    
    public void logWarning(String msg)
    {
        log(msg, LOG_WARNING);
    }
    
    public void logError(String msg)
    {
        log(msg, LOG_ERROR);
    }
    
    public void logFatal(String msg, int retVal)
    {
        log(msg, LOG_FATAL);
        System.exit(retVal);
    }

    protected String generateLine(int type, String msg)
    {
        StringBuilder prefix = new StringBuilder();
        prefix.append(typePrefix[type]);
        if (timestamp)
        {
            calendar.setTime(new Date());
            long mili = calendar.getTimeInMillis() - start;
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
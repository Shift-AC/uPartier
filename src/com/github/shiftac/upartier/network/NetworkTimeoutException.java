package com.github.shiftac.upartier.network;

import com.github.shiftac.upartier.Util;

public class NetworkTimeoutException extends Exception
{
    public static final long serialVersionUID = Util.version;

    public NetworkTimeoutException()
    {
        super();
    }

    public NetworkTimeoutException(String str)
    {
        super(str);
    }
}
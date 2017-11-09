package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.Util;

public class NoSuchBlockException extends Exception
{
    public static final long serialVersionUID = Util.version;

    public NoSuchBlockException()
    {
        super();
    }

    public NoSuchBlockException(String str)
    {
        super(str);
    }
}
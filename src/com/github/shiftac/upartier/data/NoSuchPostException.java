package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.Util;

public class NoSuchPostException extends Exception
{
    public static final long serialVersionUID = Util.version;

    public NoSuchPostException()
    {
        super();
    }

    public NoSuchPostException(String str)
    {
        super(str);
    }
}
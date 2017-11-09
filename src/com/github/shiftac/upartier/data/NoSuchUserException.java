package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.Util;

public class NoSuchUserException extends Exception
{
    public static final long serialVersionUID = Util.version;

    public NoSuchUserException()
    {
        super();
    }

    public NoSuchUserException(String str)
    {
        super(str);
    }
}
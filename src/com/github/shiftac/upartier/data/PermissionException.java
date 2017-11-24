package com.github.shiftac.upartier.data;

import com.github.shiftac.upartier.Util;

public class PermissionException extends Exception
{
    public static final long serialVersionUID = Util.version;

    public PermissionException()
    {
        super();
    }

    public PermissionException(String str)
    {
        super(str);
    }
}
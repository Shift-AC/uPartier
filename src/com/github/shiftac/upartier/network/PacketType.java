package com.github.shiftac.upartier.network;

public interface PacketType
{
    public static final int VER_RAW = 0x0;
    public static final int VER_AES128 = 0x1;

    public static final int TYPE_CTRL = 0x0;
    public static final int TYPE_PULL = 0x08;
    public static final int TYPE_PUSH = 0x10;
    public static final int TYPE_TRIGGER = 0x18;

    public static final int CTRL_LOGIN = 0x0;
    public static final int CTRL_CHANGELOGSTATE = 0x1;

    public static final int DATA_MESSAGE = 0x0;
    public static final int DATA_PROFILE = 0x1;
    public static final int DATA_BOARD = 0x2;
    public static final int DATA_POST = 0x3;
}
package com.github.shiftac.upartier.network.demo;

import com.github.shiftac.upartier.network.PacketVersion;

public interface PacketType extends PacketVersion
{
    public static final int TYPE_CTRL = 0x0;
    public static final int TYPE_PULL = 0x08;
    public static final int TYPE_PUSH = 0x10;
    public static final int TYPE_TRIGGER = 0x18;

    public static final int CTRL_LOCAL = 0x0;
    public static final int CTRL_LOGIN = 0x1;

    public static final int DATA_MESSAGE_PLAIN = 0x0;
    public static final int DATA_MESSAGE_RICH = 0x1;
    public static final int DATA_PROFILE = 0x2;
    public static final int DATA_BOARD = 0x3;
    public static final int DATA_POST = 0x4;
    public static final int DATA_GENERAL = 0x5;
}
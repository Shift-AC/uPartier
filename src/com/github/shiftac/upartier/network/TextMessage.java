package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class TextMessage extends Message
{
    byte type = ;

    protected abstract void readSpecifiedType(InputStream is) 
        throws IOException;

    protected abstract void writeSpecifiedType(OutputStream os) 
        throws IOException;

    @Override
    public void read(InputStream is) throws IOException
    {
        type = (byte)is.read();
        readSpecifiedType(is);
    }

    @Override
    public void write(OutputStream os) throws IOException
    {
        os.write(type);
        writeSpecifiedType(os);
    }
}
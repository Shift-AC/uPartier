package com.github.shiftac.upartier.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public interface NativeStreamIO
{
    public void read(InputStream is) throws IOException;
    public void write(OutputStream os) throws IOException;
}
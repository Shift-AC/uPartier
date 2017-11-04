package com.github.shiftac.upartier.network.app;

/** 
 * Provides interfaces for sending/receving data as a client. 
 * This class is a stand-alone thread working on the Android devices and it 
 * acts as a network interface.
 * <p>
 * In our application layer model, <code>Client</code> lays on the bottom of 
 * <i> network</i> layer. It implements sending/receving operations for general
 * usages, do encryption/decryption work, while other <i>network</i> layer
 * classes simply call interfaces it provides to complete the communication 
 * work. 
 * 
 * @author ShiftAC
 * @see com.github.shiftac.upartier.network.server.Server
 */
public class Client
{
    private static Client client = new Client();
    
    public static Client getInstance()
    {
        return client;
    }

    private Client()
    {
        
    }
}
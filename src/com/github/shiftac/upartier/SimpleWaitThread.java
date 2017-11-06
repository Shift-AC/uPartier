package com.github.shiftac.upartier;

import java.io.FileInputStream;
import java.util.concurrent.atomic.AtomicBoolean;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONPointer;
import org.json.JSONPointerException;
import org.json.JSONTokener;

public class SimpleWaitThread extends Thread
{
    protected AtomicBoolean wait = new AtomicBoolean(false);  

    public SimpleWaitThread()
    {
        super();
    }

    // Code from http://blog.csdn.net/historyasamirror/article/details/6709693
    public boolean doWait() 
    {  
        synchronized (this.wait) 
        {
            if (this.wait.get() == true) 
            {  
                return false;  
            }  
    
            this.wait.set(true);  
    
            try 
            {  
                this.wait.wait();  
            } catch (InterruptedException e) {}  
            
            return true;  
        }  
    }

    public boolean doNotify() 
    {  
        synchronized (this.wait) 
        {
            if (this.wait.get() == false) 
            {  
                return false;
            }
    
            this.wait.notify();   

            this.wait.set(false);            
            return true;  
        }  
    }
}
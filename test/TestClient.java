import com.github.shiftac.upartier.network.AES128Packet;
import com.github.shiftac.upartier.network.PacketType;
import com.github.shiftac.upartier.network.PlainMessage;
import com.github.shiftac.upartier.network.app.Client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

import com.github.shiftac.upartier.Util;

public class TestClient
{
    public static void main(String[] args)
    {
        try
        {
            Client.startClient(10);
            while (true)
            {
                BufferedReader is = new BufferedReader(
                    new InputStreamReader(System.in));
                String line = is.readLine();
                AES128Packet pak;
                if (line.charAt(0) == ' ')
                {
                    pak = new AES128Packet(new PlainMessage(line.substring(1)));
                    pak.type = PacketType.DATA_MESSAGE_PLAIN | 
                        PacketType.TYPE_TRIGGER;
                }
                else
                {
                    pak = new AES128Packet(new PlainMessage(line));
                    pak.type = PacketType.DATA_MESSAGE_PLAIN | 
                        PacketType.TYPE_PUSH;
                }
                Client.getInstance().issue(pak);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace(Util.log.dest);
        }
    }
}
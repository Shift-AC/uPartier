import com.github.shiftac.upartier.network.app.Client;
import com.github.shiftac.upartier.Util;

public class TestClient
{
    public static void main(String[] args)
    {
        try
        {
            Client.startClient(10);
            Thread.sleep(10000);
            System.out.println("???");
            Client.startClient(11);
        }
        catch (Exception e)
        {
            e.printStackTrace(Util.log.dest);
        }
    }
}
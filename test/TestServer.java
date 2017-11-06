import com.github.shiftac.upartier.network.server.Server;
import com.github.shiftac.upartier.Util;

public class TestServer
{
    public static void main(String[] args)
    {
        try
        {
            Server s = Server.getInstance();
        }
        catch (Exception e)
        {
            e.printStackTrace(Util.log.dest);
        }
    }
}
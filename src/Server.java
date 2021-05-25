import java.io.IOException;
import java.net.*;

/*
This Server Helps Maintain Multiple Client Calls
with Multi-Threading Features
 */
public class Server
{
    public static void main(String[] args) throws IOException
    {
        final int LOCAL_PORT = 8000;
        ServerSocket server = new ServerSocket(LOCAL_PORT);
        System.out.println("Waiting for Connection......");


        while (true)
        {
            Socket s = server.accept();
            System.out.println("Client connected!");


            ServerProtocol service = new ServerProtocol(s);
            Thread t = new Thread(service);
            t.start();
        }
    }
}

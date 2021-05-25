import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

/*
Client Sends Necessary Commands to Server
 */
public class Client
{


    public static void main(String[] args) throws Exception
    {
        //Variables to Store Sent Command and Server Response
        String command;
        String serverResponse = "";


        Socket soc = new Socket("localhost", 8000);

        Scanner in = new Scanner(soc.getInputStream());
        in.useDelimiter("!");
        PrintWriter out = new PrintWriter(soc.getOutputStream());

        Scanner inputReader = new Scanner(System.in);

        do
        {
            System.out.println("End Command Input with \"!\" ");
            System.out.println("Input QUIT as command to Close Service");
            System.out.println("Input Command: ");
            command = inputReader.nextLine();

            //Sends Command to Server
            out.print(command);
            out.flush();

            //Reads Response
            serverResponse = in.next();
            System.out.println(serverResponse);
            System.out.println();


        } while (!command.equalsIgnoreCase("quit!"));

        soc.close();
    }
}


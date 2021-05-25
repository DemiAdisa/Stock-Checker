import java.io.*;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/*
This Class Helps Server Perform
 Necessary Functions

 Methods can be found at the end of the class
 */
public class ServerProtocol implements Runnable
{
    Socket socket;

    /*
    Variables to help split command
    E.g Command supplied is User Andrew!
    commandValue gets User
    commandAppend gets andrew
     */
    String commandValue, commandAppend;

    StockPriceParser parse;

    //Client Stream Reader
    Scanner in;
    PrintWriter out;

    //File Object
    File file;

    //Read Users File
    Scanner fileReader;
    PrintWriter fileWriter;


    public ServerProtocol(Socket aSocket)
    {
        socket = aSocket;

    }

    @Override
    public void run()
    {
        try
        {
            try
            {
                in = new Scanner(socket.getInputStream()).useDelimiter("!");
                out = new PrintWriter(socket.getOutputStream());
                interpretCommand();

                while (!commandValue.equalsIgnoreCase("user"))
                {
                    out.print("Sign In First!");
                    out.flush();

                    //Method Call
                    interpretCommand();
                }

                /*
                Verifies if User has been created or not!
                Also provides helpful prompts
                 */
                String userName = commandAppend;
                file = new File(userName);
                if (!file.exists())
                {
                    out.print("New User Created!");
                    out.flush();
                    file.createNewFile();
                } else
                {
                    //Response
                    out.print("Welcome back, " + userName + "!");
                    out.flush();
                }

                do
                {
                    interpretCommand();
                    if (commandValue.equalsIgnoreCase("portfolio"))
                    {

                        fileReader = new Scanner(file);
                        String ticker = "";
                        double price = 0;
                        String output = "";

                        while (fileReader.hasNext())
                        {
                            ticker = fileReader.next();

                            //apiRetriever is a method that returns a JSON String
                            price = parse.parsePrice(apiRetriever(ticker));

                            //Skips to the Next Ticker Value in File
                            fileReader.nextFloat();

                            output += (ticker + " " + "" + price + "\n");

                        }

                        output += "!";
                        out.print(output);

                        out.flush();

                        fileReader.close();

                    } else if (commandValue.equalsIgnoreCase("track"))
                    {
                        String ticker = commandAppend;
                        FileWriter fw = new FileWriter(file, true);
                        fileWriter = new PrintWriter(fw);

                        //apiRetriever is a method that returns a JSON String
                        double price = parse.parsePrice(apiRetriever(ticker));

                        fileWriter.print(ticker);
                        fileWriter.print(" ");
                        fileWriter.println(price);

                        fileWriter.close();

                        out.println("Ok!");
                        out.flush();

                    } else if (commandValue.equalsIgnoreCase("forget"))
                    {
                         /*
                         Stream Filters contents of the User File
                         and collects the values to a List
                        */
                        List<String> editedFileLines = Files.lines(Paths.get(userName))
                                .filter(lines -> !lines.contains(commandAppend))
                                .collect(Collectors.toList());

                        fileWriter = new PrintWriter(file);

                        /*
                        Enhanced for loop writes the contents of list to the file
                         */
                        for (String line : editedFileLines)
                        {
                            fileWriter.println(line);
                        }

                        fileWriter.close();

                        out.print("Ok!");
                        out.flush();

                    } else
                    {
                        if (commandValue.equalsIgnoreCase("QUIT"))
                        {
                            out.print("GOODBYE!");
                            out.flush();
                        } else
                        {
                            out.print("Error!");
                            out.flush();
                        }
                    }


                } while (!commandValue.equalsIgnoreCase("QUIT"));

            } finally
            {
                socket.close();
            }

        } catch (Exception e)
        {
            e.printStackTrace();
        }

    }

    /**
     * This Method Interprets supplied command
     * E.g Command supplied is User Andrew!
     * commandValue gets User
     * commandAppend gets andrew
     */
    public void interpretCommand()
    {
        String line = in.next();
        Scanner lineScan = new Scanner(line);
        commandValue = lineScan.next();

        /*
        Looks for a command append if command has append
         */
        if (!commandValue.equalsIgnoreCase("portfolio") && !commandValue.equalsIgnoreCase("quit"))
        {
            commandAppend = lineScan.next();
        }
    }

    /**
     * Helps get JSON String from Provided API using
     * supplied Ticker
     *
     * @param ticker Stock Ticker needed
     * @return JSON String for requested ticker
     * @throws IOException
     */
    public String apiRetriever(String ticker) throws IOException
    {
        String JSONString = "";

        URL u = new URL("https://financialmodelingprep.com/api/v3/stock/real-time-price/" + ticker);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();

        Scanner urlServerResponse = new Scanner(connection.getInputStream());

        while (urlServerResponse.hasNext())
        {
            JSONString += urlServerResponse.nextLine();
        }

        return JSONString;
    }


}

import java.io.*;
import java.net.Socket;
import java.net.UnknownHostException;

public class ChatClient {

    //Variables to store the port number and IP address used to set up the socket.
    private int ccp;
    private String cca;

    //String to hold the message that is to be disributed.
    private String message;

    //Variable holding the socket the client is using.
    private Socket socket;

    //Flag that indicates if the client is running or not.
    private boolean clientRunning;

    //Thread variable to hold the thread for the clientReader class.
    private Thread clientReader;

    //Instantiations of the I/O streams to the server necessary for communication.
    private BufferedReader inputStream;
    private PrintStream outputStream;

    //BufferedReader that reads the input taking from the console.
    private BufferedReader inputLine;


    //Default constructor that, if no port or IP address is set by the user, sets 14001 and localhost as the default port and host.
    public ChatClient() {

        cca = "localhost";
        ccp = 14001;

        clientRunning = true;

        message = "";

    }

    //Constructor that is called if the user has chosen a port.
    public ChatClient(int chosenPort)
    {
        cca = "localhost";
        ccp = chosenPort;

        clientRunning = true;

        message = "";
    }

    //Constructor that is called if the user has chosen a host.
    public ChatClient(String host)
    {
        cca = host;
        ccp = 14001;

        clientRunning = true;

        message = "";
    }

    //Constructor that is called if the user has chosen a port and a host.
    public ChatClient(String host, int chosenPort)
    {
        cca = host;
        ccp = chosenPort;

        clientRunning = true;

        message = "";
    }

    /*  Method that attempts to connect the user to the server using the port and host specified.
        Prints an error message if the port or host is unknown.
    */
    public void connectToServer()
    {
        try
        {
            System.out.println("The port is: " + ccp);
            System.out.println("The address used is: " + cca);
            socket = new Socket(cca, ccp);
        }

        catch (UnknownHostException e)
        {
            System.err.println("Unknown host: " + cca + ". Please recompile with another address.");
            System.exit(0);
        }

        catch (IOException e)
        {
            System.err.println("Invalid port: " + ccp + ". Please recompile with another port.");
            System.exit(0);
        }

    }

    public long hostConversion(String address)
    {
        long userAddress = 0;

        if(!address.equals("localhost"))
        {
            userAddress = Long.parseLong(address);
        }

        return userAddress;
    }

    //Accessor that return whether or not the client is running.
    public boolean isClientRunning()
    {
        return clientRunning;
    }

    //Method that instantiates the IO streams for the client using the socket.
    public void initialiseIO()
    {
       try
       {
           //IO streams to send and receive data from the server.
           outputStream = new PrintStream(socket.getOutputStream());
           inputStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));

           //Reads data from the console for the client.
           inputLine = new BufferedReader(new InputStreamReader(System.in));
       }

       catch (IOException e)
       {
           System.out.println(e);
       }
    }

    /*  Method that reads input from the console.
        If the client types EXIT into the console, the client exits, otherwise it sends the message to the server.
    */
    public void sendMessages()
    {
        try
        {
            message = inputLine.readLine();

            if(message.equals("EXIT"))
            {
                shutDown();

                clientRunning = false;
            }

            else
            {
                outputStream.println(message);
            }
        }

        catch (Exception e)
        {
            System.out.println(e);
        }
    }

    //Method that closes all of the streams and the socket for the client if the user exits.
    public void shutDown()
    {
        try
        {
            inputLine.close();

            outputStream.close();
            inputStream.close();
            socket.close();
        }

        catch (IOException e)
        {
            System.out.println(e);
        }
    }

    //Method that runs the client, calling all the necessary methods and creating a new thread to read from the console.
    public void run()
    {
        connectToServer();

        initialiseIO();

        System.out.println("You are now connected to the Server.");

        clientReader = new Thread(new ChatClientReader(inputStream));

        clientReader.start();

        while(clientRunning)
        {
            sendMessages();
        }

        shutDown();
    }

    /*  Main method that checks to see if the user has entered a custom port and/or host name.
        Then starts the client with the appropriate arguments.
    */
    public static void main(String[] args)
    {
        int ccp = 0;
        String cca = "";

        boolean portChosen = false;
        boolean hostChosen = false;

        if(args.length > 0)
        {
            //For loop that runs through and checks the arguments for a port and host.
            for(int i = 0; i < args.length; i++)
            {
                String variablesToSet = args[i];

                if (variablesToSet.equals("-ccp"))
                {
                    ccp = Integer.parseInt(args[i + 1]);
                    portChosen = true;
                }

                else if (variablesToSet.equals("-cca"))
                {
                    cca = args[i + 1];
                    hostChosen = true;
                }
            }

            if(portChosen && hostChosen)
            {
                ChatClient client = new ChatClient(cca, ccp);
                client.run();
            }

            else if (portChosen ^ hostChosen)
            {
                if(portChosen)
                {
                    ChatClient client = new ChatClient(ccp);
                    client.run();
                }

                else
                {
                    ChatClient client = new ChatClient(cca);
                    client.run();
                }
            }

            else
            {
                System.err.println("Please enter a valid IP address or port number.");
                System.exit(0);
            }

        }

        else
        {
            ChatClient client = new ChatClient();
            client.run();
        }


        System.exit(0);
    }

}

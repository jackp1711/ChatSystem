import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;


/*
    This class sets up the server so that clients will be able to connect and send messages to each other.

    Once the necessary setup procedures have been undertaken, including instantiating the server socket and IO, the
    class then continues to listen for new clients to connect to the server. Once a client tries to connect, a thread
    is spawned so that all of the message handling for each client can be handled separately.

    The user is able to choose a custom port to connect to if they so wish, otherwise the port 14001 is used instead.
 */

public class ChatServer {

    //Variable that stores the port number.
    private int csp;

    //Flag that holds whether or not the server is currently running.
    private boolean serverRunning;

    //ArrayList that holds the PrintStreams of all the clients currently connected to the server.
    public static ArrayList<PrintStream> arrayOfUserStreams = new ArrayList<>();

    //Variables to store the sockets used in running the server and handling client requests.
    private ServerSocket serverSocket;
    private Socket clientSocket;

    //Holds how many clients are currently connected to the server.
    public static int currentClients;

    //Variable that stores the maximum number of clients that can connect to the server.
    private final static int maxClients = 5;

    //Terminal reader so that the user can write commands to the server.
    private BufferedReader inputTerminal;

    //Constructor called if a port is not chosen by the user.
    public ChatServer()
    {
        csp = 14001;

        setUpServer();

    }

    //Constructor called if a user chooses a port themselves.
    public ChatServer(int port)
    {
        csp = port;

        setUpServer();

    }

    //Method assigns the required values to key flags and variables used in the running of the server.
    public void setUpServer()
    {
        serverRunning = true;

        currentClients = 0;

        inputTerminal = new BufferedReader(new InputStreamReader(System.in));
    }

    //Instantiates the server socket and handles any exceptions.
    public void setUpSockets()
    {
        try
        {
            System.out.println("The port number is: " + csp);
            serverSocket = new ServerSocket(csp);
            createServerIO();

            System.out.println("Server is running. Waiting for clients:");
        }

        catch (Exception e)
        {
            System.err.println("Port not found or already in use, setup server with a valid port.");
            serverRunning = false;
            System.exit(0);
        }

    }

    //Method called when a new user connects to the server.
    public void spawnClientThreads()
    {
        try
        {
            clientSocket = serverSocket.accept();

            PrintStream os = new PrintStream((clientSocket.getOutputStream()));

            //Checks to see if the max number of clients has been reached.
            if (currentClients == maxClients)
            {
                os.println("Server busy. Try again later.");
                clientSocket.close();
            }

            //Otherwise, adds the outputstream of the client to the ArrayList and spawns a new client handler thread.
            else
            {
                arrayOfUserStreams.add(os);

                new Thread(new ChatServerClientThread(clientSocket, currentClients, arrayOfUserStreams)).start();
                currentClients++;
            }
        }

        catch (IOException e)
        {
            serverRunning = false;
        }
    }

    //Calls the necessary methods to successfully run the chat room, and continues to call them until server shutdown.
    public void runChatRoomServer() throws IOException
    {
        setUpSockets();

        while (serverRunning)
        {
            spawnClientThreads();
        }

        serverShutdown();
    }

    //Spawns a new thread to handle input from the terminal to send commands to the server.
    public void createServerIO()
    {
        new Thread(new ChatServerTerminal(inputTerminal, serverSocket)).start();
    }

    //Method that closes the server socket and sends a message to all the clients that the server has shut down.
    public void serverShutdown() throws IOException
    {
        for(int i = 0; i < arrayOfUserStreams.size(); i++)
        {
            arrayOfUserStreams.get(i).println("SERVER SHUTDOWN");
        }

        serverSocket.close();
    }

    //Accessor to return if the server is running.
    public boolean isServerRunning()
    {
        return serverRunning;
    }

    /*
        Method that is called to reduce the number of clients when one disconnects.
        It is static because it needs to be called outside of this class, in the ChatServerClientThread class.
    */
    public static void removeClient()
    {
        currentClients--;
    }

    /*
        Main method that, when run, checks to see if the user has entered their own port number.
        If a port number is detected, then it runs with that port, otherwise uses the default port of 14001.
     */
    public static void main (String[] args) throws Exception
    {
        boolean portChosen = false;
        ChatServer cs;

        int port = 0;


        if(args.length > 0)
        {
            for(int i = 0; i < args.length; i++)
            {
                if(args[i].equals("-csp"))
                {
                    port = Integer.parseInt(args[i+1]);
                    portChosen = true;
                }
            }

            if(portChosen)
            {
                cs = new ChatServer(port);
                cs.runChatRoomServer();
            }

            else
            {
                System.err.println("Please enter '-csp' and then the desired port number in order to connect.");
                System.exit(0);
            }

        }

        else
        {
            cs = new ChatServer();
            cs.runChatRoomServer();
        }

        System.exit(0);
    }
}

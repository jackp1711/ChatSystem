import java.io.DataInputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;


/*
    ChatServerClientThread is a class that is spawned for each client and uses it's socket to handle the specific
    requests of each client. Initially requests the client enter a username and then uses this to communicate to the
    rest of the clients.

    Listens for messages being sent from the client and then redistributes it to all the other clients with
    the users username.
 */

public class ChatServerClientThread implements Runnable {

    //Variables to store the socket specifics for each individual client.
    private Socket socket;
    private PrintStream serverOutputStream;
    private DataInputStream serverInputStream;

    private boolean disconnect;

    //Same variable stored in the ChatServer class. Used to send messages to the clients.
    public static ArrayList<PrintStream> arrayOfUserStreams;

    private String message;
    private boolean running;

    private int threadIndex;

    private String username;
    private String input;

    //Constructor that sets all the necessary variables based on which client is connecting.
    public ChatServerClientThread (Socket st, int threadNumb, ArrayList<PrintStream> users)
    {
        socket = st;
        running = true;

        arrayOfUserStreams = users;

        threadIndex = threadNumb;

        disconnect = false;
    }

    //Run method necessary for a Thread class.
    @Override
    public void run()
    {
        acceptNewClient();

        while (running)
        {
            messageHandling();
        }
    }

    //Method that initialises the I/O streams for the client as well as allowing the user to set their nickname.
    public void acceptNewClient()
    {
        try
        {
            serverInputStream = new DataInputStream(socket.getInputStream());
            serverOutputStream = new PrintStream(socket.getOutputStream());

            System.out.println("Client successfully connected.");

            serverOutputStream.println("Welcome to the chat room.");

            serverOutputStream.println("Enter your nickname:");

            while (true)
            {
                input = serverInputStream.readLine();

                if(input != null)
                {
                    username = input;
                    break;
                }
            }
        }

        catch (IOException e)
        {
            System.out.println("Client failed to connect.");
        }

    }

    //Method that reads input from the client's inputStream and sends it to all the connected clients.
    public synchronized void messageHandling()
    {
        try
        {
            message = serverInputStream.readLine();

            messageCheck();

            if(!disconnect);
            {
                System.out.println(username + ": " + message);

                for (int i = 0; i < arrayOfUserStreams.size(); i++)
                {
                    arrayOfUserStreams.get(i).println("From " + username + ": " + message);
                }
            }
        }

        catch (Exception e)
        {
            clientDisconnect();
        }
    }

    //Method that checks each method to see if an EXIT command has been sent. Calls the client disconnect method if so.
    public void messageCheck()
    {
        if(message.equals("EXIT"))
        {
            clientDisconnect();
            disconnect = true;
        }
        else
        {
            disconnect = false;
        }
    }

    //Method that tells all the users and the server that the current client has disconnected.
    public synchronized void printUserDisconnect()
    {
        System.out.println(username + " has disconnected.");

        for (int i = 0; i < arrayOfUserStreams.size(); i++)
        {
            if(i != threadIndex)
            {
                arrayOfUserStreams.get(i).println(username + " has disconnected.");
            }
        }
    }

    //Method that closes the I/O streams and the socket if the client chooses to disconnect.
    public void clientDisconnect()
    {
        try
        {
            arrayOfUserStreams.get(threadIndex).close();
            ChatServer.removeClient();
            printUserDisconnect();
            serverInputStream.close();
            socket.close();

            running = false;
        }

        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}

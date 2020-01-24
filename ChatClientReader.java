import java.io.BufferedReader;
import java.io.IOException;

/*
    This class reads the input stream to the client from the server and prints out the result to the console.

    Only prints to the console if there is a message from the server.
 */

public class ChatClientReader implements Runnable {

    private BufferedReader inputStream;
    private boolean running;

    private String input;

    //Constructor that instantiates the necessary variables for the class to function.
    public ChatClientReader (BufferedReader is)
    {
        inputStream = is;
        running = true;

    }

    /*  Run method that will be ran when the class is called as a thread.
        While the client is running, checks to see if any messages has been sent from the server.

        If the client stops running, or the server shuts down, the system exits.
    */
    @Override
    public void run()
    {
        while(running)
        {
            try
            {
                receiveMessages();
            }

            catch (IOException e)
            {
                break;
            }
        }

        System.out.println("You have been disconnected from the server. Shutting down the client.");
        System.exit(0);
    }

    /*  Method that when run, reads messages from the servers stream and prints them to the console.
        Reads the input to see if an exit message from the server has been sent, and exits if so.
    */
    public void receiveMessages() throws IOException
    {
        input = inputStream.readLine();

        if (input.equals("EXIT") || input.equals("SERVER SHUTDOWN"))
        {
            running = false;
            System.err.println("Server has shut down.");
        }

        else if(input != null)
        {
            System.out.println(input);
        }
    }
}

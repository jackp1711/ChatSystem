import java.io.BufferedReader;
import java.io.IOException;
import java.net.ServerSocket;

/*
    Class that reads the input from the console for the server and enacts the commands.

    Searches for the 'EXIT' command to shut down the server and disconnect all the clients.
 */


public class ChatServerTerminal implements Runnable{

    private BufferedReader terminalInput;

    private ServerSocket serverSocket;

    private String command;

    public ChatServerTerminal(BufferedReader it, ServerSocket ss)
    {
        terminalInput = it;

        serverSocket = ss;
    }

    @Override
    public void run()
    {
        while(true)
        {
            try
            {
                command = terminalInput.readLine();

                commandCheck();
            }

            catch (IOException e)
            {
                return;
            }
        }
    }

    //Method that checks the input from the console to see if the user wishes to close the server socket.
    public void commandCheck() throws IOException
    {
        if (command.equals("EXIT"))
        {
            System.out.println("Server shutdown.");
            serverSocket.close();
        }

        else
        {
            System.err.println("Invalid command.");
        }
    }
}

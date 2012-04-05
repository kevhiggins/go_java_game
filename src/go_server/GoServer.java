/*
 * GoServer.java
 *
 * Created on November 21, 2007, 11:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go_server;
import java.util.Iterator;
import ocsf.server.*;
import java.io.*;
import go.Message;
import java.net.UnknownHostException;
import go.GameQuery;
/**
 *
 * @author Kevin Higgins, Caldwell Bailey
 */

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to implement a Go server.
 * Adds players to the game who attempt to connect, transfers
 * moves between players, and communicates results from the game to the players.
 */

public class GoServer extends AbstractServer{
    
    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 4444;

    /**
     * The players of the game.
     */
    private ConnectionToClient player1;
    private ConnectionToClient player2;
    
    /**
     * Constant strings that are often sent to the players
     */
    final private static String WELCOME = "***Connection Successful. Welcome to Go.";
    final private static String GAME_FULL = "***Sorry, game full.  Try again later.";
    final private static String WAIT = "***Please wait for another player to connect.";

    private void log(String message) {
        Message tmp = new Message(message);
        sendToAllClients(tmp);
    }

    /**
     * Constructs an instance of the server.  
     * @param port The port number to connect on.
     */
    public GoServer(int port) {
	super(port);
        try
        {
            listen();
        }
        catch(IOException e)
        { 
            log("**server failed to start listening");
        }
    }
    
    /**
     * Handles new connections by first checking whether the game is
     * full.  If it is, the player is informed and the connection is
     * closed.  If not, the player is welcomed and if there is another player 
     * they are notified.
     * @param client The newly connected client.
     */
    protected void clientConnected(ConnectionToClient client) {             
        try
        {
            log("***Client connecting at " + client.getInetAddress().getLocalHost().getHostAddress());
            if ( (player1 != null) && (player2 != null)) {
                sendMessage(new Message(GAME_FULL),client);
                try 
                {
                    client.close();
                }
                catch(IOException ex) {
                    log("***Error closing client connection!");
                }
                return;
            }

            if ( player1 == null ) 
            {
                player1 = client;
                sendMessage(new Message(WELCOME), client);
                if(player2 != null)
                    sendMessage(new Message("***A new player has joined the game at " + player1.getInetAddress().getLocalHost().getHostAddress()),player2);
                else
                    sendMessage(new Message(WAIT), client);
            }
            else 
            {
                player2 = client;
                sendMessage(new Message(WELCOME), client);
                sendMessage(new Message("A new player has joined the game at " + player2.getInetAddress().getLocalHost().getHostAddress()),player1);
            }
            }
        catch(UnknownHostException e)
        {
            sendMessage(new Message("***An error occurred while trying to connect to the server"),client);
        }
    }   
    
    /**
     * Handles client disconnections by removing the player from the game.
     * If there is another player still in the game, he or she is informed
     * and asked to wait for a new player.
     * @param client The client that is disconnecting.
     */
    protected void clientDisconnected(ConnectionToClient client) {
        sendMessage(new Message("**Your opponent is disconnecting..."),enemy(client));
        if ( client == player1 ) 
            player1 = null;
        else if ( client == player2 )
            player2 = null;
        else //shouldn't happen
            throw new RuntimeException("Error in clientDisconnected.");
        sendMessage(new Message("***Your opponent disconnected, waiting for new opponent."), enemy(client));
    }

    /**
     * Method called when a client object throws an exception.
     * @param client The client throwing the exception.
     * @param exception The exception thrown.
     */
    protected synchronized void clientException(ConnectionToClient client, Throwable exception) {
       //sendToAllClients("Client exception thrown: " + exception.getClass());
    }
    
    
    private void sendMessage(Object msg, ConnectionToClient client) {
        try
        {
           client.sendToClient(msg);
        }
        catch(IOException e)
        {
           sendToAllClients(new Message("***Last message failed"));
        }
    }
    
    private ConnectionToClient enemy(ConnectionToClient client) {
        if(client == player1)
            return player2;
        return player1;
    }
    
    /**
    * This method overrides the one in the superclass.  Called
    * when the server starts listening for connections.
    */
    protected void serverStarted(){
        System.out.println("The server is listening on port " + getPort());
    }
    
    private boolean isFull(){
        if(getNumberOfClients() == 2)
            return true;
        else 
            return false;
    }
        
    
        /**
     * Handles the message object from the client.  If another player exists 
     * the message is sent to them.
     * @param msg The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if(enemy(client) != null)
            sendMessage(msg,enemy(client));
        else if(msg instanceof GameQuery)
            sendMessage(new Message("***You do not have an opponent to play against."), client);
    }
    
    /**
     * Notifys the connections that the server is closing, 
     * and then closes correctly.
     */
    public void quit() {
        try
        {
            sendToAllClients(new Message("***The server is closing."));
            sendToAllClients("ENDCLIENTS");
            close();
        }
        catch(IOException e)
        {
            sendToAllClients(new Message("***IOException Thrown"));
        }
    }
    
}

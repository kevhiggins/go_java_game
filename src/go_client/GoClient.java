/*
 * GoClient.java
 *
 * Created on November 21, 2007, 11:03 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go_client;
import java.awt.Color;
import javax.swing.JOptionPane;
import ocsf.client.*;
import java.io.*;
import go.*;
import java.util.LinkedList;
import java.net.ConnectException;
/**
 *
 * @author Kevin Higgins, Caldwell Bailey
 */

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to implement a Go client. It handles
 * connections to a Go server, and it communicates with a 
 * player through a user interface *not currently implemented*.
 */
public class GoClient extends AbstractClient {
    
    private Game goGame;
    private BoardUI gui;
    private int size;

    
   /**
     * Constructs an instance of the Goclient by attempting to connect
     * with a server.
     * @param host The server to connect to.
     * @param port The port number to connect on.
     * @throws java.io.IOException If a connection to the server cannot be established.
     * @param gui the gui for the goClient
     */

    public GoClient(String host, int port, BoardUI gui) 
	throws IOException 
    {   
        super(host, port); //Call the superclass constructor
        this.gui = gui;
        openConnection();
    }

    /**
     * This method handles all data that comes in from the server.
     * The message should be either a string or an instance of the
     * <B>GoClient.Move</B> class.  If it is the string 
     * <B>GoClient.Move</B>, the <B>GoClient.Game</B> performs the move
     * and control then shifts to the player.
     * If it is any other string, it is displayed to the player.
     * @param message The message from the server.
     */
    public void handleMessageFromServer(Object message) {
        if( message instanceof Coordinate ) {
           
            if(goGame != null) {
                if(!goGame.isScoreMode()) {
                    if(((Coordinate)message).getX() == 1000 && goGame.blacksTurn() == !goGame.isBlack()) {
                        goGame.switchTurn();
                        gui.hasPassed();
                        if(goGame.getHasPassed() == false) {
                            
                            goGame.setHasPassed();
                        }
                        else {
                            goGame.scoreGame();
                            goGame.attemptScore();
                            gui.setWhiteScore(goGame.getWhiteTerritory().size()+goGame.getWhiteCaptures());
                            gui.setBlackScore(goGame.getBlackTerritory().size()+goGame.getBlackCaptures()-6.5);
                            gui.setBlackTerr(goGame.getBlackTerritory().size());
                            gui.setWhiteTerr(goGame.getWhiteTerritory().size());
                            gui.score(goGame.getWhiteTerritory(),Color.GREEN,goGame.getBlackTerritory(),Color.PINK);
                        }   
                        gui.switchTheSwitcher();
                        gui.kill(null);
                        gui.refresher();
                        
                    }
                    else {//Move checkign!
                        LinkedList<Coordinate> tmpy = goGame.processMove(message, !goGame.isBlack());
                        gui.setBlackCaps(goGame.getBlackCaptures());
                        gui.setWhiteCaps(goGame.getWhiteCaptures());
                        if(tmpy != null) {   // this might be wrong, if stuff breaks, check here.
                            gui.addPiece((Coordinate)message);
                            gui.drawAPiece((Coordinate)message);
                        }
                        gui.kill(tmpy);
                        gui.refresher();
                        int bob = 5;
                    }
                }//This is what happens when a pass occurs
                else if(((Coordinate)message).getX() != 1000) {
                    gui.drawList(goGame.scoreKill(((Coordinate)message).getX(), ((Coordinate)message).getY()));
                    goGame.attemptScore();
                    gui.setBlackCaps(goGame.getBlackCaptures());
                    gui.setWhiteCaps(goGame.getWhiteCaptures());
                    gui.setBlackTerr(goGame.getBlackTerritory().size());
                    gui.setWhiteTerr(goGame.getWhiteTerritory().size());
                    gui.setWhiteScore(goGame.getWhiteTerritory().size()+goGame.getWhiteCaptures());
                    gui.setBlackScore(goGame.getBlackTerritory().size()+goGame.getBlackCaptures()-6.5);
                    gui.score(goGame.getWhiteTerritory(),Color.GREEN,goGame.getBlackTerritory(),Color.PINK);
                }
                
            }
            else gui.kill(null);
        }
        else if(message instanceof Message) {
            Message tmp = (Message)message;
            gui.display(tmp.getMessage());
        }
        else if(message instanceof Boolean)
        {
            Boolean response = (Boolean)message;
            if(response)
            {
                newGame(size,true);
                gui.display("***Opponent has accepted your game");
            }
            else
            {
                gui.display("***Your request for a new game has been rejected");
            }
        }            
        else if(message instanceof GameQuery)
        {
            GameQuery newGame = (GameQuery)message;
            int size = newGame.getSize();
            gui.clearLabels();
            if(size == 0)
            {
                JOptionPane.showMessageDialog(null, "You Win! Your opponent has forfeited the game.");
                endGame();
                return;
            }
            Boolean makeGame = (gui.promptUser("Would you like to join a game of size " + size + "x" + size + "?"));
            try 
            {
                sendToServer(makeGame);
            }
            catch(IOException e)
            {
                gui.display("***Response failed");
            }
            if(makeGame)
                newGame(size,false);
        }
        else if(message instanceof String) 
        {
            String tmp = (String)message;
            if(tmp == "ENDCLIENTS")
                gui.endClient();
        }
        else throw new RuntimeException("Unrecognized message type in handleMessageFromServer");  // Shouldn't happen
    }
    
     /**
     * This method handles all data coming from the user interface
     * by simply sending it on to the server.
     * @param message The message the player wishes to send to the server.
     */
    public void handleInputFromClientGUI(Object message)
    {
        try {
            if( message instanceof Coordinate ) {
                if(goGame != null) 
                    if(!goGame.isScoreMode()) {
                        if(((Coordinate)message).getX() == 1000 && goGame.blacksTurn() == goGame.isBlack()) {
                            goGame.switchTurn();
                            if(goGame.getHasPassed() == false) {
                                goGame.setHasPassed();
                            }
                            else {
                                goGame.scoreGame();
                                goGame.attemptScore();
                                gui.setBlackTerr(goGame.getBlackTerritory().size());
                                gui.setWhiteTerr(goGame.getWhiteTerritory().size());
                                gui.setWhiteScore(goGame.getWhiteTerritory().size()+goGame.getWhiteCaptures());
                                gui.setBlackScore(goGame.getBlackTerritory().size()+goGame.getBlackCaptures()-6.5);
                                gui.score(goGame.getWhiteTerritory(),Color.GREEN,goGame.getBlackTerritory(),Color.PINK);
                            }   
                            gui.switchTheSwitcher(); 
                            gui.kill(null);
                            sendToServer(message);
                        }     
                        else {
                            LinkedList<Coordinate> tmpy = goGame.processMove(message, goGame.isBlack());
                            gui.setBlackCaps(goGame.getBlackCaptures());
                            gui.setWhiteCaps(goGame.getWhiteCaptures());
                            if(tmpy != null) {   // this might be wrong, if stuff breaks, check here.
                                gui.hasNotPassed();
                                sendToServer(message);
                                gui.drawAPiece((Coordinate)message);
                            }
                            gui.kill(tmpy);  
 //                           gui.score(goGame.getWhiteTerritory(),Color.GREEN,goGame.getBlackTerritory(),Color.PINK);
                        }
                    }//This is what happens when a pass occurs
                    else if(((Coordinate)message).getX() != 1000) {
                        gui.drawList(goGame.scoreKill(((Coordinate)message).getX(), ((Coordinate)message).getY()));
                        
                        sendToServer(message);
                        goGame.attemptScore();
                        gui.setBlackCaps(goGame.getBlackCaptures());
                        gui.setWhiteCaps(goGame.getWhiteCaptures());
                        gui.setBlackTerr(goGame.getBlackTerritory().size());
                        gui.setWhiteTerr(goGame.getWhiteTerritory().size());
                        gui.setWhiteScore(goGame.getWhiteTerritory().size()+goGame.getWhiteCaptures());
                        gui.setBlackScore(goGame.getBlackTerritory().size()+goGame.getBlackCaptures()-6.5);
                        gui.score(goGame.getWhiteTerritory(),Color.GREEN,goGame.getBlackTerritory(),Color.PINK);
                    }
            }
            else if(message instanceof Message) {
                Message tmp = (Message)message;
                gui.display(tmp.getMessage());
                sendToServer(message);
            }
            else if(message instanceof Boolean) {
                sendToServer(message);
            }
            else if(message instanceof GameQuery) {
                sendToServer(message);
                GameQuery gQuery = (GameQuery)message;
                size = gQuery.getSize();
                if(size == 0)
                    gui.display("You have forfeited.");
            }
        }
        catch(IOException e) 
        {
	    gui.display("Error sending message to server.  Terminating client.");
	    quit();
	}
    }
    
    /**
     * This method creates a game object aggregating
     * a Board. The game is aggregated by this class.
     * @param size the size for the board
     * @param isBlack Returns true if the player should be black
     */
    public void newGame(int size, boolean isBlack) {
        goGame = new Game(size,isBlack);
        gui.setGrid(new GridPanel(size, gui.getGrid().getDim(), gui));
        gui.setBlackCaps(0);
        gui.setBlackScore(0);
        gui.setBlackTerr(0);
        gui.setWhiteCaps(0);
        gui.setWhiteScore(0);
        gui.setWhiteTerr(0);
    }
    /**
     * The player quits the game, disconnecting
     * them from the server, and closing the client.
     */
    
    
    public void quit() {
        try
	    {
		closeConnection();
	    }
	catch(IOException e) {}
	System.exit(0);
    }
    
    /**
     * returns true if goGame doesn't equal null
     * @return returns true if a game is being played
     */
    public boolean inGame() {
        if( goGame != null )
            return true;
        return false;
    }
    
    /**
     * informs the user the connection has been closed
     */
    protected void connectionClosed() {
       gui.display("***You have disconnected from the server");
       gui.clientDisconnected();
       endGame();
       gui.endClient();
    }
    
    /**
     * An exception that can be thrown if an error occurs
     * @param exception the exception to be thrown
     */
    protected void connectionException(Exception exception) {
    }
    public void endGame() {
        goGame = null;
    }
}

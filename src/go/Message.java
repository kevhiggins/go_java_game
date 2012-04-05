/*
 * Message.java
 *
 * Created on December 3, 2007, 7:43 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go;

/**
 * This class contains the text that the players can send 
 * back and forth to each other.
 * @author Caldwell Bailey, Kevin Higgins
 * 
 */
public class Message implements java.io.Serializable {
    private String message;
    
    /**
     * The Constructor for <B>Message</B>.
     * @param message The String to be displayed to the user.
     */
    public Message(String message) {
        this.message = message;   
    }
    /** the accessor for the message being sent
     *  @return message
     */
    public String getMessage() {
        return message;
    }
}

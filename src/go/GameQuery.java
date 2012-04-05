/*
 * GameQuery.java
 *
 * Created on December 5, 2007, 10:53 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go;
import java.io.Serializable;

/**
 * This class is a wrapper for an integer,
 * that represents a request for a new game
 * @author Caldwell Bailey
 * 
 */
public class GameQuery implements Serializable {
    
    /**
     * Creates a new instance of GameQuery
     * @param size The size for the GameQuery
     */
    public GameQuery(int size) {
        gameSize = size;
    }
    /**
     * Returns the gameSize data member
     * @return gameSize
     */
    public int getSize() {
        return gameSize;
    }
    /*The game size for the requested new game */
    private int gameSize;
}

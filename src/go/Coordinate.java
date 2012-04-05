/*
 * Coordinate.java
 *
 * Created on November 21, 2007, 11:05 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go;

/**
 *
 * @author Kevin Higgins, Caldwell Bailey
 */

/**
 * This class corresponds to a position
 * on the game board.
 */
public class Coordinate implements java.io.Serializable {
    
    private int x;
    private int y;
    
    /**
     * Creates a new instance of Coordinate containing the loc X,Y
     * @param x The x coordinate
     * @param y The y coordinate
     */
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }
    
    /**
     * Returns the X coordinate
     * @return X coordinate
     */
    public int getX() {  
        return x;
    }
    /**
     * Returns the Y coordinate
     * @return Y coordinate
     */
    public int getY() {  
        return y;
    }
}

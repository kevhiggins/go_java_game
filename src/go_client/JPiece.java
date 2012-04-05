/*
 * PieceIcon.java
 *
 * Created on November 28, 2007, 6:54 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go_client;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.*;



/**
 *
 * @author mike
 */
public class JPiece  {
    
    /** Creates a new instance of PieceIcon */
    public JPiece(int size, Color color, int x, int y, int row, int col) {
        this.size = size;
        this.color = color;
        this.x = x;
        this.y = y;
        this.visible = false;
        piece = new Ellipse2D.Double(x,y,size,size);
        this.row  = row;
        this.col = col;
        
       
        

    }
    
    public int getX()  //gets the column - Not the actual X  pixel coordinate 
    {
        return col;
    }
    
    public int getY()  //gets the row - Not the actual Y pixel coordinate
    {
        return row;
    }
    public int getWidth()
    {
        return size;
    }
    
    public int getHeight()
    {
        return size;
    }
    
    public void draw(Graphics g)
    {
        
        Graphics2D g2 = (Graphics2D) g;

        //remember the old color to put it back. 
        Color oldColor = g2.getColor();
        g2.setColor(this.color);
        g2.fill(piece);
        g2.setColor(oldColor);
    }
    
    public void setColor(Color newColor)
    {
        color = newColor;
    }
    
    public void setVisible(boolean bool){
        visible = bool;
    }
    
    public boolean isVisible()
    {
        return visible;
    }
    
    
    
    private int size;
    private Color color;
    private int x;
    private int y;
    private boolean visible;
    private Ellipse2D.Double piece;
    private int row; //the row of the board that this piece is in.
    private int col; //the col of the board that this piece is in. 
    
    
   
    
    
}

/*
 * To change this template, choose Tools | Templates
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
public class JSquare {
    
        /** Creates a new instance of PieceIcon */
    public JSquare(int size, Color color, int x, int y, int row, int col) {
        this.size = size;
        this.color = color;
        this.x = x;
        this.y = y;
        this.visible = false;
        piece = new Rectangle2D.Double((double)x, (double)y, size, size);
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
    
    public int getXpixel() //**
    {
        return x;
    }
    
    public int getYpixel() //**
    {
        return y;
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
    private Rectangle2D.Double piece;
    private int row; //the row of the board that this piece is in.
    private int col; //the col of the board that this piece is in. 
    
    

}

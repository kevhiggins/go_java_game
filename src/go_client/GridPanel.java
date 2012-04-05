/*
 * Grid.java
 *
 * Created on November 7, 2007, 5:06 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go_client;
import go.Coordinate;
import javax.swing.*;
import java.awt.geom.*;
import java.awt.*;
import java.util.*;
import java.awt.event.*;
import java.awt.Image;


import java.io.*;
import java.net.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import javax.imageio.*;
import javax.swing.*;
/**
 *
 * @author Mike Jacobson
 */
public class GridPanel extends JPanel{
    
    /** Creates a new instance of Grid 
     The Grid panel is basically the part of the game that is the grid. This class is responsible for drawing the grid on the screen, 
     *as well as drawing the pieces or any other graphic that will be drawn in the grid space. 
     */
    
    public GridPanel(int size, Dimension dim, BoardUI ui) {
        LoadImageApp(); //oads graphic
        this.ui = ui;
        this.size = size;
        if (size < 9)
            offset = 70;   
        else offset = 50;   // the default offset. Offset is the "invisible" boarder around the grid
        this.setSize(dim);
        this.dim = dim;
        //this.setMinimumSize(new Dimension(300,300));
        this.setMinimumSize(dim);
        this.setPreferredSize(dim);
        this.setMaximumSize(dim);
        clickedPieces = new LinkedList();
        this.scoreMode = false;
        width = dim.width - 2*offset;
        height = dim.height - 2*offset;
        topLeft = new Point2D.Double(offset, offset); //this is point (0,0) if you will
        topRight = new Point2D.Double(offset + width, offset);
        bottomLeft = new Point2D.Double(offset, offset + height);
        bottomRight = new Point2D.Double(offset + width, offset + height);
        dx = (int)( (topRight.getX() - topLeft.getX()) / (size -1));
        dy = (int)( (bottomLeft.getY() - topLeft.getY()) /   (size -1) ); 
        pieces = new JPiece[size][size];
        squares = new JSquare[size][size];
        Point2D startPt = new Point2D.Double( topLeft.getX() - (.5*dx), topLeft.getY() - (.5*dy));
        int startX = (int)topLeft.getX() - (int)(.5*dx);
        int startY = (int)topLeft.getY() - (int)(.5*dy);
        int startXAdj = (int)topLeft.getX() - (int)(.1*dx);
        int startYAdj = (int)topLeft.getY() - (int)(.1*dy);
        for(int i = 0; i < size; i++) {
            int x = startX; //resets to the beginning
            int xadj = startXAdj;
            //the outside loop moves down, the inside loop moves right. 
            for(int q = 0; q< size; q++) {                
                pieces[i][q] = new JPiece(dx,Color.MAGENTA,x,startY, i,q);
                squares[i][q] = new JSquare((int)(.2*dx),Color.ORANGE,xadj,startYAdj,i,q);
                x = x + dx;  
                xadj = xadj + dx;
            }
            //move down one row
            startY = startY + dy;
            startYAdj = startYAdj + dy;
        }
         
         
        addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                //JOptionPane.showMessageDialog(null,"mouseclicked");
                JPiece somePiece = getPiece(event.getPoint());
                if (somePiece != null) {   
                    sendToUI(new Coordinate(somePiece.getX(),somePiece.getY()));
                    //if killList = NULL, the move has failed! 
                    if(killList != null) {
                        if(switcher == true) somePiece.setColor(Color.BLACK);
                        else somePiece.setColor(Color.WHITE);
                        switcher = !switcher;
                        somePiece.setVisible(true);
                        if(killList.size() != 0) {    //here is how we tell if a move is legal and no pieces die. 
                            ListIterator<Coordinate> iter = killList.listIterator();
                            while(iter.hasNext()) {
                                Coordinate cur = iter.next();
                                JPiece tmp = pieces[cur.getY()][cur.getX()];   // this is backwards....
                                tmp.setVisible(false);
                            }//end while
                        }//end if.
                    clickedPieces.add(somePiece);
                   
                    repaint();
                    }//end if
                    
                }       
            }
        });
        

        
                
    }//end constructor
    
    //A method so that pieces are drawn in sync on each side of the server. 
    public void refresh() {
        if(killList != null) {
            if(killList.size() != 0) {    //here is how we tell if a move is legal and no pieces die. 
                ListIterator<Coordinate> iter = killList.listIterator();
                while(iter.hasNext()) {
                    Coordinate cur = iter.next();
                    JPiece tmp = pieces[cur.getY()][cur.getX()];   // this is backwards....
                    tmp.setVisible(false);
                }//end while
            }//end if.
            
        }
        repaint();
    }
    public Dimension getDim() {
        return dim;
    }
    private void sendToUI(Object message) {
        ui.sendToClient(message);
    }
    private JPiece getPiece(Point mousePt) {
        //int rowNum =(( ((int)(mousePt.getX())) / dx)  );
        //int colNum =(( ((int)(mousePt.getY())) / dy)  );
        double colNumD = ( (mousePt.getX() - topLeft.getX() ) / dx);
        double rowNumD = (  (mousePt.getY() - topLeft.getY() ) / dy);
        int colNumI = ( ((int)(mousePt.getX() - topLeft.getX() )) / dx);
        int rowNumI = (  ((int)(mousePt.getY() - topLeft.getY() )) / dy);
        if ((rowNumD  - rowNumI) > .5) rowNumI ++;
        if (( colNumD - colNumI) > .5) colNumI ++;
        //JOptionPane.showMessageDialog(null,"Clicked");
        try {
            return pieces[rowNumI][colNumI];
        }
        catch(ArrayIndexOutOfBoundsException e) {
            return null;
        }
    }
    public void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        img.paintIcon(this, g, 25, 25);
        this.draw(g2);
        
    }
    public void setKillList(LinkedList<Coordinate> killList) {
        this.killList = killList;
    }
    
    //When this method is called it puts the game in score mode. The 2 linked list parameters are the adjacencies that will need to be drawn.
    public void setScoring(LinkedList<Coordinate> player1Adjacencies, Color p1AdjColor, LinkedList<Coordinate> player2Adjacencies, Color p2AdjColor)
    {
	scoreMode = true;
        this.p1Adj = player1Adjacencies;
        this.p2Adj = player2Adjacencies;
        this.p1AdjColor = p1AdjColor;
        this.p2AdjColor = p2AdjColor;
        repaint();
    }
    
    //A method that can be called to draw a single piece. This is used for the small grey circle drawn on the last piece played. 
    public void drawAPiece(Coordinate cord) //**
    {
         JSquare tmp = squares[cord.getY()][cord.getX()];
         lastMove = new JPiece(tmp.getWidth()*3,Color.GRAY,tmp.getXpixel()-tmp.getWidth(),tmp.getYpixel() - tmp.getWidth(),tmp.getY(),tmp.getX());
         repaint();
    }
   
    //sets the circle list. This is used in scoring to help identify clicked peices. It is the list of yellow circles. 
    public void setCircleList(LinkedList<Coordinate> list)   //**
   {
       circleList = list;
       //may need to call repaint here if it isnt doing it right away. 
   }
    //The draw method. This method draws the grid. It is a long method. 
    private void draw(Graphics2D g2){
//        //create the 4 corner points of the board, based on this.size
//        Point2D topLeft = new Point2D.Double(offset, offset); //this is point (0,0) 
//        Point2D topRight = new Point2D.Double(offset + width, offset);
//        Point2D bottomLeft = new Point2D.Double(offset, offset + height);
//        Point2D bottomRight = new Point2D.Double(offset + width, offset + height); 
        //make the outside lines
        Line2D top = new Line2D.Double(topLeft, topRight);
        Line2D bottom = new Line2D.Double(bottomLeft, bottomRight);
        Line2D left = new Line2D.Double(topLeft, bottomLeft);
        Line2D right = new Line2D.Double(topRight, bottomRight);
        g2.setStroke(new BasicStroke(3));
        g2.draw(top);
        g2.draw(bottom);
        g2.draw(left);
        g2.draw(right);
        g2.setStroke(new BasicStroke(1));
        //this was part of making it dynamically change
        //int dx = (this.getWidth() - 2*offset) / size;
        //int dy = (this.getHeight() -2*offset) / size;
        Point2D topStart = new Point2D.Double(topLeft.getX() + dx, topLeft.getY());
        Point2D bottomStart = new Point2D.Double(bottomLeft.getX() + dx, bottomLeft.getY());
        Point2D leftStart = new Point2D.Double(topLeft.getX(), topLeft.getY() + dy);
        Point2D rightStart = new Point2D.Double(topRight.getX(), topRight.getY() + dy);
        for(int i = 1; i <= size -2; i++) {
            Line2D line = new Line2D.Double(topStart, bottomStart);
            Line2D line2 = new Line2D.Double(leftStart, rightStart);
            g2.draw(line);
            g2.draw(line2);
            //advance to points
            topStart = new Point2D.Double(topStart.getX()+dx, topStart.getY());
            bottomStart = new Point2D.Double(bottomStart.getX() + dx, bottomStart.getY());
            leftStart = new Point2D.Double(leftStart.getX(), leftStart.getY() + dy);
            rightStart = new Point2D.Double(rightStart.getX(), rightStart.getY() + dy);  
        }
        if ((clickedPieces != null) && (clickedPieces.size() != 0 )) {  //this code draws the visible pieces if there are any. 
            Iterator iter = clickedPieces.iterator();
            while(iter.hasNext()) {
                JPiece temp = (JPiece)iter.next();
                if(temp.isVisible())              //if it is visible, draw it. 
                    temp.draw((Graphics) g2);
                else iter.remove();              //else it is not visible, remove it from the list. 
            }
            
        }
        
        //draw the last played Piece.
        if(lastMove !=null) //**
           lastMove.draw((Graphics)g2); //**
        //draw the circle list
        if(circleList != null)  //**
        {
            if (circleList.size() != 0)
            {
                Iterator iter = circleList.iterator();
                Color oldColor = g2.getColor();
                g2.setColor(Color.YELLOW);
                while(iter.hasNext())
                {
                    Coordinate temp = (Coordinate)iter.next();
                    JSquare mySquare = squares[temp.getY()][temp.getX()];
                    //JPiece newPiece = new JPiece(mySquare.getWidth()*3,Color.YELLOW,mySquare.getXpixel()-mySquare.getWidth(),mySquare.getYpixel()-mySquare.getHeight(),mySquare.getY(),mySquare.getX());
                    Ellipse2D.Double piece = new Ellipse2D.Double(mySquare.getXpixel()-mySquare.getWidth(),mySquare.getYpixel()-mySquare.getHeight(), mySquare.getWidth()*3,mySquare.getWidth()*3);
                    //newPiece.draw((Graphics) g2); 
                    g2.fill(piece);
                }//end while
                g2.setColor(oldColor); //puts the old color back. 
            }
            
        }//end if **
        

        
        //Code that Draws Adjacencies
        if(scoreMode)
        {
            if(p1Adj != null)
            {
                Iterator iter = p1Adj.iterator();
                while(iter.hasNext())
                {
                    Coordinate cord = (Coordinate)iter.next();
                    squares[cord.getY()][cord.getX()].setColor(p1AdjColor);
                    squares[cord.getY()][cord.getX()].draw(g2);
                }//end while
            }//end if
                
            if(p2Adj != null)
            {
                Iterator iter = p2Adj.iterator();
                while(iter.hasNext())
                {
                    Coordinate cord = (Coordinate)iter.next();
                    squares[cord.getY()][cord.getX()].setColor(p2AdjColor);
                    squares[cord.getY()][cord.getX()].draw(g2);
                }//end while
            }//end if
        }//end if scoremode
        //test scoremode
//        for(int i =0; i<19; i++)
//            for(int q = 0; q< 19; q++)
 //               squares[i][q].draw((Graphics)g2);
        
    }//end draw

    public void pieceAdd(Coordinate piece) {
        JPiece guy = pieces[piece.getY()][piece.getX()];
        guy.setVisible(true);
        if(switcher == true) guy.setColor(Color.BLACK);
        else guy.setColor(Color.WHITE);
        switcher = !switcher;
        clickedPieces.add(guy);
    }
    //the method that switches what color the piece is that has just been played adn needs to be drawn. 
    public void switchSwitcher() {
        switcher = !switcher;
    }
    
    public void LoadImageApp() {
        ClassLoader cldr = this.getClass().getClassLoader();
        //java.net.URL imageURL   = cldr.getResource("images/beard.jpg");
        //img = new ImageIcon(imageURL);
        img = new ImageIcon(getClass().getResource("images/beard.jpg"));
     //  img = new ImageIcon(ClassLoader.getSystemResource("images/beard.jpg"));
    }

    private Dimension dim;  //dimension of this
    private int size; // this is the variable size of the grid. 
    private int offset; //this is for the boarders.
    private JPiece[][] pieces; // 2D array to store the constructed pieces
    private int width; //width of this GridPanel
    private int height; //height of this Gridpanel
    private int dx; //distance between each verticle line of grid
    private int dy; //distance between each horizontal line of the grid
    Point2D topLeft; //topLeft corner of drawn grid.
    Point2D topRight;  //topRight corner of drawn grid.
    Point2D bottomLeft; // bottomLeft corner of drawn grid. 
    Point2D bottomRight; //bottomRight corner of drawn grid. 
    private LinkedList<JPiece> clickedPieces; // the pieces that has just been clicked and will be drawn. Visible Pieces. 
    private LinkedList<Coordinate> killList; //A list of pieces that die. 
    private BoardUI ui;  //the board
    private boolean switcher = true;     
    private LinkedList<Coordinate> p1Adj;  //list of player1's adjacencies
    private LinkedList<Coordinate> p2Adj; //list of player 2's adjacencies
    private boolean scoreMode; //scoremode on or off. 
    private Color p1AdjColor; //color to draw player 1's adjacencies'
    private Color p2AdjColor; //color to draw player 2's adjacencies'
    private JSquare[][] squares; // a 2-d array of the little square boxes used in score mode. 
    private LinkedList<Coordinate> circleList;  //circleList is used on scoremode. The yellow circles. 
    private JPiece lastMove; //The piece that was the last move. 
    
    private ImageIcon img;   //the picture of the wooden board. 
}

/*
 * BoardUI.java
 *
 * Created on November 6, 2007, 11:44 AM
 */
package go_client;
import com.sun.org.apache.bcel.internal.classfile.JavaClass;
import java.awt.*;
import java.awt.geom.*;
import java.net.URISyntaxException;
//import java.util.ArrayList;
//import java.util.Arrays;
import java.util.LinkedList;
import go.Coordinate;
import go.Message;
import java.io.*;
import go_server.GoServer;
import javax.swing.JScrollBar;
import javax.xml.ws.Response;
import javax.swing.JOptionPane;
import go.GameQuery;

/**
 * The Graphical User Interface for the user
 * @author Mike Jacobson, Caldwell Bailey
 */
public class BoardUI extends javax.swing.JFrame {
    /**
     * Creates new form BoardUI
     * @param size The size the grid is set to
     */
    public BoardUI(int size) {
        initComponents();
        
        //Dimension gridSize = new Dimension((int)(topPanel.getSize().getWidth() - movePanel.getSize().getWidth()), (int)(topPanel.getSize().getHeight()) );
        //Dimension gridSize = new Dimension((int)(topPanel.getSize().getWidth() - movePanel.getSize().getWidth()),(int)(topPanel.getSize().getWidth() - movePanel.getSize().getWidth()) );
        Dimension gridSize =  new Dimension(550,550);
        this.grid = new GridPanel(size,gridSize,this);
        topPanel.add(grid, 0);
        this.setVisible(true);
    }//end constructor
    
    /**
     * sets a new grid to be displayed
     * @param newGrid the new grid to be displayed
     */
    public void setGrid(GridPanel newGrid) {
        topPanel.remove(grid);
        this.grid = newGrid;
        topPanel.add(grid, 0);
        repaint();
    }  
    /**
     * adds the user's piece to a desired place on the board.
     * @param piece The position for the piece
     */
    public void addPiece(Coordinate piece) {
        grid.pieceAdd(piece);
    }
    /**
     * refreshes the boardUI
     */
    public void refresher() {
        grid.refresh();
    }
    /**
     * Enables the connect menu button and disables the disconnect.
     */
    public void clientDisconnected() {
        connectMenu.setEnabled(true);
        disconnectMenu.setEnabled(false);
    }
    
    /**
     * Displays the string parameter to a textbox.
     * @param input The string to be displayed
     */
    public void display(String input) {
        JScrollBar vbar = jScrollPane1.getVerticalScrollBar();
        boolean autoScroll = ((vbar.getValue() + vbar.getVisibleAmount()) == vbar.getMaximum());
        textLog.append(input + '\n');
        if( autoScroll ) textLog.setCaretPosition(textLog.getDocument().getLength());
    }
    
    private void sendText() {
        if(client != null)
        {
            if(!textToSend.getText().isEmpty())
            {
                Message text = new Message(playerName + ": " + textToSend.getText());
                client.handleInputFromClientGUI(text);   
                textToSend.setText("");
            }
        }
    }
    
    /**
     * Notifies the user that their opponent has passed.
     */
    public void hasPassed() {
        passedLabel.setText("Your opponent");
        passedLabel1.setText("has passed!");
    }
    /**
     * removes the label that notifies the opponents pass
     */
    public void hasNotPassed() {
        passedLabel.setText("");
        passedLabel1.setText("");
    }
    
    /**
     * Sets the user's name
     * @param name The new name for the user.
     */
    public void setPlayerName(String name) {
        playerName = name;
    }
    
    /**
     * returns the player's name
     * @return String
     */
    public String getPlayerName() {
        return playerName;
    }
    
    /**
     * Creates the server if it doesn't already exist
     * @param port The port used to construct the server
     */
    public void initServer(int port) {
        if(server == null)
            server = new GoServer(port);
    }
    /**
     * Creates the client used to connect 
     * to the server if it does not already exist.
     * @param host the host parameter used for the creation of the client
     * @param port The port used for the creation of the client
     */
    public void initClient(String host, int port) {
        try
        {
            client = new GoClient(host, port, this);
        }
        catch(IOException ioe)
        {
            display("IOException occured while trying to connect to the server");
        }
        disconnectMenu.setEnabled(true);
        connectMenu.setEnabled(false);
    }
    
    /**
     * Closes the client and sets it to null
     */
    public void endClient() {
        client = null;
        disconnectMenu.setEnabled(false);
        connectMenu.setEnabled(true); 
    }
    
    /**
     * Removes the pieces on the board located at the coordinates stored in the
     * linked list.
     * @param killList A list of pieces to remove from the board
     */
    public void kill(LinkedList<Coordinate> killList) {
        grid.setKillList(killList);
    }
    
    /**
     * gives an object to the client so it can determine what to do with it.
     * @param message The message to send
     */
    public void sendToClient(Object message) {
        if(client != null)
            client.handleInputFromClientGUI(message);
        else this.kill(null);
    }
    /**
     * Sets which player can perfom a move
     */
    public void switchTheSwitcher() {
        grid.switchSwitcher();
    }
    /**
     * sets the display of captures for the black player
     * @param caps the current amount of captures
     */
    public void setBlackCaps(int caps) {
        jLabel5.setText(""+caps);
    }
    /**
     * sets the display of captures for the white player
     * @param caps the amount of captures
     */
    public void setWhiteCaps(int caps) {
        jLabel6.setText(""+caps);
    }
    /**
     * Sets the label for whites territory
     * @param terr the number of territories
     */
    public void setWhiteTerr(int terr) {
        jLabel9.setText(""+terr);
    }
    /**
     * Sets the label for black territories
     * @param terr the number of territories
     */
    public void setBlackTerr(int terr) {
        jLabel8.setText(""+terr);
    }
    /**
     * Sets the label for white's score
     * @param score the new score
     */
    public void setWhiteScore(int score) {
        jLabel12.setText(""+score);
    }
    /**
     * Sets the label for black's score
     * @param score the new score
     */
    public void setBlackScore(double score) {
        jLabel11.setText(""+score);
    }
     //Any coordinate in the linked lists will get drawn with the provided color. 
    //Calling this method will immediately redraw the grid with the adjacencies drawn. 
    /**
     * Begins the scoring of the game
     */
    public void score(LinkedList<Coordinate> player1Adjacencies, Color p1AdjColor, LinkedList<Coordinate> player2Adjacencies, Color p2AdjColor)
    {
        grid.setScoring(player1Adjacencies, p1AdjColor, player2Adjacencies, p2AdjColor);
    }
    
    /**
     * Draws the piece on the board
     * @param cord the cordinate to draw the piece at
     */
    public void drawAPiece(Coordinate cord) //**
    {
        grid.drawAPiece(cord);
    }
//note: This method does not immediately draw the paramerter cord. 
 //Next time the board is re-drawn it will get drawn. 
    /**
     * Sets a list of pieces to be drawn on the board
     * @param cord a list of pieces
     */
   public void drawList(LinkedList<Coordinate> cord) //**
   {
       grid.setCircleList(cord);
       
   }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc=" Generated Code ">//GEN-BEGIN:initComponents
    private void initComponents() {
        topPanel = new javax.swing.JPanel();
        movePanel = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        passedLabel = new javax.swing.JLabel();
        passedLabel1 = new javax.swing.JLabel();
        bottomPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        textLog = new javax.swing.JTextArea();
        buttonSend = new javax.swing.JButton();
        textToSend = new javax.swing.JTextField();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        connectMenu = new javax.swing.JMenuItem();
        disconnectMenu = new javax.swing.JMenuItem();
        newGameMenu = new javax.swing.JMenuItem();
        exitGameMenu = new javax.swing.JMenuItem();
        helpMenu = new javax.swing.JMenu();
        howToPlayItem = new javax.swing.JMenuItem();
        aboutMenuItem = new javax.swing.JMenuItem();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("GO!");
        setMinimumSize(new java.awt.Dimension(700, 700));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        topPanel.setLayout(new javax.swing.BoxLayout(topPanel, javax.swing.BoxLayout.X_AXIS));

        topPanel.setFocusable(false);
        topPanel.setMinimumSize(new java.awt.Dimension(400, 300));
        topPanel.setPreferredSize(new java.awt.Dimension(400, 300));
        topPanel.setRequestFocusEnabled(false);
        movePanel.setMaximumSize(new java.awt.Dimension(125, 32767));
        movePanel.setMinimumSize(new java.awt.Dimension(100, 300));
        movePanel.setPreferredSize(new java.awt.Dimension(125, 300));
        jButton1.setText("Pass");
        jButton1.setFocusable(false);
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Forfeit");
        jButton2.setFocusPainted(false);
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel2.setText("Caps:");

        jLabel3.setText("B");

        jLabel4.setText("W");

        jLabel5.setText("0");

        jLabel6.setText("0");

        jLabel7.setText("Terr:");

        jLabel8.setText("0");

        jLabel9.setText("0");

        jLabel10.setText("Score:");

        jLabel11.setText("0");

        jLabel12.setText("0");

        passedLabel.setFont(new java.awt.Font("Tahoma", 1, 11));
        passedLabel.setForeground(new java.awt.Color(255, 0, 0));

        passedLabel1.setFont(new java.awt.Font("Tahoma", 1, 11));
        passedLabel1.setForeground(new java.awt.Color(255, 0, 0));

        org.jdesktop.layout.GroupLayout movePanelLayout = new org.jdesktop.layout.GroupLayout(movePanel);
        movePanel.setLayout(movePanelLayout);
        movePanelLayout.setHorizontalGroup(
            movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(movePanelLayout.createSequentialGroup()
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.TRAILING)
                    .add(jLabel10)
                    .add(jLabel2)
                    .add(jLabel7))
                .add(16, 16, 16)
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel3)
                    .add(jLabel5)
                    .add(jLabel8)
                    .add(jLabel11))
                .add(18, 18, 18)
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jLabel12)
                    .add(jLabel9)
                    .add(jLabel6)
                    .add(jLabel4))
                .add(44, 44, 44))
            .add(movePanelLayout.createSequentialGroup()
                .add(40, 40, 40)
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jButton1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                    .add(jButton2, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE))
                .addContainerGap())
            .add(org.jdesktop.layout.GroupLayout.TRAILING, movePanelLayout.createSequentialGroup()
                .add(40, 40, 40)
                .add(passedLabel1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 75, Short.MAX_VALUE)
                .addContainerGap())
            .add(movePanelLayout.createSequentialGroup()
                .add(29, 29, 29)
                .add(passedLabel, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE)
                .addContainerGap())
        );
        movePanelLayout.setVerticalGroup(
            movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(movePanelLayout.createSequentialGroup()
                .add(28, 28, 28)
                .add(passedLabel, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 10, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(passedLabel1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 9, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(jButton1)
                .add(20, 20, 20)
                .add(jButton2)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, 29, Short.MAX_VALUE)
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel3)
                    .add(jLabel4))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel5)
                    .add(jLabel6)
                    .add(jLabel2))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel7)
                    .add(jLabel8)
                    .add(jLabel9))
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                .add(movePanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(jLabel10)
                    .add(jLabel11)
                    .add(jLabel12))
                .add(373, 373, 373))
        );
        topPanel.add(movePanel);

        getContentPane().add(topPanel, java.awt.BorderLayout.CENTER);

        bottomPanel.setMinimumSize(new java.awt.Dimension(400, 100));
        bottomPanel.setPreferredSize(new java.awt.Dimension(400, 100));
        textLog.setColumns(20);
        textLog.setEditable(false);
        textLog.setLineWrap(true);
        textLog.setWrapStyleWord(true);
        jScrollPane1.setViewportView(textLog);

        buttonSend.setText("Send");
        buttonSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                buttonSendActionPerformed(evt);
            }
        });

        textToSend.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                textToSendKeyPressed(evt);
            }
        });

        org.jdesktop.layout.GroupLayout bottomPanelLayout = new org.jdesktop.layout.GroupLayout(bottomPanel);
        bottomPanel.setLayout(bottomPanelLayout);
        bottomPanelLayout.setHorizontalGroup(
            bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(bottomPanelLayout.createSequentialGroup()
                .addContainerGap()
                .add(bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
                    .add(jScrollPane1, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, 447, Short.MAX_VALUE)
                    .add(bottomPanelLayout.createSequentialGroup()
                        .add(textToSend, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 351, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED)
                        .add(buttonSend)))
                .addContainerGap())
        );
        bottomPanelLayout.setVerticalGroup(
            bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(org.jdesktop.layout.GroupLayout.TRAILING, bottomPanelLayout.createSequentialGroup()
                .add(jScrollPane1, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, 60, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(org.jdesktop.layout.LayoutStyle.RELATED, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .add(bottomPanelLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.BASELINE)
                    .add(textToSend, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.PREFERRED_SIZE)
                    .add(buttonSend))
                .addContainerGap())
        );
        getContentPane().add(bottomPanel, java.awt.BorderLayout.SOUTH);

        fileMenu.setText("File");
        connectMenu.setText("Connect");
        connectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectMenuActionPerformed(evt);
            }
        });

        fileMenu.add(connectMenu);

        disconnectMenu.setText("Disconnect/Close Server");
        disconnectMenu.setEnabled(false);
        disconnectMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectMenuActionPerformed(evt);
            }
        });

        fileMenu.add(disconnectMenu);

        newGameMenu.setText("New Game");
        newGameMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newGameMenuActionPerformed(evt);
            }
        });

        fileMenu.add(newGameMenu);

        exitGameMenu.setText("Exit");
        exitGameMenu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitGameMenuActionPerformed(evt);
            }
        });

        fileMenu.add(exitGameMenu);

        jMenuBar1.add(fileMenu);

        helpMenu.setText("Help");
        howToPlayItem.setText("How to Play Go");
        howToPlayItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                howToPlayItemActionPerformed(evt);
            }
        });

        helpMenu.add(howToPlayItem);

        aboutMenuItem.setText("About");
        aboutMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aboutMenuItemActionPerformed(evt);
            }
        });

        helpMenu.add(aboutMenuItem);

        jMenuBar1.add(helpMenu);

        setJMenuBar(jMenuBar1);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void aboutMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aboutMenuItemActionPerformed
        JOptionPane.showMessageDialog(null,"This game was created by Mike Jacobson," + '\n' +
                                           "Kevin Higgins, and Caldwell Bailey." + '\n' + 
                                           "December 2007","About",JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_aboutMenuItemActionPerformed

    private void howToPlayItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_howToPlayItemActionPerformed
        try
        {
            java.awt.Desktop.getDesktop().browse(new java.net.URI("http://playgo.to/interactive/"));
        }
        catch(IOException e)
        {
            display("***Cannot launch your web browser.");
        }
        catch(URISyntaxException a)
        {
            display("***Webpage not found.");
        }
    }//GEN-LAST:event_howToPlayItemActionPerformed

    
    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        if(client != null)
            if(client.inGame())
                if(promptUser("Are you sure you wan't to forfeit?"))
                {   display("***You have forfeited the game to your opponent.");
                    sendToClient(new GameQuery(0));
                    client.endGame();
                }

    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
        server.quit();
	client.endGame();
    }//GEN-LAST:event_formWindowClosed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        if(client != null) {
            Coordinate tmp = new Coordinate(1000,1000);
            client.handleInputFromClientGUI(tmp);
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void disconnectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectMenuActionPerformed
     try
        {
            client.closeConnection();
            client = null;
            if(server != null)
            {
                server.quit();
                server = null;
            }
        }
    catch(IOException e)
        {
            display("***IOException occured");
        }
        finally
        {
            connectMenu.setEnabled(true);
            disconnectMenu.setEnabled(false);
        }
    }//GEN-LAST:event_disconnectMenuActionPerformed

    private void textToSendKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textToSendKeyPressed
        if(evt.getKeyCode() == evt.VK_ENTER) {
            sendText();
        }
    }//GEN-LAST:event_textToSendKeyPressed
    
    private void buttonSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_buttonSendActionPerformed
        sendText();
    }//GEN-LAST:event_buttonSendActionPerformed

    private void connectMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectMenuActionPerformed
        ConnectFrame frame = new ConnectFrame();
        frame.setLocationRelativeTo(this);
        frame.setBoardUI(this);
    }//GEN-LAST:event_connectMenuActionPerformed

    private void exitGameMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitGameMenuActionPerformed
        if(server != null)
            server.quit();
        System.exit(1);
    }//GEN-LAST:event_exitGameMenuActionPerformed

    private void newGameMenuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newGameMenuActionPerformed
    if(client != null)
    {
        NewGameFrame frame =  new NewGameFrame();
        frame.setLocationRelativeTo(this);
        frame.setBoardUI(this);
    }
    else
        display("**You cannot create a new game because you aren't connected to a server");
    }//GEN-LAST:event_newGameMenuActionPerformed

    /**
     * Gets the current <B>GridPanel</B>
     * @return GridPanel
     */
    public GridPanel getGrid() {
        return grid;
    }
    
    /**
     * gets the client's IP address in String form
     * @return String
     */
    public String getInetAddress() {
        //System.out.println(client.getInetAddress().getHostAddress());
        return (client.getInetAddress().toString());
    }
    
    /**
     * Asks the user a yes or no question
     * @param prompt The question to ask
     * @return String
     */
    public boolean promptUser(String prompt) {
        boolean response = true;
        if(JOptionPane.showConfirmDialog(null,prompt,null,JOptionPane.YES_NO_OPTION) == JOptionPane.NO_OPTION)
            response = false;
        return response;
    }

    /**
     * removes the passed labels
     */
    public void clearLabels() {
        passedLabel.setText("");
        passedLabel1.setText("");
    }
    
    private static GridPanel grid;
    private GoClient client;
    private GoServer server;
    private String playerName;
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem aboutMenuItem;
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton buttonSend;
    private javax.swing.JMenuItem connectMenu;
    private javax.swing.JMenuItem disconnectMenu;
    private javax.swing.JMenuItem exitGameMenu;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu helpMenu;
    private javax.swing.JMenuItem howToPlayItem;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JPanel movePanel;
    private javax.swing.JMenuItem newGameMenu;
    private javax.swing.JLabel passedLabel;
    private javax.swing.JLabel passedLabel1;
    private javax.swing.JTextArea textLog;
    private javax.swing.JTextField textToSend;
    private javax.swing.JPanel topPanel;
    // End of variables declaration//GEN-END:variables
     
    /**
     * the main function
     */
    public static void main(String[] args) {
        BoardUI board = new BoardUI(19);  //Here we pass in the size we want the grid to be. It will default at 19. 
    }
}
    
    
    
    


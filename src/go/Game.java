/*
 * Game.java
 *
 * Created on November 21, 2007, 11:04 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package go;
import java.util.*;
/**
 *
 * @author Kevin Higgins
 */
 
/**
 * Control class for managing
 * the current game. Contains
 * game components and processes
 * incoming data/moves.
 */
public class Game {
    private boolean hasPassed = false;
    private Boolean foundBlack;
    private boolean scoreMode = false;
    private boolean teamIsBlack;
    private boolean turnBlack = true;
    private Boolean[][] board;
    private Boolean[][] prevBoard;
    private Boolean[][] prevPrevBoard;
    private Boolean[][] path;
    private Boolean[][] scorePath;
    private Boolean[][] scoreBoard;
    private int scoreBlack = 0;
    private int scoreWhite = 0;
    private int capturesByWhite = 0;
    private int capturesByBlack = 0;
    private LinkedList<Coordinate> deads;
    private LinkedList<Coordinate> tmpDeads;
    private LinkedList<Coordinate> whiteTerritory;
    private LinkedList<Coordinate> blackTerritory;
    private LinkedList<Coordinate> tmpTerritory;
    private LinkedList<Coordinate> clickDead;
    private Boolean[][] contingency;
    /** Creates a new instance of Game */
    public Game(int size, boolean isBlack) {
        teamIsBlack = isBlack;
        board = new Boolean[size][size];
        //possibly remove this
        for(int i = 0; i < size; i++)
        {
            for(int j = 0; j < size; j++)
                board[i][j] = null;
        }             
        prevBoard = boardCopy();
        prevPrevBoard = boardCopy();
        path = new Boolean[board.length][board.length];
        
    }
    /**
     * Processes an incoming move and
     * performs the actions required
     * of it. Also returns move success. Null is move failure.
     * @param message to perform move with.
     * @param isBlack the team color who is making the move.
     * @return whether or not the move was successful.
     */
    public LinkedList<Coordinate> processMove(Object message, boolean isBlack) {
        Coordinate coord = (Coordinate)message;
        
        int x = coord.getX();
        int y = coord.getY();
        if(isBlack == turnBlack) {
            if( board[x][y] == null) {
                contingency = boardCopy();
                pathReset();
                deads = new LinkedList();
                board[x][y] = isBlack;
                deadCheck(x,y,isBlack);
                if(deads != null) {
                    hasPassed = false;
                    
                    if(deads.size() != 0)
                        captureCount();
                    //fortesting
  //                  scoreGame();
//                    attemptScore();
                    turnBlack = !turnBlack;
                }
                return deads;
            }
        }
        return null;
    }   //might be giving some null errors.
    //Method used to switch whose turn it is. Primarily used for turn passing.
    public void switchTurn() {
        turnBlack = !turnBlack;
    }
    //Checks to see if the last player passed. This is because 2 passes in a row throws the game into score mode.
    public boolean getHasPassed() {
        return hasPassed;
    }
    //Sets the pass variable to true, signifying that the player has passed on their turn.
    public void setHasPassed() {
        hasPassed = true;
    }
 

    //Returns the game owners team color

    public boolean isBlack() {
        return teamIsBlack;
}
       //Returns the boolean representing if it isBlack's turn.
    public boolean blacksTurn() {
        return turnBlack;
    }
    public int getWhiteScore() {
        return scoreWhite;
    }
    public int getBlackScore() {
        return scoreBlack;
    }
    public int getWhiteCaptures() {
        return capturesByWhite;
    }
    public int getBlackCaptures() {
        return capturesByBlack;
    }
    public LinkedList<Coordinate> getWhiteTerritory() {
        return whiteTerritory;
    }
    public LinkedList<Coordinate> getBlackTerritory() {
        return blackTerritory;
    }
    public boolean isScoreMode() {
        return scoreMode;
    }
    
    //resets the path array
    private void pathReset() {
        for(int i = 0; i < path.length; i++)
                for(int j = 0; j < path.length; j++)
                    path[i][j] = false;
    }

    private void scorePathReset() {
        for(int i = 0; i < scorePath.length; i++)
                for(int j = 0; j < scorePath.length; j++)
                    scorePath[i][j] = false;
    }
    //Recursive calls a recursive method on the 4 adjacencies of the given x,y location
    private void deadCheck(int x, int y, boolean isBlack) {
        tmpDeads = new LinkedList();
        if(x != 0)
            if(board[x-1][y] != null)
                if(board[x-1][y] != isBlack)
                    if(isDead(x-1,y,!isBlack))
                        deads.addAll(tmpDeads);
        cleanDead();
        pathReset();
        tmpDeads = new LinkedList();
        if(y != 0)
            if(board[x][y-1] != null)
                if(board[x][y-1] != isBlack)
                    if(isDead(x,y-1,!isBlack))
                        deads.addAll(tmpDeads);
        cleanDead();
        pathReset();
        tmpDeads = new LinkedList();
        if(x != board.length-1)
            if(board[x+1][y] != null)
                if(board[x+1][y] != isBlack)
                    if(isDead(x+1,y,!isBlack))
                        deads.addAll(tmpDeads);
        cleanDead();
        pathReset();
       tmpDeads = new LinkedList();
        if(y != board.length-1)
            if(board[x][y+1] != null)
                if(board[x][y+1] != isBlack)
                    if(isDead(x,y+1,!isBlack))
                        deads.addAll(tmpDeads);         
        cleanDead();
        pathReset();
        tmpDeads = new LinkedList();
        if(isDead(x,y,isBlack))
            deads.addAll(tmpDeads);
        if(!isLegal(isBlack)) {
            deads = null;
            board[x][y] = null;
        }
        else cleanDead();
        if(isKo()) {
            board = contingency;
            deads = null;
        }
        else {
            prevPrevBoard = prevBoard;
            prevBoard = boardCopy();

        }
    }
    //Makes a copy of the board array because I didn't want to spend time getting clone working :)
    private Boolean[][] boardCopy() {
        Boolean[][] tmpCopy = new Boolean[board.length][board.length];
        for(int i =0; i<board.length;i++) {
            for(int j=0; j<board.length;j++) {
                tmpCopy[i][j] = board[i][j];
            }
        }
        return tmpCopy;
    }
    //Fun recursive function to essentially map through pieces neighboring the given piece and see if they are dead
    private boolean isDead(int x, int y, boolean isBlack) {
        
        path[x][y] = true;
        tmpDeads.add(new Coordinate(x,y));
        
        if(x != 0) {
            if(board[x-1][y] == null) 
                return false;
            else if(board[x-1][y] == isBlack)
                if(path[x-1][y] == false) 
                    if(!isDead(x-1,y,isBlack))
                        return false;        
        }
        if(y != 0) {
            if(board[x][y-1] == null) 
                return false;
            else if(board[x][y-1] == isBlack)
                if(path[x][y-1] == false) 
                    if(!isDead(x,y-1,isBlack)) return false;
        }      
        if(x != board.length-1) {
            if(board[x+1][y] == null) 
                return false;
            else if(board[x+1][y] == isBlack)
                if(path[x+1][y] == false) 
                    if(!isDead(x+1,y,isBlack)) return false;
        }     
        if(y != board.length-1) {
            if(board[x][y+1] == null) 
                return false;
            else if(board[x][y+1] == isBlack)
                if(path[x][y+1] == false) 
                    if(!isDead(x,y+1,isBlack)) return false;              
        }       
        return true;
    }
    //Cleans the dead pieces from the board array.
    private void cleanDead() {
        ListIterator<Coordinate> itty = deads.listIterator();
        while(itty.hasNext()) {
            Coordinate cur = itty.next();
            board[cur.getX()][cur.getY()] = null;
        }
    }
     // Checks some legal aspects of a move. This makes it so players can't play into
    // a spot that will only kill their piece and nothing else.'
    private boolean isLegal(boolean pieceIsBlack) {
        if(deads.size() == 0)
            return true;
        ListIterator<Coordinate> itty = deads.listIterator();
        while(itty.hasNext()) {
            Coordinate cur = itty.next();
            if(board[cur.getX()][cur.getY()] == null)
                return true;
        }
        return false;
    }
    
    /**
     * Checks to see if Ko has occurred.
     * @return if Ko has been reached.
     */
    private boolean isKo() {
        for(int i = 0; i<prevPrevBoard.length;i++) {
            for(int j = 0; j<prevPrevBoard.length;j++) {
                
                if(board[i][j] == null)
                    if(prevPrevBoard[i][j] != null)
                        return false;
                if(prevPrevBoard[i][j] == null)
                    if(board[i][j] != null)
                        return false;
                if(board[i][j] != prevPrevBoard[i][j])
                    return false;
            }
        }
        return true;
    }
    //Checks to see how many pieces have been captured by the teams.
    // or rather, it keeps the capture counts updated.
    private void captureCount() {
        ListIterator<Coordinate> countz = deads.listIterator();
        Coordinate tmp;
        while(countz.hasNext()) {
            tmp = countz.next();
            if(turnBlack)
                capturesByBlack++;
            else capturesByWhite++;
        }
    }
    // public method to initiate score mode.
    public void scoreGame() {  
        scoreMode = true;
        scorePath = new Boolean[path.length][path.length];
        scoreBoard = boardCopy();       
        clickDead = new LinkedList();
 //       attemptScore();
    }
    // Estimates the current score. This will count only
    // territory that has no enemy  pieces in it.
    // to accurately score the game, the players democratically
    // remove pieces that are counted as dead, and the game
    // is dynamically updated on the fly in regards to score.
    public void attemptScore() {
        scorePathReset();
        whiteTerritory = new LinkedList();
        blackTerritory = new LinkedList();
        for(int x=0;x<scoreBoard.length;x++) {
            for(int y=0;y<scoreBoard.length;y++) {
                if(scoreBoard[x][y] == null) 
                    if(scorePath[x][y] == false) {
                        foundBlack = null;
                        tmpTerritory = new LinkedList();
                        if(isTerritory(x,y)) {
                            if(foundBlack != null){
                                if(foundBlack) blackTerritory.addAll(tmpTerritory);
                                else whiteTerritory.addAll(tmpTerritory);
                            }
                        }
                    }
            }
        }
        scoreWhite = whiteTerritory.size();
        scoreBlack = blackTerritory.size();
    }
    //Checks to see if the given spot is empty territory that is
    //surrounded only by one color of piece, and/or wall
    private boolean isTerritory(int x, int y) {
        boolean isGood = true;
        scorePath[x][y] = true;
        tmpTerritory.add(new Coordinate(x,y));
        if(x != 0)
            if(scorePath[x-1][y] == false)
                if(scoreBoard[x-1][y] == null) {
                    if(!isTerritory(x-1,y))
                        isGood = false;
                }
                else if(foundBlack == null) // might cause problem
                    foundBlack = scoreBoard[x-1][y];
                else if(foundBlack != scoreBoard[x-1][y])
                    isGood = false;
        if(y != 0)
            if(scorePath[x][y-1] == false)
                if(scoreBoard[x][y-1] == null) {
                    if(!isTerritory(x,y-1))
                        isGood = false;
                }
                else if(foundBlack == null) // might cause problem
                    foundBlack = scoreBoard[x][y-1];
                else if(foundBlack != scoreBoard[x][y-1])
                    isGood = false;
        if(x != scoreBoard.length-1)
            if(scorePath[x+1][y] == false)
                if(scoreBoard[x+1][y] == null) {
                    if(!isTerritory(x+1,y))
                        isGood = false;
                }
                else if(foundBlack == null) // might cause problem
                    foundBlack = scoreBoard[x+1][y];
                else if(foundBlack != scoreBoard[x+1][y])
                    isGood = false;
        if(y != scoreBoard.length-1)
            if(scorePath[x][y+1] == false)
                if(scoreBoard[x][y+1] == null) {
                    if(!isTerritory(x,y+1))
                        isGood = false;
                }
                else if(foundBlack == null) // might cause problem
                    foundBlack = scoreBoard[x][y+1];
                else if(foundBlack != scoreBoard[x][y+1])
                    isGood = false;
        return isGood;
    }

    //In score mode, ths is the function that lets players kill
    // the dead pieces so as to correctly score the game.
    public LinkedList<Coordinate> scoreKill(int x, int y) {
        if(scoreBoard[x][y] != null) 
            scorePlague(x,y);
        else if(board[x][y] != null) {
            pathReset();
            scoreUnPlague(x,y);   
        }
        return clickDead;
    }
    //When a player clicks a piece that is alive, this method
    //is entered. it recursively checks all pieces of the same
    //color in a group, adds them to a list and kills them.
    // this is done on a separate board though.
    private void scorePlague(int x, int y) {
        boolean wuz = scoreBoard[x][y];
        scoreBoard[x][y] = null;
        clickDead.add(new Coordinate(x,y));
        if(wuz)
            capturesByWhite++;
        else capturesByBlack++;
        if(x != 0)
            if(scoreBoard[x-1][y] != null)
                if(wuz == scoreBoard[x-1][y])
                    scorePlague(x-1,y);
        if(y != 0)
            if(scoreBoard[x][y-1] != null)
                if(wuz == scoreBoard[x][y-1])
                    scorePlague(x,y-1);
        if(x != scoreBoard.length-1)
            if(scoreBoard[x+1][y] != null)
                if(wuz == scoreBoard[x+1][y])
                    scorePlague(x+1,y);
        if(y != scoreBoard.length-1)
            if(scoreBoard[x][y+1] != null)
                if(wuz == scoreBoard[x][y+1])
                    scorePlague(x,y+1);
        
    }
    //If the clicked piece is dead, it checks to see
    //if it was dead when the game ended. if not,
    //it is brought back to life, along with all pieces
    //in the same group as it.
    private void scoreUnPlague(int x, int y) {
        scoreBoard[x][y] = board[x][y].booleanValue();
        removeRez(x,y);
        if(scoreBoard[x][y])
            capturesByWhite--;
        else capturesByBlack--;
        path[x][y] = true;
        if(x != 0)
            if(board[x-1][y] != null)
                if(path[x-1][y] == false)
                    if(board[x][y] == board[x-1][y])
                        scoreUnPlague(x-1,y);
        if(y != 0)
            if(board[x][y-1] != null)
                if(path[x][y-1] == false)
                    if(board[x][y] == board[x][y-1])
                        scoreUnPlague(x,y-1);
        if(x != board.length-1)
            if(board[x+1][y] != null)
                if(path[x+1][y] == false)
                    if(board[x][y] == board[x+1][y])
                        scoreUnPlague(x+1,y);
        if(y != board.length-1)
            if(board[x][y+1] != null)
                if(path[x][y+1] == false)
                    if(board[x][y] == board[x][y+1])
                        scoreUnPlague(x,y+1);
    }
    //Removes guys from the deadlist
    private void removeRez(int x, int y) {
        ListIterator<Coordinate> itty = clickDead.listIterator();
        Coordinate tmp;
        while(itty.hasNext()) {
            tmp = itty.next();
            if(tmp.getX()== x && tmp.getY() == y) {
                itty.remove();
            }
        }
    }
}

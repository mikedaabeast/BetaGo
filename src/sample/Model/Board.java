package sample.Model;
import java.util.*;

import javafx.scene.paint.Color;
import sample.Model.Utility.Pair;

public class Board {

    private Stone[][] board;

    public Board(int size) {
        board = new Stone[size][size];
        //place empty stones on every position on the board
    }

    public boolean isValidMove(int row, int col, Color color) {
        if(!isValidLocation(row, col) || board[row][col] != null)
            return false;

        boolean isValidMove = false;
        placeStoneOnBoard(row, col, color);

        if(Stone.getNumLiberties(board[row][col], new HashSet<>()) > 0)
            isValidMove = true;
        else {
            Queue<Stone> q1 = new LinkedList<>();
            Queue<Stone> q2 = new LinkedList<>();
            Set<Stone> visited = new HashSet<>();

            Stone newStone = board[row][col];
            q1.add(newStone);
            visited.add(newStone);

            while (!q1.isEmpty()) {
                Stone stone = q1.remove();
                for (Stone adjacentStone : stone.getAdjacentStones()) {
                    if (adjacentStone.getColor() != stone.getColor())
                        q2.add(adjacentStone);
                    else if (!visited.contains(adjacentStone)) {
                        q1.add(adjacentStone);
                        visited.add(adjacentStone);
                    }
                }
            }

            int numLiberties = 0;
            for (Stone s : q2)
                numLiberties += Stone.getNumLiberties(s, new HashSet<>());

            if (numLiberties == 0)
                isValidMove = true;

            for (int i = 0; i < board.length; i++)                      // check if stone captured
                for (int j = 0; j < board.length; j++)
                    if(board[i][j] != null && board[i][j].getColor() != color && Stone.getNumLiberties(board[i][j], new HashSet<>()) == 0)
                        isValidMove = true;
        }

        removeStoneFromBoard(row, col);         // remove stone so board state not modified by isValidMove() method
        return isValidMove;
    }

    public int captureStones(Color currPlayer) {
        List<Pair<Integer, Integer>> stonesCaptured = new LinkedList<>();

        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board.length; j++)     // capture enemy stones
                if (board[i][j] != null && board[i][j].getColor() != currPlayer && Stone.getNumLiberties(board[i][j], new HashSet<>()) == 0)
                    stonesCaptured.add(new Pair<>(i, j));

        for (Pair p : stonesCaptured)
            removeStoneFromBoard( (int) p.getKey(), (int) p.getValue() );

        return stonesCaptured.size();
    }

    public int countCapturedStones(Color currPlayer) {
        int n = 0;
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board.length; j++)
                if (board[i][j] != null && board[i][j].getColor() != currPlayer && Stone.getNumLiberties(board[i][j], new HashSet<>()) == 0)
                    n++;
        return n;
    }

    public Pair<Integer, Integer> captureSingleStone(Color currPlayer) {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board.length; j++)     // capture enemy stones
                if (board[i][j] != null && board[i][j].getColor() != currPlayer && Stone.getNumLiberties(board[i][j], new HashSet<>()) == 0)
                    return new Pair<>(i, j);
        throw new RuntimeException("Oops");
    }

    private Stone createStone(int row, int col, Color color) {
        int maxLiberties;

        if( (row == 0 && col == 0) || (row == board.length - 1 && col == board.length - 1) || (row == 0 && col == board.length - 1) || (row == board.length - 1 && col == 0))
            maxLiberties = 2;   // corner
        else if (row == 0 || col == 0 || row == board.length - 1 || col == board.length - 1)
            maxLiberties = 3;   // edge
        else
            maxLiberties = 4;

        return new Stone(color, maxLiberties);
    }

    public void placeStoneOnBoard(int row, int col, Color color) {  // preconditions: move is valid
        Stone newStone = createStone(row, col, color);
        board[row][col] = newStone;

        for (Stone adjacentStone : getAdjacentStonesNESW(row, col)) {
            if (adjacentStone != null) {
                newStone.getAdjacentStones().add(adjacentStone);
                adjacentStone.getAdjacentStones().add(newStone);
            }
        }
    }
    public Stone placeStone(int row, int col, Color color) {  // preconditions: move is valid
        Stone newStone = createStone(row, col, color);
        board[row][col] = newStone;

        for (Stone adjacentStone : getAdjacentStonesNESW(row, col)) {
            if (adjacentStone != null) {
                newStone.getAdjacentStones().add(adjacentStone);
                adjacentStone.getAdjacentStones().add(newStone);
            }
        }
        return newStone;
    }

    public void removeStoneFromBoard(int row, int col) {
        for (Stone adjacentStone : getAdjacentStonesNESW(row, col)) {
            if (adjacentStone != null) {
                board[row][col].getAdjacentStones().remove(adjacentStone);
                adjacentStone.getAdjacentStones().remove(board[row][col]);
            }
        }

        board[row][col] = null;
    }

    private Stone[] getAdjacentStonesNESW(int row, int col) {
        Stone north = getStone(row - 1, col);
        Stone east  = getStone(row, col + 1);
        Stone south = getStone(row + 1, col);
        Stone west  = getStone(row, col - 1);

        return new Stone[]{north, east, south, west};
    }

    private Stone getStone(int row, int col) {
        if (isValidLocation(row, col))
            return board[row][col];
        return null;
    }

    private boolean isValidLocation(int row, int col) {
        return row >= 0 && col >= 0 && row < board.length && col < board.length;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (int i = 0; i < board.length; i++) sb.append(i).append(" ");
        sb.append("\n   ");
        for (int n = 0; n < board.length * 2 + 1; n++) sb.append("-");
        sb.append("\n");

        for (int i = 0; i < board.length; i++) {
            sb.append(i).append(" | ");
            for (int j = 0; j < board.length; j++) {
                if(board[i][j] == null)
                    sb.append(" ");
                else if(board[i][j].getColor() == Color.BLACK)
                    sb.append("0");
                else if(board[i][j].getColor() == Color.WHITE)
                    sb.append("1");
                sb.append(" ");
            }
            sb.append("|\n");
        }

        sb.append("   ");
        for (int n = 0; n < board.length * 2 + 1; n++) sb.append("-");
        sb.append("\n");

        return sb.toString();
    }

    public void clearBoard() {
        for (int i = 0; i < board.length; i++)
            for (int j = 0; j < board.length; j++)
                board[i][j] = null;
    }

    public int size() {
        return board.length;
    }

    public Stone[][] getBoard() {   // used for testing purposes
        return board;
    }

    public Pair<Integer,Integer> scoreBoard(){
        Set<Stone> visited = new HashSet<>();
        Queue<Stone> emptyPoints; // unvisited empty points
        Queue<Stone> unexploredNeighbors;
        ArrayList<Stone> connectedGroup;//connected group of empty points
        ArrayList <Pair <List,String> > listOfGroups = new ArrayList <> ();
        String territory;

        emptyPoints = createEmptyStones(); //initialize empty points
       //begin territory exploration algorithm
       while(emptyPoints.size()>0){
           Stone s = emptyPoints.remove();
           if(!visited.contains(s)){
               unexploredNeighbors = new LinkedList<>();
               unexploredNeighbors.add(s);
               connectedGroup = new ArrayList<>();
               territory = "U";
               while(unexploredNeighbors.size()>0){
                   Stone n = unexploredNeighbors.remove();
                   if(!visited.contains(n)){
                       visited.add(n);
                   }
                   if(emptyPoints.contains(n)){
                      emptyPoints.remove(n);
                   }
                   connectedGroup.add(n);
                   List<Stone> adjList = n.getAdjacentStones();
                   for (Stone adjacentStone : adjList){
                       if(adjacentStone.getColor() == Color.DARKGREY) {
                           if((!visited.contains(adjacentStone)) && (!unexploredNeighbors.contains(adjacentStone))){
                               unexploredNeighbors.add(adjacentStone);
                           }
                       }else if (adjacentStone.getColor() == Color.BLACK) {
                           if (territory == "W" || territory == "BW") {
                               territory = "BW";
                           }else{
                               territory = "B";
                           }
                       } else if (adjacentStone.getColor() == Color.WHITE) {
                           if (territory == "B" || territory == "BW") {
                               territory = "BW";
                           }else{
                               territory = "W";
                           }
                       }
                   }
               }
               listOfGroups.add(new Pair(connectedGroup, territory));
           }
       }
        removeEmptyStones();
        return calculateScore(listOfGroups);
    }
    public void removeEmptyStones(){
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++)
                if(board[row][col].getColor() == Color.DARKGREY){
                    removeStoneFromBoard(row,col);
                }
    }
    public Queue<Stone> createEmptyStones(){
        Queue<Stone> emptyPoints = new LinkedList<>(); // unvisited empty points
        //create placeholder stones for empty points
        for (int row = 0; row < board.length; row++)
            for (int col = 0; col < board.length; col++)
                if(board[row][col] == null){
                    emptyPoints.add( placeStone(row,col,Color.DARKGREY)); // add to queue with all unvisted empty points
                }
        return emptyPoints;
    }
    public Pair<Integer,Integer> calculateScore (ArrayList <Pair <List,String> > listOfGroups){
        int black = 0;
        int white = 0;
        for(Pair <List,String> group:listOfGroups){
            if(group.getValue() == "B"){
                black += group.getKey().size();
            }else if(group.getValue() == "W"){
                white += group.getKey().size();
            }
        }
        return new Pair(black, white);
    }

}
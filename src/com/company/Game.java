package com.company;

import com.company.Utility.Pair;
import java.awt.*;

public class Game {
    private Player[] players;
    private Board board;
    private int turn;
    private MoveData prevMove;  // ko rule

    public Game() {
        players = new Player[]{ new Player("Player 1", Color.BLACK),
                                new Player("Player 2", Color.WHITE)
                              };
        board = new Board(9);
    }

    public boolean isValidMove(Player currentPlayer, int row, int col) {
        if (!board.isValidMove(row, col, currentPlayer.getColor()))
            return false;
        else {              // TODO: improve below methods, both O(n^2) where n = board size
            board.placeStoneOnBoard(row, col, currentPlayer.getColor());                        // make move
            if (board.countCaptureStones(currentPlayer.getColor()) == 1) {
                Pair<Integer, Integer> capturedStone = board.captureSingleStone(currentPlayer.getColor());
                MoveData currMove = new MoveData(capturedStone, 1);
                if (prevMove != null && currMove.equals(prevMove))                              // check for ko rule
                    return false;
                prevMove = new MoveData(row, col, 1);
            }
            board.removeStoneFromBoard(row, col);                                               // undo move
            return true;
        }
    }

    public void playerMove(int row, int col) {
        Player currentPlayer = currentPlayer();
        System.out.println((currentPlayer.getColor() == Color.WHITE ? "WHITE" : "BLACK") + " attempt to place stone at [" + row + "," + col + "]");

        if (!isValidMove(currentPlayer, row, col)) {
            System.out.println("Invalid move attempt!");
            return;
        }

        board.placeStoneOnBoard(row, col, currentPlayer.getColor());                // make move
        int numStonesCaptured = board.captureStones(currentPlayer.getColor());      // capture enemy stones
        currentPlayer.incrementStonesCaptured(numStonesCaptured);                   // increment score by # stones captured

        printGameState();
    }

    public void nextTurn() {
        turn = ++turn % 2;
    }

    public void printGameState() {
        System.out.print(board.toString());
        System.out.println("SCORES P1: " + players[0].numStonesCaptured() + " P2: " + players[1].numStonesCaptured() + '\n');
    }

    public Player currentPlayer() {
        return players[turn];
    }

    public Player[] getPlayers() {
        return players;
    }

    private static class MoveData {

        private Pair<Integer, Integer> position;
        private int stonesCaptured;

        public MoveData(int x, int y, int stonesCaptured) {
            position = new Pair<>(x, y);
            this.stonesCaptured = stonesCaptured;
        }

        public MoveData(Pair<Integer, Integer> pair, int stonesCaptured) {
            this.position = pair;
            this.stonesCaptured = stonesCaptured;
        }

        @Override
        public boolean equals(Object o) {
            if(!(o instanceof MoveData)) return false;
            if(o == this) return true;
            return ((MoveData)o).stonesCaptured == this.stonesCaptured && ((MoveData)o).position.equals(this.position);
        }

    }

}
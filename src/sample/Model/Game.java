package sample.Model;
import javafx.scene.paint.Color;
import sample.Model.Utility.Pair;

public class Game {
    private Player[] players;
    private Board board;
    private int turn;
    private MoveData prevMove;          // ko rule
    private boolean lastTurnPassed;

    public Game() {
        players = new Player[]{
                                 new Player("Player 1", Color.BLACK),
                                 new Player("Player 2", Color.WHITE)
                              };
        setBoardSize(9);
    }

    public void setBoardSize(int size) {
        board = new Board(size);
    }

    public boolean isValidMove(int row, int col) {
        Player currentPlayer = getCurrentPlayer();

        return board.isValidMove(row, col, currentPlayer.getColor()) && !isRepeatBoardPosition(row, col, currentPlayer.getColor());
}

    private boolean isRepeatBoardPosition(int row, int col, Color color) {
        boolean isRepeatPosition = false;

        board.placeStoneOnBoard(row, col, color);                        // make move
        if (board.countCapturedStones(color) == 1) {
            Pair<Integer, Integer> capturedStone = board.captureSingleStone(color);
            MoveData currMove = new MoveData(capturedStone, 1);
            if (prevMove != null && currMove.equals(prevMove))                           // check for ko rule
                isRepeatPosition = true;
        }

        board.removeStoneFromBoard(row, col);                                               // undo move
        return isRepeatPosition;
    }

    public void playerMove(int row, int col) {
        Player currentPlayer = getCurrentPlayer();
        // System.out.println((currentPlayer.getColor() == Color.WHITE ? "WHITE" : "BLACK") + " attempt to place stone at [" + row + "," + col + "]");

        if (!isValidMove(row, col)) {
            System.out.println("INVALID MOVE!! TRY AGAIN!");
            return;
        }

        board.placeStoneOnBoard(row, col, currentPlayer.getColor());                // make move
        int numStonesCaptured = board.captureStones(currentPlayer.getColor());      // capture enemy stones
        currentPlayer.incrementScore(numStonesCaptured);                   // increment score by # stones captured

        prevMove = new MoveData(row, col, numStonesCaptured);
        lastTurnPassed = false;
        // System.out.println(toString());
    }

    public void passTurn() {
        if(lastTurnPassed) {
            gameOver();
            //System.exit(0);
        }

        nextTurn();
        lastTurnPassed = true;
    }

    public void nextTurn() {
        turn = ++turn % 2;
    }

    @Override
    public String toString() {
        return board.toString() + "SCORES P1: " + players[0].getScore() + " P2: " + players[1].getScore() + '\n';
    }

    public Player getCurrentPlayer() {
        return players[turn];
    }

    public Player[] getPlayers() {
        return players;
    }

    public Board getBoard() {
        return board;
    }

    public int getBoardSize() {
        return board.size();
    }

    public void gameOver(){
        Pair<Integer,Integer> score = board.scoreBoard();
        for (Player p : players){
            if(p.getColor() == Color.BLACK){
                p.incrementScore(score.getKey());
            }else{
                p.incrementScore(score.getValue());
            }
        }
    }
    public void restartGame() {
        board.clearBoard();
        for (Player p : players) p.resetScore();
        turn = 0;
        lastTurnPassed = false;
        prevMove = null;
    }

}

class MoveData {
    private Pair<Integer, Integer> position;
    private int stonesCaptured;

    MoveData(int x, int y, int stonesCaptured) {
        position = new Pair<>(x, y);
        this.stonesCaptured = stonesCaptured;
    }

    MoveData(Pair<Integer, Integer> pair, int stonesCaptured) {
        this.position = pair;
        this.stonesCaptured = stonesCaptured;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof MoveData && (o == this || ((MoveData) o).stonesCaptured == this.stonesCaptured && ((MoveData) o).position.equals(this.position));
    }
}
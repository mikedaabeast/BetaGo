package sample;
import javafx.scene.paint.Color;

public class Player {

    private Color color;
    private String name;
    private int enemyStonesCaptured;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void incrementStonesCaptured(int n) {
        enemyStonesCaptured += n;
    }

    public int numStonesCaptured() {
        return enemyStonesCaptured;
    }

    public void resetScore() {
        enemyStonesCaptured = 0;
    }

    public String getName() {
        return name;
    }

}
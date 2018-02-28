package sample.Model;
import javafx.scene.paint.Color;

public class Player {

    private Color color;
    private String name;
    private int score;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
    }

    public Color getColor() {
        return color;
    }

    public void incrementScore(int n) {
        score += n;
    }

    public int getScore() {
        return score;
    }

    public void resetScore() {
        score = 0;
    }

    public String getName() {
        return name;
    }

}
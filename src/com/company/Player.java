package com.company;
import java.awt.*;

public class Player {

    private Color color;
    private String name;
    private int enemyStonesCaptures;

    public Player(String name, Color color) {
        this.name = name;
        this.color = color;
        enemyStonesCaptures = 0;
    }

    public Color getColor() {
        return color;
    }

    public void incrementStonesCaptured(int n) {
        enemyStonesCaptures += n;
    }

    public int numEnemyStonesCaptured() {
        return enemyStonesCaptures;
    }

    public String getName() {
        return name;
    }

}
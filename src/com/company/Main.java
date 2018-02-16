package com.company;

public class Main {

    public static void main(String[] args) {

        Game game = new Game();

        game.playerMove(8, 8);
        game.nextTurn();
        game.playerMove(0, 8);
        game.nextTurn();

        game.playerMove(1, 8);
        game.playerMove(0, 7);
        game.nextTurn();

        game.playerMove(3, 4);
        game.playerMove(5, 4);
        game.playerMove(4, 3);

        game.playerMove(4, 5);
        game.nextTurn();

        game.playerMove(4, 2);
        game.playerMove(5, 5);
        game.playerMove(3, 3);
        game.playerMove(3, 5);
        game.playerMove(5, 3);
        game.playerMove(2, 4);
        game.playerMove(6, 4);
        game.nextTurn();

        game.playerMove(4, 7);
        game.nextTurn();

        game.playerMove(4, 4);
        game.playerMove(4, 6);

    }

}
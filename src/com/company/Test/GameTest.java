package com.company.Test;

import static org.junit.jupiter.api.Assertions.*;
import com.company.Game;
import com.company.Player;
import org.junit.jupiter.api.Test;
import java.awt.*;

class GameTest {

    @Test
    void testTurnSwitch() {
        Game game = new Game();

        Player[] players = game.getPlayers();

        assertEquals(players[0].getColor(), Color.BLACK);
        assertEquals(players[1].getColor(), Color.WHITE);

        assertEquals(game.currentPlayer(), players[0]);
        game.nextTurn();
        assertEquals(game.currentPlayer(), players[1]);
        game.nextTurn();
        assertEquals(game.currentPlayer(), players[0]);
        game.nextTurn();
        assertEquals(game.currentPlayer(), players[1]);
    }

    @Test
    void testKO() {
        Game game = new Game();

        game.playerMove(8, 6);
        game.playerMove(7, 7);
        game.playerMove(8, 8);
        game.nextTurn();
        game.playerMove(7, 8);
        assertEquals(true, game.isValidMove(game.currentPlayer(), 8, 7));
        game.playerMove(8, 7);
        assertEquals(1, game.currentPlayer().numStonesCaptured());
        game.nextTurn();
        assertEquals(false, game.isValidMove(game.currentPlayer(), 8, 8)); // ko rule
    }

    @Test
    void testRepeatMove() {                 // test ko rule
        Game game = new Game();

        game.playerMove(3, 3);
        game.playerMove(4, 2);
        game.playerMove(5, 3);

        game.nextTurn();
        game.playerMove(3, 4);
        game.playerMove(4, 5);
        game.playerMove(5, 4);

        game.nextTurn();
        game.playerMove(4, 4);

        game.nextTurn();
        game.printGameState();
        assertEquals(true, game.isValidMove(game.currentPlayer(), 4, 3)); // capture
        game.playerMove(4, 3);
        assertEquals(game.currentPlayer().numStonesCaptured(), 1);

        game.nextTurn();
        assertEquals(false, game.isValidMove(game.currentPlayer(), 4, 4)); // ko rule
        game.nextTurn();
        assertEquals(false, game.isValidMove(game.currentPlayer(), 4, 4));
        game.playerMove(4, 4);
    }

}
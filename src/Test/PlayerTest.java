package Test;

import static org.junit.jupiter.api.Assertions.*;
import sample.Model.Player;
import org.junit.jupiter.api.Test;
import javafx.scene.paint.Color;

class PlayerTest {

    @Test
    void testSuicideAndCapture_1() {
        Player player1 = new Player("P1", Color.BLACK);
        Player player2 = new Player("P2", Color.WHITE);

        assertEquals(player1.getName(), "P1");
        assertEquals(player1.getColor(), Color.BLACK);

        assertEquals(player2.getName(), "P2");
        assertEquals(player2.getColor(), Color.WHITE);

        player1.incrementScore(10);
        player1.incrementScore(-5);
        player2.incrementScore(20);
        player2.incrementScore(-5);
        assertEquals(player1.getScore(), 5);
        assertEquals(player2.getScore(), 15);
    }

}
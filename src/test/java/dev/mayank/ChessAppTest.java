package dev.mayank;

import dev.mayank.app.GamePanel;
import dev.mayank.piece.Bishop;
import dev.mayank.piece.King;
import dev.mayank.piece.Pawn;
import dev.mayank.piece.Queen;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import static dev.mayank.app.GamePanel.BLACK;
import static dev.mayank.app.GamePanel.WHITE;

/**
 * Unit test for simple App.
 */
public class ChessAppTest extends TestCase {
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public ChessAppTest(String testName) {
        super(testName);
    }

    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(ChessAppTest.class);
    }

    /**
     * Rigourous Test :-)
     */
    public void testApp() {
        assertTrue(true);
    }

    public void testIllegalKingMoves() {
        // Test for illegal moves for the king
        /*GamePanel.pieces.add(new Pawn(6, 7, WHITE));
        GamePanel.pieces.add(new King(7, 3, WHITE));
        GamePanel.pieces.add(new King(3, 0, BLACK));
        GamePanel.pieces.add(new Bishop(4, 1, BLACK));
        GamePanel.pieces.add(new Queen(5, 4, BLACK));*/
    }
}

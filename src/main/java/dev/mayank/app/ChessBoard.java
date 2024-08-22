package dev.mayank.app;

import java.awt.*;

public class ChessBoard {
    public static final int MAX_ROWS = 8;
    public static final int MAX_COLS = 8;
    public static final int SQUARE_SIZE = 100;  // The size of each square on the chessBoard is 100x100 pixels
    public static final int HALF_SQUARE_SIZE = SQUARE_SIZE / 2;

    /**
     * Draw the chess board with light brown and dark brown squares
     */
    public void drawBoard(Graphics2D g2d) {
        boolean isLightBrownSquare = true;  //
        for (int row = 0; row < MAX_ROWS; row++) {
            for (int col = 0; col < MAX_COLS; col++) {
                if (isLightBrownSquare) {
                    g2d.setColor(new Color(210, 165, 125));
                } else {
                    g2d.setColor(new Color(175, 115, 70));
                }
                isLightBrownSquare = !isLightBrownSquare; // Alternate the square color
                g2d.fillRect(col * SQUARE_SIZE, row * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            }
            isLightBrownSquare = !isLightBrownSquare; // Change the  starting square color for the next row
        }
    }
}

package dev.mayank.piece;

import dev.mayank.app.GamePanel;

public class King extends ChessPiece {

    public King(int row, int col, int color) {
        super(row, col, color);

        if (color == GamePanel.WHITE) {
            this.setImage(getImage("/images/piece/w-king"));
        } else {
            this.setImage(getImage("/images/piece/b-king"));
        }
    }

    /**
     * Kings can move only one square in any direction: horizontally, vertically, or diagonally. <br>
     * So, either the sum for the row and column should be 1,
     * or the movement ratio of the row and column should be 1:1.
     */
    @Override
    public boolean canMove(int targetRow, int targetCol) {
        if (isWithinBoard(targetRow, targetCol)) {
            if ((Math.abs(targetRow - getPrevRow()) + Math.abs(targetCol - getPrevCol()) == 1)    // horizontal or vertical step
                    || (Math.abs(targetRow - getPrevRow()) * Math.abs(targetCol - getPrevCol()) == 1)) {    // diagonal step
                if (isValidNextMove(targetRow, targetCol)) {
                    return true;
                }
            }
        }
        return false;
    }
}
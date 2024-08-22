package dev.mayank.piece;

import dev.mayank.app.GamePanel;

public class Knight extends ChessPiece {

    public Knight(int row, int col, int color) {
        super(row, col, color);

        if (color == GamePanel.WHITE) {
            this.setImage(getImage("/images/piece/w-knight"));
        } else {
            this.setImage(getImage("/images/piece/b-knight"));
        }
    }

    /**
     * Knights can move in an L-shape: 2 squares in one direction and 1 square in a perpendicular direction or vice versa. <br>
     * So, the movement ratio of the row and column should be 2:1 or 1:2.
     */
    @Override
    public boolean canMove(int targetRow, int targetCol) {
        if (isWithinBoard(targetRow, targetCol)) {
            if (Math.abs(targetRow - getPrevRow()) * Math.abs(targetCol - getPrevCol()) == 2) {
                if (isValidNextMove(targetRow, targetCol)) {
                    return true;
                }
            }
        }
        return false;
    }
}
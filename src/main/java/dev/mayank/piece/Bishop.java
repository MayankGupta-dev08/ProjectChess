package dev.mayank.piece;

import dev.mayank.app.GamePanel;

public class Bishop extends ChessPiece {

    public Bishop(int row, int col, int color) {
        super(row, col, color);

        if (color == GamePanel.WHITE) {
            this.setImage(getImage("/images/piece/w-bishop"));
        } else {
            this.setImage(getImage("/images/piece/b-bishop"));
        }
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        if (isWithinBoard(targetRow, targetCol) && !isSameSquare(targetRow, targetCol)) {   // within board & different square
            if (Math.abs(targetRow - getPrevRow()) == Math.abs(targetCol - getPrevCol())) {   // diagonal step
                if (isValidNextMove(targetRow, targetCol) && !isPieceOnDiagonalPath(targetRow, targetCol)) {
                    return true;
                }
            }
        }
        return false;
    }
}
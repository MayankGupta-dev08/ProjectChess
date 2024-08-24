package dev.mayank.piece;

import dev.mayank.app.GamePanel;

public class Queen extends ChessPiece {

    public Queen(int row, int col, int color) {
        super(row, col, color);

        if (color == GamePanel.WHITE) {
            this.setImage(getImage("/images/piece/w-queen"));
        } else {
            this.setImage(getImage("/images/piece/b-queen"));
        }
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        if (isWithinBoard(targetRow, targetCol) && !isSameSquare(targetRow, targetCol)) {
            if (targetRow == getPrevRow() || targetCol == getPrevCol()) {   // horizontal or vertical step
                if (isValidNextMove(targetRow, targetCol) && !isPieceOnStraightPath(targetRow, targetCol))
                    return true;
            }
            if (Math.abs(targetRow - getPrevRow()) == Math.abs(targetCol - getPrevCol())) {  // diagonal step
                if (isValidNextMove(targetRow, targetCol) && !isPieceOnDiagonalPath(targetRow, targetCol))
                    return true;
            }
        }
        return false;
    }
}
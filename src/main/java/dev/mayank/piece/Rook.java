package dev.mayank.piece;

import dev.mayank.app.GamePanel;

public class Rook extends ChessPiece {

    public Rook(int row, int col, int color) {
        super(row, col, color);

        if (color == GamePanel.WHITE) {
            this.setImage(getImage("/images/piece/w-rook"));
        } else {
            this.setImage(getImage("/images/piece/b-rook"));
        }
    }

    /**
     * Rooks can move only horizontally or vertically, but not only at the same square. <br>
     * So, either the row or the column should be the same as the previous row or column.
     * But the rook cannot move diagonally or jump over other pieces.
     */
    @Override
    public boolean canMove(int targetRow, int targetCol) {
        if (isWithinBoard(targetRow, targetCol)) {
            if ((targetRow == getPrevRow() || targetCol == getPrevCol()) && !isSameSquare(targetRow, targetCol)) {
                if (isValidNextMove(targetRow, targetCol) && !isPieceOnStraightPath(targetRow, targetCol)) {
                    return true;
                }
            }
        }
        return false;
    }
}
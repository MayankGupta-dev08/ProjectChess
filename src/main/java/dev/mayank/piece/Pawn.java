package dev.mayank.piece;

import dev.mayank.app.GamePanel;

public class Pawn extends ChessPiece {

    public Pawn(int row, int col, int color) {
        super(row, col, color);

        if (color == GamePanel.WHITE) {
            this.setImage(getImage("/images/piece/w-pawn"));
        } else {
            this.setImage(getImage("/images/piece/b-pawn"));
        }
    }

    @Override
    public boolean canMove(int targetRow, int targetCol) {
        if (isWithinBoard(targetRow, targetCol) && !isSameSquare(targetRow, targetCol)) {
            int moveValue = getColor() == GamePanel.WHITE ? -1 : 1;
            ChessPiece pieceBeingHit = getHittingPiece(targetRow, targetCol);

            /* moving 2 steps in straight line, each pawn is allowed only for their 1st move */
            if (targetCol == getPrevCol() && (targetRow == getPrevRow() + moveValue * 2) && pieceBeingHit == null
                    && !isMoved() && !isPieceOnStraightPath(targetRow, targetCol)) {
                return true;
            }

            /* moving 1 step in straight line */
            if (targetCol == getPrevCol() && (targetRow == getPrevRow() + moveValue) && pieceBeingHit == null) {
                return true;
            }

            /* moving 1 step diagonally to capture opponent's piece */
            if (Math.abs(targetCol - getPrevCol()) == 1 && (targetRow == getPrevRow() + moveValue) && pieceBeingHit != null
                    && pieceBeingHit.getColor() != getColor()) {
                this.setHittingPiece(pieceBeingHit);
                return true;
            }
        }
        return false;
    }


}
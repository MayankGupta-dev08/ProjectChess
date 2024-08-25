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
            /*BASIC MOVEMENT*/
            if ((Math.abs(targetRow - getPrevRow()) + Math.abs(targetCol - getPrevCol()) == 1)    // horizontal or vertical step
                    || (Math.abs(targetRow - getPrevRow()) * Math.abs(targetCol - getPrevCol()) == 1)) {    // diagonal step
                if (isValidNextMove(targetRow, targetCol)) {
                    return true;
                }
            }

            /*CASTLING*/
            if (canKingCastle(targetRow, targetCol)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Once per game, each king can make a move known as castling.
     * Castling consists of moving the king two squares toward a rook of the same color on the same rank,
     * and then placing the rook on the square that the king crossed. <br>
     * Castling is permissible if the following conditions are met:
     * <ul>
     *     <li>neither the king nor the chosen rook has previously moved.</li>
     *     <li>there are no pieces between the king and the chosen rook.</li>
     *     <li>the king is not currently in check.</li>
     *     <li>The king is not in check and does not pass through or finish on a square attacked by an enemy piece.</li>
     * </ul>
     */
    private boolean canKingCastle(int targetRow, int targetCol) {
        if (!isMoved()) {
            /*Casting on the right side*/
            if (targetRow == getPrevRow() && targetCol == getPrevCol() + 2
                    && !isPieceOnStraightPath(targetRow, targetCol) && getHittingPiece(targetRow, targetCol) == null) {
                for (ChessPiece piece : GamePanel.simPieces) {
                    if (piece instanceof Rook && piece.getRow() == getPrevRow() && piece.getCol() == getPrevCol() + 3 && !piece.isMoved()) {
                        GamePanel.castlingRook = piece;
                        return true;
                    }
                }
            }
            /*Casting on the left side*/
            if (targetRow == getPrevRow() && targetCol == getPrevCol() - 2
                    && !isPieceOnStraightPath(targetRow, targetCol) && getHittingPiece(targetRow, targetCol) == null) {
                ChessPiece[] nullAndRook = new ChessPiece[2];
                for (ChessPiece piece : GamePanel.simPieces) {
                    if (piece.getCol() == this.getPrevCol() - 3 && piece.getPrevRow() == targetRow) {
                        nullAndRook[0] = piece;
                    }
                    if (piece instanceof Rook && piece.getCol() == this.getPrevCol() - 4 && piece.getRow() == targetRow && !piece.isMoved()) {
                        nullAndRook[1] = piece;
                    }
                }
                if (nullAndRook[0] == null && nullAndRook[1] != null) {
                    GamePanel.castlingRook = nullAndRook[1];
                    return true;
                }
            }
        }
        return false;
    }
}
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
}
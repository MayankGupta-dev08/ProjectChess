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
}
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
}
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
}
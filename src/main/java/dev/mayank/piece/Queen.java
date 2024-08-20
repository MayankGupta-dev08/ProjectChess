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
}
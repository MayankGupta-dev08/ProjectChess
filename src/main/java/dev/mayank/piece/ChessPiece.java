package dev.mayank.piece;

import dev.mayank.app.ChessBoard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ChessPiece {
    private BufferedImage image;
    private int x, y;
    private int row, col, prevRow, prevCol;
    private int color;  // 0 for white, 1 for black

    public ChessPiece(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.color = color;

        this.x = getPosition(col);
        this.y = getPosition(row);
        this.prevRow = row;
        this.prevCol = col;
    }

    public BufferedImage getImage(String imagePath) {
        BufferedImage image = null;
        try {
            image = ImageIO.read(getClass().getResourceAsStream(imagePath + ".png"));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public void drawImage(Graphics2D g2d) {
        g2d.drawImage(image, x, y, ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, null);
    }

    private int getPosition(int pos) {
        return pos * ChessBoard.SQUARE_SIZE;
    }
}
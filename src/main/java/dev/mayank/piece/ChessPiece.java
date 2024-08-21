package dev.mayank.piece;

import dev.mayank.app.ChessBoard;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class ChessPiece {
    private final int color;  // 0 for white, 1 for black
    private BufferedImage image;
    private int x, y;   // Actual x and y coordinates of the piece
    private int row, col, prevRow, prevCol; // Row and column of the piece

    public ChessPiece(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.color = color;

        this.x = calcX(col);
        this.y = calcY(row);
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

    public void drawPiece(Graphics2D g2d) {
        g2d.drawImage(image, x, y, ChessBoard.SQUARE_SIZE, ChessBoard.SQUARE_SIZE, null);
    }

    public int calcX(int col) {
        return col * ChessBoard.SQUARE_SIZE;
    }

    public int calcY(int row) {
        return row * ChessBoard.SQUARE_SIZE;
    }

    public int calcRow(int y) {
        return (y + ChessBoard.HALF_SQUARE_SIZE) / ChessBoard.SQUARE_SIZE;
    }

    public int calcCol(int x) {
        return (x + ChessBoard.HALF_SQUARE_SIZE) / ChessBoard.SQUARE_SIZE;
    }

    /**
     * Correctly update the position of the piece
     */
    public void updatePosition() {
        x = calcX(col);
        y = calcY(row);
        prevRow = calcRow(y);
        prevCol = calcCol(x);
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public int getColor() {
        return color;
    }
}
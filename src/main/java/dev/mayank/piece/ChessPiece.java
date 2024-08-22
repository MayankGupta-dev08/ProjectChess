package dev.mayank.piece;

import dev.mayank.app.ChessBoard;
import dev.mayank.app.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;

import static dev.mayank.app.ChessBoard.MAX_COLS;
import static dev.mayank.app.ChessBoard.MAX_ROWS;

public abstract class ChessPiece {
    private final int color;  // 0 for white, 1 for black
    private BufferedImage image;
    private int x, y;   // Actual x and y coordinates of the piece
    private int row, col, prevRow, prevCol; // Row and column of the piece
    public ChessPiece hittingPiece = null;  // The piece that is being hit by the current piece

    public ChessPiece(int row, int col, int color) {
        this.row = row;
        this.col = col;
        this.color = color;

        this.x = calcX(col);
        this.y = calcY(row);
        this.prevRow = row;
        this.prevCol = col;
    }

    /**
     * @param targetRow The row to which the piece is to be moved
     * @param targetCol The column to which the piece is to be moved
     * @return true if the piece can be moved to the target position, false otherwise
     */
    public abstract boolean canMove(int targetRow, int targetCol);

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

    /**
     * Reset the position of the piece to the previous position
     */
    public void resetPosition() {
        row = prevRow;
        col = prevCol;
        x = calcX(col);
        y = calcY(row);
    }

    public boolean isWithinBoard(int targetRow, int targetCol) {
        return targetRow >= 0 && targetRow < MAX_ROWS && targetCol >= 0 && targetCol < MAX_COLS;
    }

    /**
     * @param targetRow The row to which the piece is to be moved
     * @param targetCol The column to which the piece is to be moved
     * @return The piece that is being hit by the current piece null if no piece is being hit
     */
    public ChessPiece getHittingPiece(int targetRow, int targetCol) {
        for (ChessPiece piece : GamePanel.simPieces) {
            if (piece.getRow() == targetRow && piece.getCol() == targetCol && piece != this) {
                return piece;
            }
        }
        return null;
    }

    /**
     * @param targetRow The row to which the piece is to be moved
     * @param targetCol The column to which the piece is to be moved
     * @return true if the piece can be moved to the target position, false otherwise
     */
    public boolean isValidNextMove(int targetRow, int targetCol) {
        this.hittingPiece = getHittingPiece(targetRow, targetCol);
        if (hittingPiece == null) { // No piece is being hit
            return true;
        } else {
            if (hittingPiece.getColor() != this.color) {    // The piece could be captured
                return true;
            } else {
                this.hittingPiece = null;
            }
        }
        return false;
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

    public int getPrevRow() {
        return prevRow;
    }

    public void setPrevRow(int prevRow) {
        this.prevRow = prevRow;
    }

    public int getPrevCol() {
        return prevCol;
    }

    public void setPrevCol(int prevCol) {
        this.prevCol = prevCol;
    }

    public int getPieceIndex() {
        for (int i = 0; i < GamePanel.simPieces.size(); i++) {
            if (GamePanel.simPieces.get(i) == this)
                return i;
        }
        return -1;
    }
}
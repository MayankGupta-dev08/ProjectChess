package dev.mayank.piece;

import dev.mayank.app.ChessBoard;
import dev.mayank.app.GamePanel;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Objects;

import static dev.mayank.app.ChessBoard.MAX_COLS;
import static dev.mayank.app.ChessBoard.MAX_ROWS;

public abstract class ChessPiece {
    private final int color;  // 0 for white, 1 for black
    private BufferedImage image;
    private int x;   // Actual x coordinate of the piece
    private int y;   // Actual y coordinate of the piece
    private int row;    // Current row of the piece
    private int col;    // Current column of the piece
    private int prevRow;    // Previous row of the piece
    private int prevCol;    // Previous column of the piece
    private ChessPiece hittingPiece = null;  // The piece that is being hit by the current piece
    private boolean isMoved = false;  // Flag to check if the piece has been moved
    private boolean isTwoStepsMove = false;  // Flag to check if the pawn has moved two steps

    protected ChessPiece(int row, int col, int color) {
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
        BufferedImage pieceImage = null;
        try {
            pieceImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath + ".png")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pieceImage;
    }

    public BufferedImage getImage() {
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
        if (this instanceof Pawn) // for en passant
            isTwoStepsMove = Math.abs(row - prevRow) == 2;

        x = calcX(col);
        y = calcY(row);
        prevRow = calcRow(y);
        prevCol = calcCol(x);
        isMoved = true;
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

    /**
     * @return true if the target position is within the board, false otherwise
     */
    protected boolean isWithinBoard(int targetRow, int targetCol) {
        return targetRow >= 0 && targetRow < MAX_ROWS && targetCol >= 0 && targetCol < MAX_COLS;
    }

    /**
     * @param targetRow The row to which the piece is to be moved
     * @param targetCol The column to which the piece is to be moved
     * @return true if the target position is the same as the current position, false otherwise
     */
    protected boolean isSameSquare(int targetRow, int targetCol) {
        return targetRow == prevRow && targetCol == prevCol;
    }

    /**
     * @param targetRow The row to which the piece is to be moved
     * @param targetCol The column to which the piece is to be moved
     * @return The piece that is being hit by the current piece null if no piece is being hit
     */
    protected ChessPiece getHittingPiece(int targetRow, int targetCol) {
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
    protected boolean isValidNextMove(int targetRow, int targetCol) {
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

    /**
     * @return true if there is a piece in the path of the current piece, false otherwise
     */
    protected boolean isPieceOnStraightPath(int targetRow, int targetCol) {
        int rowDiff = targetRow - prevRow;
        int colDiff = targetCol - prevCol;

        if (rowDiff == 0) { // Moving horizontally
            int step = colDiff > 0 ? 1 : -1;    // -1 for the left, 1 for the right
            for (int i = prevCol + step; i != targetCol; i += step) {
                for (ChessPiece piece : GamePanel.simPieces) {
                    if (piece.getRow() == targetRow && piece.getCol() == i) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        } else if (colDiff == 0) {  // Moving vertically
            int step = rowDiff > 0 ? 1 : -1;    // -1 for the up, 1 for the down
            for (int i = prevRow + step; i != targetRow; i += step) {
                for (ChessPiece piece : GamePanel.simPieces) {
                    if (piece.getRow() == i && piece.getCol() == targetCol) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * @return true if there is a piece in the diagonal path of the current piece, false otherwise
     */
    protected boolean isPieceOnDiagonalPath(int targetRow, int targetCol) {
        if (targetRow < prevRow) {  // Moving up
            for (int c = prevCol + 1; c < targetCol; c++) { // Moving right
                for (ChessPiece piece : GamePanel.simPieces) {
                    int diff = Math.abs(c - prevCol);
                    if (piece.getCol() == c && piece.getRow() == prevRow - diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
            for (int c = prevCol - 1; c > targetCol; c--) { // Moving left
                for (ChessPiece piece : GamePanel.simPieces) {
                    int diff = Math.abs(c - prevCol);
                    if (piece.getCol() == c && piece.getRow() == prevRow - diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
        } else {  // Moving down
            for (int c = prevCol + 1; c < targetCol; c++) { // Moving right
                for (ChessPiece piece : GamePanel.simPieces) {
                    int diff = Math.abs(c - prevCol);
                    if (piece.getCol() == c && piece.getRow() == prevRow + diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
            }
            for (int c = prevCol - 1; c > targetCol; c--) { // Moving left
                for (ChessPiece piece : GamePanel.simPieces) {
                    int diff = Math.abs(c - prevCol);
                    if (piece.getCol() == c && piece.getRow() == prevRow + diff) {
                        hittingPiece = piece;
                        return true;
                    }
                }
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

    protected int getPrevRow() {
        return prevRow;
    }

    protected int getPrevCol() {
        return prevCol;
    }

    public ChessPiece getHittingPiece() {
        return hittingPiece;
    }

    protected void setHittingPiece(ChessPiece hittingPiece) {
        this.hittingPiece = hittingPiece;
    }

    protected boolean isMoved() {
        return isMoved;
    }

    public boolean isTwoStepsMove() {
        return isTwoStepsMove;
    }

    public void setTwoStepsMove(boolean twoStepsMove) {
        isTwoStepsMove = twoStepsMove;
    }
}
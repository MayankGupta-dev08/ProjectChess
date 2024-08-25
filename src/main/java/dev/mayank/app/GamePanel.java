package dev.mayank.app;

import dev.mayank.piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;

import static dev.mayank.app.ChessBoard.HALF_SQUARE_SIZE;
import static dev.mayank.app.ChessBoard.SQUARE_SIZE;

/**
 * Panel for the Chess Game
 */
public class GamePanel extends JPanel implements Runnable {
    public static final int WHITE = 0;  // 0 for white pieces
    public static final int BLACK = 1;  // 1 for black pieces
    public static final ArrayList<ChessPiece> simPieces = new ArrayList<>();  // List of simulated chess pieces (actual pieces)

    private static final int FPS = 60; // Frames per second
    private static final int WIDTH = 1100;  // Width of the panel
    private static final int HEIGHT = 800;  // Height of the panel
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());
    private static final ArrayList<ChessPiece> pieces = new ArrayList<>();  // List of chess pieces for restoring state

    private Thread gameThread;  // Thread for the game
    private ChessBoard chessBoard = new ChessBoard();   // Chess Board
    private int currentColor = WHITE;  // The color for which the piece is to be moved.
    private Mouse mouse = new Mouse();  // Mouse listener
    private ChessPiece activePiece = null;  // Selected piece
    private boolean pieceCanMove = false;   // Flag to check if the piece can move
    private boolean isValidSquare = false;  // Flag to check if the square is valid

    public GamePanel() {
        LOGGER.info("Creating Game Panel");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    public void launchGame() {
        LOGGER.info("Launching the game");
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
        LOGGER.info("Setting the pieces");
        // Setting the white team
        GamePanel.pieces.add(new Pawn(6, 0, WHITE));
        GamePanel.pieces.add(new Pawn(6, 1, WHITE));
        GamePanel.pieces.add(new Pawn(6, 2, WHITE));
        GamePanel.pieces.add(new Pawn(6, 3, WHITE));
        GamePanel.pieces.add(new Pawn(6, 4, WHITE));
        GamePanel.pieces.add(new Pawn(6, 5, WHITE));
        GamePanel.pieces.add(new Pawn(6, 6, WHITE));
        GamePanel.pieces.add(new Pawn(6, 7, WHITE));
        GamePanel.pieces.add(new Rook(7, 0, WHITE));
        GamePanel.pieces.add(new Knight(7, 1, WHITE));
        GamePanel.pieces.add(new Bishop(7, 2, WHITE));
        GamePanel.pieces.add(new Queen(7, 3, WHITE));
        GamePanel.pieces.add(new King(7, 4, WHITE));
        GamePanel.pieces.add(new Bishop(7, 5, WHITE));
        GamePanel.pieces.add(new Knight(7, 6, WHITE));
        GamePanel.pieces.add(new Rook(7, 7, WHITE));

        // Setting the black team
        GamePanel.pieces.add(new Pawn(1, 0, BLACK));
        GamePanel.pieces.add(new Pawn(1, 1, BLACK));
        GamePanel.pieces.add(new Pawn(1, 2, BLACK));
        GamePanel.pieces.add(new Pawn(1, 3, BLACK));
        GamePanel.pieces.add(new Pawn(1, 4, BLACK));
        GamePanel.pieces.add(new Pawn(1, 5, BLACK));
        GamePanel.pieces.add(new Pawn(1, 6, BLACK));
        GamePanel.pieces.add(new Pawn(1, 7, BLACK));
        GamePanel.pieces.add(new Rook(0, 0, BLACK));
        GamePanel.pieces.add(new Knight(0, 1, BLACK));
        GamePanel.pieces.add(new Bishop(0, 2, BLACK));
        GamePanel.pieces.add(new Queen(0, 3, BLACK));
        GamePanel.pieces.add(new King(0, 4, BLACK));
        GamePanel.pieces.add(new Bishop(0, 5, BLACK));
        GamePanel.pieces.add(new Knight(0, 6, BLACK));
        GamePanel.pieces.add(new Rook(0, 7, BLACK));
        LOGGER.info("Pieces set");
    }

    private void copyPieces(ArrayList<ChessPiece> source, ArrayList<ChessPiece> target) {
        target.clear();
        target.addAll(source);
    }

    /**
     * Update the game state. <br>
     * 1. If the mouse button is pressed && a chess piece is not selected, then check if you could pick up a piece <br>
     * 2. If the mouse button is pressed && a chess piece is selected, then check if you could move the piece <br>
     */
    private void update() {
        /*If the mouse button is pressed*/
        if (mouse.isPressed()) {
            if (activePiece == null) {    // If no piece is selected
                for (ChessPiece piece : simPieces) {
                    if (piece.getColor() == currentColor && piece.getRow() == mouse.getY() / SQUARE_SIZE
                            && piece.getCol() == mouse.getX() / SQUARE_SIZE) {  // select the piece for the current player
                        activePiece = piece;
                    }
                }
            } else { // If a player is holding the piece and may either move it to any valid position, capture an opponent piece or release the piece
                simulateMove();
            }
        }

        /*If the mouse button is released & a piece was selected*/
        if (!mouse.isPressed() && activePiece != null) {
            if (isValidSquare) {    // move is confirmed
                copyPieces(simPieces, pieces);  // update the actual pieces
                activePiece.updatePosition();
                /* The current player's turn is over */
                switchPlayer();
            } else {
                copyPieces(pieces, simPieces);  // reset the simulated pieces
                activePiece.resetPosition();
                activePiece = null;
            }
        }
    }

    private void simulateMove() {
        pieceCanMove = false;
        isValidSquare = false;

        // Reset the pieces for each simulation, for restoring the removed piece during the simulation and not during the actual move
        copyPieces(pieces, simPieces);

        // If a piece is being held, then update its position
        activePiece.setX(mouse.getX() - HALF_SQUARE_SIZE);    // subtracting half of the square size to keep the piece at the center of the mouse
        activePiece.setY(mouse.getY() - HALF_SQUARE_SIZE);
        activePiece.setRow(activePiece.calcRow(activePiece.getY()));
        activePiece.setCol(activePiece.calcCol(activePiece.getX()));

        // If the piece is released, then check if the move is valid
        if (activePiece.canMove(activePiece.getRow(), activePiece.getCol())) {
            pieceCanMove = true;
            isValidSquare = true;

            // if the piece is hitting opponent's piece, then capture it
            if (activePiece.getHittingPiece() != null) {
                simPieces.remove(activePiece.getHittingPiece());
            }
        }
    }

    /**
     * Switch the player's turn
     */
    private void switchPlayer() {
        currentColor = currentColor == WHITE ? BLACK : WHITE;
        activePiece = null;
    }

    /**
     * To draw the components on the panel.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        // Draw the chess board
        chessBoard.drawBoard(g2d);

        // Draw the pieces
        for (ChessPiece piece : simPieces) {
            piece.drawPiece(g2d);
        }

        if (activePiece != null) {
            if (pieceCanMove) {
                g2d.setColor(Color.WHITE);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
                g2d.fillRect(activePiece.getCol() * SQUARE_SIZE, activePiece.getRow() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            }
            activePiece.drawPiece(g2d);
        }

        /* STATUS MESSAGE */
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Trebuchet MS", Font.CENTER_BASELINE, 30));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        if ((currentColor == WHITE)) {
            g2d.drawString(">> White's Turn", 840, 550);
        } else {
            g2d.drawString(">> Black's Turn", 840, 250);
        }
    }

    /**
     * Does two things: <br>
     * 1. Update game state <br>
     * 2. Repaint the panel <br>
     */
    @Override
    public void run() {
        LOGGER.info("Running the game");
        double drawInterval = 1000_000_000.00 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval;
            lastTime = currentTime;

            if (delta >= 1) {
                update();   // Update the game state
                repaint();  // Repaint the panel using paintComponent()
                delta--;
            }
        }
    }
}
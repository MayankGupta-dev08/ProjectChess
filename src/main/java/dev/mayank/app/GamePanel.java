package dev.mayank.app;

import dev.mayank.piece.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Panel for the Chess Game
 */
public class GamePanel extends JPanel implements Runnable {
    public static final int WHITE = 0;  // 0 for white pieces
    public static final int BLACK = 1;  // 1 for black pieces

    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());
    private static final int WIDTH = 1100;  // Width of the panel
    private static final int HEIGHT = 800;  // Height of the panel
    private static final ArrayList<ChessPiece> pieces = new ArrayList<>();  // List of chess pieces
    private static final ArrayList<ChessPiece> simPieces = new ArrayList<>();  // List of simulated chess pieces
    private static final int FPS = 60; // Frames per second

    Thread gameThread;  // Thread for the game
    ChessBoard chessBoard = new ChessBoard();   // Chess Board
    int currentPieceColor = WHITE;  // Current piece color
    Mouse mouse = new Mouse();  // Mouse listener

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
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void setPieces() {
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
    }

    private void copyPieces(ArrayList<ChessPiece> source, ArrayList<ChessPiece> target) {
        target.clear();
        for (ChessPiece chessPiece : source) {
            target.add(chessPiece);
        }
    }

    private void update() {
        // TODO: Update the game state
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
        for (ChessPiece piece : pieces) {
            piece.drawImage(g2d);
        }
    }

    /**
     * Does two things: <br>
     * 1. Update game state <br>
     * 2. Repaint the panel <br>
     */
    @Override
    public void run() {
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
                repaint();  // Repaint the panel
                delta--;
            }
        }
    }
}
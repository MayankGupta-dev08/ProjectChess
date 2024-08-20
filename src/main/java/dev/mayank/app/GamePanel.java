package dev.mayank.app;

import javax.swing.*;
import java.awt.*;
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

    final int FPS = 60; // Frames per second
    Thread gameThread;  // Thread for the game
    ChessBoard chessBoard = new ChessBoard();   // Chess Board
    int pieceColor = WHITE;

    public GamePanel() {
        LOGGER.info("Creating Game Panel");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
    }

    public void launchGame() {
        gameThread = new Thread(this);
        gameThread.start();
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
        chessBoard.drawBoard(g2d);
    }

    /**
     * Does two things: <br>
     * 1. Update game state <br>
     * 2. Repaint the panel <br>
     */
    @Override
    public void run() {
        double drawInterval = 1000_000_000 / FPS;
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
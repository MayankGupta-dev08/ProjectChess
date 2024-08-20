package dev.mayank.app;

import javax.swing.*;
import java.util.logging.Logger;

/**
 * Main class for the Chess Application
 */
public class ChessApp {
    private static final Logger LOGGER = Logger.getLogger(ChessApp.class.getName());

    public static void main(String[] args) {
        LOGGER.info("Starting Chess Application");

        // Create a new ChessBoard
        JFrame window = new JFrame("Project Chess");
        window.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        window.setVisible(true);

        GamePanel gamePanel = new GamePanel();
        window.add(gamePanel);
        window.pack();  // Resize the window to fit the panel

        window.setResizable(false); // Prevent resizing
        window.setLocationRelativeTo(null); // Center the window
        gamePanel.launchGame();
    }
}
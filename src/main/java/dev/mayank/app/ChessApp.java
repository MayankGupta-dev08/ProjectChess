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

        window.add(new GamePanel());
        window.pack();  // Resize the window to fit the panel

        window.setVisible(true);
        window.setResizable(false); // Prevent resizing
        window.setLocationRelativeTo(null); // Center the window
    }
}
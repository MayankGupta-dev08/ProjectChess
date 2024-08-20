package dev.mayank.app;

import javax.swing.*;
import java.awt.*;
import java.util.logging.Logger;

/**
 * Panel for the Chess Game
 */
public class GamePanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());
    private static final int WIDTH = 1100;
    private static final int HEIGHT = 800;

    public GamePanel() {
        LOGGER.info("Creating Game Panel");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);
    }
}
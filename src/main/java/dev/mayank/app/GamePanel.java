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

    public static ChessPiece castlingRook = null; // Rook for castling

    private static final int FPS = 60; // Frames per second
    private static final int WIDTH = 1100;  // Width of the panel
    private static final int HEIGHT = 800;  // Height of the panel
    private static final Logger LOGGER = Logger.getLogger(GamePanel.class.getName());
    private static final ArrayList<ChessPiece> pieces = new ArrayList<>();  // List of chess pieces for restoring state
    private static ArrayList<ChessPiece> promotionPieces = new ArrayList<>(); // Promotion pieces for the pawn

    private Thread gameThread;  // Thread for the game
    private ChessBoard chessBoard = new ChessBoard();   // Chess Board
    private int currentColor = WHITE;  // The color for which the piece is to be moved.
    private Mouse mouse = new Mouse();  // Mouse listener
    private ChessPiece activePiece = null;  // Selected piece
    private ChessPiece checkPiece = null;   // piece which is giving check to the king
    private boolean pieceCanMove = false;   // Flag to check if the piece can move
    private boolean isValidSquare = false;  // Flag to check if the square is valid
    private boolean promotePawn = false;  // Flag to check if the pawn is promoted
    private boolean isGameOver = false;  // Flag to check if the game is over
    private boolean isStalemate = false;  // Flag to check if the game is in stalemate

    /**
     * Constructor to create the Game Panel, activate Mouse and set the pieces on the board.
     */
    public GamePanel() {
        LOGGER.info("Creating Game Panel");
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.BLACK);

        addMouseMotionListener(mouse);
        addMouseListener(mouse);

        setPieces();
        copyPieces(pieces, simPieces);
    }

    /**
     * Launch the game by starting the game thread.
     */
    public void launchGame() {
        LOGGER.info("Launching the game");
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * Set the pieces on the board.
     */
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
        LOGGER.info("Pieces set on the board");
    }

    /**
     * Copy the pieces from the source list to the target list.
     *
     * @param source The source list from which the pieces are to be copied
     * @param target The target list to which the pieces are to be copied
     */
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
        if (promotePawn) {  // Pawn promotion
            promotingPawn();
        } else if (!isGameOver && !isStalemate) {   // The game is still on
            /*If the mouse button is pressed*/
            if (mouse.isPressed()) {
                if (activePiece == null) {    // If no piece is selected
                    for (ChessPiece piece : simPieces) {
                        // select the piece for the current player
                        if (piece.getColor() == currentColor && piece.getRow() == mouse.getY() / SQUARE_SIZE
                                && piece.getCol() == mouse.getX() / SQUARE_SIZE)
                            activePiece = piece;
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

                    if (castlingRook != null)
                        castlingRook.updatePosition(); // update the rook's position for castling

                    if (isKingInCheck() && isCheckMate()) {
                        LOGGER.info("~~Game Over~~");
                        isGameOver = true;
                    } else if (!isKingInCheck() && isStalemateCondition()) {
                        LOGGER.info("Stalemate!");
                        isStalemate = true;
                    } else {    // The game is still on!!
                        if (canPawnPromote())
                            promotePawn = true; // pawn can be promoted & after promotion switching of player will happen
                        else
                            switchingPlayer(); // The current player's turn is over
                    }
                } else {
                    copyPieces(pieces, simPieces);  // reset the simulated pieces
                    activePiece.resetPosition();
                    activePiece = null;
                }
            }
        }
    }

    /**
     * Simulate the move of the piece, and check if the move is valid or not. <br>
     * If the move is valid, then update the simulated pieces list, otherwise reset the piece's position.
     */
    private void simulateMove() {
        pieceCanMove = false;
        isValidSquare = false;

        // Reset the pieces for each simulation, for restoring the removed piece during the simulation and not during the actual move
        copyPieces(pieces, simPieces);

        // Reset the castling rook's position
        if (castlingRook != null) {
            castlingRook.resetPosition();
            castlingRook = null;
        }

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

            // Check if the king can move to the target position, such that no other opponent piece can hit the king
            if (canOpponentCaptureKing() || (activePiece instanceof King && isIllegalForKing(activePiece))) {
                isValidSquare = false;
                pieceCanMove = false;
            }

            checkForCastling(); // simulating rook's position if castling
        }
    }

    /**
     * Switch the player's turn -
     * If the current player is white, then switch to black and vice versa. <br>
     * Also, reset the twoStepMove for the pawns of the current player,
     * since the two-step move is only valid for the next move. <br>
     */
    private void switchingPlayer() {
        if (currentColor == WHITE) {
            currentColor = BLACK;
            for (ChessPiece piece : simPieces) {
                if (piece instanceof Pawn && piece.getColor() == BLACK)
                    piece.setTwoStepsMove(false);   // reset for black pawns, since white's turn is over
            }
        } else {
            currentColor = WHITE;
            for (ChessPiece piece : simPieces) {
                if (piece instanceof Pawn && piece.getColor() == WHITE)
                    piece.setTwoStepsMove(false);   // reset for white pawns, since black's turn is over
            }
        }
        activePiece = null;
    }

    /**
     * Checks if the opponent's King is in check by the active piece and sets checkPiece if checking.
     *
     * @return true if the opponent's King is in check, false otherwise.
     */
    private boolean isKingInCheck() {
        ChessPiece opponentKing = getKing(true);
        assert opponentKing != null;
        if (activePiece.canMove(opponentKing.getRow(), opponentKing.getCol())) {
            checkPiece = activePiece;
            return true;
        }

        checkPiece = null;
        return false;
    }

    /**
     * Retrieves the King piece based on the current player's color or the opponent's color.
     *
     * @param forOpponent A boolean flag indicating whether to get the opponent's King.
     *                    If true, the method returns the opponent's King.
     *                    If false, the method returns the current player's King.
     * @return The King piece of the specified color, or null if no King is found.
     */
    private ChessPiece getKing(boolean forOpponent) {
        int opponentColor = currentColor == WHITE ? BLACK : WHITE;
        int kingColor = forOpponent ? opponentColor : currentColor;

        for (ChessPiece piece : simPieces) {
            if (piece instanceof King && piece.getColor() == kingColor)
                return piece;
        }
        return null;
    }

    /**
     * Check if the king is moving for castling, then move the rook to the correct position
     */
    private void checkForCastling() {
        if (activePiece instanceof King && castlingRook != null) {
            int col = castlingRook.getCol();    // either 0 or 7
            castlingRook.setCol(col == 0 ? col + 3 : col - 2);
            castlingRook.setX(castlingRook.calcX(castlingRook.getCol()));
        }
    }

    /**
     * Checks if the move is illegal for king or not by checking if any of the opponent's piece could reach at king's (row,col)
     */
    private boolean isIllegalForKing(ChessPiece king) {
        for (ChessPiece piece : simPieces) {
            if (piece.getColor() != king.getColor() && piece.canMove(king.getRow(), king.getCol()))
                return true;
        }
        return false;
    }

    /**
     * Checks if any opponent player's piece could possibly capture the current player's King. <br>
     * This forces the current player to either block the King or move it to a safe position, or capture the opponent's piece.
     *
     * @return true, if yes, otherwise false.
     */
    private boolean canOpponentCaptureKing() {
        ChessPiece currKing = getKing(false);
        assert currKing != null;
        // Check if the opponent's piece can capture the current player's king
        return isIllegalForKing(currKing);
    }

    /**
     * Checks if the active pawn can be promoted or not,
     * if yes, then prepares the promotionPieces list and return true, otherwise false. <br>
     */
    private boolean canPawnPromote() {
        if (activePiece instanceof Pawn && ((currentColor == WHITE && activePiece.getRow() == 0) || (currentColor == BLACK && activePiece.getRow() == 7))) {
            promotionPieces.clear();
            promotionPieces.add(new Queen(2, 9, currentColor));
            promotionPieces.add(new Bishop(3, 9, currentColor));
            promotionPieces.add(new Knight(4, 9, currentColor));
            promotionPieces.add(new Rook(5, 9, currentColor));
            return true;
        }
        return false;
    }

    /**
     * Promoting the pawn, and switching the player for next turn.
     */
    private void promotingPawn() {
        if (mouse.isPressed()) {
            for (ChessPiece piece : promotionPieces) {
                if (piece.getCol() == mouse.getX() / SQUARE_SIZE && piece.getRow() == mouse.getY() / SQUARE_SIZE) {
                    if (piece instanceof Queen)
                        simPieces.add(new Queen(activePiece.getRow(), activePiece.getCol(), currentColor));
                    else if (piece instanceof Bishop)
                        simPieces.add(new Bishop(activePiece.getRow(), activePiece.getCol(), currentColor));
                    else if (piece instanceof Knight)
                        simPieces.add(new Knight(activePiece.getRow(), activePiece.getCol(), currentColor));
                    else if (piece instanceof Rook)
                        simPieces.add(new Rook(activePiece.getRow(), activePiece.getCol(), currentColor));

                    simPieces.remove(activePiece);  // remove the pawn which is being promoted
                    copyPieces(simPieces, pieces);
                    activePiece = null;
                    promotePawn = false;
                    switchingPlayer();  // switch the player for the next turn
                }
            }
        }
    }

    /**
     * The king is not in checkmate if:
     * <ul>
     *     <li> The king can move to a safe square which is not under attack </li>
     *     <li> The king or ally piece could capture the checking piece </li>
     *     <li> An ally piece could be placed to block the check. </li>
     * </ul>
     * Check if the opponent's king is in checkmate or not.
     * If the king is in checkmate, then the game is over and the current player has won. <br>
     */
    private boolean isCheckMate() {
        ChessPiece oppKing = getKing(true);
        assert oppKing != null;
        if (simulateKingMoves(oppKing)) // if opponent's king has any valid move, hence not a checkmate
            return false;   // opponent's king has some move (capture by king also considered)

        /* Checking if opponent could actually block the attack or capture it using any other piece than king to stay in the game */
        int colDiff = Math.abs(oppKing.getCol() - checkPiece.getCol());
        int rowDiff = Math.abs(oppKing.getRow() - checkPiece.getRow());

        // Now, we need to consider the attacking path & check if any opponent's piece could block the attack or capture the attacking piece
        if (colDiff == 0) { // vertical attack
            int step = checkPiece.getRow() < oppKing.getRow() ? 1 : -1;    // 1 for downwards and -1 for upwards
            for (int r = checkPiece.getRow(); r != oppKing.getRow(); r += step) {
                for (ChessPiece piece : simPieces) {
                    if (piece.getColor() != currentColor && piece != oppKing && piece.canMove(r, checkPiece.getCol()))
                        return false;
                }
            }
        } else if (rowDiff == 0) {  // horizontal attack
            int step = checkPiece.getCol() < oppKing.getCol() ? 1 : -1;   // 1 for rightwards and -1 for leftwards
            for (int c = checkPiece.getCol(); c != oppKing.getCol(); c += step) {
                for (ChessPiece piece : simPieces) {
                    if (piece.getColor() != currentColor && piece != oppKing && piece.canMove(checkPiece.getRow(), c))
                        return false;
                }
            }
        } else {    // diagonal attack
            int rowStep = checkPiece.getRow() < oppKing.getRow() ? 1 : -1;  // 1 for downwards and -1 for upwards
            int colStep = checkPiece.getCol() < oppKing.getCol() ? 1 : -1;  // 1 for rightwards and -1 for leftwards
            for (int r = checkPiece.getRow(), c = checkPiece.getCol(); r != oppKing.getRow(); r += rowStep, c += colStep) {
                for (ChessPiece piece : simPieces) {
                    if (piece.getColor() != currentColor && piece != oppKing && piece.canMove(r, c))
                        return false;
                }
            }
        }

        return true;    // opponent's king is in checkmate
    }

    /**
     * Simulate the king's moves to check if the king has any move to make, such that it is not in check.
     */
    private boolean simulateKingMoves(ChessPiece king) {
        if (checkIfKingStillHasAnyMove(king, -1, 0)) return true;   // up
        if (checkIfKingStillHasAnyMove(king, -1, 1)) return true;   // up-right
        if (checkIfKingStillHasAnyMove(king, 0, 1)) return true;    // right
        if (checkIfKingStillHasAnyMove(king, 1, 1)) return true;    // down-right
        if (checkIfKingStillHasAnyMove(king, 1, 0)) return true;    // down
        if (checkIfKingStillHasAnyMove(king, -1, -1)) return true;  // down-left
        if (checkIfKingStillHasAnyMove(king, 0, -1)) return true;   // left
        if (checkIfKingStillHasAnyMove(king, -1, -1)) return true;  // up-left
        return false;
    }

    /**
     * Check if the king still has any move to make, such that it is not in check.
     */
    private boolean checkIfKingStillHasAnyMove(ChessPiece king, int rowPlus, int colPlus) {
        boolean hasValidMove = false;

        king.setRow(king.getRow() + rowPlus);
        king.setCol(king.getCol() + colPlus);

        /* Temporary updating the king's position and checking if safe position is available or not */
        if (king.canMove(king.getRow(), king.getCol())) {
            if (king.getHittingPiece() != null)  // hitting opponent's piece, which could be a safe move without a check
                simPieces.remove(king.getHittingPiece());
            if (!isIllegalForKing(king))  // no opponent's piece could reach at this king's position
                hasValidMove = true;
        }

        /* Restoring the king's original state, which was before this method call */
        king.resetPosition();
        copyPieces(pieces, simPieces);

        return hasValidMove;
    }

    private boolean isStalemateCondition() {
        int totalPiecesInHand = 0;
        int totalPiecesOfOpponent = 0;
        for (ChessPiece piece : simPieces) {
            if (piece.getColor() != currentColor) totalPiecesOfOpponent++;
            if (piece.getColor() == currentColor) totalPiecesInHand++;
        }


        // If the opponent has only one piece left (king), and the king can't move to any safe position
        if (totalPiecesOfOpponent == 1) {
            if (totalPiecesInHand == 1) return true; // only two kings are left on the board

            ChessPiece opponentKing = getKing(true);
            assert opponentKing != null;
            if (!simulateKingMoves(opponentKing)) return true;
        }
        return false;
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
            if (activePiece.isSameSquare()) {
                g2d.setColor(Color.LIGHT_GRAY);
            } else if (pieceCanMove || isValidSquare)
                g2d.setColor(Color.GREEN);
            else {
                g2d.setColor(Color.RED);
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.7f));
            g2d.fillRect(activePiece.getCol() * SQUARE_SIZE, activePiece.getRow() * SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
            activePiece.drawPiece(g2d); // Draw the selected piece, after drawing the board and other pieces
        }

        /* STATUS MESSAGE */
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Trebuchet MS", Font.PLAIN, 30));
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        if (promotePawn) {  // Pawn promotion status message and pieces options
            g2d.drawString("~Promote Pawn~", 840, 150);
            for (ChessPiece piece : promotionPieces)
                g2d.drawImage(piece.getImage(), piece.calcX(piece.getCol()), piece.calcY(piece.getRow()),
                        SQUARE_SIZE, SQUARE_SIZE, null);
        } else if (isGameOver) {    // Game Over status message
            String winner = currentColor == WHITE ? "White Wins!" : "Black Wins!";
            g2d.setColor(Color.BLUE);
            g2d.setFont(new Font("Trebuchet MS", Font.BOLD, 90));
            g2d.drawString("~~Game Over~~", 60, 350);
            g2d.drawString(winner, 150, 450);
        } else if (isStalemate) {    // Stalemate status message
            g2d.setColor(Color.DARK_GRAY);
            g2d.setFont(new Font("Trebuchet MS", Font.TRUETYPE_FONT, 90));
            g2d.drawString("~~Stalemate~~", 100, 425);
        } else {    // Player's turn status message
            if ((currentColor == WHITE)) {
                g2d.drawString(">> White's Turn", 840, 550);
                if (checkPiece != null && checkPiece.getColor() == BLACK) {
                    g2d.setColor(Color.YELLOW);
                    g2d.drawString("Check!", 840, 600);
                }
            } else {
                g2d.drawString(">> Black's Turn", 840, 250);
                if (checkPiece != null && checkPiece.getColor() == WHITE) {
                    g2d.setColor(Color.YELLOW);
                    g2d.drawString("Check!", 840, 300);
                }
            }
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
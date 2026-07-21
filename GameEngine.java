//AT THE BOTTOM OF THIS FILE YOU WILL FIND THE MAIN METHOD FOR RUNNING THE GAME IN TERMINAL MODE. TO LAUNCH THE GUI, RUN THE MAIN METHOD IN GUI.java INSTEAD.

import javax.swing.*;
import java.util.Scanner;

/**
 * The central controller for a Hasami Shogi game.
 * Manages the board, turn order, win conditions, and bridges between
 * the terminal interface and the Swing GUI. Supports both PvP and PvC modes.
 */
public class GameEngine {
    private board gameBoard;
    private position selectedPiece = null;
    private GUI gameGUI;

    // Black goes first
    private String currentTurn = "B";
    private boolean isGameOver = false;

    private String gameMode = "PvP";
    private computer computerPlayer = null;

    /**
     * Creates a new GameEngine. If a GUI is provided it runs in graphical mode;
     * passing null causes it to fall back to terminal output.
     *
     * @param gui the GUI instance to link with, or null for terminal-only play
     */
    public GameEngine(GUI gui) {
        this.gameBoard = new board();
        this.gameGUI = gui;

        // Only print to terminal here if we are NOT using the GUI window
        if (gameGUI == null) {
            gameBoard.displayBoard();
            System.out.println("It is now " + currentTurn + "'s turn.");
        }
    }

    /**
     * Sets the game mode (either "PvP" or "PvC").
     * Call this before the game starts so the engine knows whether
     * to trigger the computer's turn automatically.
     *
     * @param mode "PvP" for two human players, "PvC" for player vs computer
     */
    public void setGameMode(String mode) {
        this.gameMode = mode;
    }

    /**
     * Assigns a computer player instance to handle the automated side.
     * Only relevant when the game mode is "PvC".
     *
     * @param comp the computer player object to use, or null to clear it
     */
    public void setComputerPlayer(computer comp) {
        this.computerPlayer = comp;
    }

    /**
     * Completely resets the game to its initial state — new board, Black's turn,
     * no winner, no selected piece. Refreshes the GUI if one is attached.
     */
    public void restartGame() {
        this.gameBoard = new board();
        this.currentTurn = "B";
        this.isGameOver = false;
        this.selectedPiece = null;

        if (gameGUI != null) {
            gameGUI.refreshScreen();
        } else {
            System.out.println("Game reset completely! It is now B's turn.");
            gameBoard.displayBoard();
        }
    }

    /**
     * Runs the full terminal-based game loop, including mode selection and restart prompts.
     * Handles all input validation and keeps looping until the user decides to quit.
     */
    public void startTerminalGame() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("");

            // Ask for PvC or PvP first in terminal version
            while (true) {
                System.out.print("Choose mode - type 1 for PvP (player vs player) or 2 for PvC (vs computer): ");
                String choice = scanner.nextLine().trim();
                if (choice.equals("1")) {
                    gameMode = "PvP";
                    computerPlayer = null; // Reset computer player instance if switching back to PvP
                    System.out.println("Starting Player vs Player mode!");
                    break;
                } else if (choice.equals("2")) {
                    gameMode = "PvC";
                    computerPlayer = new computer("W"); // Computer takes white pieces
                    System.out.println("Starting Player vs Computer mode!");
                    break;
                } else {
                    System.out.println("Invalid choice! Type 1 or 2.");
                }
            }

            // Show initial layout for terminal play
            gameBoard.displayBoard();
            System.out.println("It is now " + currentTurn + "'s turn.");

            // Core gameplay loop
            while (!isGameOver) {
                // If PvC mode and it is the computer's turn, run it automatically
                if (gameMode.equals("PvC") && currentTurn.equals("W")) {
                    makeComputerMove();
                    continue;
                }

                position textSelectedPiece = null;

                // Loop for picking a valid start spot
                while (true) {
                    System.out.print("Where would you like to start (example: B1): ");
                    String input = scanner.nextLine().trim();

                    // Check if they typed an invalid format or length
                    if (input.length() != 2 || !Character.isLetter(input.charAt(0)) || !Character.isDigit(input.charAt(1))) {
                        System.out.println("Invalid input! Please enter a coordinate like A4.");
                        continue;
                    }

                    char rowChar = Character.toUpperCase(input.charAt(0));
                    int colNum = Character.getNumericValue(input.charAt(1));

                    int r = rowChar - 'A';
                    int c = colNum - 1;

                    // If they type something way off the grid
                    if (r < 0 || r > 8 || c < 0 || c > 8) {
                        System.out.println("Out of bounds! Please enter a coordinate like A4.");
                        continue;
                    }

                    position piece = gameBoard.getPiece(r, c);
                    // Make sure there is a piece there and it is their own color
                    if (piece == null || !piece.getColor().equals(currentTurn)) {
                        System.out.println("You can't start there — select one of your own pieces.");
                        continue;
                    }

                    // Found a valid piece — exit this inner loop
                    textSelectedPiece = piece;
                    break;
                }

                // Loop for picking a valid destination
                while (true) {
                    System.out.print("Where would you like to move to (example: A1): ");
                    String input = scanner.nextLine().trim();

                    // Format check for the end target
                    if (input.length() != 2 || !Character.isLetter(input.charAt(0)) || !Character.isDigit(input.charAt(1))) {
                        System.out.println("Invalid input! Please enter a coordinate like A4.");
                        continue;
                    }

                    char rowChar = Character.toUpperCase(input.charAt(0));
                    int colNum = Character.getNumericValue(input.charAt(1));

                    int r = rowChar - 'A';
                    int c = colNum - 1;

                    if (r < 0 || r > 8 || c < 0 || c > 8) {
                        System.out.println("Out of bounds! Please enter a coordinate like A4.");
                        continue;
                    }

                    // Create target position to test if the board allows it
                    position targetSpot = new position("A", 1, "");
                    targetSpot.setRowIndex(r);
                    targetSpot.setColIndex(c);

                    // Check if the move is legal
                    if (gameBoard.isValidMove(textSelectedPiece, targetSpot)) {
                        System.out.println("Valid move!");
                        gameBoard.movePiece(textSelectedPiece, targetSpot);

                        // Show the updated board right after moving
                        gameBoard.displayBoard();

                        System.out.println("Black Captures: " + gameBoard.getBlackCapturedCount());
                        System.out.println("White Captures: " + gameBoard.getWhiteCapturedCount());

                        // Check if the game is over
                        checkWinConditions();

                        // Switch turns if the game is still going
                        if (!isGameOver) {
                            if (currentTurn.equals("B")) {
                                currentTurn = "W";
                            } else {
                                currentTurn = "B";
                            }
                            System.out.println("It is now " + currentTurn + "'s turn.");
                        }
                        break; // Break this inner loop to start the next turn
                    } else {
                        System.out.println("Invalid move! You can't go there.");
                    }
                }
            }

            // Ask to restart the terminal game
            System.out.print("Game finished! Would you like to restart? (yes/no): ");
            String response = scanner.nextLine().trim().toLowerCase();

            if (response.equals("yes") || response.equals("y")) {
                // Reset everything cleanly so it starts completely fresh
                this.gameBoard = new board();
                this.currentTurn = "B";
                this.isGameOver = false;
                this.selectedPiece = null;
                this.computerPlayer = null;
                System.out.println("\n--- RESTARTING GAME ---");
            } else {
                System.out.println("Thanks for playing! See you later.");
                break; // Exit the outer loop and end the app cleanly
            }
        }
    }

    /**
     * Handles a click on the board grid at position (r, c) during GUI play.
     * First click selects a piece; second click attempts to move it there.
     * Shows a popup dialog for invalid actions instead of printing to the terminal.
     *
     * @param r the row index that was clicked (0–8)
     * @param c the column index that was clicked (0–8)
     */
    public void processButtonClick(int r, int c) {
        // Stop everything if the game is already done
        if (isGameOver) {
            JOptionPane.showMessageDialog(gameGUI.getWindow(), "The game is already over!");
            return;
        }

        position clickedSpot = gameBoard.getPiece(r, c);

        // First click — selecting a piece
        if (selectedPiece == null) {
            if (clickedSpot != null) {
                // Make sure they don't click the wrong color
                if (clickedSpot.getColor().equals(currentTurn)) {
                    selectedPiece = clickedSpot;
                } else {
                    JOptionPane.showMessageDialog(gameGUI.getWindow(),
                        "It is " + currentTurn + "'s turn! You cannot select the other player's piece.");
                }
            }
        }
        // Second click — moving the piece
        else {
            position targetSpot = new position("A", 1, "");
            targetSpot.setRowIndex(r);
            targetSpot.setColIndex(c);

            if (gameBoard.isValidMove(selectedPiece, targetSpot)) {
                gameBoard.movePiece(selectedPiece, targetSpot);

                selectedPiece = null; // Deselect the piece after moving
                gameGUI.refreshScreen(); // Redraw the board to show changes

                // Check if the game is over
                checkWinConditions();

                // Game not done yet — switch turns
                if (!isGameOver) {
                    if (currentTurn.equals("B")) {
                        currentTurn = "W";
                    } else {
                        currentTurn = "B";
                    }

                    // If PvC mode, trigger the computer's move automatically
                    if (gameMode.equals("PvC") && currentTurn.equals("W")) {
                        makeComputerMove();
                    }
                }

            } else {
                // Wrong move popup
                JOptionPane.showMessageDialog(gameGUI.getWindow(), "Invalid move! Please try again.");
                selectedPiece = null;
            }
        }
    }

    /**
     * Runs the computer's turn seamlessly in the background.
     * After the move, refreshes the screen and checks for a win,
     * then hands the turn back to the human player.
     */
    private void makeComputerMove() {
        if (computerPlayer != null && !isGameOver) {
            if (gameGUI == null) {
                System.out.println("The computer is making its move...");
            }

            computerPlayer.makeMove(gameBoard);

            // Only print text boards if we are playing entirely in terminal view
            if (gameGUI == null) {
                gameBoard.displayBoard();
                System.out.println("Black Captures: " + gameBoard.getBlackCapturedCount());
                System.out.println("White Captures: " + gameBoard.getWhiteCapturedCount());
            }

            // Redraw screen so the GUI updates
            if (gameGUI != null) {
                gameGUI.refreshScreen();
            }

            checkWinConditions();

            // Give the turn back to the human Black player
            if (!isGameOver) {
                currentTurn = "B";
                if (gameGUI == null) {
                    System.out.println("It is now " + currentTurn + "'s turn.");
                }
            }
        }
    }

    /**
     * Checks whether either player has reached 5 captures and ends the game if so.
     * Shows a popup in GUI mode or a terminal message in text mode.
     */
    private void checkWinConditions() {
        // Check if anyone has taken 5 pieces
        if (gameBoard.getBlackCapturedCount() >= 5) {
            if (gameGUI != null) {
                JOptionPane.showMessageDialog(gameGUI.getWindow(),
                    "GAME OVER! Black wins with " + gameBoard.getBlackCapturedCount() + " captures!");
            } else {
                System.out.println("GAME OVER! Black wins with " + gameBoard.getBlackCapturedCount() + " captures!");
            }
            isGameOver = true;
        } else if (gameBoard.getWhiteCapturedCount() >= 5) {
            if (gameGUI != null) {
                JOptionPane.showMessageDialog(gameGUI.getWindow(),
                    "GAME OVER! White wins with " + gameBoard.getWhiteCapturedCount() + " captures!");
            } else {
                System.out.println("GAME OVER! White wins with " + gameBoard.getWhiteCapturedCount() + " captures!");
            }
            isGameOver = true;
        }
    }

    /**
     * Returns the piece at the given board position.
     * Used by the GUI to know what to draw on each cell.
     *
     * @param r the row index (0–8)
     * @param c the column index (0–8)
     * @return the position object at that cell, or null if empty
     */
    public position getPieceAt(int r, int c) {
        return gameBoard.getPiece(r, c);
    }

    /**
     * Returns the color of whichever player's turn it currently is.
     *
     * @return "B" for Black or "W" for White
     */
    public String getCurrentTurn() {
        return currentTurn;
    }

    /**
     * Returns the total number of pieces Black has captured this game.
     *
     * @return Black's capture count
     */
    public int getBlackCapturedCount() {
        return gameBoard.getBlackCapturedCount();
    }

    /**
     * Returns the total number of pieces White has captured this game.
     *
     * @return White's capture count
     */
    public int getWhiteCapturedCount() {
        return gameBoard.getWhiteCapturedCount();
    }

    /**
     * Entry point for running the game in terminal mode (no GUI window).
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        GameEngine engine = new GameEngine(null); // No GUI panel passed here
        engine.startTerminalGame();
    }
}
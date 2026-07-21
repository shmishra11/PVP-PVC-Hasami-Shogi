//AT THE BOTTOM OF THIS FILE YOU WILL FIND THE MAIN METHOD FOR RUNNING THE GAME IN GUI MODE. TO LAUNCH THE TERMINAL VERSION, RUN THE MAIN METHOD IN GameEngine.java INSTEAD.

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * This class handles the graphical user interface for the Hasami Shogi game.
 * It sets up the main window, creates the 9x9 board of buttons, and manages 
 * the side panel that displays the score and reset button.
 */
public class GUI {
    private GameEngine engine;
    private JButton[][] buttonGrid;
    private JFrame window; 
    
    // Labels for the scoreboard on the right
    private JLabel blackCountLabel;
    private JLabel whiteCountLabel;

    /**
     * Sets up the game window, launches the mode selector, and arranges the 
     * layout so the board sits in the middle and the score is on the side.
     */
    public GUI() {
        engine = new GameEngine(this);
        buttonGrid = new JButton[9][9];
        
        window = new JFrame("Hasami Shogi");
        
        // Ask the player what game mode they want before the game starts
        selectGameMode();
        
        // Main panel to hold everything
        JPanel wrapperPanel = new JPanel();
        wrapperPanel.setLayout(new BorderLayout());
        
        // The 9x9 board grid
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new GridLayout(9, 9));
        
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                JButton button = new JButton();
                buttonGrid[r][c] = button;
                
                final int row = r;
                final int col = c;
                
                // When a button is clicked, tell the engine which spot it was
                button.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent e) {
                        engine.processButtonClick(row, col);
                    }
                });
                
                mainPanel.add(button);
            }
        }

        // Sidebar for the scoreboard
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("CAPTURES");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Initialize score labels
        blackCountLabel = new JLabel("Black Captures: 0");
        blackCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        whiteCountLabel = new JLabel("White Captures: 0");
        whiteCountLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        sidePanel.add(titleLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        sidePanel.add(blackCountLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        sidePanel.add(whiteCountLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Reset button
        JButton restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 12));
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                engine.restartGame();
            }
        });
        sidePanel.add(restartButton);

        // Put board in the middle and scoreboard on the right
        wrapperPanel.add(mainPanel, BorderLayout.CENTER);
        wrapperPanel.add(sidePanel, BorderLayout.EAST);

        refreshScreen();

        window.add(wrapperPanel);
        window.setSize(1000, 800);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setVisible(true);
    }

    /**
     * Loops through every square on the board to update the button text and colors 
     * based on the current state of the game. It also updates the capture counts.
     */
    public void refreshScreen() {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                position piece = engine.getPieceAt(r, c);
                JButton button = buttonGrid[r][c];
                
                if (piece != null) {
                    button.setText(piece.getColor());
                    
                    if (piece.getColor().equals("B")) {
                        button.setForeground(Color.BLACK);
                    } else if (piece.getColor().equals("W")) {
                        button.setForeground(Color.GRAY);
                    }
                } else {
                    button.setText(".");
                    button.setForeground(Color.BLACK);
                }
            }
        }
        
        // Refresh the score labels whenever the board changes
        if (engine != null && blackCountLabel != null && whiteCountLabel != null) {
            blackCountLabel.setText("Black Captures: " + engine.getBlackCapturedCount());
            whiteCountLabel.setText("White Captures: " + engine.getWhiteCapturedCount());
        }
    }

    /**
     * Pops up a dialog box at the start of the game so the user can choose 
     * between playing against a friend or playing against the computer.
     */
    private void selectGameMode() {
        String[] options = {"Player vs Player", "Player vs Computer"};
        int choice = JOptionPane.showOptionDialog(
            null,
            "Choose game mode:",
            "Hasami Shogi",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            null
        );
        
        if (choice == 1) {
            engine.setGameMode("PvC");
            engine.setComputerPlayer(new computer("W"));
        } else {
            engine.setGameMode("PvP");
        }
    }

    /**
     * A simple way to get the window reference so other parts of the game 
     * (like the Engine) can show popup alerts on top of the GUI.
     * * @return The active JFrame window.
     */
    public JFrame getWindow() {
        return window;
    }

    /**
     * The main entry point to start the game.
     */
    public static void main(String[] args) {
        new GUI(); 
    }
}
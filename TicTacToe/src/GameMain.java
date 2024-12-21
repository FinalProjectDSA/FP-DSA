/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #7
 * 1 - 5026231011 - William Bryan Pangestu
 * 2 - 5026231022 - Tiffany Catherine Prasetya
 * 3 - 5026231081 - Oryza Reynaleta Wibowo
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;
    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG_LIGHT = Color.WHITE;
    public static final Color COLOR_BG_STATUS_LIGHT = new Color(216, 216, 216);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);
    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private JButton restartButton;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem exitItem;
    private JMenuItem backToHomePage; // New menu item for AI toggle
    private JMenuItem aiToggleItem; // New menu item for AI toggle
    private BackgroundMusic backgroundMusic;
    private boolean gameOverPopupShown = false;
    private boolean aiEnabled = false; // Toggle AI mode
    private String crossPlayerName = null; // Store the name for Cross
    private String noughtPlayerName = null; // Store the name for Nought
    private ImageIcon backgroundImage;
    public GameMain(boolean aienabled) {
        this.aiEnabled = aienabled;
        setLayout(new BorderLayout());
        setBackground(COLOR_BG_LIGHT);
        // Initialize background music
        backgroundMusic = new BackgroundMusic("audio/bgm2.wav"); // path to your audio file
        // Game board panel
        JPanel gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                board.paint(g);
                if (backgroundImage != null) {
                    // Draw the GIF, scaling it to the size of the panel
                    g.drawImage(backgroundImage.getImage(), 0, 0, getWidth(), getHeight(), this);
                }
            }
        };
        gameBoardPanel.setPreferredSize(new Dimension(Board.CANVAS_WIDTH, Board.CANVAS_HEIGHT));
        gameBoardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = e.getY() / Cell.SIZE;
                int col = e.getX() / Cell.SIZE;
                if (currentState == State.PLAYING) {
                    if (row >= 0 && row < Board.ROWS && col >= 0 && col < Board.COLS
                            && board.cells[row][col].content == Seed.NO_SEED) {
                        currentState = board.stepGame(currentPlayer, row, col);
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        // If playing against AI, AI will play after human's turn
                        if (aiEnabled && currentState == State.PLAYING) {
                            makeAIMove();
                        }
                        if (currentState == State.PLAYING) SoundEffect.WUP.play();
                    }
                }
                repaint();
            }
        });
        // Status bar and button panel
        statusBar = new JLabel();
        statusBar.setFont(FONT_STATUS);
        statusBar.setOpaque(true);
        statusBar.setBackground(COLOR_BG_STATUS_LIGHT);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setHorizontalAlignment(JLabel.LEFT);
        // Restart button
        restartButton = new JButton("Restart Game");
        restartButton.setFont(new Font("Arial", Font.PLAIN, 14));
        restartButton.setBackground(null);
        restartButton.setFocusPainted(false);
        restartButton.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        restartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                newGame();
                repaint();
            }
        });
        // Create a panel to hold the status bar and restart button
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusBar, BorderLayout.CENTER);
        statusPanel.add(restartButton, BorderLayout.EAST);
        // Menu bar and items
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        exitItem = new JMenuItem("Exit");
        // Initialize the AI toggle menu item with dynamic text based on the current AI mode
        aiToggleItem = new JMenuItem(aiEnabled ? "Play vs Human" : "Play vs AI");
        backToHomePage = new JMenuItem("Back to Home Page");
        backToHomePage.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                BackgroundMusic.stop();
                // Dispose of the parent frame before navigating
                Window parentWindow = SwingUtilities.getWindowAncestor(statusBar);
                if (parentWindow instanceof JFrame) {
                    parentWindow.dispose(); // Close the current JFrame
                }
                // Open the game options page
                HomePage home = new HomePage();
                home.setVisible(true);
            }
        });
        // Toggle AI mode
        aiToggleItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                aiEnabled = !aiEnabled; // Toggle AI mode
                if (aiEnabled) {
                    aiToggleItem.setText("Play vs Human");
                } else {
                    aiToggleItem.setText("Play vs AI");
                }
                newGame(); // Reset the game after toggling AI mode
            }
        });
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        menu.add(aiToggleItem); // Add AI toggle to the menu
        menu.add(backToHomePage);
        menu.add(exitItem);
        menuBar.add(menu);
        // Add components
        add(gameBoardPanel, BorderLayout.CENTER);
        add(statusPanel, BorderLayout.PAGE_END);
        // Set the menu bar
        JFrame frame = new JFrame(TITLE);
        frame.setJMenuBar(menuBar);
        frame.setContentPane(this);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        initGame();
        newGame();
        javax.swing.Timer timer = new javax.swing.Timer(50, new java.awt.event.ActionListener() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                repaint();
            }
        });
        timer.start();
    }
    // Initialize the game
    public void initGame() {
        board = new Board();
    }
    // Start a new game
    public void newGame() {
        backgroundMusic.play();
        if (!aiEnabled) {
            // Ask for player names if AI is not enabled
            if (crossPlayerName == null || noughtPlayerName == null) {
                crossPlayerName = JOptionPane.showInputDialog("Enter name for Player 1 (Cross, default 'X'): ");
                noughtPlayerName = JOptionPane.showInputDialog("Enter name for Player 2 (Nought, default 'O'): ");
                if (crossPlayerName == null || crossPlayerName.isEmpty()) crossPlayerName = "Player 1 (X)";
                if (noughtPlayerName == null || noughtPlayerName.isEmpty()) noughtPlayerName = "Player 2 (O)";
                Seed.CROSS.setDisplayName(crossPlayerName);
                Seed.NOUGHT.setDisplayName(noughtPlayerName);
            }
            // Add character selection popup
            String crossCharacter = chooseCharacter(crossPlayerName + ", select your character:");
            String noughtCharacter = chooseCharacter(noughtPlayerName + ", select your character (different from Player 1):");
            while (crossCharacter.equals(noughtCharacter)) {
                JOptionPane.showMessageDialog(null, "Both players cannot select the same character. Please choose again.");
                noughtCharacter = chooseCharacter(noughtPlayerName + ", select your character (different from Player 1):");
            }
            Seed.CROSS.setImageFileName(crossCharacter);
            Seed.NOUGHT.setImageFileName(noughtCharacter);
        } else {
            // AI mode: Only ask for Player 1's name
            if (crossPlayerName == null) {
                crossPlayerName = JOptionPane.showInputDialog("Enter name for Player 1 (Cross, default 'X'): ");
                if (crossPlayerName == null || crossPlayerName.isEmpty()) crossPlayerName = "Player 1 (X)";
                Seed.CROSS.setDisplayName(crossPlayerName);
            }
            // Add character selection for Player 1
            String crossCharacter = chooseCharacter(crossPlayerName + ", select your character:");
            Seed.CROSS.setImageFileName(crossCharacter);
            // Default AI character
            String aiCharacter = chooseCharacter("AI, select its character (different from Player 1):");
            while (crossCharacter.equals(aiCharacter)) {
                JOptionPane.showMessageDialog(null, "AI cannot select the same character as Player 1. Please choose again.");
                aiCharacter = chooseCharacter("AI, select its character (different from Player 1):");
            }
            Seed.NOUGHT.setDisplayName("Computer");
            Seed.NOUGHT.setImageFileName(aiCharacter);
        }
        // Reset the game board
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }
        // Set the initial player and game state
        currentPlayer = Seed.CROSS; // Player 1 (Cross) starts the game
        currentState = State.PLAYING;
        statusBar.setText(currentPlayer.getDisplayName() + "'s Turn");
    }
    private String chooseCharacter(String message) {
        // Character options (names)
        String[] options = {"Oryza", "William", "Tiffany"};
        // Corresponding image file paths for each character
        String[] imagePaths = {"image/ory.gif", "image/webe.gif", "image/tiffy.gif"};
        // Show the dialog with character names
        int choice = JOptionPane.showOptionDialog(
                null,
                message,
                "Character Selection",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                options,
                options[0]
        );
        // Ensure a valid selection is made
        if (choice < 0) {
            JOptionPane.showMessageDialog(null, "You must select a character to proceed.");
            return chooseCharacter(message); // Prompt again if no selection is made
        }
        // Return the corresponding image file path based on the selected character
        return imagePaths[choice];
    }
    // Method to handle AI's move
    private void makeAIMove() {
        if (currentState != State.PLAYING) return;
        // Use Minimax for AI move
        AIPlayerMinimax aiPlayer = new AIPlayerMinimax(board); // AI plays as NOUGHT (O)
        int[] aiMove = aiPlayer.move();
        int row = aiMove[0];
        int col = aiMove[1];
        currentState = board.stepGame(currentPlayer, row, col);
        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
        if (currentState == State.PLAYING) {
            statusBar.setText(currentPlayer.getDisplayName() + "'s Turn");
        }
        repaint();
    }
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);  // Always call super for proper painting
        board.paint(g);
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText(currentPlayer.getDisplayName() + "'s Turn");
        } else if (currentState != null && !gameOverPopupShown) {  // Check if the game is over
            showGameOverPopup();  // Show the game over popup only if the game is actually over
        }
    }
    public void showGameOverPopup() {
        BackgroundMusic.stop();
        gameOverPopupShown = true;  // Prevent the pop-up from showing again
        // Update the status bar message based on the game result
        if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            SoundEffect.TIE.play();
            statusBar.setText("It's a Draw! Click to play again.");
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            SoundEffect.WIN.play();
            statusBar.setText("'" + Seed.CROSS.getDisplayName() + "' Won! Super cool!");
        } else if (currentState == State.NOUGHT_WON && !aiEnabled) {
            statusBar.setForeground(Color.RED);
            SoundEffect.WIN.play();
            statusBar.setText("'" + Seed.NOUGHT.getDisplayName() + "' Won! Super cool!");
        } else if (currentState == State.NOUGHT_WON && aiEnabled) {
            statusBar.setForeground(Color.RED);
            SoundEffect.LOSE.play();
            statusBar.setText("'" + Seed.NOUGHT.getDisplayName() + "' Lose! Try Again.");
        }
        // Create a JButton for "Play Again" or game restart
        JButton playAgainButton = new JButton("Play Again");
        playAgainButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Dispose of the parent frame before navigating
                Window parentWindow = SwingUtilities.getWindowAncestor(statusBar);
                if (parentWindow instanceof JFrame) {
                    parentWindow.dispose(); // Close the current JFrame
                }
                // Open the game options page
                HomePage home = new HomePage();
                home.setVisible(true);
                home.showGameOptions();
            }
        });
        // Create a JButton for exiting the game
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Dispose of the parent frame before exiting
                Window parentWindow = SwingUtilities.getWindowAncestor(statusBar);
                if (parentWindow instanceof JFrame) {
                    parentWindow.dispose(); // Close the current JFrame
                }
                System.exit(0); // Exit the application
            }
        });
        // Display options in a dialog
        Object[] options = {playAgainButton, exitButton};
        JOptionPane.showOptionDialog(
                this,
                "Do you want to play again?",
                "Game Over",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                options,
                options[0]
        );
    }
    // Home page constructor and methods
    public static class HomePage extends JFrame {
        private JPanel buttonPanel;
        private JButton startButton;
        private JButton playerVsPlayerButton;
        private JButton playerVsAiButton;
        private JButton connect4Button; // Button for Connect 4
        private JButton exitButton;
        public HomePage() {
            // Set up the frame
            setTitle("Tic Tac Toe - Home");
            setSize(400, 400);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());
            // Add a background image
            URL imgURL = getClass().getClassLoader().getResource("image/homepage.gif");
            JLabel background = null;
            if (imgURL == null) {
                System.err.println("Error: Couldn't find the background image file!");
                getContentPane().setBackground(Color.LIGHT_GRAY); // Fallback background
            } else {
                background = new JLabel(new ImageIcon(imgURL));
                background.setLayout(new BorderLayout());
                add(background);
            }
            // Button panel
            buttonPanel = new JPanel(new GridLayout(4, 1, 10, 10)); // Initially, we have only 3 buttons
            buttonPanel.setOpaque(false);
            buttonPanel.setLayout(new GridLayout(4, 1, 5, 8));  // Adjusted for 3 buttons initially
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(100, 100, 60, 100));
            buttonPanel.setPreferredSize(new Dimension(100, 40));

            // Start Game button
            startButton = new JButton();
            startButton.setIcon(new ImageIcon(getClass().getResource("image/TTT.png"))); // Replace with your actual image file path
            startButton.setBorderPainted(false); // Remove default button border
            startButton.setContentAreaFilled(false); // Remove default button background
            startButton.setFocusPainted(false); // Remove focus border
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    showGameOptions();
                }
            });
            // Instructions button
            JButton instructionsButton = new JButton();
            instructionsButton.setIcon(new ImageIcon(getClass().getResource("image/INFO.png"))); // Replace with your actual image file path
            instructionsButton.setBorderPainted(false); // Remove default button border
            instructionsButton.setContentAreaFilled(false); // Remove default button background
            instructionsButton.setFocusPainted(false); // Remove focus border
            instructionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "Tic Tac Toe Instructions:\n\n"
                            + "1. The game is played on a 3x3 grid.\n"
                            + "2. Player X always goes first.\n"
                            + "3. Players take turns placing their marks (X or O).\n"
                            + "4. The first player to get 3 marks in a row (horizontally, vertically, or diagonally) wins.\n"
                            + "5. If all 9 squares are filled and no player has 3 in a row, the game ends in a draw.\n\n"
                            + "Connect 4 Instructions:\n\n"
                            + "1. The game is played on a 7x6 grid.\n"
                            + "2. Players take turns dropping their discs into one of the columns.\n"
                            + "3. The first player to get 4 discs in a row (horizontally, vertically, or diagonally) wins.\n"
                            + "4. If all 6 rows are filled and no player has 4 in a row, the game ends in a draw.");
                }
            });

            // Connect 4 button
            connect4Button = new JButton();
            connect4Button.setIcon(new ImageIcon(getClass().getResource("image/C4.png"))); // Replace with your actual image file path
            connect4Button.setBorderPainted(false); // Remove default button border
            connect4Button.setContentAreaFilled(false); // Remove default button background
            connect4Button.setFocusPainted(false); // Remove focus border
            connect4Button.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();  // Close the home page
                    new TTTGraphics(); // Start Connect 4 game
                }
            });

            // Exit button
            exitButton = new JButton();
            exitButton.setIcon(new ImageIcon(getClass().getResource("image/EXIT.png"))); // Replace with your actual image file path
            exitButton.setBorderPainted(false); // Remove default button border
            exitButton.setContentAreaFilled(false); // Remove default button background
            exitButton.setFocusPainted(false); // Remove focus border
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                    System.exit(0); // Exit the application
                }
            });

            buttonPanel.add(startButton);
            buttonPanel.add(connect4Button); // Add Connect 4 button
            buttonPanel.add(instructionsButton);
            buttonPanel.add(exitButton);
            background.add(buttonPanel, BorderLayout.CENTER);
        }
        // Method to show game options after clicking Start Game
        private void showGameOptions() {
            // Clear the current button panel
            buttonPanel.removeAll();

            // Create the new game mode buttons
            playerVsPlayerButton = new JButton();
            playerVsPlayerButton.setIcon(new ImageIcon(getClass().getResource("image/PVP.png"))); // Replace with your actual image file path
            playerVsPlayerButton.setBorderPainted(false); // Remove default button border
            playerVsPlayerButton.setContentAreaFilled(false); // Remove default button background
            playerVsPlayerButton.setFocusPainted(false); // Remove focus border
            playerVsPlayerButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();  // Close the home page
                    GameMain game = new GameMain(false);
                }
            });

            playerVsAiButton = new JButton();
            playerVsAiButton.setIcon(new ImageIcon(getClass().getResource("image/PVAI.png"))); // Replace with your actual image file path
            playerVsAiButton.setBorderPainted(false); // Remove default button border
            playerVsAiButton.setContentAreaFilled(false); // Remove default button background
            playerVsAiButton.setFocusPainted(false); // Remove focus border
            playerVsAiButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();  // Close the home page
                    GameMain game = new GameMain(true);
                }
            });

            // Add the new buttons to the panel
            buttonPanel.setLayout(new GridLayout(3, 1, 10, 10));  // Adjusted for 3 buttons initially
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(120, 100, 60, 100));
            buttonPanel.setPreferredSize(new Dimension(100, 40));
            buttonPanel.add(playerVsPlayerButton);  // Add Player vs Player button
            buttonPanel.add(playerVsAiButton);  // Add Player vs AI button
            buttonPanel.add(exitButton);  // Re-add Exit button
            // Refresh the panel to show the new buttons
            buttonPanel.revalidate();
            buttonPanel.repaint();
        }
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HomePage homePage = new HomePage();
                homePage.setVisible(true);
            }
        });
    }
}

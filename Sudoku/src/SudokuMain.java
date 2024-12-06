/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #1
 * 1 - 5026231011 - William Bryan Pangestu
 * 2 - 5026231022 - Tiffany Catherine Prasetya
 * 3 - 5026231081 - Oryza Reynaleta Wibowo
 */

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class SudokuMain extends JFrame {
    private static final long serialVersionUID = 1L;

    // private variables
    GameBoardPanel board = new GameBoardPanel(this);
    JButton restartGame = new JButton("Restart Game");
    private JLabel scoreLabel = new JLabel("Score: "); // Score label
    private String playerName = ""; // Player's name

    private JLabel playerNameLabel = new JLabel("Player name: " + playerName);

    private int score = 0; // Player's score

    // Constructor
    public SudokuMain() {
        showHomePage();

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());

        cp.add(board, BorderLayout.CENTER);

        JMenuBar menuBar = createMenuBar();
        setJMenuBar(menuBar);

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sudoku");
        setVisible(true);

        cp.add(restartGame, BorderLayout.SOUTH);

        restartGame.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                board.restartGame();
                resetScore();
            }
        });

        // Initialize the game board to start the game
        board.newGame();

        pack();
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Sudoku");
        setVisible(true);
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // File menu
        JMenu fileMenu = new JMenu("File");
        menuBar.add(fileMenu);

        // New Game Menu Item
        JMenuItem newGameItem = new JMenuItem("New Game");
        newGameItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                board.newGame();
                resetScore(); // Reset score when a new game starts
            }
        });
        fileMenu.add(newGameItem);

        // Exit Menu Item
        JMenuItem exitItem = new JMenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        fileMenu.add(exitItem);

        // Create a JPanel to hold the score label with some space
        JPanel scorePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0)); // Align to left with some margin
        scorePanel.setOpaque(false); // Make sure the background is transparent
        scorePanel.add(scoreLabel); // Add the score label to the panel
        JPanel namePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        namePanel.add(playerNameLabel); // Add the player name label to the panel

        // Add the score panel to the menu bar
        menuBar.add(scorePanel);
        menuBar.add(namePanel);

        return menuBar;
    }


    private void showHomePage() {
        JDialog homeDialog = new JDialog(this, "Welcome to Sudoku", true);

        // Load the background image as a resource
        ImageIcon backgroundIcon = new ImageIcon(getClass().getResource("/homepagebg.jpg"));
        Image backgroundImage = backgroundIcon.getImage();

        // Create a custom panel to display the background image
        JPanel backgroundPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(new BorderLayout());

        JLabel welcomeLabel = new JLabel(
                "<html><h1 style='color:white;'>Welcome to Sudoku!</h1><p style='color:white;'>How to play:</p>" +
                        "<p style='color:white;'>Fill in a 9x9 grid so that each column, row, and 3x3 subgrid " +
                        "contains all digits from 1 to 9.</p></html>", SwingConstants.CENTER);
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        welcomeLabel.setVerticalAlignment(SwingConstants.CENTER);

        JTextField nameField = new JTextField();
        JButton startGameButton = new JButton("Start Game");

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BorderLayout());
        inputPanel.add(nameField, BorderLayout.CENTER);
        inputPanel.add(startGameButton, BorderLayout.EAST);

        startGameButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                playerName = nameField.getText().trim();
                if (playerName.isEmpty()) {
                    playerName = "Player"; // Jika tidak ada nama yang dimasukkan, gunakan "Player"
                }
                playerNameLabel.setText("Player name: " + playerName); // Perbarui label nama pemain
                homeDialog.dispose(); // Tutup dialog setelah nama dimasukkan
            }
        });

        backgroundPanel.add(welcomeLabel, BorderLayout.CENTER);
        backgroundPanel.add(inputPanel, BorderLayout.SOUTH);

        homeDialog.setContentPane(backgroundPanel);
        homeDialog.setSize(800, 500);
        homeDialog.setLocationRelativeTo(null);
        homeDialog.setVisible(true);
    }

    // Update the score
    public void updateScore(int points) {
        score += points;
        scoreLabel.setText("Score: " + score); // Update the score label
    }

    // Reset the score
    public void resetScore() {
        score = 0;
        scoreLabel.setText("Score: " + score); // Reset the score label
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new SudokuMain();
            }
        });
    }
}

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.net.URL;

public class GameMain extends JPanel {
    private static final long serialVersionUID = 1L;

    public static final String TITLE = "Tic Tac Toe";
    public static final Color COLOR_BG_LIGHT = Color.WHITE;
    public static final Color COLOR_BG_DARK = new Color(30, 30, 30);
    public static final Color COLOR_BG_STATUS_LIGHT = new Color(216, 216, 216);
    public static final Color COLOR_BG_STATUS_DARK = new Color(50, 50, 50);
    public static final Color COLOR_CROSS = new Color(239, 105, 80);
    public static final Color COLOR_NOUGHT = new Color(64, 154, 225);
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.PLAIN, 14);

    private Board board;
    private State currentState;
    private Seed currentPlayer;
    private JLabel statusBar;
    private JButton restartButton;
    private JMenuBar menuBar;
    private JMenu menu;
    private JMenuItem themeItem;
    private JMenuItem exitItem;

    private boolean isDarkMode = false;

    private String crossPlayerName = null;  // Store the name for Cross
    private String noughtPlayerName = null;  // Store the name for Nought

    public GameMain() {
        setLayout(new BorderLayout());
        setBackground(COLOR_BG_LIGHT);

        // Game board panel
        JPanel gameBoardPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                board.paint(g);
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
                        if (currentState == State.PLAYING) SoundEffect.WUP.play();
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        // Status bar
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

        // Status bar and button panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(COLOR_BG_STATUS_LIGHT);
        bottomPanel.add(statusBar, BorderLayout.CENTER);
        bottomPanel.add(restartButton, BorderLayout.EAST);

        // Menu bar and items
        menuBar = new JMenuBar();
        menu = new JMenu("Menu");
        themeItem = new JMenuItem("Switch to Dark Mode");
        exitItem = new JMenuItem("Exit");

        themeItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                toggleTheme();
            }
        });

        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        menu.add(themeItem);
        menu.add(exitItem);
        menuBar.add(menu);

        // Add components
        add(gameBoardPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.PAGE_END);

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
    }

    // Toggle between light and dark theme
    private void toggleTheme() {
        isDarkMode = !isDarkMode;
        if (isDarkMode) {
            setBackground(COLOR_BG_DARK);
            statusBar.setBackground(COLOR_BG_STATUS_DARK);
            statusBar.setForeground(Color.WHITE);
            themeItem.setText("Switch to Light Mode");
        } else {
            setBackground(COLOR_BG_LIGHT);
            statusBar.setBackground(COLOR_BG_STATUS_LIGHT);
            statusBar.setForeground(Color.BLACK);
            themeItem.setText("Switch to Dark Mode");
        }
        repaint();
    }

    public void initGame() {
        board = new Board();
    }

    public void newGame() {
        // Ask for player names or symbols only if they are not set
        if (crossPlayerName == null || noughtPlayerName == null) {
            crossPlayerName = JOptionPane.showInputDialog("Enter name for Player 1 (Cross, default 'X'):");
            noughtPlayerName = JOptionPane.showInputDialog("Enter name for Player 2 (Nought, default 'O'):");

            // Default to 'X' and 'O' if the input is empty
            if (crossPlayerName == null || crossPlayerName.isEmpty()) crossPlayerName = "Player 1 (X)";
            if (noughtPlayerName == null || noughtPlayerName.isEmpty()) noughtPlayerName = "Player 2 (O)";

            // Update Seed enum display names dynamically
            Seed.CROSS.setDisplayName(crossPlayerName);
            Seed.NOUGHT.setDisplayName(noughtPlayerName);
        }

        // Reset the game board
        for (int row = 0; row < Board.ROWS; ++row) {
            for (int col = 0; col < Board.COLS; ++col) {
                board.cells[row][col].content = Seed.NO_SEED;
            }
        }

        // Set initial player and game state
        currentPlayer = Seed.CROSS;  // Player 1 (Cross) starts the game
        currentState = State.PLAYING;
        statusBar.setText(currentPlayer.getDisplayName() + "'s Turn");
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Paint the board
        board.paint(g);

        // Set status based on game state
        if (currentState == State.PLAYING) {
            statusBar.setForeground(Color.BLACK);
            statusBar.setText(currentPlayer.getDisplayName() + "'s Turn");
        } else if (currentState == State.DRAW) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("It's a Draw! Click to play again.");
            SoundEffect.TIE.play();
        } else if (currentState == State.CROSS_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'" + Seed.CROSS.getDisplayName() + "' Won! Click to play again.");
            SoundEffect.WIN.play();
        } else if (currentState == State.NOUGHT_WON) {
            statusBar.setForeground(Color.RED);
            statusBar.setText("'" + Seed.NOUGHT.getDisplayName() + "' Won! Click to play again.");
            SoundEffect.WIN.play();
        }
    }

    // Home page constructor and methods
    public static class HomePage extends JFrame {

        public HomePage() {
            // Set up the frame
            setTitle("Tic Tac Toe - Home");
            setSize(500, 500);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout());

            // Add a background image
            URL imgURL = getClass().getClassLoader().getResource("image/background");
            JLabel background = null;
            if (imgURL == null) {
                System.err.println("Error: Couldn't find the background image file!");
                getContentPane().setBackground(Color.LIGHT_GRAY); // Fallback background
            } else {
                background = new JLabel(new ImageIcon(imgURL));
                background.setLayout(new BorderLayout());
                add(background);
            }

            // Title label
            JLabel titleLabel = new JLabel("Tic Tac Toe");
            titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
            titleLabel.setFont(new Font("Poppins", Font.BOLD, 40));
            titleLabel.setForeground(Color.WHITE);
            background.add(titleLabel, BorderLayout.NORTH);

            // Button panel
            JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
            buttonPanel.setOpaque(false);
            buttonPanel.setLayout(new GridLayout(3, 1, 20, 20));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(50, 100, 50, 100));


            // Start button
            JButton startButton = new JButton("Start Game");
            startButton.setFont(new Font("Poppins", Font.BOLD, 18));
            startButton.setBackground(new Color(255, 255, 255));
            startButton.setForeground(Color.BLACK);
            startButton.setFocusPainted(false);
            startButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    dispose();
                    new GameMain();
                }
            });

            // Instructions button
            JButton instructionsButton = new JButton("Instructions");
            instructionsButton.setFont(new Font("Poppins", Font.BOLD, 18));
            instructionsButton.setBackground(new Color(255, 255, 255));
            instructionsButton.setForeground(Color.BLACK);
            instructionsButton.setFocusPainted(false);
            instructionsButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, "Tic Tac Toe Instructions:\n\n"
                            + "1. The game is played on a 3x3 grid.\n"
                            + "2. Player X always goes first.\n"
                            + "3. Players take turns placing their marks (X or O).\n"
                            + "4. The first player to get 3 marks in a row (horizontally, vertically, or diagonally) wins.\n"
                            + "5. If all 9 squares are filled and no player has 3 in a row, the game ends in a draw.");
                }
            });

            // Exit button
            JButton exitButton = new JButton("Exit");
            exitButton.setFont(new Font("Poppins", Font.BOLD, 18));
            exitButton.setBackground(new Color(255, 255, 255));
            exitButton.setForeground(Color.BLACK);
            exitButton.setFocusPainted(false);
            exitButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0);
                }
            });

            buttonPanel.add(startButton);
            buttonPanel.add(instructionsButton);
            buttonPanel.add(exitButton);

            background.add(buttonPanel, BorderLayout.CENTER);
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
}

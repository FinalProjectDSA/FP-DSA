import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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
        restartButton.addActionListener(e -> {
            newGame();
            repaint();
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

        themeItem.addActionListener(e -> toggleTheme());
        exitItem.addActionListener(e -> System.exit(0));

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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(GameMain::new);
    }
}

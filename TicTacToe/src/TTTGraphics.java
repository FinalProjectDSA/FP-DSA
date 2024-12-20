import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Timer;
import java.util.TimerTask;

public class TTTGraphics extends JFrame {
    private static final long serialVersionUID = 1L;

    public static final int ROWS = 6;
    public static final int COLS = 7;
    public static final int CELL_SIZE = 100;
    public static final int BOARD_WIDTH = CELL_SIZE * COLS;
    public static final int BOARD_HEIGHT = CELL_SIZE * ROWS;
    public static final int GRID_WIDTH = 8;
    public static final int GRID_WIDTH_HALF = GRID_WIDTH / 2;
    public static final Color COLOR_BG = new Color(24, 119, 242); // Blue background
    public static final Color COLOR_GRID = new Color(0, 0, 102);  // Dark grid
    public static final Color COLOR_CROSS = new Color(255, 0, 0); // Red for Player 1
    public static final Color COLOR_NOUGHT = new Color(255, 255, 0); // Yellow for Player 2
    public static final Font FONT_STATUS = new Font("OCR A Extended", Font.BOLD, 16);

    public enum State {
        PLAYING, DRAW, CROSS_WON, NOUGHT_WON
    }

    private State currentState;
    private int[] highlightCells = null;

    public enum Seed {
        CROSS, NOUGHT, NO_SEED
    }

    private Seed currentPlayer;
    private Seed[][] board;

    private GamePanel gamePanel;
    private JLabel statusBar;
    private JButton resetButton;
    private JButton exitButton;

    private int redScore = 0;
    private int yellowScore = 0;
    private JLabel scoreLabel;

    private Timer turnTimer;
    private JLabel timerLabel;
    private int timeRemaining;

    public TTTGraphics() {
        initGame();

        gamePanel = new GamePanel();
        gamePanel.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        gamePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int mouseX = e.getX();
                int colSelected = mouseX / CELL_SIZE;

                if (currentState == State.PLAYING) {
                    if (colSelected >= 0 && colSelected < COLS) {
                        for (int row = ROWS - 1; row >= 0; row--) {
                            if (board[row][colSelected] == Seed.NO_SEED) {
                                board[row][colSelected] = currentPlayer;
                                currentState = stepGame(currentPlayer, row, colSelected);
                                currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                                resetTimer();
                                break;
                            }
                        }
                    }
                } else {
                    newGame();
                }
                repaint();
            }
        });

        statusBar = new JLabel("Welcome to Connect Four!");
        statusBar.setFont(FONT_STATUS);
        statusBar.setHorizontalAlignment(SwingConstants.CENTER);
        statusBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        statusBar.setForeground(Color.WHITE);

        scoreLabel = new JLabel("Red: 0 | Yellow: 0");
        scoreLabel.setFont(FONT_STATUS);
        scoreLabel.setForeground(Color.WHITE);

        timerLabel = new JLabel("Time Remaining: 10");
        timerLabel.setFont(FONT_STATUS);
        timerLabel.setForeground(Color.WHITE);

        resetButton = new JButton("Restart Game");
        resetButton.setFont(FONT_STATUS);
        resetButton.setBackground(Color.WHITE);
        resetButton.setForeground(COLOR_GRID);
        resetButton.addActionListener(e -> newGame());

        exitButton = new JButton("Exit Game");
        exitButton.setFont(FONT_STATUS);
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(COLOR_GRID);
        exitButton.addActionListener(e -> System.exit(0));

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusBar, BorderLayout.CENTER);
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(resetButton);
        buttonPanel.add(exitButton);
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        bottomPanel.setBackground(COLOR_BG);

        JPanel topPanel = new JPanel(new GridLayout(1, 2));
        topPanel.add(scoreLabel);
        topPanel.add(timerLabel);
        topPanel.setBackground(COLOR_BG);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(bottomPanel, BorderLayout.SOUTH);
        cp.add(topPanel, BorderLayout.NORTH);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setTitle("Connect Four");
        setVisible(true);

        newGame();
    }

    public void initGame() {
        board = new Seed[ROWS][COLS];
    }

    public void newGame() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                board[row][col] = Seed.NO_SEED;
            }
        }
        currentPlayer = Seed.CROSS;
        currentState = State.PLAYING;
        highlightCells = null;
        statusBar.setText("Red's Turn");
        updateScores();
        resetTimer();
        repaint();
    }

    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        board[selectedRow][selectedCol] = player;

        if (hasWon(player, selectedRow, selectedCol)) {
            if (player == Seed.CROSS) redScore++;
            else yellowScore++;
            return (player == Seed.CROSS) ? State.CROSS_WON : State.NOUGHT_WON;
        } else {
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    if (board[row][col] == Seed.NO_SEED) {
                        return State.PLAYING;
                    }
                }
            }
            return State.DRAW;
        }
    }

    public boolean hasWon(Seed theSeed, int rowSelected, int colSelected) {
        highlightCells = checkHighlightDirection(theSeed, rowSelected, colSelected);
        return highlightCells != null;
    }

    private int[] checkHighlightDirection(Seed theSeed, int row, int col) {
        int[][] directions = {{0, 1}, {1, 0}, {1, 1}, {1, -1}}; // {dx, dy} pairs
        for (int[] direction : directions) {
            int count = 1;
            int[] cells = new int[8];
            cells[0] = row;
            cells[1] = col;

            // Cek ke satu arah
            for (int i = 1; i < 4; i++) {
                int r = row + i * direction[0];
                int c = col + i * direction[1];
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                    cells[count * 2] = r;
                    cells[count * 2 + 1] = c;
                    count++;
                } else {
                    break;
                }
            }

            // Cek ke arah berlawanan
            for (int i = 1; i < 4; i++) {
                int r = row - i * direction[0];
                int c = col - i * direction[1];
                if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                    cells[count * 2] = r;
                    cells[count * 2 + 1] = c;
                    count++;
                } else {
                    break;
                }
            }

            if (count >= 4) return cells; // Jika ditemukan 4 berturut-turut
        }
        return null; // Tidak ditemukan 4 berturut-turut
    }

    private void updateScores() {
        scoreLabel.setText("Red: " + redScore + " | Yellow: " + yellowScore);
    }

    private void resetTimer() {
        if (turnTimer != null) turnTimer.cancel();
        timeRemaining = 10;
        timerLabel.setText("Time Remaining: " + timeRemaining);

        turnTimer = new Timer();
        turnTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (timeRemaining > 0) {
                    timeRemaining--;
                    SwingUtilities.invokeLater(() -> timerLabel.setText("Time Remaining: " + timeRemaining));
                } else {
                    turnTimer.cancel();
                    SwingUtilities.invokeLater(() -> {
                        currentPlayer = (currentPlayer == Seed.CROSS) ? Seed.NOUGHT : Seed.CROSS;
                        resetTimer();
                        repaint();
                    });
                }
            }
        }, 1000, 1000);
    }

    class GamePanel extends JPanel {
        private static final long serialVersionUID = 1L;

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            setBackground(COLOR_BG);

            g.setColor(COLOR_GRID);
            for (int row = 1; row < ROWS; ++row) {
                g.fillRoundRect(0, CELL_SIZE * row - GRID_WIDTH_HALF,
                        BOARD_WIDTH - 1, GRID_WIDTH, GRID_WIDTH, GRID_WIDTH);
            }
            for (int col = 1; col < COLS; ++col) {
                g.fillRoundRect(CELL_SIZE * col - GRID_WIDTH_HALF, 0,
                        GRID_WIDTH, BOARD_HEIGHT - 1, GRID_WIDTH, GRID_WIDTH);
            }

            Graphics2D g2d = (Graphics2D) g;
            for (int row = 0; row < ROWS; ++row) {
                for (int col = 0; col < COLS; ++col) {
                    int x1 = col * CELL_SIZE + CELL_SIZE / 8;
                    int y1 = row * CELL_SIZE + CELL_SIZE / 8;
                    if (board[row][col] == Seed.CROSS) {
                        g2d.setColor(COLOR_CROSS);
                        g2d.fillOval(x1, y1, CELL_SIZE - CELL_SIZE / 4, CELL_SIZE - CELL_SIZE / 4);
                    } else if (board[row][col] == Seed.NOUGHT) {
                        g2d.setColor(COLOR_NOUGHT);
                        g2d.fillOval(x1, y1, CELL_SIZE - CELL_SIZE / 4, CELL_SIZE - CELL_SIZE / 4);
                    }
                }
            }

            if (highlightCells != null) {
                g.setColor(Color.GREEN);
                for (int i = 0; i < highlightCells.length; i += 2) {
                    int x1 = highlightCells[i + 1] * CELL_SIZE + CELL_SIZE / 8;
                    int y1 = highlightCells[i] * CELL_SIZE + CELL_SIZE / 8;
                    g.fillOval(x1, y1, CELL_SIZE - CELL_SIZE / 4, CELL_SIZE - CELL_SIZE / 4);
                }
            }

            if (currentState == State.PLAYING) {
                statusBar.setText((currentPlayer == Seed.CROSS) ? "Red's Turn" : "Yellow's Turn");
            } else if (currentState == State.DRAW) {
                statusBar.setText("It's a Draw! Click Restart to play again.");
            } else if (currentState == State.CROSS_WON) {
                statusBar.setText("Red Won! Click Restart to play again.");
            } else if (currentState == State.NOUGHT_WON) {
                statusBar.setText("Yellow Won! Click Restart to play again.");
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(TTTGraphics::new);
    }
}

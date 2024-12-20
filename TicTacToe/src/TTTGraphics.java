import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

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

    public enum Seed {
        CROSS, NOUGHT, NO_SEED
    }

    private Seed currentPlayer;
    private Seed[][] board;

    private GamePanel gamePanel;
    private JLabel statusBar;
    private JButton resetButton;

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

        resetButton = new JButton("Restart Game");
        resetButton.setFont(FONT_STATUS);
        resetButton.setBackground(Color.WHITE);
        resetButton.setForeground(COLOR_GRID);
        resetButton.addActionListener(e -> newGame());

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(statusBar, BorderLayout.CENTER);
        bottomPanel.add(resetButton, BorderLayout.EAST);
        bottomPanel.setBackground(COLOR_BG);

        Container cp = getContentPane();
        cp.setLayout(new BorderLayout());
        cp.add(gamePanel, BorderLayout.CENTER);
        cp.add(bottomPanel, BorderLayout.SOUTH);

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
        statusBar.setText("Red's Turn");
        repaint();
    }

    public State stepGame(Seed player, int selectedRow, int selectedCol) {
        board[selectedRow][selectedCol] = player;

        if (hasWon(player, selectedRow, selectedCol)) {
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
        return checkDirection(theSeed, rowSelected, colSelected, 0, 1) ||
                checkDirection(theSeed, rowSelected, colSelected, 1, 0) ||
                checkDirection(theSeed, rowSelected, colSelected, 1, 1) ||
                checkDirection(theSeed, rowSelected, colSelected, 1, -1);
    }

    private boolean checkDirection(Seed theSeed, int row, int col, int rowIncrement, int colIncrement) {
        int count = 1;

        for (int i = 1; i < 4; i++) {
            int r = row + i * rowIncrement;
            int c = col + i * colIncrement;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                count++;
            } else {
                break;
            }
        }

        for (int i = 1; i < 4; i++) {
            int r = row - i * rowIncrement;
            int c = col - i * colIncrement;
            if (r >= 0 && r < ROWS && c >= 0 && c < COLS && board[r][c] == theSeed) {
                count++;
            } else {
                break;
            }
        }

        return count >= 4;
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

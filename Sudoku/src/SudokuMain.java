    import java.awt.*;
    import java.awt.event.*;
    import javax.swing.*;
    /**
     * The main Sudoku program
     */
    public class SudokuMain extends JFrame {
        private static final long serialVersionUID = 1L;  // to prevent serial warning

        // private variables
        GameBoardPanel board = new GameBoardPanel();
        JButton restartGame = new JButton("Restart Game");

        // Constructor
        public SudokuMain() {
            Container cp = getContentPane();
            cp.setLayout(new BorderLayout());

            cp.add(board, BorderLayout.CENTER);

            JMenuBar menuBar = createMenuBar();
            setJMenuBar(menuBar);

            pack();     // Pack the UI components, instead of using setSize()
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
            setTitle("Sudoku");
            setVisible(true);

            JPopupMenu popupMenu = new JPopupMenu();

            JOptionPane.showMessageDialog(this,
                    "Welcome to Sudoku! \n" +
                            "How to play this game: \nFill in a 9x9 grid so that each column, \neach row, and each of the nine 3x3 subgrids contains all of the digits from 1 to 9.",
                    "Welcome to Sudoku",
                    JOptionPane.INFORMATION_MESSAGE);

            cp.add(restartGame, BorderLayout.SOUTH);

            restartGame.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    board.restartGame();
                }
            });


            board.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    showPopup(e);
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    showPopup(e);
                }

                private void showPopup(MouseEvent e) {
                    if (e.isPopupTrigger()) { // Checks for right-click on different platforms
                        popupMenu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });


            // Initialize the game board to start the game
            board.newGame();

            pack();     // Pack the UI components, instead of using setSize()
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);  // to handle window-closing
            setTitle("Sudoku");
            setVisible(true);
        }



        // Create the menu bar
        private JMenuBar createMenuBar() {
            JMenuBar menuBar = new JMenuBar();

            // File menu
            JMenu fileMenu = new JMenu("File");
            menuBar.add(fileMenu);

            // New Game Menu Item
            JMenuItem newGameItem = new JMenuItem("New Game");
            newGameItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    board.newGame(); // Start a new game
                }
            });
            fileMenu.add(newGameItem);

            // Exit Menu Item
            JMenuItem exitItem = new JMenuItem("Exit");
            exitItem.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    System.exit(0); // Exit the game
                }
            });
            fileMenu.add(exitItem);
            return menuBar;
        }
        /** The entry main() entry method */
        public static void main(String[] args) {
            // [TODO 1] Check "Swing program template" on how to run
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    new SudokuMain(); // Create and show the GUI
                }
            });
        }
    }
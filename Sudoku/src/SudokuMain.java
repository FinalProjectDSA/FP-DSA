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
        JButton btnNewGame = new JButton("New Game");

        // Constructor
        public SudokuMain() {
            Container cp = getContentPane();
            cp.setLayout(new BorderLayout());

            cp.add(board, BorderLayout.CENTER);

            JPopupMenu popupMenu = new JPopupMenu();
            JMenuItem restartMenuItem = new JMenuItem("Restart Game");

            JOptionPane.showMessageDialog(this,
                    "Welcome to Sudoku! \n" +
                            "How to play this game: \nFill in a 9x9 grid so that each column, \neach row, and each of the nine 3x3 subgrids contains all of the digits from 1 to 9.",
                    "Welcome to Sudoku",
                    JOptionPane.INFORMATION_MESSAGE);

            restartMenuItem.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    board.newGame();
                }
            });

            cp.add(btnNewGame, BorderLayout.SOUTH);

            popupMenu.add(restartMenuItem);

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
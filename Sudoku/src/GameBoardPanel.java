import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.Border;

public class GameBoardPanel extends JPanel {
    private static final long serialVersionUID = 1L;  // to prevent serial warning
    private int incorrectGuesses = 0; // Counter for incorrect guesses
    private static final int MAX_INCORRECT_GUESSES = 3; // Maximum allowed incorrect guesses
    private SoundEffect soundEffects = new SoundEffect();

    // Define named constants for UI sizes
    public static final int CELL_SIZE = 60;   // Cell width/height in pixels
    public static final int BOARD_WIDTH  = CELL_SIZE * SudokuConstants.GRID_SIZE;
    public static final int BOARD_HEIGHT = CELL_SIZE * SudokuConstants.GRID_SIZE;
    // Board width/height in pixels

    // Define properties
    /** The game board composes of 9x9 Cells (customized JTextFields) */
    private Cell[][] cells = new Cell[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    /** It also contains a Puzzle with array numbers and isGiven */
    private Puzzle puzzle = new Puzzle();

    /** Constructor */
    public GameBoardPanel() {
        super.setLayout(new GridLayout(SudokuConstants.GRID_SIZE, SudokuConstants.GRID_SIZE));  // JPanel

        // Allocate the 2D array of Cell, and add to the JPanel
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col] = new Cell(row, col);
                cells[row][col].setBorder(createCellBorder(row, col)); // Set the custom border
                super.add(cells[row][col]);  // Add the cell to the panel
            }
        }

        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));

        // [TODO 3] Allocate a common listener as the ActionEvent listener for all the
        //  Cells (JTextFields)
        CellInputListener listener = new CellInputListener();

        // [TODO 4] Adds this common listener to all editable cells
        for(int r = 0; r < SudokuConstants.GRID_SIZE; ++r){
            for(int c = 0; c < SudokuConstants.GRID_SIZE; ++c){
                if(!puzzle.isGiven[r][c]){
                    cells[r][c].addActionListener(listener);
                }
            }
        }
        super.setPreferredSize(new Dimension(BOARD_WIDTH, BOARD_HEIGHT));
    }
    private Border createCellBorder(int row, int col) {
        // Define the thickness for the borders
        int top = (row % SudokuConstants.SUBGRID_SIZE == 0) ? 4 : 1;
        int left = (col % SudokuConstants.SUBGRID_SIZE == 0) ? 4 : 1;
        int bottom = (row == SudokuConstants.GRID_SIZE - 1) ? 4 : 1;
        int right = (col == SudokuConstants.GRID_SIZE - 1) ? 4 : 1;

        // Return a MatteBorder with the specified thickness
        return BorderFactory.createMatteBorder(top, left, bottom, right, Color.LIGHT_GRAY);
    }
    /**
     * Generate a new puzzle; and reset the game board of cells based on the puzzle.
     * You can call this method to start a new game.
     */
    public void newGame() {
        // Generate a new puzzle
        puzzle.newPuzzle();

        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }
    }

    public void restartGame(){
        puzzle.restartPuzzle();

        // Initialize all the 9x9 cells, based on the puzzle.
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                cells[row][col].newGame(puzzle.numbers[row][col], puzzle.isGiven[row][col]);
            }
        }
    }

    /**
     * Return true if the puzzle is solved
     * i.e., none of the cell have status of TO_GUESS or WRONG_GUESS
     */
    public boolean isSolved() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                if (cells[row][col].status == CellStatus.TO_GUESS || cells[row][col].status == CellStatus.WRONG_GUESS) {
                    return false;
                }
            }
        }
        return true;
    }

    // [TODO 2] Define a Listener Inner Class for all the editable Cells
    private class CellInputListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            // Get a reference of the JTextField that triggers this action event
            Cell sourceCell = (Cell)e.getSource();

            // Retrieve the int entered
            int numberIn = Integer.parseInt(sourceCell.getText());
            // For debugging
            System.out.println("You entered " + numberIn);

            /*
             * [TODO 5] (later - after TODO 3 and 4)
             * Check the numberIn against sourceCell.number.
             * Update the cell status sourceCell.status,
             * and re-paint the cell via sourceCell.paint().
             */
            if (numberIn == sourceCell.number) {
                sourceCell.status = CellStatus.CORRECT_GUESS;
                soundEffects.playCorrectSound(); // Play correct sound
            } else {
                sourceCell.status = CellStatus.WRONG_GUESS;
                incorrectGuesses++;
                soundEffects.playWrongSound(); // Play wrong sound
                if (incorrectGuesses >= MAX_INCORRECT_GUESSES) {
                    JOptionPane.showMessageDialog(null, "You've used all your chances! Game Over.", "Game Over", JOptionPane.WARNING_MESSAGE);
                    disableAllCells();
                    return;
                }
            }
            sourceCell.paint();   // re-paint this cell based on its status

            /*
             * [TODO 6] (later)
             * Check if the player has solved the puzzle after this move,
             *   by calling isSolved(). Put up a congratulation JOptionPane, if so.
             */
            if(isSolved()){
                JOptionPane.showMessageDialog(null, "Congratulations, you solved it!",
                        "Sudoku Solved", JOptionPane.INFORMATION_MESSAGE);
                int option = JOptionPane.showConfirmDialog(null, "Play Again?", "Restart", JOptionPane.YES_NO_OPTION);
                if(option == JOptionPane.YES_OPTION){
                    newGame();
                } else{
                    JOptionPane.showMessageDialog(null, "Thank You!");
                    System.exit(0); // Close the application
                }
            }
        }
    }
    private void disableAllCells() {
        for (int r = 0; r < SudokuConstants.GRID_SIZE; ++r) {
            for (int c = 0; c < SudokuConstants.GRID_SIZE; ++c) {
                cells[r][c].setEditable(false); // Disable editing
            }
        }
    }
}

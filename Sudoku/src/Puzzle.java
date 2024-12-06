import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Puzzle {
    // All variables have package access
    // The numbers on the puzzle
    int[][] numbers = new int[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];
    // The clues - isGiven (no need to guess) or need to guess
    boolean[][] isGiven = new boolean[SudokuConstants.GRID_SIZE][SudokuConstants.GRID_SIZE];

    private int level;

    // Constructor
    public Puzzle() {
        super();
    }

    // Generate a new puzzle given the number of cells to be guessed, which can be used
    //  to control the difficulty level.
    // This method shall set (or update) the arrays numbers and isGiven
    public void newPuzzle() {
        level = JOptionPane.showOptionDialog(null, "Choose your difficulty level", "Level", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[]{"Easy", "Medium", "Hard"}, null);
        // Generate a random Sudoku grid
        generateRandomSudoku();

        // Randomly make some cells "not given" (false) by removing their numbers

        randomizePuzzle();
    }

    public void restartPuzzle(){
        generateRandomSudoku();
        randomizePuzzle();
    }

    // Generates a random Sudoku board using a backtracking approach
    private void generateRandomSudoku() {
        // Use a helper method to fill the board with valid Sudoku numbers
        fillBoard(0, 0);
    }

    // Backtracking method to fill the Sudoku grid with valid numbers
    private boolean fillBoard(int row, int col) {
        if (row == SudokuConstants.GRID_SIZE - 1 && col == SudokuConstants.GRID_SIZE) {
            return true; // Board is completely filled
        }
        if (col == SudokuConstants.GRID_SIZE) {
            row++;
            col = 0; // Move to the next row
        }

        // Try placing a random number in the current cell
        List<Integer> numbersList = getShuffledNumbers(); // Shuffle numbers 1 to 9
        for (int num : numbersList) {
            if (isSafe(row, col, num)) {
                numbers[row][col] = num;
                if (fillBoard(row, col + 1)) {
                    return true; // Recursively attempt to fill the next cell
                }
                numbers[row][col] = 0; // Backtrack if no valid number
            }
        }
        return false; // No valid number found
    }

    // Checks if placing 'num' at (row, col) is safe
    private boolean isSafe(int row, int col, int num) {
        // Check the row and column for conflicts
        for (int i = 0; i < SudokuConstants.GRID_SIZE; i++) {
            if (numbers[row][i] == num || numbers[i][col] == num) {
                return false;
            }
        }

        // Check the 3x3 sub-grid for conflicts
        int startRow = row - row % SudokuConstants.SUBGRID_SIZE;
        int startCol = col - col % SudokuConstants.SUBGRID_SIZE;
        for (int i = 0; i < SudokuConstants.SUBGRID_SIZE; i++) {
            for (int j = 0; j < SudokuConstants.SUBGRID_SIZE; j++) {
                if (numbers[i + startRow][j + startCol] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    // Generate a shuffled list of numbers from 1 to 9 to randomize the order
    private List<Integer> getShuffledNumbers() {
        List<Integer> numbersList = new ArrayList<>();
        for (int i = 1; i <= SudokuConstants.GRID_SIZE; i++) {
            numbersList.add(i);
        }
        Collections.shuffle(numbersList); // Shuffle the numbers to randomize
        return numbersList;
    }

    // Randomly remove numbers from the grid to create the puzzle
    private void randomizePuzzle() {
        // Set all cells to "given" initially (true)
        for (int row = 0; row < SudokuConstants.GRID_SIZE; ++row) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; ++col) {
                isGiven[row][col] = true;
            }
        }

        int cellsToRemove = 0;

        // Calculate how many cells need to be removed (30% will be removed)
        if(level == 0) cellsToRemove = 5;
        else if(level == 1){
            cellsToRemove = SudokuConstants.GRID_SIZE*SudokuConstants.GRID_SIZE/5;
        } else{
            cellsToRemove = SudokuConstants.GRID_SIZE * SudokuConstants.GRID_SIZE/2;
        }

        Random rand = new Random();
        int removedCells = 0;

        // Randomly choose cells to remove until we reach the target number
        while (removedCells < cellsToRemove) {
            int row = rand.nextInt(SudokuConstants.GRID_SIZE);
            int col = rand.nextInt(SudokuConstants.GRID_SIZE);

            // If the cell is already a given cell, make it a guessed cell (remove it)
            if (isGiven[row][col]) {
                isGiven[row][col] = false;
                removedCells++;
            }
        }
    }

    // (For testing) Print the Sudoku board
    public void printBoard() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                System.out.print(numbers[row][col] + " ");
            }
            System.out.println();
        }
    }

    // (For testing) Print the "isGiven" grid (clues vs to-guess cells)
    public void printIsGiven() {
        for (int row = 0; row < SudokuConstants.GRID_SIZE; row++) {
            for (int col = 0; col < SudokuConstants.GRID_SIZE; col++) {
                System.out.print((isGiven[row][col] ? "T " : "F "));
            }
            System.out.println();
        }
    }
}
//(For advanced students) use singleton design pattern for this class


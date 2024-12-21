/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #7
 * 1 - 5026231011 - William Bryan Pangestu
 * 2 - 5026231022 - Tiffany Catherine Prasetya
 * 3 - 5026231081 - Oryza Reynaleta Wibowo
 */

import java.util.*;

/** AIPlayer using Minimax algorithm with alpha-beta pruning */
public class AIPlayerMinimax extends AIPlayer {

    /** Constructor with the given game board */
    public AIPlayerMinimax(Board board) {
        super(board);
    }

    /** Get next best move for computer. Return int[2] of {row, col} */
    @Override
    int[] move() {
        if (!isValidBoardState()) {
            throw new IllegalStateException("Invalid board state: no empty cells or corrupted game state!");
        }

        int[] result = minimax(2, mySeed, Integer.MIN_VALUE, Integer.MAX_VALUE); // depth, max-turn, alpha, beta
        if (result[1] == -1 || result[2] == -1) { // Handle invalid moves from minimax
            List<int[]> nextMoves = generateMoves();
            if (!nextMoves.isEmpty()) {
                int[] fallbackMove = nextMoves.get(0); // Fallback to the first valid move
                System.err.println("AI fallback move: " + Arrays.toString(fallbackMove));
                return fallbackMove;
            } else {
                throw new IllegalStateException("No valid moves available for AI!");
            }
        }

        System.out.println("AI calculated move: " + Arrays.toString(new int[]{result[1], result[2]}));
        return new int[]{result[1], result[2]}; // Return the valid move
    }

    /** Minimax (recursive) at level of depth for maximizing or minimizing player
     * with alpha-beta pruning. Return int[3] of {score, row, col} */
    private int[] minimax(int depth, Seed player, int alpha, int beta) {
        if (depth == 0 || isCloseToEndGame()) {
            return new int[]{evaluate(), -1, -1}; // Return evaluation if depth is zero or game close to end
        }

        List<int[]> nextMoves = generateMoves();
        if (nextMoves.isEmpty()) { // No valid moves left
            return new int[]{evaluate(), -1, -1};
        }

        int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestRow = -1;
        int bestCol = -1;

        for (int[] move : nextMoves) {
            cells[move[0]][move[1]].content = player; // Try this move

            if (player == mySeed) { // Maximizing player
                currentScore = minimax(depth - 1, oppSeed, alpha, beta)[0];
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestRow = move[0];
                    bestCol = move[1];
                }
                alpha = Math.max(alpha, bestScore);
            } else { // Minimizing player
                currentScore = minimax(depth - 1, mySeed, alpha, beta)[0];
                if (currentScore < bestScore) {
                    bestScore = currentScore;
                    bestRow = move[0];
                    bestCol = move[1];
                }
                beta = Math.min(beta, bestScore);
            }

            cells[move[0]][move[1]].content = Seed.NO_SEED; // Undo move

            if (alpha >= beta) break; // Alpha-beta pruning
        }

        return new int[]{bestScore, bestRow, bestCol};
    }

    /** Find all valid next moves.
     * Return List of moves in int[2] of {row, col} or empty list if gameover */
    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<>();

        // If the game is over, return an empty list
        if (hasWon(mySeed) || hasWon(oppSeed) || isBoardFull()) {
            return nextMoves; // No moves available
        }

        // Generate all valid moves
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    nextMoves.add(new int[]{row, col});
                }
            }
        }

        return nextMoves;
    }

    /** The heuristic evaluation function for the current board
     * Return +1000, +100, +10 for EACH 3-, 2-, 1-in-a-line for AI.
     * Return -1000, -100, -10 for EACH 3-, 2-, 1-in-a-line for opponent. */
    private int evaluate() {
        int score = 0;

        // Evaluate center position
        if (cells[1][1].content == mySeed) {
            score += 3; // Center is more valuable
        } else if (cells[1][1].content == oppSeed) {
            score -= 3;
        }

        // Evaluate all lines (rows, cols, diagonals)
        score += evaluateLine(0, 0, 0, 1, 0, 2); // Row 0
        score += evaluateLine(1, 0, 1, 1, 1, 2); // Row 1
        score += evaluateLine(2, 0, 2, 1, 2, 2); // Row 2
        score += evaluateLine(0, 0, 1, 0, 2, 0); // Col 0
        score += evaluateLine(0, 1, 1, 1, 2, 1); // Col 1
        score += evaluateLine(0, 2, 1, 2, 2, 2); // Col 2
        score += evaluateLine(0, 0, 1, 1, 2, 2); // Diagonal
        score += evaluateLine(0, 2, 1, 1, 2, 0); // Alternate diagonal

        return score;
    }

    /** The heuristic evaluation function for the given line of 3 cells */
    private int evaluateLine(int row1, int col1, int row2, int col2, int row3, int col3) {
        int score = 0;

        // First cell
        if (cells[row1][col1].content == mySeed) {
            score = 1;
        } else if (cells[row1][col1].content == oppSeed) {
            score = -1;
        }

        // Second cell
        if (cells[row2][col2].content == mySeed) {
            score = (score == 1) ? 10 : 1;
        } else if (cells[row2][col2].content == oppSeed) {
            score = (score == -1) ? -10 : -1;
        }

        // Third cell
        if (cells[row3][col3].content == mySeed) {
            score = (score > 0) ? score * 10 : 1;
        } else if (cells[row3][col3].content == oppSeed) {
            score = (score < 0) ? score * 10 : -1;
        }

        return score;
    }

    /** Returns true if the game is close to end and few moves are left */
    private boolean isCloseToEndGame() {
        int emptyCells = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    emptyCells++;
                }
            }
        }
        return emptyCells <= 3; // Consider the game close to end if <= 3 moves left
    }

    /** Returns true if the game board is full */
    private boolean isBoardFull() {
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    return false;
                }
            }
        }
        return true;
    }

    /** Returns true if the given player has won */
    private boolean hasWon(Seed thePlayer) {
        int pattern = 0b000000000; // 9-bit pattern for the 9 cells
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == thePlayer) {
                    pattern |= (1 << (row * COLS + col));
                }
            }
        }
        for (int winningPattern : winningPatterns) {
            if ((pattern & winningPattern) == winningPattern) return true;
        }
        return false;
    }

    private int[] winningPatterns = {
            0b111000000, 0b000111000, 0b000000111, // Rows
            0b100100100, 0b010010010, 0b001001001, // Columns
            0b100010001, 0b001010100               // Diagonals
    };

    /** Validate the current board state */
    private boolean isValidBoardState() {
        int emptyCells = 0;
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    emptyCells++;
                }
            }
        }
        return emptyCells > 0; // Ensure at least one cell is empty
    }
}

import java.util.*;

/** AIPlayer using Minimax algorithm */
public class AIPlayerMinimax extends AIPlayer {

    /** Constructor with the given game board */
    public AIPlayerMinimax(Board board) {
        super(board);
    }

    /** Get next best move for computer. Return int[2] of {row, col} */
    @Override
    int[] move() {
        int[] result = minimax(2, mySeed, Integer.MIN_VALUE, Integer.MAX_VALUE);
        // depth, max-turn, alpha, beta
        return new int[] {result[1], result[2]};   // row, col
    }

    /** Minimax (recursive) at level of depth for maximizing or minimizing player
     * with alpha-beta cut-off. Return int[3] of {score, row, col} */
    private int[] minimax(int depth, Seed player, int alpha, int beta) {
        if (depth == 0 || isCloseToEndGame()) {
            return new int[] {evaluate(), -1, -1};  // Return evaluation when depth is zero or end game
        }

        // Generate possible next moves in a list of int[2] of {row, col}.
        List<int[]> nextMoves = generateMoves();

        // mySeed is maximizing; while oppSeed is minimizing
        int bestScore = (player == mySeed) ? Integer.MIN_VALUE : Integer.MAX_VALUE;
        int currentScore;
        int bestRow = -1;
        int bestCol = -1;

        for (int[] move : nextMoves) {
            // Try this move for the current "player"
            cells[move[0]][move[1]].content = player;

            if (player == mySeed) {  // mySeed (computer) is maximizing player
                currentScore = minimax(depth - 1, oppSeed, alpha, beta)[0];
                if (currentScore > bestScore) {
                    bestScore = currentScore;
                    bestRow = move[0];
                    bestCol = move[1];
                }
                alpha = Math.max(alpha, bestScore);
            } else {  // oppSeed is minimizing player
                currentScore = minimax(depth - 1, mySeed, alpha, beta)[0];
                if (currentScore < bestScore) {
                    bestScore = currentScore;
                    bestRow = move[0];
                    bestCol = move[1];
                }
                beta = Math.min(beta, bestScore);
            }

            // Undo move
            cells[move[0]][move[1]].content = Seed.NO_SEED;

            // Alpha-Beta Pruning
            if (alpha >= beta) break;
        }

        return new int[] {bestScore, bestRow, bestCol};
    }

    /** Find all valid next moves.
     * Return List of moves in int[2] of {row, col} or empty list if gameover */
    private List<int[]> generateMoves() {
        List<int[]> nextMoves = new ArrayList<int[]>(); // allocate List

        // If gameover, i.e., no next move
        if (hasWon(mySeed) || hasWon(oppSeed)) {
            return nextMoves;   // return empty list
        }

        // Search for empty cells and add to the List
        for (int row = 0; row < ROWS; ++row) {
            for (int col = 0; col < COLS; ++col) {
                if (cells[row][col].content == Seed.NO_SEED) {
                    cells[row][col].content = oppSeed;  // Simulate opponent move
                    if (hasWon(oppSeed)) {
                        // High priority for blocking opponent's win
                        nextMoves.clear();
                        nextMoves.add(new int[] {row, col});
                        cells[row][col].content = Seed.NO_SEED;  // Undo simulation
                        return nextMoves;
                    }
                    cells[row][col].content = Seed.NO_SEED;  // Undo simulation
                    nextMoves.add(new int[] {row, col});
                }
            }
        }
        return nextMoves;
    }

    /** The heuristic evaluation function for the current board
     * Return +100, +10, +1 for EACH 3-, 2-, 1-in-a-line for computer.
     * -100, -10, -1 for EACH 3-, 2-, 1-in-a-line for opponent.
     * 0 otherwise */
    private int evaluate() {
        int score = 0;

        // Evaluate center position
        if (cells[1][1].content == mySeed) {
            score += 3;  // Center is more valuable
        } else if (cells[1][1].content == oppSeed) {
            score -= 3;
        }

        // Evaluate score for each of the 8 lines (rows, columns, diagonals)
        score += evaluateLine(0, 0, 0, 1, 0, 2);  // row 0
        score += evaluateLine(1, 0, 1, 1, 1, 2);  // row 1
        score += evaluateLine(2, 0, 2, 1, 2, 2);  // row 2
        score += evaluateLine(0, 0, 1, 0, 2, 0);  // col 0
        score += evaluateLine(0, 1, 1, 1, 2, 1);  // col 1
        score += evaluateLine(0, 2, 1, 2, 2, 2);  // col 2
        score += evaluateLine(0, 0, 1, 1, 2, 2);  // diagonal
        score += evaluateLine(0, 2, 1, 1, 2, 0);  // alternate diagonal

        return score;
    }

    /** The heuristic evaluation function for the given line of 3 cells
     * Return +100, +10, +1 for 3-, 2-, 1-in-a-line for computer.
     * -100, -10, -1 for 3-, 2-, 1-in-a-line for opponent.
     * 0 otherwise */
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
            if (score == 1) {   // cell1 is mySeed
                score = 10;
            } else if (score == -1) {  // cell1 is oppSeed
                return 0;
            } else {  // cell1 is empty
                score = 1;
            }
        } else if (cells[row2][col2].content == oppSeed) {
            if (score == -1) { // cell1 is oppSeed
                score = -10;
            } else if (score == 1) { // cell1 is mySeed
                return 0;
            } else {  // cell1 is empty
                score = -1;
            }
        }

        // Third cell
        if (cells[row3][col3].content == mySeed) {
            if (score > 0) {  // cell1 and/or cell2 is mySeed
                score *= 10;
            } else if (score < 0) {  // cell1 and/or cell2 is oppSeed
                return 0;
            } else {  // cell1 and cell2 are empty
                score = 1;
            }
        } else if (cells[row3][col3].content == oppSeed) {
            if (score < 0) {  // cell1 and/or cell2 is oppSeed
                score *= 10;
            } else if (score > 1) {  // cell1 and/or cell2 is mySeed
                return 0;
            } else {  // cell1 and cell2 are empty
                score = -1;
            }
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
        return emptyCells <= 3;  // If only 3 or fewer moves left, consider the game close to end
    }

    private int[] winningPatterns = {
            0b111000000, 0b000111000, 0b000000111, // rows
            0b100100100, 0b010010010, 0b001001001, // cols
            0b100010001, 0b001010100               // diagonals
    };

    /** Returns true if thePlayer wins */
    private boolean hasWon(Seed thePlayer) {
        int pattern = 0b000000000;  // 9-bit pattern for the 9 cells
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
}

/**
 * ES234317-Algorithm and Data Structures
 * Semester Ganjil, 2024/2025
 * Group Capstone Project
 * Group #7
 * 1 - 5026231011 - William Bryan Pangestu
 * 2 - 5026231022 - Tiffany Catherine Prasetya
 * 3 - 5026231081 - Oryza Reynaleta Wibowo
 */

import java.awt.*;
/**
 * The Cell class models each individual cell of the game board.
 */
public class Cell {
    // Define named constants for drawing
    public static final int SIZE = 120; // cell width/height (square)
    // Symbols (cross/nought) are displayed inside a cell, with padding from border
    public static final int PADDING = SIZE / 5;
    public static final int SEED_SIZE = SIZE - PADDING * 2;
    public static final int SEED_STROKE_WIDTH = 8; // pen's stroke width

    // Define properties (package-visible)
    /** Content of this cell (Seed.EMPTY, Seed.CROSS, or Seed.NOUGHT) */
    Seed content;
    /** Row and column of this cell */
    int row, col;

    /** Constructor to initialize this cell with the specified row and col */
    public Cell(int row, int col) {
        this.row = row;
        this.col = col;
        content = Seed.NO_SEED;
    }

    /** Reset this cell's content to EMPTY, ready for new game */
    public void newGame() {
        content = Seed.NO_SEED;
    }

    /** Paint itself on the graphics canvas, given the Graphics context */
    public void paint(Graphics g) {
        // Use Graphics2D which allows us to set the pen's stroke
        Graphics2D g2d = (Graphics2D)g;
        g2d.setStroke(new BasicStroke(SEED_STROKE_WIDTH,
                BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        // Draw the Seed if it is not empty
        int x1 = col * SIZE + PADDING;
        int y1 = row * SIZE + PADDING;
        if (content == Seed.CROSS || content == Seed.NOUGHT) {
            g.drawImage(content.getImage(), x1, y1, SEED_SIZE, SEED_SIZE, null);
        }
    }
}
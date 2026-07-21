/**
 * Represents the 9x9 Hasami Shogi game board.
 * Handles piece placement, movement validation, and capture logic
 * including standard sandwiches and corner traps.
 */
public class board {
    private static final int BOARD_SIZE = 9;
    private static final int[] direction_row = {-1, 1, 0, 0};
    private static final int[] direction_col = {0, 0, -1, 1};

    private position[][] grid;
    private int blackCapturedCount = 0;
    private int whiteCapturedCount = 0;

    /**
     * Creates a fresh board and sets up the starting piece layout.
     * White fills row A, Black fills row I.
     */
    public board() {
        grid = new position[BOARD_SIZE][BOARD_SIZE];
        initializeBoard();
    }

    /**
     * Checks whether the given row and column fall inside the 9x9 grid.
     *
     * @param row the row index to check (0–8)
     * @param col the column index to check (0–8)
     * @return true if both coordinates are within the valid range
     */
    private boolean isWithinBounds(int row, int col) {
        return row >= 0 && row < BOARD_SIZE && col >= 0 && col < BOARD_SIZE;
    }

    /**
     * Adds one capture to the running total for the attacking color.
     * Called every time a piece is removed from the board.
     *
     * @param color the color of the player who made the capture ("B" or "W")
     */
    private void incrementCaptureCount(String color) {
        if (color.equals("B")) {
            blackCapturedCount++;
        } else if (color.equals("W")) {
            whiteCapturedCount++;
        }
    }

    /**
     * Returns the color that is opposite to the one provided.
     * Used throughout capture logic to identify the victim side.
     *
     * @param attackerColor the color of the attacking player ("B" or "W")
     * @return the opponent's color string
     */
    private String getOpponentColor(String attackerColor) {
        if (attackerColor.equals("W")) {
            return "B";
        } else {
            return "W";
        }
    }

    /**
     * Returns the piece at the given board coordinates, or null if the cell is empty.
     *
     * @param r the row index (0–8)
     * @param c the column index (0–8)
     * @return the position object at that cell, or null if empty or out of bounds
     */
    public position getPiece(int r, int c) {
        if (!isWithinBounds(r, c)) {
            return null;
        }
        return grid[r][c];
    }

    /**
     * Clears the board and places all 9 White pieces on row A
     * and all 9 Black pieces on row I, matching the standard starting layout.
     */
    private void initializeBoard() {
        for (int r = 0; r < BOARD_SIZE; r++) {
            for (int c = 0; c < BOARD_SIZE; c++) {
                grid[r][c] = null;
            }
        }
        for (int c = 0; c < BOARD_SIZE; c++) {
            grid[0][c] = new position("A", c + 1, "W");
        }
        for (int c = 0; c < BOARD_SIZE; c++) {
            grid[BOARD_SIZE - 1][c] = new position("I", c + 1, "B");
        }
    }

    /** @return the total number of pieces captured by Black so far */
    public int getBlackCapturedCount() { return blackCapturedCount; }

    /** @return the total number of pieces captured by White so far */
    public int getWhiteCapturedCount() { return whiteCapturedCount; }

    /**
     * Prints the current board state to the terminal with row letters
     * and column numbers as headers. Empty cells show as dots.
     */
    public void displayBoard() {
        System.out.println("   1 2 3 4 5 6 7 8 9");
        for (int r = 0; r < BOARD_SIZE; r++) {
            char rowLetter = (char) ('A' + r);
            System.out.print(rowLetter + "  ");
            for (int c = 0; c < BOARD_SIZE; c++) {
                if (grid[r][c] == null) {
                    System.out.print(". ");
                } else {
                    System.out.print(grid[r][c].getColor() + " ");
                }
            }
            System.out.println();
        }
    }

    /**
     * Checks whether moving a piece from start to end is legal under Hasami Shogi rules.
     * The path must be horizontal or vertical, completely clear, and land on an empty cell.
     *
     * @param start the position the piece is currently sitting on
     * @param end   the target position the player wants to move to
     * @return true if the move is valid, false otherwise
     */
    public boolean isValidMove(position start, position end) {
        int startRow = start.getRowIndex();
        int startCol = start.getColIndex();
        int endRow = end.getRowIndex();
        int endCol = end.getColIndex();

        if (!isWithinBounds(startRow, startCol) || !isWithinBounds(endRow, endCol)) {
            return false;
        }
        if (grid[startRow][startCol] == null || grid[endRow][endCol] != null) {
            return false;
        }
        // Must move horizontally or vertically, not diagonally
        if (startRow != endRow && startCol != endCol) {
            return false;
        }
        // Check if path is clear (no pieces in the way)
        if (startRow == endRow) {
            int left = Math.min(startCol, endCol);
            int right = Math.max(startCol, endCol);
            for (int c = left + 1; c < right; c++) {
                if (grid[startRow][c] != null) return false;
            }
        } else {
            int top = Math.min(startRow, endRow);
            int bottom = Math.max(startRow, endRow);
            for (int r = top + 1; r < bottom; r++) {
                if (grid[r][startCol] != null) return false;
            }
        }
        return true;
    }

    /**
     * Moves a piece from the start position to the end position on the grid,
     * then checks all four directions for any captures triggered by the move.
     *
     * @param start the current position of the piece being moved
     * @param end   the destination position for the piece
     */
    public void movePiece(position start, position end) {
        int startRow = start.getRowIndex();
        int startCol = start.getColIndex();
        int endRow = end.getRowIndex();
        int endCol = end.getColIndex();

        position playerPiece = grid[startRow][startCol];
        playerPiece.setRow(end.getRow());
        playerPiece.setCol(end.getCol());

        grid[endRow][endCol] = playerPiece;
        grid[startRow][startCol] = null;

        checkCaptures(endRow, endCol, playerPiece.getColor());
    }

    /**
     * Triggers capture checks in all four directions from the square a piece just landed on.
     *
     * @param row           the row the piece just moved to
     * @param col           the column the piece just moved to
     * @param attackerColor the color of the piece that just moved
     */
    private void checkCaptures(int row, int col, String attackerColor) {
        String victimColor = getOpponentColor(attackerColor);

        for (int i = 0; i < direction_row.length; i++) {
            checkCapturesInDirection(row, col, i, attackerColor, victimColor);
        }
    }

    /**
     * Walks in one direction from the moved piece, counts consecutive enemy pieces,
     * then decides if they should be captured via sandwich or corner trap.
     *
     * @param row           the row of the piece that just moved
     * @param col           the column of the piece that just moved
     * @param direction     index into direction_row/direction_col (0=up, 1=down, 2=left, 3=right)
     * @param attackerColor the moving player's color
     * @param victimColor   the opponent's color
     */
    private void checkCapturesInDirection(int row, int col, int direction, String attackerColor, String victimColor) {
        int dRow = direction_row[direction];
        int dCol = direction_col[direction];

        int count = 0;
        int r = row + dRow;
        int c = col + dCol;

        // Walk and count contiguous enemy pieces in this direction
        while (isWithinBounds(r, c) && grid[r][c] != null && grid[r][c].getColor().equals(victimColor)) {
            count++;
            r += dRow;
            c += dCol;
        }

        if (count == 0) return;

        // The last enemy piece we stepped past before stopping
        int lastR = r - dRow;
        int lastC = c - dCol;

        boolean doCapture = false;

        // Case A: normal sandwich — our piece is right after the enemy line
        if (isWithinBounds(r, c) && grid[r][c] != null && grid[r][c].getColor().equals(attackerColor)) {
            doCapture = true;
        }

        // Case B: corner L-trap — enemy piece is in a corner, check perpendicular direction
        if (!doCapture && isCornerPosition(lastR, lastC)) {
            doCapture = checkCornerCapture(lastR, lastC, dRow, dCol, attackerColor, victimColor);
        }

        // Remove the victims if we found a valid sandwich or trap
        if (doCapture) {
            captureVictims(row + dRow, col + dCol, dRow, dCol, count, attackerColor);
        }
    }

    /**
     * Returns true if the given cell is one of the four corners of the board.
     *
     * @param r the row to check
     * @param c the column to check
     * @return true if (r, c) is a corner cell
     */
    private boolean isCornerPosition(int r, int c) {
        return (r == 0 || r == BOARD_SIZE - 1) && (c == 0 || c == BOARD_SIZE - 1);
    }

    /**
     * Checks whether an enemy piece sitting in a corner can be captured via an L-shaped trap.
     * This happens when two attacker pieces close off both sides of the corner orthogonally.
     * Any enemy pieces along the perpendicular leg are also removed as part of the trap.
     *
     * @param cornerR      the row of the corner cell
     * @param cornerC      the column of the corner cell
     * @param dRow         the row direction we arrived from
     * @param dCol         the column direction we arrived from
     * @param attackerColor the attacker's color
     * @param victimColor   the victim's color
     * @return true if the corner trap is fully closed and the capture should happen
     */
    private boolean checkCornerCapture(int cornerR, int cornerC, int dRow, int dCol, String attackerColor, String victimColor) {
        // Figure out which way the other leg of the L-shape points
        int perpDR = 0;
        int perpDC = 0;

        if (dCol == 0) { // Came in vertically, so look horizontally
            perpDR = 0;
            if (cornerC == 0) {
                perpDC = 1;
            } else {
                perpDC = -1;
            }
        } else { // Came in horizontally, so look vertically
            if (cornerR == 0) {
                perpDR = 1;
            } else {
                perpDR = -1;
            }
            perpDC = 0;
        }

        // Trace the other leg of the corner to see if it's a solid line of victims
        int checkR = cornerR + perpDR;
        int checkC = cornerC + perpDC;
        int perpVictimCount = 0;

        while (isWithinBounds(checkR, checkC) && grid[checkR][checkC] != null &&
               grid[checkR][checkC].getColor().equals(victimColor)) {
            perpVictimCount++;
            checkR += perpDR;
            checkC += perpDC;
        }

        // If the other leg ends with our piece, the trap is fully closed
        if (isWithinBounds(checkR, checkC) && grid[checkR][checkC] != null &&
            grid[checkR][checkC].getColor().equals(attackerColor)) {

            // Wipe out the victims on the perpendicular leg
            int capPerpR = cornerR + perpDR;
            int capPerpC = cornerC + perpDC;
            for (int k = 0; k < perpVictimCount; k++) {
                grid[capPerpR][capPerpC] = null;
                incrementCaptureCount(attackerColor);
                capPerpR += perpDR;
                capPerpC += perpDC;
            }

            return true;
        }

        return false;
    }

    /**
     * Removes a line of captured enemy pieces from the board, starting at (startR, startC)
     * and stepping in the given direction for exactly count cells.
     *
     * @param startR        the row of the first piece to remove
     * @param startC        the column of the first piece to remove
     * @param dRow          the row step direction
     * @param dCol          the column step direction
     * @param count         the number of pieces to remove
     * @param attackerColor the color of the player doing the capturing (for score tracking)
     */
    private void captureVictims(int startR, int startC, int dRow, int dCol, int count, String attackerColor) {
        int capR = startR;
        int capC = startC;
        for (int k = 0; k < count; k++) {
            grid[capR][capC] = null;
            incrementCaptureCount(attackerColor);
            capR += dRow;
            capC += dCol;
        }
    }

    /** @return the raw 2D grid array backing this board */
    public position[][] getGrid() {
        return grid;
    }

    /**
     * Replaces the entire grid — mainly used during testing to set up custom scenarios.
     *
     * @param grid the new 9x9 grid to use
     */
    public void setGrid(position[][] grid) {
        this.grid = grid;
    }

    /**
     * Directly sets Black's captured piece count.
     * Useful when restoring a saved game state or setting up test cases.
     *
     * @param blackCapturedCount the value to assign
     */
    public void setBlackCapturedCount(int blackCapturedCount) {
        this.blackCapturedCount = blackCapturedCount;
    }

    /**
     * Directly sets White's captured piece count.
     * Useful when restoring a saved game state or setting up test cases.
     *
     * @param whiteCapturedCount the value to assign
     */
    public void setWhiteCapturedCount(int whiteCapturedCount) {
        this.whiteCapturedCount = whiteCapturedCount;
    }
}
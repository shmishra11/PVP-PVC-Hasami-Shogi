/**
 * Keeps track of a single square on our 9x9 Hasami Shogi board.
 * It stores coordinates using the official game layout (Row letters A-I 
 * and Column numbers 1-9) along with whichever piece is currently sitting there.
 * It also handles converting those game letters/numbers into standard 0-8 array indexes.
 */
public class position {
    private static final String[] VALID_ROWS = {"A", "B", "C", "D", "E", "F", "G", "H", "I"};
    private static final int MIN_COL = 1;
    private static final int MAX_COL = 9;
    private static final String[] VALID_COLORS = {"B", "W", ""};
    
    private String row;
    private int col;
    private String color;

    /**
     * Creates a new position on the board.
     * We validate the inputs right away to make sure the game coordinates are legal 
     * and that the color is either Black, White, or completely empty.
     *
     * @param row   The board row letter (A to I).
     * @param col   The board column number (1 to 9).
     * @param color What piece is on the square ("B" for Black, "W" for White, or "" for empty).
     * @throws IllegalArgumentException If someone tries to pass a completely fake coordinate or color.
     */
    public position(String row, int col, String color) throws IllegalArgumentException {
        if (!isValidRow(row) || !isValidCol(col) || !isValidColor(color)) {
            throw new IllegalArgumentException("Invalid position parameters: row=" + row + ", col=" + col + ", color=" + color);
        }
        this.row = row.toUpperCase();
        this.col = col;
        this.color = color;
    }
    
    // Helper method to validate row
    private static boolean isValidRow(String row) {
        if (row == null) return false;
        String upperRow = row.toUpperCase();
        for (String validRow : VALID_ROWS) {
            if (validRow.equals(upperRow)) return true;
        }
        return false;
    }
    
    // Helper method to validate column
    private static boolean isValidCol(int col) {
        return col >= MIN_COL && col <= MAX_COL;
    }
    
    // Helper method to validate color
    private static boolean isValidColor(String color) {
        if (color == null) return false;
        for (String validColor : VALID_COLORS) {
            if (validColor.equals(color)) return true;
        }
        return false;
    }

    /**
     * Finds out which player owns the piece currently sitting on this square.
     *
     * @return "B", "W", or an empty string if no one is on it.
     */
    public String getColor() {
        return color;
    }

    /**
     * Changes or updates the color state of this square when a piece moves or gets captured.
     *
     * @param color The updated piece color status string.
     */
    public void setColor(String color) {
        this.color = color;
    }

    /**
     * Converts our text-based row letter (like 'D') into a 0-indexed integer (like 3)
     * so that our 2D grid array can safely read it without throwing crashes.
     *
     * @return The array row index from 0 to 8, or -1 if something goes wrong.
     */
    public int getRowIndex() {
        String r = row.toUpperCase();
        for (int i = 0; i < VALID_ROWS.length; i++) {
            if (VALID_ROWS[i].equals(r)) {
                return i;
            }
        }
        return -1; // invalid row
    }

    /**
     * Converts our user-facing column number (1-9) into a 0-indexed integer (0-8)
     * so it cleanly maps directly to our backend array logic.
     *
     * @return The 0-indexed column integer mapping.
     */
    public int getColIndex() {
        return col - 1;
    }

    /**
     * Grabs the uppercase row letter of this coordinate.
     *
     * @return The row character string (A-I).
     */
    public String getRow() {
        return row;
    }

    /**
     * Grabs the plain game column number of this coordinate.
     *
     * @return The column integer (1-9).
     */
    public int getCol() {
        return col;
    }

    /**
     * Generates a simple, readable string format showing the current board coordinates.
     * Useful for running terminal logs and print debugging.
     *
     * @return A nice clean text message showing the row and column.
     */
    @Override
    public String toString() {
        return "Row: " + row + ", Col: " + col;
    }

    /**
     * Directly modifies the row label character of this position space.
     *
     * @param row The new row letter to apply.
     */
    public void setRow(String row) {
        this.row = row;
    }

    /**
     * Directly modifies the numerical column attribute of this position space.
     *
     * @param col The new column integer to apply.
     */
    public void setCol(int col) {
        this.col = col;
    }

    /**
     * Sets the row location by taking a raw 2D array index and turning it back 
     * into its matching game board letter string (e.g., index 0 becomes "A").
     *
     * @param index The array boundary index integer (0 through 8).
     * @throws IllegalArgumentException If the array index falls off the 9x9 grid setup.
     */
    public void setRowIndex(int index) throws IllegalArgumentException {
        if (index < 0 || index >= VALID_ROWS.length) {
            throw new IllegalArgumentException("Invalid row index: " + index);
        }
        this.row = VALID_ROWS[index];
    }

    /**
     * Sets the column location by taking a raw 2D array index and converting it 
     * back into a standard user-facing column integer (e.g., index 0 becomes column 1).
     *
     * @param index The array boundary index integer (0 through 8).
     * @throws IllegalArgumentException If the array index is out of bounds.
     */
    public void setColIndex(int index) throws IllegalArgumentException {
        if (index < 0 || index >= 9) {
            throw new IllegalArgumentException("Invalid column index: " + index);
        }
        this.col = index + 1;
    }

    /**
     * Local sandbox main method for quickly testing standalone class logic.
     *
     * @param args System configuration array arguments.
     */
    public static void main(String[] args) {
        
    }
}
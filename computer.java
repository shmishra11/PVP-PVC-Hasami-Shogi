/**
 * Represents the computer-controlled player in Player vs Computer mode.
 * Uses a random strategy, picks a random piece and a random
 * valid destination until it finds a legal move.
 */
public class computer {
    private String color;

    /**
     * Creates a computer player assigned to the given color.
     *
     * @param color the piece color this computer controls ("B" or "W")
     */
    public computer(String color) {
        this.color = color;
    }

    /**
     * Makes a random legal move on behalf of the computer.
     * Repeatedly picks a random piece of the computer's color and a random
     * destination until a valid move is found, then executes it.
     *
     * @param gameBoard the current board state to move on
     */
    public void makeMove(board gameBoard) {
        boolean moved = false;

        while (!moved) {
            int r1 = (int) (Math.random() * 9);
            int c1 = (int) (Math.random() * 9);

            position piece = gameBoard.getPiece(r1, c1);

            // Only pick our own color
            if (piece != null && piece.getColor().equals(color)) {
                int r2 = (int) (Math.random() * 9);
                int c2 = (int) (Math.random() * 9);

                position target = new position("A", 1, "");
                target.setRowIndex(r2);
                target.setColIndex(c2);

                if (gameBoard.isValidMove(piece, target)) {
                    gameBoard.movePiece(piece, target);
                    moved = true;
                }
            }
        }
    }
}
package chessfx.game;

/**
 * Entity representing a single square on a given board. May be empty or contain a piece.
 */
public class Square {
    
    public final int x;
    public final int y;
    private final Board board;
    private Piece piece;

    /**
     * Sole constructor.
     * 
     * @param board associated {@link chessfx.game.Board Board}
     * @param x coordinate
     * @param y coordinate
     * @throws IllegalArgumentException if (x, y) is not part of board
     */
    public Square(Board board, int x, int y) {
        if (x < 0 || y < 0 || x >= board.getSize() || y >= board.getSize()) {
            throw new IllegalArgumentException(String.format("Square outside of board (board size = %d, x = %d, y = %d)", board.getSize(), x, y));
        }
        this.board = board;
        this.x = x;
        this.y = y;
    }

    /**
     * Check if square contains a piece.
     * 
     * @return whether square contains a piece
     */
    public boolean hasPiece() {
        return piece != null;
    }

    /**
     * Return piece placed on square.
     * 
     * @return {@link chessfx.game.Piece Piece} on square
     * @throws IllegalStateException if square is empty
     */
    public Piece getPiece() {
        if (piece == null) throw new IllegalStateException(String.format("Square (%s) has no piece", toString(), x, y));
        return piece;
    }

    /**
     * Place piece on square.
     * 
     * @param piece
     * @throws IllegalStateException if the piece's square field does not correspond to this square
     */
    protected void setPiece(Piece piece) {
        this.piece = piece;
        if (piece.getSquare().x != x || piece.getSquare().y != y) {
            throw new IllegalStateException(String.format("Piece and square out of sync. Square(%s) vs Piece(%s)", toString(), piece.getSquare().toString()));
        }
    }

    /**
     * Remove piece from square.
     */
    protected void removePiece() {
        if (piece == null) throw new IllegalStateException("Cannot remove piece from empty square");
        piece = null;
    }

    /**
     * Return coordinate of square in chess board format (ie. "e4" or "g7")
     */
    public String toString() {
        return String.format("%c%d", x + 'a', y + 1);
    }

    /**
     * Check whether given square share coordinates with this square.
     * 
     * @param square
     * @return whether the inputted square have identical (x, y)
     */
    public boolean equals(Square square) {
        return square.x == x && square.y == y;
    }

    /**
     * Return the board this square belongs to.
     * 
     * @return {@link chessfx.game.Board Board} instance square is part of
     */
    protected Board getBoard() {
        return board;
    }

}

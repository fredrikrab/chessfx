package chessfx.game;

import chessfx.game.Piece.Color;
import chessfx.game.Piece.PieceType;
import chessfx.game.pieces.King;

/**
 * Entity representing a chess board, containing NxN squares (declared with N=8).
 */
public class Board {

    public static final int boardSize = 8;  // Number of squares in one direction
    private final Square[][] board;         // 2D-array of squares
    private King blackKing;
    private King whiteKing;

    /**
     * Sole constructor.
     * 
     * @param boardSize size of board (number of squares)
     */
    public Board() {
        this.board = new Square[boardSize][boardSize];
        
        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                board[x][y] = new Square(this, x, y);
            }
        }
    }

    /**
     * Place piece on board on its corresponding square.
     * 
     * Can only place one king of each color on the board.
     * 
     * @param piece
     * @throws IllegalStateException if multiple kings of same colors are placed on the board
     */
    public void placePiece(Piece piece) {
        if (piece.getType() == PieceType.King && ((piece.getColor() == Color.White && whiteKing != piece) || (piece.getColor() == Color.Black && blackKing != piece))) {
            setKing((King) piece);
        }
        Square square = piece.getSquare();
        board[square.x][square.y].setPiece(piece);
    }

    /**
     * Remove piece from square.
     * 
     * @param square
     */
    public void removePiece(Square square) {
        board[square.x][square.y].removePiece();
    }

    /**
     * Help function to place King on board.
     * 
     * Does nothing if King in argument was previously placed on board.
     * Declares black or white king for the board if it's the initial King placement.
     * 
     * @param king
     * @throws IllegalStateException if it's the second King of its color to be placed on board
     */
    private void setKing(King king) {
        if (king.getColor() == Color.White && whiteKing == null) {
            whiteKing = king;
        } else if (king.getColor() == Color.Black && blackKing == null) {
            blackKing = king;
        } else if (king == whiteKing || king == blackKing) {
            return;
        } else {
            throw new IllegalStateException("There can only be one king for each color");
        }
    }

    /**
     * Return King of requested color.
     * 
     * @param color
     * @return King
     * @throws IllegalStateException if King of requested color does not exist on the board
     */
    public King getKing(Color color) {
        King king = color == Color.White ? whiteKing : blackKing;
        if (king == null) {
            throw new IllegalStateException(String.format("%s king is null", color.toString()));
        }
        return king;
    }

    /**
     * Return a given square on the board.
     * 
     * @param x coordinate
     * @param y coordinate
     * @return {@link chessfx.game.Square Square} at given (x, y)
     * @throws IllegalArgumentException if (x, y) is not part of board
     */
    public Square getSquare(int x, int y) {
        if (x < 0 || y < 0 || x >= boardSize || y >= boardSize) {
            throw new IllegalArgumentException(String.format("Square (%d, %d) is not part of board", x, y));
        }
        return board[x][y];
    }

    /**
     * Given a square, return square with same coordinates on the board.
     * 
     * @param square
     * @return {@link chessfx.game.Square Square} at identical (x, y)
     * @throws IllegalArgumentException if (x, y) is not part of board
     */
    public Square getSquare(Square square) {
        return getSquare(square.x, square.y);
    }

    /**
     * Return a given square on the board.
     * 
     * @param square String representation of square (e.g. "e4")
     * @return {@link chessfx.game.Square Square} corresponding to inputted string
     * @throws IllegalArgumentException if square is not part of board
     */
    public Square getSquare(String square) {
        int x = (int) (square.charAt(0) - 'a');
        int y = (int) (square.charAt(1) - '0' - 1);
        return getSquare(x, y);
    }

    /**
     * @return dimension of board
     */
    public int getSize() {
        return boardSize;
    }

}

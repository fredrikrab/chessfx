package chessfx.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chessfx.game.Piece.Color;
import chessfx.game.pieces.King;

public class SquareTest {
    
    private Board board;
    private int boardSize;

    @BeforeEach
    public void setup() {
        board = new Board();
        boardSize = board.getSize();
    }

    @Test
    public void testConstructor() {
        assertThrows(
            IllegalArgumentException.class,
            () -> new Square(board, -1, 0),
            "Expected IllegalArgumentException for x < 0"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Square(board, 0, -1),
            "Expected IllegalArgumentException for y < 0"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Square(board, -1, 0),
            "Expected IllegalArgumentException for x < 0"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Square(board, boardSize, 0),
            "Expected IllegalArgumentException for x > boardSize"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Square(board, 0, boardSize),
            "Expected IllegalArgumentException for y > boardSize"
        );

        assertThrows(
            NullPointerException.class,
            () -> new Square(null, 0, 0),
            "Expected NullPointerException when board == null"
        );

        assertDoesNotThrow(() -> new Square(board, 0, 0));
        assertDoesNotThrow(() -> new Square(board, boardSize - 1, boardSize - 1));
    }

    @Test
    public void testComparison() {
        Square a = new Square(board, 1, 2);
        Square b = new Square(board, 1, 0); // Same x
        Square c = new Square(board, 0, 2); // Same y
        Square d = new Square(board, 1, 2); // Same x and y

        assertFalse(a.equals(b));
        assertFalse(a.equals(c));
        assertTrue(a.equals(d));
    }

    @Test
    public void testToString() {
        List<String> file = Arrays.asList("a", "b", "c", "d", "e", "f", "g", "h");
        List<String> rank = Arrays.asList("1", "2", "3", "4", "5", "6", "7", "8");
        for (int y = 0; y < 8; y++) {
            for (int x = 0; x < 8; x++) {
                assertEquals(String.format("%s%s", file.get(x), rank.get(y)), new Square(board, x, y).toString());
            }
        }
    }

    @Test
    public void testPieceFunctions() {
        Square square = board.getSquare(5, 0);
        King king = new King(Color.White, square);

        // Before piece is placed on square
        assertFalse(square.hasPiece());
        assertThrows(IllegalStateException.class, () -> square.getPiece());
        assertThrows(IllegalStateException.class, () -> square.removePiece());

        // Place piece on square
        assertDoesNotThrow(() -> square.setPiece(king));

        // After piece is placed on square
        assertTrue(square.hasPiece());
        assertDoesNotThrow(() -> square.getPiece());
        assertEquals(king, square.getPiece());
        assertDoesNotThrow(() -> square.removePiece());

        // Square should once again be empty
        assertFalse(square.hasPiece());
        assertThrows(IllegalStateException.class, () -> square.getPiece());
        assertThrows(IllegalStateException.class, () -> square.removePiece());
    }

    @Test
    public void testPieceOutOfSync() {
        Square correctSquare = board.getSquare(5, 0);
        Square wrongSquare = board.getSquare(6, 0);
        King king = new King(Color.White, correctSquare);

        assertDoesNotThrow(() -> correctSquare.setPiece(king));
        assertThrows(IllegalStateException.class, () -> wrongSquare.setPiece(king));
    }

}

package chessfx.game;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chessfx.game.Piece.Color;
import chessfx.game.pieces.King;

public class BoardTest {
    
    private Board board;

    @BeforeEach
    public void setup() {
        board = new Board();
    }

    @Test
    public void testSquareFunctions() {
        Square square = new Square(board, 4, 4);
        
        assertTrue(square.equals(board.getSquare(4, 4)));               // coordinate equality
        assertTrue(square.equals(board.getSquare("e5")));               // coordinate equality
        assertEquals(board.getSquare(square), board.getSquare(4, 4));   // square object equality
        assertEquals(board.getSquare(square), board.getSquare("e5"));   // square object equality

        assertThrows(
            IllegalArgumentException.class,
            () -> board.getSquare(-1, 0),
            "Expected IllegalArgumentException for square which is not part of board"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> board.getSquare(0, -1),
            "Expected IllegalArgumentException for square which is not part of board"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> board.getSquare(board.getSize(), board.getSize()),
            "Expected IllegalArgumentException for square which is not part of board"
        );

        assertDoesNotThrow(() -> board.getSquare(0, 0));
        assertDoesNotThrow(() -> board.getSquare(square));
    }

    @Test
    public void testPlaceRemovePiece() {
        Square square = board.getSquare(4, 4);
        King king = new King(Color.White, square);

        assertDoesNotThrow(() -> board.placePiece(king));
        assertEquals(board.getSquare(square).getPiece(), king);
        assertDoesNotThrow(() -> board.getSquare(square).removePiece());
    }

    @Test
    public void testKingFunctions() {
        King blackKing = new King(Color.Black, board.getSquare("e8"));
        King whiteKing = new King(Color.White, board.getSquare("e1"));
        King extraBlackKing = new King(Color.Black, board.getSquare("d8"));
        King extraWhiteKing = new King(Color.White, board.getSquare("d1"));

        // Attempt getKing() when there is no king
        assertThrows(IllegalStateException.class, () -> board.getKing(Color.Black));
        assertThrows(IllegalStateException.class, () -> board.getKing(Color.White));

        // Place and get kings
        board.placePiece(blackKing);
        assertDoesNotThrow(() -> board.getKing(Color.Black));
        board.placePiece(whiteKing);
        assertDoesNotThrow(() -> board.getKing(Color.White));

        // Attempt to place more than one king of same color
        assertThrows(IllegalStateException.class, () -> board.placePiece(extraBlackKing));
        assertThrows(IllegalStateException.class, () -> board.placePiece(extraWhiteKing));

        // Verify that getKing() returns correct object
        assertEquals(blackKing, board.getKing(Color.Black));
        assertEquals(whiteKing, board.getKing(Color.White));

        // Verify that it is allowed to repeatedly place the existing kings
        assertDoesNotThrow(() -> board.placePiece(blackKing));
        assertDoesNotThrow(() -> board.placePiece(whiteKing));



    }
    
    

}

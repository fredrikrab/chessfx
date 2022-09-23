package chessfx.game;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chessfx.game.Game.GameStatus;
import chessfx.game.Piece.PieceType;
import chessfx.game.pieces.Queen;

public class MoveTest {
    
    private Game game;
    private List<Move> moveList;

    @BeforeEach()
    public void setup() {
        game = new Game(true);
        game.move("e2", "e4");  // e4
        game.move("e7", "e5");  // e5
        game.move("g1", "f3");  // Nf3
        game.move("b8", "c6");  // Nc6
        game.move("f1", "c4");  // Bc4
        game.move("d8", "h4");  // Qh4
        game.move("d2", "d3");  // d3
        game.move("g8", "f6");  // Nf6
        game.move("c1", "g5");  // Bg5
        game.move("f6", "g4");  // Ng4
        game.move("f3", "e5");  // Nxe5
        moveList = game.getMoveList();
    }

    @Test
    public void testIntegrityOfSetupFunction() {
        assertTrue(moveList.size() == 11);
        List<String> notation = Arrays.asList("e4", "e5", "Nf3", "Nc6", "Bc4", "Qh4", "d3", "Nf6", "Bg5", "Ng4", "Nxe5");
        for (int i = 0; i < 11; i++) {
            assertEquals(notation.get(i), moveList.get(i).getNotation());   // Check notation
            assertEquals((i / 2) + 1, moveList.get(i).getMoveCount());      // Check move count
            assertEquals(notation.get(i).substring(notation.get(i).length() - 2), moveList.get(i).getDestination().toString()); // Check destination
        }
    }

    @Test
    public void testCheckMateQxf7() {
        assertTrue(game.getGameStatus() == GameStatus.ActiveGame);
        game.move("h4", "f2");  // Qxf2#
        assertTrue(game.getGameStatus() == GameStatus.BlackWin);
    }

    @Test
    public void testCheckBf7() {
        game.move("f7", "f6");  // f6 (blocking path of g5 Bishop)
        game.move("c4", "f7");  // Bf7+
        Move move = game.getMoveList().get(12);

        assertTrue(move.getPiece().getType() == PieceType.Bishop);
        assertTrue(move.isCheck());
        assertFalse(move.isCapture());
        assertFalse(move.isCastle());
        assertFalse(move.isMate());
        assertFalse(move.isPromotion());
    }

    @Test
    public void testCheckMateBxf7() {
        game.move("h8", "g8");  // Rg8 (random move)
        game.move("c4", "f7");  // Bxf7#
        Move move = game.getMoveList().get(12);

        assertTrue(game.getMoveList().get(11).getPiece().getType() == PieceType.Rook);

        assertTrue(move.getPiece().getType() == PieceType.Bishop);
        assertTrue(move.isCapture());
        assertTrue(move.isMate());
        assertFalse(move.isCheck());
        assertFalse(move.isCastle());
        assertFalse(move.isPromotion());

        assertTrue(game.getGameStatus() == GameStatus.WhiteWin);
        assertThrows(
            IllegalStateException.class,
            () -> game.move("a2", "a3")
        );
    }

    @Test
    public void testCastle() {
        game.move("a7", "a6");  // a6 (random move)
        game.move("e1", "g1");  // 0-0
        Move move = game.getMoveList().get(12);

        assertTrue(game.getMoveList().get(11).getPiece().getType() == PieceType.Pawn);

        assertTrue(move.getPiece().getType() == PieceType.King);
        assertTrue(move.isCastle());
        assertFalse(move.isCapture());
        assertFalse(move.isCheck());
        assertFalse(move.isMate());
        assertFalse(move.isPromotion());
    }

    @Test
    public void testPawnPromotion() {
        game.move("f7", "f5");  // f5
        game.move("a2", "a4");  // a4
        game.move("f5", "f4");  // f4
        game.move("a4", "a5");  // a5
        game.move("f4", "f3");  // f3
        game.move("a5", "a6");  // a6
        game.move("f3", "g2");  // fxg2
        game.move("a6", "b7");  // axb7
        game.move("g2", "h1");  // bxh1=Q+

        // Black pawn promotion - with check and capture
        Move move = game.getMoveList().get(game.getMoveList().size() - 1);
        assertTrue(move.getPiece().getType() == PieceType.Pawn);
        assertTrue(move.isCapture());
        assertTrue(move.isCheck());
        assertTrue(move.isPromotion());
        assertTrue(game.getBoard().getSquare("d1").getPiece().getType() == PieceType.Queen);

        // White pawn promotion
        game.move("e1", "e2");  // Ke2
        game.move("f8", "e7");  // Be7;
        game.move("b7", "b8");  // b8=Q

        move = game.getMoveList().get(game.getMoveList().size() - 1);
        assertTrue(move.getPiece().getType() == PieceType.Pawn);
        assertTrue(move.isPromotion());
        assertTrue(game.getBoard().getSquare("b8").getPiece().getType() == PieceType.Queen);
        assertFalse(move.isCapture());
        assertFalse(move.isCheck());
    }

    @Test
    public void testValidConstructor() {
        Square origin = game.getBoard().getSquare("d5");
        Square destination = game.getBoard().getSquare("d4");
        Queen queen = new Queen(Piece.Color.Black, destination);
        Move move = new Move(queen, origin, destination, 1, false, false, false);

        assertEquals(move.getPiece().getType(), PieceType.Queen);
        assertEquals(move.getOrigin(), origin);
        assertEquals(move.getDestination(), destination);
        assertFalse(move.isCapture());
        assertFalse(move.isCheck());
        assertFalse(move.isMate());
    }

    @Test
    public void testInvalidConstructor() {
        Square origin = game.getBoard().getSquare("d5");
        Square destination = game.getBoard().getSquare("d4");
        Queen queen = new Queen(Piece.Color.Black, destination);

        assertThrows(
            IllegalArgumentException.class,
            () -> new Move(queen, origin, destination, -1, false, false, false),
            "Expected IllegalArgumentException for moveCount < 0"
        );
        
        assertThrows(
            IllegalArgumentException.class,
            () -> new Move(null, origin, destination, 2, false, false, false),
            "Expected IllegalArgumentException for piece == null"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Move(queen, null, destination, 2, false, false, false),
            "Expected IllegalArgumentException for origin == null"
        );

        assertThrows(
            IllegalArgumentException.class,
            () -> new Move(queen, origin, null, 2, false, false, false),
            "Expected IllegalArgumentException for destination == null"
        );
    }

    @Test
    public void testMoveOutOfTurn() {
        assertThrows(
            IllegalStateException.class,
            () -> game.move("a2", "a4")
        );
    }

    @Test
    public void testDoubleComputerMove() {
        game.move(game.getRandomComputerMove());
        assertThrows(
            IllegalStateException.class,
            () -> game.move(game.getRandomComputerMove())
        );
    }

}

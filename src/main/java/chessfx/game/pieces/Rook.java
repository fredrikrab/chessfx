package chessfx.game.pieces;

import chessfx.game.Piece;
import chessfx.game.Square;

public class Rook extends Piece {

    public Rook(Color color, Square square) {
        super(PieceType.Rook, color, square);
    }

    @Override
    protected boolean isValidMovePattern(Square origin, Square destination) {
        return (destination.x == origin.x ^ destination.y == origin.y) ? true : false;
    }

    @Override
    public String toString() {
        return "Rook";
    }

}

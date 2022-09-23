package chessfx.game.pieces;

import chessfx.game.Piece;
import chessfx.game.Square;

public class Bishop extends Piece {

    public Bishop(Color color, Square square) {
        super(PieceType.Bishop, color, square);
    }

    @Override
    protected boolean isValidMovePattern(Square origin, Square destination) {
        return Math.abs(destination.x - origin.x) == Math.abs(destination.y - origin.y) ? true : false;
    }

    @Override
    public String toString() {
        return "Bishop";
    }
}

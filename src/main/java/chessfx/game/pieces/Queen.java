package chessfx.game.pieces;

import chessfx.game.Piece;
import chessfx.game.Square;

public class Queen extends Piece {

    public Queen(Color color, Square square) {
        super(PieceType.Queen, color, square);
    }

    @Override
    protected boolean isValidMovePattern(Square origin, Square destination) {
        boolean bishop = Math.abs(destination.x - origin.x) == Math.abs(destination.y - origin.y) ? true : false;
        boolean rook = (destination.x == origin.x ^ destination.y == origin.y) ? true : false;
        return bishop ^ rook;
    }

    @Override
    public String toString() {
        return "Queen";
    }

}

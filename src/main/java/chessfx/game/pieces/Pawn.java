package chessfx.game.pieces;

import chessfx.game.Piece;
import chessfx.game.Square;

public class Pawn extends Piece {

    private final int direction = getColor() == Color.White ? 1 : -1;

    public Pawn(Color color, Square square) {
        super(PieceType.Pawn, color, square);
    }

    @Override
    protected boolean isValidMovePattern(Square origin, Square destination) {
        int firstMove = (getColor() == Color.White && origin.y == 1) || (getColor() == Color.Black && origin.y == 6) ? 1 : 0;
        boolean forward = (destination.y - origin.y) * direction > 0 ? true : false;
        boolean push = (destination.y - origin.y) * direction <= 1 + firstMove && origin.x == destination.x && !destination.hasPiece() ? true : false;
        boolean capture = (destination.y - origin.y) * direction == 1 && Math.abs(destination.x - origin.x) == 1 && destination.hasPiece() ? true : false;
        return forward && (push ^ capture);
    }
    @Override
    public String toString() {
        return "pawn";
    }

}

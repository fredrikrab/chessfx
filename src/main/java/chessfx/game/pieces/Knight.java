package chessfx.game.pieces;


import chessfx.game.Piece;
import chessfx.game.Square;

public class Knight extends Piece {

    public Knight(Color color, Square square) {
        super(PieceType.Knight, color, square);
    }

    @Override
    protected boolean isValidMovePattern(Square origin, Square destination) {
        return Math.abs((destination.x - origin.x) * (destination.y - origin.y)) == 2 ? true : false;
    }

    @Override
    public String toString() {
        return "Knight";
    }

}

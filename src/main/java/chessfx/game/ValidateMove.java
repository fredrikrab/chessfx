package chessfx.game;

import chessfx.game.Piece.PieceType;
import chessfx.game.pieces.King;

public interface ValidateMove {
    
    default boolean isSameSquare(Piece piece, Square destination) {
        return piece.getSquare().equals(destination);
    }

    default boolean isOccupiedByOwnPiece(Board board, Piece piece, Square destination) {
        return destination.hasPiece() && destination.getPiece().getColor() == piece.getColor();
    }

    default boolean isPathBlocked(Board board, Piece piece, Square destination) {
        if (piece.getType() == PieceType.Knight) {
            return false;
        }

        int dx = Integer.signum(destination.x - piece.getSquare().x);
        int dy = Integer.signum(destination.y - piece.getSquare().y);
        
        Square square = board.getSquare(piece.getSquare().x + dx, piece.getSquare().y + dy);
        while (!square.equals(destination)) {
            if (square.hasPiece()) {
                return true;
            } else {
                square = board.getSquare(square.x + dx, square.y + dy);
            }
        } 

        return false;
    }

    default boolean isKingInCheck(King king) {
        for (Square square : king.getCheckingSquares()) {
            if (square.hasPiece() && square.getPiece().getLegalMoves().contains(king.getSquare())) {
                return true;
            }
        }
        return false;
    }

    default boolean isDestinationAttacked(Board board, Square destination) {
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                if (board.getSquare(x, y).hasPiece() && board.getSquare(x, y).getPiece().getLegalMoves().contains(destination)) {
                    return true;
                }
            }
        }
        return false;
    }

    default boolean isKingInCheckAfterwards(King king) {
        for (Square square : king.getCheckingSquares()) {
            if (square.hasPiece() && square.getPiece().getLegalMoves().contains(king.getSquare())) {
                return true;
            }
        }
        return false;
    }
}

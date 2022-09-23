package chessfx.game;

import chessfx.game.Piece.PieceType;

public class Move {

    private Piece piece;
    private Square origin;
    private Square destination;
    private int moveCount;
    private String notation;
    
    public Move(Piece piece, Square origin, Square destination, int moveCount, boolean capture, boolean check, boolean mate) {
        if (piece == null || origin == null || destination == null) {
            throw new IllegalArgumentException("Piece, origin or destination cannot be null");
        }
        
        this.piece = piece;
        this.origin = origin;
        this.destination = destination;
        this.moveCount = moveCount;
        this.notation = "";

        if (moveCount < 0) {
            throw new IllegalArgumentException("Move count cannot be less than 0");
        }

        switch (piece.getType()) {
            case Bishop: notation += "B"; break;
            case King: notation += "K"; break;
            case Knight: notation += "N"; break;
            case Pawn: break;
            case Queen: notation += "Q"; break;
            case Rook: notation += "R"; break;
            default: throw new IllegalArgumentException("Invalid piece type: " + piece.getType());
        }

        if (capture) {
            notation += (piece.getType() == PieceType.Pawn) ? origin.toString().charAt(0) + "x" : "x";
        }

        notation += destination.toString();

        if (piece.getType() == PieceType.Pawn && (destination.y == 0 || destination.y == 7)) {
            notation += "=Q";
        }

        if (piece.getType() == PieceType.King && destination.x - origin.x == 2) {
            notation = "0-0";
        } else if (piece.getType() == PieceType.King && destination.x - origin.x == -2) {
            notation = "0-0-0";
        }
        

        if (check && !mate) {
            notation += "+";
        }

        if (mate) {
            notation += "#";
        }

    }

    public Piece getPiece() {
        return piece;
    }

    public Square getOrigin() {
        return origin;
    }

    public Square getDestination() {
        return destination;
    }

    public int getMoveCount() {
        return moveCount;
    }

    public boolean isCapture() {
        return notation.contains("x");
    }

    public boolean isCheck() {
        return notation.contains("+");
    }

    public boolean isCastle() {
        return notation.contains("0");
    }

    public boolean isMate() {
        return notation.contains("#");
    }

    public boolean isPromotion() {
        return notation.contains("=");
    }

    public String getNotation() {
        return notation;
    }

    public void setMoveCount(int moveCount) {
        this.moveCount = moveCount;
    }

    public String toString() {
        return getNotation();
    }

}

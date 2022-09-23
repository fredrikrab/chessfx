package chessfx.game;

import java.util.ArrayList;
import java.util.List;

import chessfx.game.pieces.King;

public abstract class Piece implements ValidateMove {

    // Enums
    public static enum Color { Black, White }
    public static enum PieceType { Bishop, King, Knight, Pawn, Queen, Rook }

    // Fields
    protected Color color;
    private PieceType type;
    protected Square square;
    protected Board board;

    // Constructor
    protected Piece(PieceType type, Color color, Square square) {
        this.color = color;
        this.type = type;
        this.square = square;
        this.board = square.getBoard();
    }

    protected abstract boolean isValidMovePattern(Square origin, Square destination);

    protected boolean isValidMovePattern(Square destination) {
        return isValidMovePattern(square, destination);
    }

    public Color getColor() {
        return color;
    }

    public PieceType getType() {
        return type;
    }

    protected void setSquare(Square square) {
        this.square = square;
    }

    public Square getSquare() {
        return square;
    }

    public List<Square> getLegalMoves() {
        List<Square> moves = getCandidateMoves();
        moves.removeIf(destination -> isKingInCheckAfterwards(destination));
        return moves;
    }

    private List<Square> getCandidateMoves() {
        List<Square> moves = new ArrayList<Square>();
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                Square destination = board.getSquare(x, y);
                if (isValidCandidateMove(destination)) {
                    moves.add(destination);
                }
            }
        }
        return moves;
    }

    private boolean isValidCandidateMove(Square destination) {
        
        if (isSameSquare(this, destination)) return false;
        if (!isValidMovePattern(destination)) return false;
        if (isOccupiedByOwnPiece(board, this, destination)) return false;
        if (isPathBlocked(board, this, destination)) return false;

        return true;
    }

    private boolean isKingInCheckAfterwards(Square destination) {
        Square origin = square;
        Piece capturedPiece = board.getSquare(destination).hasPiece() ? board.getSquare(destination).getPiece() : null;

        // Temporarily execute move
        setSquare(destination);
        board.removePiece(origin);
        board.placePiece(this);

        // Check if player is in check afterwards
        boolean check = false;
        King king = board.getKing(color);
        for (Square square : king.getCheckingSquares()) {
            if (square.hasPiece() && square.getPiece().getCandidateMoves().contains(king.getSquare())) {
                check = true;
            }
        }

        // Undo move
        setSquare(origin);
        board.removePiece(destination);
        board.placePiece(this);
        if (capturedPiece != null) board.placePiece(capturedPiece);

        return check;
    }

}

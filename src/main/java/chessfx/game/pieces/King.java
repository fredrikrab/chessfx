package chessfx.game.pieces;

import java.util.ArrayList;
import java.util.List;

import chessfx.game.Piece;
import chessfx.game.Square;

public class King extends Piece {

    private List<Square> checkingSquares;
    private boolean shortCastlingPrivileges;
    private boolean longCastlingPrivileges;

    public King(Color color, Square square) {
        super(PieceType.King, color, square);
        shortCastlingPrivileges = true;
        longCastlingPrivileges = true;
    }

    @Override
    protected void setSquare(Square square) {
        this.square = square;
        updateCheckingSquares();
    }

    private void updateCheckingSquares() {
        if (checkingSquares == null) {
            checkingSquares = new ArrayList<Square>();
        }
        for (int y = 0; y < board.getSize(); y++) {
            for (int x = 0; x < board.getSize(); x++) {
                boolean alignedSquare = x == square.x ^ y == square.y ? true : false;
                boolean diagonalSquare = Math.abs(x - square.x) == Math.abs(y - square.y) ? true : false;
                boolean knightJumpSquare = Math.abs((x - square.x) * (y - square.y)) == 2 ? true : false;
                if (alignedSquare || diagonalSquare || knightJumpSquare) {
                    checkingSquares.add(board.getSquare(x, y));
                }
            }
        }
    }

    public List<Square> getCheckingSquares() {
        if (checkingSquares == null) {
            updateCheckingSquares();
        }
        return checkingSquares;
    }

    protected boolean canCastleShort() {
        return shortCastlingPrivileges;
    }

    protected boolean canCastleLong() {
        return longCastlingPrivileges;
    }

    public void removeShortCastlePrivileges() {
        shortCastlingPrivileges = false;
    }

    public void removeLongCastlePrivileges() {
        longCastlingPrivileges = false;
    }

    @Override
    protected boolean isValidMovePattern(Square origin, Square destination) {
        boolean normalPattern = (Math.abs(destination.x - origin.x) <= 1 && Math.abs(destination.y - origin.y) <= 1) ? true : false;
        boolean shortCastleWhite = color == Color.White && shortCastlingPrivileges && destination.y == origin.y && destination.x - origin.x == 2 && board.getSquare(7, origin.y).hasPiece() && board.getSquare(7, origin.y).getPiece().getType() == PieceType.Rook;
        boolean longCastleWhite = color == Color.White && longCastlingPrivileges && destination.y == origin.y && destination.x - origin.x == -2 && board.getSquare(0, origin.y).hasPiece() && board.getSquare(0, origin.y).getPiece().getType() == PieceType.Rook && !isPathBlocked(board, this, board.getSquare(1, origin.y));
        boolean shortCastleBlack = color == Color.Black && shortCastlingPrivileges && destination.y == origin.y && destination.x - origin.x == -2 && board.getSquare(0, origin.y).hasPiece() && board.getSquare(0, origin.y).getPiece().getType() == PieceType.Rook;
        boolean longCastleBlack = color == Color.Black && longCastlingPrivileges && destination.y == origin.y && destination.x - origin.x == 2 && board.getSquare(7, origin.y).hasPiece() && board.getSquare(7, origin.y).getPiece().getType() == PieceType.Rook && !isPathBlocked(board, this, board.getSquare(6, origin.y));
        return normalPattern || shortCastleWhite || longCastleWhite || shortCastleBlack || longCastleBlack;
    }

    @Override
    public String toString() {
        return "King";
    }

}

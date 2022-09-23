package chessfx.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import chessfx.game.Piece.Color;
import chessfx.game.Piece.PieceType;
import chessfx.game.pieces.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class Game {
    
    private final Random randomizer = new Random();

    public enum GameStatus { ActiveGame, BlackWin, Draw, WhiteWin }
    private final boolean isHumanPlayingWhite;
    private final Color computerColor;

    private Board board;
    private List<Piece> pieceList = new ArrayList<Piece>();
    private GameStatus gameStatus = GameStatus.ActiveGame;
    private Color playerToMove = Color.White;
    private List<Move> moveList = new ArrayList<Move>();
    private ObservableList<ObservableMovePair> observableList = FXCollections.observableArrayList();
    private King blackKing;
    private King whiteKing;

    public Game(boolean isHumanPlayingWhite) {
        this.isHumanPlayingWhite = isHumanPlayingWhite;
        computerColor = isHumanPlayingWhite ? Color.Black : Color.White;
        board = new Board();
        setupDefaultPieces();
    }

    public void move(Piece piece, Square destination) {
        if (gameStatus != GameStatus.ActiveGame) {
            throw new IllegalStateException();
        }

        if (piece.getColor() != playerToMove) {
            throw new IllegalStateException();
        }

        if (!piece.getLegalMoves().contains(destination)) {
            throw new IllegalCallerException();
        }
        
        // Is it a capture?
        boolean capture = destination.hasPiece();
        if (capture) pieceList.remove(destination.getPiece());

        // Is it a King move? (Implications for castling)
        if (piece.getType() == PieceType.King) {
            ((King) piece).removeShortCastlePrivileges();
            ((King) piece).removeLongCastlePrivileges();
        }

        // Is it a Rook move? (Implications for castling)
        if (piece.getType() == PieceType.Rook) {
            if (piece.getColor() == Color.White && piece.getSquare().equals(board.getSquare("a1"))) {
                whiteKing.removeLongCastlePrivileges();
            } else if (piece.getColor() == Color.White && piece.getSquare().equals(board.getSquare("h1"))) {
                whiteKing.removeShortCastlePrivileges();
            } else if (piece.getColor() == Color.Black && piece.getSquare().equals(board.getSquare("a8"))) {
                blackKing.removeLongCastlePrivileges();
            } else if (piece.getColor() == Color.Black && piece.getSquare().equals(board.getSquare("h8"))) {
                blackKing.removeLongCastlePrivileges();
            }
        }

        // Is it a castle move?
        if (piece.getType() == PieceType.King && destination.x - piece.getSquare().x == 2) {
            Piece rook = board.getSquare(7, piece.getSquare().y).getPiece();
            board.removePiece(rook.getSquare());
            rook.setSquare(board.getSquare(5, piece.getSquare().y));
            board.placePiece(rook);
        }

        if (piece.getType() == PieceType.King && destination.x - piece.getSquare().x == -2) {
            Piece rook = board.getSquare(0, piece.getSquare().y).getPiece();
            board.removePiece(rook.getSquare());
            rook.setSquare(board.getSquare(3, piece.getSquare().y));
            board.placePiece(rook);
        }

        // Make move
        Square origin = piece.getSquare();
        piece.setSquare(destination);
        board.placePiece(piece);
        board.removePiece(origin);

        // Is it a move which promoted a pawn?
        if (piece.getType() == PieceType.Pawn && (destination.y == 0 || destination.y == 7)) {
            pieceList.remove(piece);
            board.removePiece(destination);
            Piece newQueen = new Queen(piece.getColor(), destination);
            pieceList.add(newQueen);
            board.placePiece(newQueen);
        }

        // Is player to move now in check?
        playerToMove = (playerToMove == Color.White) ? Color.Black : Color.White;

        King king = playerToMove == Color.Black ? blackKing : whiteKing;
        List<Square> checkingSquares = king.getCheckingSquares();
        Square kingSquare = king.getSquare();
        
        boolean check = checkingSquares.stream()
                        .filter(square -> square.hasPiece())
                        .filter(square -> square.getPiece().getColor() != playerToMove)
                        .filter(square -> square.getPiece().isValidMovePattern(kingSquare))
                        .anyMatch(square -> square.getPiece().getLegalMoves().contains(kingSquare));
        
        
        // Is it also mate?
        boolean mate = false;
        if (check) {
            mate = !pieceList.stream()
                        .filter(p -> p.getColor() == playerToMove)
                        .anyMatch(p -> p.getLegalMoves().size() > 0);
            
            if (mate) gameStatus = (playerToMove == Color.White) ? GameStatus.BlackWin : GameStatus.WhiteWin;
        }
        
        // Add move to list
        int moveCount = (moveList.size() / 2) + 1;
        Move move = new Move(piece, origin, destination, moveCount, capture, check, mate);
        if (playerToMove == Color.White) {
            observableList.remove(observableList.size() - 1);
            observableList.add(new ObservableMovePair(getLastMove(), move));
        } else {
            observableList.add(new ObservableMovePair(move, null));
        }
        moveList.add(move);

    }

    public void move(Move move) {
        move(move.getPiece(), move.getDestination());
    }

    public void move(String originSquare, String destinationSquare) {
        Piece piece = null;
        try {
            piece = board.getSquare(originSquare).getPiece();
        } catch (IllegalStateException e) {
            throw new IllegalArgumentException(String.format("%s has no piece", originSquare));
        }
        
        Square destination = board.getSquare(destinationSquare);
        move(piece, destination);

    }

    public List<Move> getMoveList() {
        return moveList;
    }

    public Move getLastMove() {
        return moveList.get(moveList.size() - 1);
    }

    public ObservableList<ObservableMovePair> getObservableMoveList() {
        return observableList;
    }

    public Board getBoard() {
        return board;
    }

    public GameStatus getGameStatus() {
        return gameStatus;
    }

    public boolean isHumanPlayingWhite() {
        return isHumanPlayingWhite;
    }

    public Color getPlayerToMove() {
        return playerToMove;
    }

    public Move getRandomComputerMove() {
        if (gameStatus != GameStatus.ActiveGame) throw new IllegalStateException();
        if (playerToMove != computerColor) throw new IllegalStateException();

        List<Piece> candidatePieceList = pieceList.stream()
            .filter(piece -> piece.getColor() == computerColor)
            .filter(piece -> piece.getLegalMoves().size() > 0)
            .toList();
        
        if (candidatePieceList.size() == 0) {
            throw new IllegalStateException("Cannot request computer move when there are no legal moves to make");
        }

        Piece piece = candidatePieceList.get(randomizer.nextInt(candidatePieceList.size()));
        Square destination = piece.getLegalMoves().get(randomizer.nextInt(piece.getLegalMoves().size()));

        return new Move(piece, piece.getSquare(), destination, 0, destination.hasPiece(), false, false);
    }

    private void setupDefaultPieces() {
        // Throw exception if no board
        if (board == null) throw new IllegalStateException("Cannot setup pieces before intializing a board");
        
        // Ensure empty list before adding pieces
        pieceList.clear();

        // Add white major and minor pieces to list
        pieceList.add(new Rook(Color.White, board.getSquare("a1")));
        pieceList.add(new Knight(Color.White, board.getSquare("b1")));
        pieceList.add(new Bishop(Color.White, board.getSquare("c1")));
        pieceList.add(new Queen(Color.White, board.getSquare("d1")));
        pieceList.add(new King(Color.White, board.getSquare("e1")));
        pieceList.add(new Bishop(Color.White, board.getSquare("f1")));
        pieceList.add(new Knight(Color.White, board.getSquare("g1")));
        pieceList.add(new Rook(Color.White, board.getSquare("h1")));

        // Add white pawns to list
        pieceList.add(new Pawn(Color.White, board.getSquare("a2")));
        pieceList.add(new Pawn(Color.White, board.getSquare("b2")));
        pieceList.add(new Pawn(Color.White, board.getSquare("c2")));
        pieceList.add(new Pawn(Color.White, board.getSquare("d2")));
        pieceList.add(new Pawn(Color.White, board.getSquare("e2")));
        pieceList.add(new Pawn(Color.White, board.getSquare("f2")));
        pieceList.add(new Pawn(Color.White, board.getSquare("g2")));
        pieceList.add(new Pawn(Color.White, board.getSquare("h2")));

        // Add black major and minor pieces to list
        pieceList.add(new Rook(Color.Black, board.getSquare("a8")));
        pieceList.add(new Knight(Color.Black, board.getSquare("b8")));
        pieceList.add(new Bishop(Color.Black, board.getSquare("c8")));
        pieceList.add(new Queen(Color.Black, board.getSquare("d8")));
        pieceList.add(new King(Color.Black, board.getSquare("e8")));
        pieceList.add(new Bishop(Color.Black, board.getSquare("f8")));
        pieceList.add(new Knight(Color.Black, board.getSquare("g8")));
        pieceList.add(new Rook(Color.Black, board.getSquare("h8")));

        // Add black pawns to list
        pieceList.add(new Pawn(Color.Black, board.getSquare("a7")));
        pieceList.add(new Pawn(Color.Black, board.getSquare("b7")));
        pieceList.add(new Pawn(Color.Black, board.getSquare("c7")));
        pieceList.add(new Pawn(Color.Black, board.getSquare("d7")));
        pieceList.add(new Pawn(Color.Black, board.getSquare("e7")));
        pieceList.add(new Pawn(Color.Black, board.getSquare("f7")));
        pieceList.add(new Pawn(Color.Black, board.getSquare("g7")));
        pieceList.add(new Pawn(Color.Black, board.getSquare("h7")));

        // Place all pieces in list on board
        for (Piece piece : pieceList) {
            board.placePiece(piece);
        }

        blackKing = board.getKing(Color.Black);
        whiteKing = board.getKing(Color.White);
    }

}

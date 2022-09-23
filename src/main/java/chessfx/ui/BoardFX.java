package chessfx.ui;

import chessfx.game.Board;
import chessfx.game.Game;
import chessfx.game.Move;
import chessfx.game.Piece;
import chessfx.game.Square;
import chessfx.game.Game.GameStatus;
import chessfx.game.Piece.PieceType;

import java.util.List;

import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.animation.PauseTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.TranslateTransition;
import javafx.util.Duration;

public class BoardFX {

    protected static final int SQUARE_SIZE = 100;

    private Board board;
    private Game game;
    private SquareFX[][] boardFX;
    private Group squareGroup = new Group();
    private Group pieceGroup = new Group();
    private Group highlightGroup = new Group();

    protected Parent createBoard(Game game) {
        this.game = game;
        this.board = game.getBoard();
        final int boardSize = board.getSize();
        boardFX = new SquareFX[boardSize][boardSize];

        Pane root = new Pane();
        root.setPrefSize(boardSize * SQUARE_SIZE, boardSize * SQUARE_SIZE);
        root.getChildren().addAll(squareGroup, pieceGroup, highlightGroup);

        for (int y = 0; y < boardSize; y++) {
            for (int x = 0; x < boardSize; x++) {
                SquareFX squareFX = new SquareFX((x + y) % 2 == 0, x, y);
                boardFX[x][y] = squareFX;
                squareGroup.getChildren().add(squareFX);

                if (board.getSquare(x, y).hasPiece()) {
                    addPieceFX(board.getSquare(x, y).getPiece());
                }
            }
        }

        root.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                highlightGroup.getChildren().clear();
                e.consume();
            }
        });

        root.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> {
            int x = (int) (e.getSceneX() / SQUARE_SIZE);
            int y = (int) (e.getSceneY() / SQUARE_SIZE);
            if (game.isHumanPlayingWhite()) y += (boardSize - (y + (y+1)));
            else x += (boardSize - (x + (x+1)));

            // Right click gives circle highlight
            if (e.getButton() == MouseButton.SECONDARY) {
                if (highlightGroup.lookup("#circle_" + boardFX[x][y].toString()) == null) {
                    Circle circle = new Circle(x * SQUARE_SIZE + SQUARE_SIZE/2, y * SQUARE_SIZE + SQUARE_SIZE/2, SQUARE_SIZE * 0.48, Color.TRANSPARENT);
                    circle.setStrokeWidth(4);
                    circle.setStroke((x + y) % 2 == 0 ? Color.web("#15781B", 0.75) : Color.web("#15781B", 0.8));
                    circle.setId("circle_" + boardFX[x][y].toString());
                    highlightGroup.getChildren().add(circle);
                } else {
                    highlightGroup.getChildren().remove(highlightGroup.lookup("#circle_" + boardFX[x][y].toString()));                
                }
                return;
            }

            // Click on piece gives validMoves highlight
            if (!board.getSquare(x, y).hasPiece()) return;
            // if (board.getSquare(x, y).getPiece().getColor() != game.getPlayerToMove()) return;
            PieceFX pieceFX = boardFX[x][y].getPieceFX();

            if (pieceFX.isPressed()) {
            List<Square> validMoves = board.getSquare(x, y).getPiece().getLegalMoves();
                for (Square square : validMoves) {
                    Circle dot = new Circle(square.x * SQUARE_SIZE + SQUARE_SIZE/2, square.y * SQUARE_SIZE + SQUARE_SIZE/2, SQUARE_SIZE * 0.15);
                    dot.setFill((x + y) % 2 == 0 ? Color.web("#15781B", 0.7) : Color.web("#15781B", 0.85));
                    dot.setId("dot_" + boardFX[x][y].toString());
                    highlightGroup.getChildren().add(dot);
                }
            }
            
        });

        if (game.isHumanPlayingWhite()) {
            root.setRotationAxis(new Point3D(Math.PI/2, 0, 0));
        } else {
            root.setRotationAxis(new Point3D(0, Math.PI, 0));
        }
        root.setRotate(180);
        
        return root;
    }

    private void addPieceFX(Piece piece) {
        PieceFX pieceFX = new PieceFX(piece, game.isHumanPlayingWhite());
        final int boardSize = board.getSize();

        pieceFX.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) highlightGroup.getChildren().clear();
            if (piece.getColor() != game.getPlayerToMove()) {
                pieceFX.abortMove();
                return;
            }

            int file = (int) (pieceFX.getLayoutX() + SQUARE_SIZE / 2) / SQUARE_SIZE;
            int rank = (int) (pieceFX.getLayoutY() + SQUARE_SIZE / 2) / SQUARE_SIZE;

            if (file < 0 || rank < 0 || file >= boardSize || rank >= boardSize) {
                pieceFX.abortMove();
                return;
            }

            Square destination = board.getSquare(file, rank);
            SquareFX originFX = boardFX[piece.getSquare().x][piece.getSquare().y];
            SquareFX destinationFX = boardFX[file][rank];

            if (!piece.getLegalMoves().contains(destination)) {
                pieceFX.abortMove();
            } else {
                // Remove captured piece
                if (destination.hasPiece()) pieceGroup.getChildren().remove(destinationFX.getPieceFX());

                // Animate rook move if castle
                if (piece.getType() == PieceType.King && destination.x - piece.getSquare().x == 2) {
                    animateCastle(board.getSquare(7, destination.y), board.getSquare(5, destination.y));
                }
                if (piece.getType() == PieceType.King && destination.x - piece.getSquare().x == -2) {
                    animateCastle(board.getSquare(0, destination.y), board.getSquare(3, destination.y));
                }

                // Execute move, incl. handle eventual check highlights
                handleCheckHighlight();
                game.move(piece, destination);
                handleCheckHighlight();
                originFX.removePieceFX();
                destinationFX.setPieceFX(pieceFX);
                pieceFX.moveFX(destination);

                // Handle pawn promotion
                if (piece.getType() == PieceType.Pawn && (destination.y == 0 || destination.y == 7)) {
                    pieceFX.pawnPromotion(destination.getPiece());
                }

                // Request computer move
                if (game.getGameStatus() == GameStatus.ActiveGame) {
                    Move computerMove = game.getRandomComputerMove();
                    animateMove(computerMove);
                }
            }
        });
        pieceGroup.getChildren().add(pieceFX);
        boardFX[piece.getSquare().x][piece.getSquare().y].setPieceFX(pieceFX);
    }

    protected void animateMove(Move move) {
        Piece piece = move.getPiece();
        Square origin = move.getOrigin();
        Square destination = move.getDestination();

        PieceFX pieceFX = boardFX[origin.x][origin.y].getPieceFX();
        pieceFX.toFront();
        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(450), pieceFX);
        moveTransition.setToX((destination.x - origin.x) * SQUARE_SIZE);
        moveTransition.setToY((destination.y - origin.y) * SQUARE_SIZE);
        moveTransition.setOnFinished(e -> {
            
            // Execute move, incl. handle eventual check highlights
            handleCheckHighlight();
            game.move(piece, destination);
            handleCheckHighlight();

            // Remove captured pieceFX if applicable
            if (move.isCapture()) {
                pieceGroup.getChildren().remove(boardFX[destination.x][destination.y].getPieceFX());
            }

            // Move rook if castle
            if (piece.getType() == PieceType.King && destination.x - piece.getSquare().x == 2) {
                animateCastle(board.getSquare(7, destination.y), board.getSquare(5, destination.y));
            }
            if (piece.getType() == PieceType.King && destination.x - piece.getSquare().x == -2) {
                animateCastle(board.getSquare(0, destination.y), board.getSquare(3, destination.y));
            }

            // Move pieceFX
            boardFX[origin.x][origin.y].removePieceFX();
            boardFX[destination.x][destination.y].setPieceFX(pieceFX);
            pieceFX.moveFX(destination);
        });

        SequentialTransition computerAnimation = new SequentialTransition(new PauseTransition(Duration.millis(400)), moveTransition);
        computerAnimation.play();
    }

    private void animateCastle(Square origin, Square destination) {
        PieceFX pieceFX = boardFX[origin.x][origin.y].getPieceFX();
        TranslateTransition moveTransition = new TranslateTransition(Duration.millis(350), pieceFX);
        moveTransition.setToX((destination.x - origin.x) * SQUARE_SIZE);
        moveTransition.setToY((destination.y - origin.y) * SQUARE_SIZE);
        moveTransition.setOnFinished(e -> {
            boardFX[origin.x][origin.y].removePieceFX();
            boardFX[destination.x][destination.y].setPieceFX(pieceFX);
            pieceFX.moveFX(destination);
        });
        moveTransition.play();
    }

    private void handleCheckHighlight() {
        if (game.getMoveList().size() == 0) {
            return;
        }
        boolean isCheck = game.getLastMove().isCheck() || game.getLastMove().isMate();
        if (isCheck) {
            Square square = board.getKing(game.getPlayerToMove()).getSquare();
            boardFX[square.x][square.y].toggleCheckHighlight();   
        }
    }
    
}

package chessfx.ui;

import chessfx.game.Piece;
import chessfx.game.Square;
import chessfx.game.Piece.PieceType;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;

import static chessfx.ui.BoardFX.SQUARE_SIZE;

public class PieceFX extends StackPane {

    private Piece piece;
    private double mouseX, mouseY;
    private double pieceX, pieceY;

    protected PieceFX(Piece piece, boolean isHumanPlayingWhite) {
        this.piece = piece;
        int orientation = isHumanPlayingWhite ? -1 : 1;

        // Piece image
        Image piece_img = new Image(getClass().getResourceAsStream("pieces/" + piece.getColor().name().toLowerCase() + "_png/" + piece.getType().name().toLowerCase() + ".png"));
        ImageView piece_view = new ImageView(piece_img);

        if (isHumanPlayingWhite) {
            piece_view.setRotationAxis(new Point3D(Math.PI/2, 0, 0));
        } else {
            piece_view.setRotationAxis(new Point3D(0, Math.PI, 0));
        }

        piece_view.setRotate(180);
        getChildren().add(piece_view);

        // Move piece to square
        pieceX = piece.getSquare().x * SQUARE_SIZE;
        pieceY = piece.getSquare().y * SQUARE_SIZE;
        relocate(pieceX, pieceY);

        // On mouse behaviour
        setOnMousePressed(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            toFront();
            mouseX = e.getSceneX();
            mouseY = e.getSceneY();
        });

        setOnMouseDragged(e -> {
            if (e.getButton() != MouseButton.PRIMARY) return;
            relocate(pieceX + (mouseX - e.getSceneX()) * orientation, pieceY + (e.getSceneY() - mouseY) * orientation);
        });
    }

    // Methods
    protected void moveFX(Square destination) {
        setTranslateX(0);
        setTranslateY(0);
        
        pieceX = piece.getSquare().x * SQUARE_SIZE;
        pieceY = piece.getSquare().y * SQUARE_SIZE;
        relocate(pieceX, pieceY);
    }

    protected void abortMove() {
        relocate(pieceX, pieceY);
    }

    protected void pawnPromotion(Piece promotionPiece) {
        if (piece.getType() != PieceType.Pawn) throw new IllegalCallerException();

        piece = promotionPiece;
        Point3D rotationAxis = getChildren().get(0).getRotationAxis();
        double rotation = getChildren().get(0).getRotate();

        getChildren().clear();
        Image piece_img = new Image(getClass().getResourceAsStream("pieces/" + piece.getColor().name().toLowerCase() + "_png/" + piece.getType().name().toLowerCase() + ".png"));
        ImageView piece_view = new ImageView(piece_img);
        piece_view.setRotationAxis(rotationAxis);
        piece_view.setRotate(rotation);
        getChildren().add(piece_view);
        pieceX = piece.getSquare().x * SQUARE_SIZE;
        pieceY = piece.getSquare().y * SQUARE_SIZE;
        relocate(pieceX, pieceY);
        System.out.println(piece);
        
    }
}
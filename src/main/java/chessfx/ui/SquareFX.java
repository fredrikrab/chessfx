package chessfx.ui;

import javafx.scene.effect.InnerShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import static chessfx.ui.BoardFX.SQUARE_SIZE;

public class SquareFX extends Rectangle {

    private static final String whiteSquare = "#F2F5F3";
    private static final String blackSquare = "#71828F";
    private PieceFX pieceFX;
    private InnerShadow checkHighlight = new InnerShadow(20, 0, 0, Color.RED);

    public SquareFX(boolean black, int x, int y) {
        setWidth(SQUARE_SIZE);
        setHeight(SQUARE_SIZE);
        relocate(x * SQUARE_SIZE, y * SQUARE_SIZE);
        setFill(black ? Color.valueOf(blackSquare) : Color.valueOf(whiteSquare));
        setId((char)('a' + x) + "" + String.valueOf(y + 1));
    }

    public PieceFX getPieceFX() {
        return pieceFX;
    }

    protected void setPieceFX(PieceFX pieceFX) {
        this.pieceFX = pieceFX;
    }

    protected void removePieceFX() {
        this.pieceFX = null;
    }

    protected void toggleCheckHighlight() {
        if (getEffect() == null) {
            setEffect(checkHighlight);
        } else {
            setEffect(null);
        }
    }

    public String toString() {
        return getId();
    }
}
package chessfx.game;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;

public class ObservableMovePair {
    private Move whiteMove;
    private Move blackMove;

    ObservableMovePair(Move whiteMove, Move blackMove) {
        this.whiteMove = whiteMove;
        this.blackMove = blackMove;
    }

    public ObservableValue<Integer> getMoveCountProperty() {
        return new SimpleIntegerProperty(whiteMove.getMoveCount()).asObject();
    }

    public SimpleStringProperty getWhiteMoveNotationProperty() {
        return new SimpleStringProperty(whiteMove.getNotation());
    }

    public SimpleStringProperty getBlackMoveNotationProperty() {
        if (blackMove == null) {
            return null;
        }
        return new SimpleStringProperty(blackMove.getNotation());
    }

}
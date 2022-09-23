package chessfx.ui;

import chessfx.game.Game;
import chessfx.game.Move;
import chessfx.game.ObservableMovePair;
import chessfx.game.Piece.Color;
import chessfx.persistence.SaveLoadAgent;

import java.io.File;
import java.io.FileNotFoundException;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.FileChooser.ExtensionFilter;

public class ChessFXController {

    private Game game;
    private final SaveLoadAgent saveLoadAgent = new SaveLoadAgent();

    @FXML
    private Pane chessBoardPane;

    @FXML
    Button btnStartGame;

    @FXML
    MenuItem menuSaveGame;

    @FXML
    MenuItem menuLoadGame;

    @FXML
    MenuItem menuRemoveGame;

    @FXML
    ToggleGroup playingColor;

    @FXML
    RadioButton radioButtonPlayWhite;

    @FXML
    private TableView<ObservableMovePair> tbMoves;

    @FXML
    public TableColumn<ObservableMovePair, String> whiteMove;

    @FXML
    public TableColumn<ObservableMovePair, String> blackMove;

    @FXML
    public TableColumn<ObservableMovePair, Integer> moveCount;

    @FXML
    private void newGame() {
        game = new Game(radioButtonPlayWhite.isSelected() ? true : false);
        newGame(game);
    }

    private void newGame(Game game) {
        BoardFX boardFX = new BoardFX();
        Parent chessBoard = boardFX.createBoard(game);
        chessBoard.setId("chessboard");
        chessBoardPane.getChildren().add(chessBoard);

        menuSaveGame.setDisable(false);
        menuRemoveGame.setDisable(false);

        moveCount.setCellValueFactory(cellData -> cellData.getValue().getMoveCountProperty());
        whiteMove.setCellValueFactory(cellData -> cellData.getValue().getWhiteMoveNotationProperty());
        blackMove.setCellValueFactory(cellData -> cellData.getValue().getBlackMoveNotationProperty());
        tbMoves.setItems(game.getObservableMoveList());

        if (game.getPlayerToMove() == Color.White && !game.isHumanPlayingWhite() || game.getPlayerToMove() == Color.Black && game.isHumanPlayingWhite()) {
            Move move = game.getRandomComputerMove();
            boardFX.animateMove(move);
        }
    }

    @FXML
    private void removeGame() {
        chessBoardPane.getChildren().remove(chessBoardPane.lookup("#chessboard"));
        menuSaveGame.setDisable(true);
        menuRemoveGame.setDisable(true);
        tbMoves.getItems().clear();
    }

    @FXML
    private void saveGame() {
        Stage newWindow = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save game");
        // fileChooser.setInitialDirectory(saveLoadAgent.getDefaultDirectory());
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter(".json", "*.json"));
        File file = fileChooser.showSaveDialog(newWindow);
        try {
            saveLoadAgent.save(game, file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void loadGame() {
        Stage newWindow = new Stage();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Load game");
        // fileChooser.setInitialDirectory(saveLoadAgent.getDefaultDirectory());
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter(".json", "*.json"));
        File file = fileChooser.showOpenDialog(newWindow);
        
        try {
            game = saveLoadAgent.load(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        removeGame();
        newGame(game);
    }

}

package chessfx.persistence;

import chessfx.game.Game;
import chessfx.game.Game.GameStatus;
import chessfx.game.Move;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Scanner;

public class SaveLoadAgent implements SaveLoadInterface {

    private final File defaultFile = Paths.get(Path.of("") + "game.json").toFile();
    
    public void save(Game game) throws FileNotFoundException {
        save(game, defaultFile);
    }

    public Game load() throws FileNotFoundException {
        return load(defaultFile);
    }

    public void save(Game game, File file) throws FileNotFoundException {
        String whitePlayer = game.isHumanPlayingWhite() ? "Human" : "Computer";
        String blackPlayer = game.isHumanPlayingWhite() ? "Computer" : "Human";
        GameStatus gameStatus = game.getGameStatus();
        List<Move> moveList = game.getMoveList();

        if (!file.toString().endsWith(".json")) {
            file.renameTo(new File(file.getAbsoluteFile() + ".json"));
        }
        
        try (PrintWriter writer = new PrintWriter(file)) {

            writer.println("{");
            writer.printf("\t\"whitePlayer\": \"%s\",\n", whitePlayer);
            writer.printf("\t\"blackPlayer\": \"%s\",\n", blackPlayer);
            writer.printf("\t\"result\": \"%s\",\n", gameStatus);
            writer.printf("\t\"moves\": [\n");

            int moveListSize = moveList.size();
            int i = 0;
            for (Move move : moveList) {
                writer.println("\t\t{");
                writer.printf("\t\t\t\"moveCount\": %d,\n", move.getMoveCount());
                writer.printf("\t\t\t\"notation\": \"%s\",\n", move.getNotation());
                writer.printf("\t\t\t\"color\": \"%s\",\n", move.getPiece().getColor().toString());
                writer.printf("\t\t\t\"piece\": \"%s\",\n", move.getPiece().toString());
                writer.printf("\t\t\t\"fromSquare\": \"%s\",\n", move.getOrigin().toString());
                writer.printf("\t\t\t\"toSquare\": \"%s\",\n", move.getDestination().toString());
                writer.print("\t\t}");
                if (++i < moveListSize) writer.println(",");
            }
            writer.println("\n\t]");
            writer.println("}");
            writer.close();
        }

    }

    public Game load(File file) throws FileNotFoundException {
        Game game = null;
        try (Scanner scanner = new Scanner(file)) {

            String whitePlayer = null;
            String blackPlayer = null;
            String fromSquare = null;
            String toSquare = null;
            String text;

            while (scanner.hasNextLine()) {
                text =scanner.nextLine();
                if (text.startsWith("\t\"whitePlayer\": ")) {

                    whitePlayer = getValueFromKeyValuePair(text);

                } else if (text.startsWith("\t\"blackPlayer\": ")) {

                    blackPlayer = getValueFromKeyValuePair(text);

                    if (whitePlayer.equals("Human") && blackPlayer.equals("Computer")) {
                        game = new Game(true);
                    } else if (whitePlayer.equals("Computer") && blackPlayer.equals("Human")) {
                        game = new Game(false);
                    } else {
                        throw new IllegalStateException("Can currently only load games with Human vs Computer");
                    }

                } else if (text.startsWith("\t\t\t\"fromSquare\": ")) {
                    fromSquare = getValueFromKeyValuePair(text);

                } else if (text.startsWith("\t\t\t\"toSquare\": ")) {
                    toSquare = getValueFromKeyValuePair(text);
                    game.move(game.getBoard().getSquare(fromSquare).getPiece(), game.getBoard().getSquare(toSquare));

                } else {
                    continue;
                }
            }
        }

        return game;
    }

    private String getValueFromKeyValuePair(String keyValuePair) {
        return keyValuePair
            .split("\": ")[1]                   // Only keep substring following key
            .replace(",", "")       // Remove comma
            .replace("\"", "");     // Remove quotation marks
    }

}

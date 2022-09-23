package chessfx.persistence;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import chessfx.game.Game;
import chessfx.game.Move;
import chessfx.game.Game.GameStatus;
import chessfx.game.Piece.Color;

public class SaveLoadAgentTest {

    private Game game;
    private SaveLoadAgent agent = new SaveLoadAgent();
    private static File temporaryFile = Paths.get(Path.of("") + "saveLoadAgentTest.json").toFile();

    @BeforeEach
    public void setup() {
        game = new Game(true);
    }

    @Test
    public void saveFile() {
        try {
            agent.save(game, temporaryFile);
        } catch (FileNotFoundException e) {
            fail("Could not save file");
		}

        byte[] testFile = null, newFile = null;

        try {
			testFile = Files.readAllBytes(temporaryFile.toPath());
		} catch (IOException e) {
			fail("Could not load test file");
		}

		try {
			newFile = Files.readAllBytes(temporaryFile.toPath());
		} catch (IOException e) {
			fail("Could not load saved file");
		}
		assertNotNull(testFile);
		assertNotNull(newFile);
		assertTrue(Arrays.equals(testFile, newFile));
    }

    @Test
    public void saveAndLoadDefault() {
        game.move("e2", "e4");  // e4
        game.move("e7", "e5");  // e5
        game.move("g1", "f3");  // Nf3
        game.move("b8", "c6");  // Nc6
        game.move("f1", "c4");  // Bc4
        game.move("d8", "h4");  // Qh4
        game.move("d2", "d3");  // d3
        game.move("g8", "f6");  // Nf6
        game.move("c1", "g5");  // Bg5
        game.move("f6", "g4");  // Ng4
        game.move("f3", "e5");  // Nxe5

        List<Move> moveList = game.getMoveList();
        GameStatus gameStatus = game.getGameStatus();
        Color playerToMove = game.getPlayerToMove();

        try {
            agent.save(game);
        } catch (FileNotFoundException e) {
            fail("Could not save file");
		}

        try {
            Game loadGame = agent.load();
            assertTrue(loadGame.isHumanPlayingWhite());
            assertEquals(gameStatus, loadGame.getGameStatus());

            assertTrue(loadGame.getMoveList().size() == 11);
            for (int i = 0; i < 11; i++) {
                assertEquals(moveList.get(i).toString(), loadGame.getMoveList().get(i).toString());
            }

            assertEquals(playerToMove, loadGame.getPlayerToMove());

        } catch (FileNotFoundException e) {
            fail("Could not load file");
        }

    }

    @Test
    public void saveLoadHumanPlaysBlack() {
        game = new Game(false);
        game.move("e2", "e4");  // e4
        game.move("e7", "e5");  // e5

        assertFalse(game.isHumanPlayingWhite());

        try {
            agent.save(game);
        } catch (FileNotFoundException e) {
            fail("Could not save file");
		}

        try {
            Game loadGame = agent.load();
            assertFalse(loadGame.isHumanPlayingWhite());

        } catch (FileNotFoundException e) {
            fail("Could not load file");
        }
    }

    @Test
    public void saveLoadCustomFile() {
        game.move("e2", "e4");  // e4
        game.move("e7", "e5");  // e5
        game.move("g1", "f3");  // Nf3
        game.move("b8", "c6");  // Nc6

        List<Move> moveList = game.getMoveList();
        GameStatus gameStatus = game.getGameStatus();
        Color playerToMove = game.getPlayerToMove();

        try {
            agent.save(game, temporaryFile);
        } catch (FileNotFoundException e) {
            fail("Could not save file");
		}

        try {
            Game loadGame = agent.load(temporaryFile);
            assertTrue(loadGame.isHumanPlayingWhite());
            assertEquals(gameStatus, loadGame.getGameStatus());

            assertTrue(loadGame.getMoveList().size() == 4);
            for (int i = 0; i < 4; i++) {
                assertEquals(moveList.get(i).toString(), loadGame.getMoveList().get(i).toString());
            }

            assertEquals(playerToMove, loadGame.getPlayerToMove());

        } catch (FileNotFoundException e) {
            fail("Could not load file");
        }
    }

    @AfterAll
    static void deleteTemporaryFile() {
        temporaryFile.delete();
    }

}

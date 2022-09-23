package chessfx.persistence;

import java.io.File;
import java.io.FileNotFoundException;

import chessfx.game.Game;

public interface SaveLoadInterface {
    
    public void save(Game game) throws FileNotFoundException;
    public void save(Game game, File file) throws FileNotFoundException;

    public Game load() throws FileNotFoundException;
    public Game load(File file) throws FileNotFoundException;

}

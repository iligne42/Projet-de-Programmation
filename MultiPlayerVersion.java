import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.scene.Group;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Stack;

public class MultiPlayerVersion implements Serializable {
    protected GameVersion game;
    protected IntegerProperty timeToBeat;
    protected Scores scores;
    protected Stack<Player> players;

    public MultiPlayerVersion(String[] names, MazeFloors maze) throws FormatNotSupported, IOException {
        players = new Stack<>();
        scores = new Scores();
        for (int i = names.length - 1; i >= 0; i--) players.push(new Player(names[i]));
        game = new SoloVersion(maze, players.pop(), scores);
    }

    public GameVersion getGame() {
        return game;
    }

   /* public void save(String file) throws IOException{
      game.save(file);
      //save the players too
  }*/

    public void save(String file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }

    public MultiPlayerVersion load(String file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream(file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        MultiPlayerVersion g = (MultiPlayerVersion) ois.readObject();
        ois.close();
        return g;
    }

    public boolean gameOver() {
        return players.isEmpty();
    }

    public void next() throws IOException, FormatNotSupported {
        game = new SoloVersion(game.maze(), players.pop(), scores);
    }

    public Stack<Player> getPlayers() {
        return players;
    }
}
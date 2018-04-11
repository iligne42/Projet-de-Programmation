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
    protected MazeFloors originalMaze;
    protected GameVersion game;
    protected Scores scores;
    protected Stack<Player> players;

    public MultiPlayerVersion(String[] names, MazeFloors maze) throws FormatNotSupported, IOException{
        players = new Stack<>();
        scores = new Scores();
        for (int i = names.length - 1; i >= 0; i--) players.push(new Player(names[i]));
        try{
        game = new SoloVersion(maze.clone(), players.pop(), scores);
            originalMaze=maze;
        }
        catch(CloneNotSupportedException e){

        }
    }

    public GameVersion getGame() {
        return game;
    }

   /* public void save(String file) throws IOException{
      game.save(file);
      //save the players too
  }*/

    public void save(String file) throws IOException {
        File f=new File(file);
        if(!f.exists()) f.createNewFile();
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }


    public boolean gameOver() {
        return players.isEmpty();
    }

    public void next() throws IOException, FormatNotSupported,CloneNotSupportedException {
        game = new SoloVersion(originalMaze.clone(), players.pop(), scores);
    }

    public Stack<Player> getPlayers() {
        return players;
    }
}
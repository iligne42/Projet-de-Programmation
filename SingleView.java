
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.IOException;

public class SingleView extends View{

    public SingleView(GameVersion game) throws IOException{
       // super(new BorderPane(),game);
        super(new StackPane(),game);
        control=new SoloControl();
    }

    protected class SoloControl extends GameControl{

        public SoloControl() throws IOException {
            super();
        }

        public void whenIsFinished(){
            //game.stop();
            timePane.stop();
            gameTimer.stop();
            SingleView.this.setOnKeyPressed(null);
            game.addToScoresFile();
           displayScores(game.scores());

        }


    }
    }

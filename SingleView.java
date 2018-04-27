
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

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
        protected class EndPane extends TimePane{
            public EndPane(){
                super(0);
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(0.2),(event)->{
                    game.convertBonus();
                    timePane.timeSeconds.set(game.getElapsed());
                    if(game.player.getBonus().size()==0) {
                        game.addToScoresFile();
                        displayScores(game.scores());
                        timeLine.stop();
                    }
                }));
            }
        }

        public void whenIsFinished(){
            //game.stop();
            timePane.stop();
            gameTimer.stop();
            SingleView.this.setOnKeyPressed(null);
            new EndPane().play();

        }


    }
    }

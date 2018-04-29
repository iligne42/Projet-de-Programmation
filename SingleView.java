
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
        super(new StackPane(),game);
        control=new SoloControl();
    }

    protected class SoloControl extends GameControl{

        public SoloControl() throws IOException {
            super(false);
        }
        protected class EndPane extends TimePane{
            public EndPane(){
                super(0);
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(0.1),(event)->{
                    game.convertBonus();
                    timePane.timeSeconds.set(game.getElapsed());
                    if(game.player.getBonus().size()==0 && !MazeInterface.sounds(5).isPlaying()) {
                        game.addToScoresFile();
                        displayScores(game.scores());
                        timeLine.stop();
                    }
                }));
            }
        }
        @Override
        public void whenIsFinished(){
            MazeInterface.sounds(5).play();
            timePane.stop();
            gameTimer.stop();
            SingleView.this.setOnKeyPressed(null);
            new EndPane().play();
        }


    }
}

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.util.Duration;


public class View extends Node {
    protected GameVersion game;
    protected TimePane timePane;
    protected MazePane mazePane;

    private abstract class TimePane extends Pane {//not sure though, it is a layout
        protected Timeline timeLine;
        protected Label timeLabel;
        protected IntegerProperty timeSeconds;

        public TimePane(int time) {
            timeSeconds = new SimpleIntegerProperty(time) {
                @Override
                public StringBinding asString() {
                    return new StringBinding() {
                        {
                            super.bind(timeSeconds);
                        }
                        @Override
                        protected String computeValue() {
                           return game.getTime(timeSeconds.getValue());
                        }
                    };
                }
            };
            timeLabel = new Label();
            timeLabel.textProperty().bind(timeSeconds.asString());
            //when there are only a few seconds left timeLabel.setTextFill(Color.RED);
            //timeLabel.setStyle("");
            timeLine = new Timeline();



            //to laucnh timeLine.playFromStart();
        }

        private class TimeTrialPane extends TimePane{
            protected int timeLimit;

            public TimeTrialPane(int timeLimit){
                super(timeLimit);
                this.timeLimit=timeLimit;
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(timeLimit + 1),
                                new KeyValue(timeSeconds, 0)));
            }
        }

        private class SoloTimePane extends TimePane{

            public SoloTimePane(){
                timeSeconds=new SimpleIntegerProperty(0);

                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1),(event)->{
                    timeSeconds.set(timeSeconds.get()+1);
                }));
            }

        public boolean timeOver(){
            return timeSeconds.get()==0;
        }

        public String getElapsedTime() {
            return game.getTime(timeLimit-timeSeconds.get());
        }

    }

    /*private class TimePane extends Pane{//not sure though, it is a layout
        protected Timeline timeLine;
        protected Label timeLabel;
        protected StringProperty timeToPrint;
        protected IntegerProperty timeSeconds;

        public TimePane(){
            timeToPrint=new SimpleStringProperty(game.getTime());
            timeLabel=new Label();
            timeLabel.textProperty().bind(timeToPrint);
            timeLabel.setText(game.getTime());
            //when there are only a few seconds left timeLabel.setTextFill(Color.RED);
            //timeLabel.setStyle("");
            timeLine=new Timeline();
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.getKeyFrames().add(
                    new KeyFrame(Duration.seconds(1),(event)-> {
                        game.modifyTime();
                        timeLabel.setText(game.getTime());
                        if(game.timeOver()) timeLine.stop();
                    }
                )
            );
            timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds()), new KeyValue(timeSeconds,0))


           //to laucnh timeLine.playFromStart();
        }

    }*/

    private class MazePane extends Pane {

    }

    public View(GameVersion game){
        this.game=game;
        this.timePane=new TimePane();
    }

    //Put a countdown shade, like a transparent one and then start the timer

    //Somewhere here, handle keyboard events
    //Put a vertical panel at the right, to contain the timer and other stuff
    public void action(){
       int i;
       if(game.gameOver()){
           //remove le temps
           //afficher sur un joptionpane
           //Switch to print of 10 bestScores with name;
           game.addToScores(timePane.getElapsedTime());
       }
       else if(game.timeOver()){
            //remove timePanel
           //print you loose, dumb ass

       }
       else{
           //game.move(i);
       }
        //remove the panels with the maze and time and switch to score viewing
        //if(game.timeOver()) remove the panel with time and print tome over, only in time trial mode though
    }

    //Use a path transition to slide the player
}*/
}

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.io.File;
import java.sql.Time;
import java.util.ArrayList;
import java.util.HashMap;


public abstract class View extends BorderPane{
    protected GameVersion game;
    protected TimePane timePane;
    protected MazePane mazePane;
    protected GameControl control;
//add Menu bar to save game or start a new game, and view the help and shir
    //timeLine.pause();
    protected abstract class TimePane extends Pane {//not sure though, it is a layout
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
                            return MazeInterface.getTime(timeSeconds.getValue());
                        }
                    };
                }
            };
            //timeSeconds=new TimeProperty(time);
            timeLabel = new Label();
            timeLabel.textProperty().bind(timeSeconds.asString());
            timeLine = new Timeline();
        }

        public abstract String getElapsedTime();


        public abstract int getElapsedSeconds();

        public boolean timeOver() {
            return false;
        }

        public void setTimeSeconds(int time){
            timeSeconds.set(time);
        }


        public void stop(){
            timeLine.stop();
            this.setVisible(false);
        }

        public void pause(){
            timeLine.pause();
        }

        public void start(){
            timeLine.playFromStart();
        }
    }

        protected class TimeTrialPane extends TimePane{
            protected int timeLimit;

            public TimeTrialPane(int timeLimit){
                super(timeLimit);
                this.timeLimit=timeLimit;
                timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(timeLimit + 1),
                                new KeyValue(timeSeconds, 0)));
            }

            public boolean timeOver(){
                return timeSeconds.get()==0;
            }

            public String getElapsedTime() {
                return MazeInterface.getTime(timeLimit-timeSeconds.get());
            }

            public int getElapsedSeconds() {
                return timeLimit-timeSeconds.get();
            }
        }

        protected class SoloTimePane extends TimePane{

            public SoloTimePane(){
                super(0);
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1),(event)->{
                    timeSeconds.set(timeSeconds.get()+1);
                }));
            }

            public String getElapsedTime() {
                return MazeInterface.getTime(timeSeconds.get());
            }


            public int getElapsedSeconds() {
                return timeSeconds.get();
            }

    }


    protected class MazePane extends Pane {
        protected Maze maze;

        public MazePane() {
            maze = game.maze();
        }

        public void initMaze(){

        }

        public void printMaze() {
        }
    }

    public class TimeProperty extends SimpleIntegerProperty{
        public TimeProperty(int time){
            super(time);
        }

        @Override
        public StringBinding asString() {
            return new StringBinding() {
                {
                    super.bind(TimeProperty.this);
                }
                @Override
                protected String computeValue() {
                    return MazeInterface.getTime(TimeProperty.this.getValue());
                }
            };



        }
    }


    public Timeline countdownToStart(IntegerProperty starter){
        Timeline countdown = new Timeline();
        countdown.getKeyFrames().add(new KeyFrame(
                Duration.seconds(5),
                new KeyValue(starter,0))
        );
        return countdown;
    }

   /* public void beatTheRecord(java.time.Duration rec){
        if(rec.compareTo(record)>0){
            record=rec;
            Alert newRecord= new Alert(Alert.AlertType.INFORMATION);
            newRecord.setTitle("");
            newRecord.setHeaderText("CONGRATULATION ! ");
            newRecord.setContentText("You have made a new record ! ");
            newRecord.show();
        }
    }*/

    protected class GameControl {
        protected Scores scores;

        public void displayScore(Pane root) {
            root.getChildren().clear();
            String display = scores.getScores();
            String[] splits = display.split("\n");
            for (String s : splits) root.getChildren().add(new Label(s));
        }
    }

    public View(GameVersion game){
        this.game=game;
        this.mazePane=new MazePane();
        if(game instanceof SoloVersion) timePane=new SoloTimePane();
        else if(game instanceof TimeTrialVersion) timePane=new TimeTrialPane(((TimeTrialVersion) game).timeLimit);
        //this.timePane=new TimePane(time);
        //this.getChildren().addAll(timePane,mazePane);
        this.setLeft(timePane);
        this.setCenter(mazePane);
    }


    //Put a countdown shade, like a transparent one and then start the timer

    //Somewhere here, handle keyboard events
    //Put a vertical panel at the right, to contain the timer and other stuff

   // public abstract void action();


}
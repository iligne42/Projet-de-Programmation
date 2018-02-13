
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Box;
import javafx.scene.shape.Rectangle;
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
    protected Scene scene;
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
            timeLabel.setStyle("-fx-alignment:center; -fx-background-color:grey");
            timeLine = new Timeline();
            this.getChildren().add(timeLabel);
            this.setStyle("-fx-border-color:red; -fx-alignment:center");
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
            Group rotate = new Group();
            ObservableList<Node> childs= rotate.getChildren();
            Box cell = new Box();
            int hori=0,depth=0;
            for (int i = 0;i<maze.getHeight() ;i++ ) {
                for (int j = 0; j <maze.getWidth() ;j++ ) {
                    if(maze.getCase(i,j)==Maze.WALL){
                        cell = new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
                        cell.setMaterial(COLOR_WALL);
                        cell.setTranslateY(400);
                    }
                    else{
                        cell = new Box(SIZE_BOX,0,SIZE_BOX);
                        cell.setMaterial(COLOR_WAY);
                        cell.setTranslateY(SIZE_BOX+SIZE_BOX/2);
                    }
                    hori+=SIZE_BOX;
                    cell.setTranslateX(hori);
                    cell.setTranslateZ(depth);
                    childs.add(cell);
                }
                hori=0;
                depth+=SIZE_BOX;
            }
            Slider s = new Slider(0,360,0);
            s.setTranslateX(50);
            s.setTranslateY(50);
            rotate.rotateProperty().bind(s.valueProperty());

            Slider t = new Slider(-10000,10000,0);
            t.setTranslateX(200);
            t.setTranslateY(50);
            rotate.translateZProperty().bind(t.valueProperty());

            Slider g = new Slider(-10000,10000,0);
            g.setTranslateX(400);
            g.setTranslateY(50);
            rotate.translateXProperty().bind(g.valueProperty());
            this.getChildren().addAll(rotate,s,t,g);

        }

        public void printMaze() {
          /*  for(int i=0;i<maze.length;i++){
                for(int j=0;j<maze[i].length;j++){
                    if(maze.getCase(i,j)==Maze.WALL){
                        //width=this.getWidth()/maze.width() and same for length
                        //acutally no since there will be a camera associated with it, and it will take all of the available sace, maybe init maze insread
                      //  this.getChildren().add(new Rectangle()
                    }
                }
            }*/
        }



    public class TimeProperty extends SimpleIntegerProperty {
        public TimeProperty(int time) {
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

        public void countDownToStart(){
            Label label=new Label();
            label.setStyle("-fx-background-color:transparent");
            IntegerProperty count=new SimpleIntegerProperty(5);
            label.textProperty().bind(count.asString());

            Timeline countdown = new Timeline();
            countdown.getKeyFrames().add(new KeyFrame(
                    Duration.seconds(5),
                    new KeyValue(count,0))
            );
            if(count.get()==0){
                //remove
                //game.start();
                //timePane.start();
            }
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

    public void setScene(Scene s){
        scene=s;
    }


    //Put a countdown shade, like a transparent one and then start the timer

    //Somewhere here, handle keyboard events
    //Put a depthical panel at the right, to contain the timer and other stuff

   // public abstract void action();


}
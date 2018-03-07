import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.ObservableList;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;
import javafx.util.Duration;

import java.awt.geom.Point2D;

public class View extends Scene {
    protected BorderPane content;
    protected GameVersion game;
    protected TimePane timePane;
    protected MazePane mazePane;
    protected GameControl control;
    ;
    protected SubScene mazeScene;

    public View(BorderPane root, GameVersion game) {
        super(root);
        this.game = game;
        content = root;
        PerspectiveCamera cam = new PerspectiveCamera(true);
        this.mazePane = new MazePane(cam);
        mazeScene = new SubScene(mazePane, 1000, 1000, true, SceneAntialiasing.BALANCED);
        mazeScene.setCamera(cam);
        if (game instanceof SoloVersion) timePane = new SoloTimePane();
        else if (game instanceof TimeTrialVersion) timePane = new TimeTrialPane(((TimeTrialVersion) game).timeLimit);
        content.setCenter(mazeScene);
        content.setLeft(timePane);
    }

    //add Menu bar to save game or start a new game, and view the help and shir
    //timeLine.pause();

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
                    return MazeInterface.getT(TimeProperty.this.getValue());
                }
            };


        }
    }

    protected abstract class TimePane extends Pane {//not sure though, it is a layout
        protected Timeline timeLine;
        protected Label timeLabel;
        protected IntegerProperty timeSeconds;



        public TimePane(int time) {
           timeSeconds=new TimeProperty(time);
            timeLabel = new Label();
            timeLabel.textProperty().bind(timeSeconds.asString());
            timeLabel.setStyle("-fx-alignment:center; -fx-background-color:grey");
            timeLine = new Timeline();
            this.getChildren().add(timeLabel);
            this.setStyle("-fx-border-color:red; -fx-alignment:center");
        }

        public String getElapsedTime(){
            return MazeInterface.getT(game.getElapsed());
        }

        public  int getElapsedSeconds(){
            return game.getElapsed();
        }

        public boolean timeOver() {
            return false;
        }

        public void setTimeSeconds(int time) {
            timeSeconds.set(time);
        }


        public void stop() {
            timeLine.stop();
            this.setVisible(false);
        }

        public void pause() {
            timeLine.pause();
        }

        public void start() {
            timeLine.playFromStart();
        }
    }

    protected class TimeTrialPane extends TimePane {
        protected int timeLimit;

        public TimeTrialPane(int timeLimit) {
            super(timeLimit);
            this.timeLimit = timeLimit;
               /* timeLine.getKeyFrames().add(
                        new KeyFrame(Duration.seconds(timeLimit + 1),
                                new KeyValue(timeSeconds, 0)));*/
               /* timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(timeLimit+1),(event)->{
                    game.elapse(1);
                    timeSeconds.set(timeLimit-game.getElapsed());
                }));*/
            // timeLine.setCycleCount(timeLimit);
            timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
                game.elapse(1);
                timeSeconds.set(timeLimit - game.getElapsed());
                if (timeSeconds.get() == 0) timeLine.stop();
            }));
        }

        public boolean timeOver() {
            return timeSeconds.get() == 0;
        }
    }
    //Study interpolator

    protected class SoloTimePane extends TimePane {

        public SoloTimePane() {
            super(0);
            timeLine.setCycleCount(Timeline.INDEFINITE);
               /* timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1),(event)->{
                    timeSeconds.set(timeSeconds.get()+1);
                }));*/
            timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
                game.elapse(1);
                timeSeconds.set(game.getElapsed());
            }));
        }

    }


    protected class MazePane extends Pane {
        protected final Maze maze = game.maze();
        protected Camera camera;
        protected PointLight light;
        protected final int MAZE_LENGTH = maze.getHeight();
        protected final int MAZE_WIDTH = maze.getWidth();
        protected final int SIZE_BOX = 400;
        protected PhongMaterial COLOR_WALL = new PhongMaterial(Color.DARKGREY);
        protected PhongMaterial COLOR_WAY = new PhongMaterial(Color.BLACK);
        protected PhongMaterial COLOR_ENTRY = new PhongMaterial(Color.RED);
        protected PhongMaterial COLOR_END = new PhongMaterial(Color.LIGHTGOLDENRODYELLOW);
        protected DoubleProperty x, z, angle;

        public MazePane(Camera cam) {
            camera = cam;
            light = new PointLight(Color.DARKORANGE);
        }

        public void initMaze() {
            Group rotate = new Group();
            ObservableList<Node> childs = rotate.getChildren();
            Box cell;
            //Box test = null;
            Box roof = new Box(MAZE_WIDTH * SIZE_BOX, 0, MAZE_LENGTH * SIZE_BOX);
            roof.setMaterial(COLOR_WAY);
            roof.setTranslateY(-SIZE_BOX / 2);
            roof.setTranslateX(SIZE_BOX*(MAZE_WIDTH-1)/2);
            roof.setTranslateZ(SIZE_BOX*(MAZE_LENGTH-1)/2);

            for (int i = 0; i < MAZE_LENGTH; i++) {
                for (int j = 0; j < MAZE_WIDTH; j++) {
                    if (maze.getCase(i, j) == Maze.WALL) {
                        cell = new Box(SIZE_BOX, SIZE_BOX, SIZE_BOX);
                        cell.setMaterial(COLOR_WALL);
                        cell.setTranslateY(0);
                    } else {
                        cell = new Box(SIZE_BOX, 0, SIZE_BOX);
                        if (maze.getCase(i, j) == Maze.START) {
                            cell.setMaterial(COLOR_ENTRY);
                            //test = cell;
                        }
                        else if (maze.getCase(i, j) == Maze.END) cell.setMaterial(COLOR_END);
                        else cell.setMaterial(COLOR_WAY);
                        cell.setTranslateY(SIZE_BOX / 2);
                    }
                    cell.setTranslateX(j * SIZE_BOX);
                    cell.setTranslateZ(i * SIZE_BOX);
                    childs.add(cell);
                }
            }
            rotate.getChildren().add(roof);
            this.getChildren().add(rotate);
            buildCamera(rotate);
            printMaze();
           // if (test != null) System.out.println("Test " + test.getTranslateX() + "   " + test.getTranslateZ());

        }

        public void printMaze() {
            for (int i = 0; i < MAZE_LENGTH; i++) {
                for (int j = 0; j < MAZE_WIDTH; j++)
                    System.out.print(maze.getCase(i, j) + "  ");
                System.out.println();
            }
        }

        public void buildCamera(Group root) {
            //Ici vérifier dans quel sens la début est pour modifier x et z
            System.out.println("Height " + maze.getHeight() + "   Width  " + maze.getWidth());
            camera.setFarClip(10000);
            camera.setNearClip(0.1);
            camera.setTranslateY(0);
            light.setTranslateY(0);
            Point2D position = game.player().getPosition();
            System.out.println(position);
            x = new SimpleDoubleProperty(position.getX() * SIZE_BOX-SIZE_BOX/2);
            z = new SimpleDoubleProperty(position.getY() * SIZE_BOX-SIZE_BOX/2);
            angle = new SimpleDoubleProperty(90 - game.player().orientation());
            camera.translateXProperty().bind(x);
            camera.translateZProperty().bind(z);
            camera.rotateProperty().bind(angle);
            camera.setRotationAxis(Rotate.Y_AXIS);
            light.translateXProperty().bind(x);
            light.translateZProperty().bind(z);
            root.getChildren().add(camera);
            System.out.println(camera.getTranslateX() + "   " + camera.getTranslateY() + "    " + camera.getTranslateZ());
        }

        public void reset() {

        }
    }





   /* public void beatTheRecord(java.time.Duration rec){
        if(rec.compareTo(record)>0){
            record=rec;
            Alert newRecord= new Alert(Alert.AlertType.INFORMATION);
            newRecord.setTitle("");
            newRecord.setHeaderText("CONGRATULATIONS ! ");
            newRecord.setContentText("You have made a new record ! ");
            newRecord.show();
        }
    }*/

    protected abstract class GameControl {

        public void displayScore(Pane root) {
            root.getChildren().clear();
            String display = game.scores().getScores();
            String[] splits = display.split("\n");
            for (String s : splits) root.getChildren().add(new Label(s));
        }

        public void countDownToStart() {
            Label label = new Label();
            label.setStyle("-fx-background-color:transparent");
            IntegerProperty count = new SimpleIntegerProperty(5);
            label.textProperty().bind(count.asString());

            Timeline countdown = new Timeline();
            countdown.getKeyFrames().add(new KeyFrame(
                    Duration.seconds(5),
                    new KeyValue(count, 0))
            );
            if (count.get() == 0) {
                //remove
                //game.start();
                //timePane.start();
            }
        }

        public void handleAction() {
            View.this.setOnKeyPressed(e -> {
                System.out.println(e.getCode());
                switch (e.getCode()) {
                    case UP:
                        game.move(1);
                        break;
                    case RIGHT:
                        game.move(4);
                        break;
                    case DOWN:
                        game.move(2);
                        break;
                    case LEFT:
                        game.move(3);
                        break;
                    // case S: mazePane.rotateY.setAngle(mazePane.rotateY.getAngle()+5);
                }
                Point2D pos=game.player().getPosition();
                mazePane.x.set(pos.getX()*mazePane.SIZE_BOX-mazePane.SIZE_BOX/2);
                mazePane.z.set(pos.getY()*mazePane.SIZE_BOX-mazePane.SIZE_BOX/2);
                mazePane.angle.set(90-game.player().orientation());
                System.out.println(mazeScene.getCamera().getTranslateX() + "     " + mazeScene.getCamera().getTranslateZ() + "   " + mazeScene.getCamera().getRotate());

            });
              /*  View.this.addEventHandler(MouseEvent.ANY,evt->{
                    //save mouseold coordinates somewhere
                    double mouseXnew=evt.getSceneX();
                    double mouseYnew=evt.getSceneY();
                    switch (evt.getEventType()){
                        //checkfor, orin case
                        case MouseEvent.MOUSE_DRAGGED:
                            double pitchR=
                            break;
                            case MouseEvent.
                    }
                }
            });*/
        }

        public void whenIsFinished(){

        }


   /* public void setScene(Scene s){
        scene=s;
    }*/


        //Put a countdown shade, like a transparent one and then start the timer

        //Somewhere here, handle keyboard events
        //Put a depthical panel at the right, to contain the timer and other stuff

        // public abstract void action();


    }
}
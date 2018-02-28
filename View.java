import javafx.animation.AnimationTimer;

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

import javafx.scene.transform.Translate;

import javafx.util.Duration;



import java.awt.geom.Point2D;



public class View extends Scene{

    protected BorderPane content;

    protected GameVersion game;

    protected TimePane timePane;

    protected MazePane mazePane;

    protected GameControl control;;

    protected SubScene mazeScene;



    public View(BorderPane root, GameVersion game){

        super(root,1000,1000,true);

        PerspectiveCamera cam=new PerspectiveCamera(true);

        this.setCamera(cam);

        content=root;

       // content.getChildren().add(cam);

        this.game=game;

        //this.camera=camera;

        this.mazePane=new MazePane();

        //this.mazeScene=new SubScene();

        if(game instanceof SoloVersion) timePane=new SoloTimePane();

        else if(game instanceof TimeTrialVersion) timePane=new TimeTrialPane(((TimeTrialVersion) game).timeLimit);

        //this.timePane=new TimePane(time);

        //this.getChildren().addAll(timePane,mazePane);

        //this.setLeft(timePane);

        content.setCenter(mazePane);

    }



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



        protected class TimeTrialPane extends TimePane {

            protected int timeLimit;



            public TimeTrialPane(int timeLimit){

                super(timeLimit);

                this.timeLimit=timeLimit;

                timeLine.getKeyFrames().add(

                        new KeyFrame(Duration.seconds(timeLimit + 1),

                                new KeyValue(timeSeconds, 0)));

                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(timeLimit+1),(event)->{

                    game.elapse(1);

                    timeSeconds.set(timeLimit-game.getElapsed());

                }));

               // timeLine.setCycleCount(timeLimit);

                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1),(event)->{

                    game.elapse(1);

                    timeSeconds.set(timeLimit-game.getElapsed());

                    if(timeSeconds.get()==0) timeLine.stop();

                }));

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

        //Study interpolator



        protected class SoloTimePane extends TimePane {



            public SoloTimePane(){

                super(0);

                timeLine.setCycleCount(Timeline.INDEFINITE);

                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1),(event)->{

                    timeSeconds.set(timeSeconds.get()+1);

                }));

                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1),(event)->{

                    game.elapse(1);

                    timeSeconds.set(game.getElapsed());

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

            protected final Maze maze=game.maze();

            protected Camera camera;

            //protected PerspectiveCamera camera=new PerspectiveCamera(true);

            protected final int MAZE_LENGTH = maze.getHeight();

            protected final int MAZE_WIDTH = maze.getWidth();

            protected final int SIZE_BOX = 400;

            protected PhongMaterial COLOR_WALL = new PhongMaterial(Color.DARKGREY);

            protected PhongMaterial COLOR_WAY = new PhongMaterial(Color.BLACK);

            protected PhongMaterial COLOR_ENTRY = new PhongMaterial(Color.RED);

            protected PhongMaterial COLOR_END = new PhongMaterial(Color.LIGHTGOLDENRODYELLOW);



            // camera.setTranslateY(SIZE_BOX);

            protected DoubleProperty x,z,angle;



            public MazePane() {

                // maze = game.maze();

                camera=View.this.getCamera();

               // camera.setNearClip(0.1);

                //camera.setFarClip(10000.0);

                //scene.setCamera(camera);

            }



            public void initMaze() {

                Group rotate = new Group();

                ObservableList<Node> childs = rotate.getChildren();

                Box cell;

                Box roof=new Box(maze.getWidth()*SIZE_BOX,0,maze.getHeight()*SIZE_BOX);

                roof.setMaterial(COLOR_WAY);

                roof.setTranslateY(-SIZE_BOX/2);

                roof.setTranslateX(maze.getWidth()*SIZE_BOX/2);

                roof.setTranslateZ(maze.getHeight()*SIZE_BOX/2);

                //scene.setCamera(camera);



                for (int i = 0; i < MAZE_LENGTH; i++) {

                    for (int j = 0; j < MAZE_WIDTH; j++) {

                        if (maze.getCase(i, j) == Maze.WALL) {

                            cell = new Box(SIZE_BOX, SIZE_BOX, SIZE_BOX);

                            cell.setMaterial(COLOR_WALL);

                            cell.setTranslateY(0);

                        } else {

                            cell = new Box(SIZE_BOX, 0, SIZE_BOX);

                            if(maze.getCase(i,j)==Maze.START) cell.setMaterial(COLOR_ENTRY);

                            else if(maze.getCase(i,j)==Maze.END) cell.setMaterial(COLOR_END);

                            else cell.setMaterial(COLOR_WAY);

                            cell.setTranslateY(SIZE_BOX / 2);

                        }

                        cell.setTranslateX(j*SIZE_BOX);

                        cell.setTranslateZ(i*SIZE_BOX);

                        childs.add(cell);

                    }

                }

                rotate.getChildren().add(roof);

                this.getChildren().add(rotate);

                buildCamera(rotate);



            }



            public void printMaze() {

            }



           /* public void buildCamera(){

             translateX=new Translate(0,0,0);

             translateZ=new Translate().bind

            }*/



            public void buildCamera(Group root){

                //Ici vérifier dans quel sens la début est pour modifier x et z

                System.out.println("Height "+maze.getHeight()+"   Width  "+maze.getWidth());

                camera.setFarClip(6000);

                camera.setNearClip(0.1);

                camera.setTranslateY(0);

                Point2D position=game.player().getPosition();

                x=new SimpleDoubleProperty(position.getX()*SIZE_BOX);

                z=new SimpleDoubleProperty(position.getY()*SIZE_BOX);

                angle=new SimpleDoubleProperty(90-game.player().orientation());

                camera.translateXProperty().bind(x);

                camera.translateZProperty().bind(z);

                camera.rotateProperty().bind(angle);

                camera.setRotationAxis(Rotate.Y_AXIS);

                root.getChildren().add(camera);



                //utliser les objets transform plutot car la c'est les propriétés par défaut

               System.out.println(camera.getTranslateX()+"   "+camera.getTranslateY()+"    "+camera.getTranslateZ());

            }



            /*public void buildCamera(PerspectiveCamera cam){



                cam.setFarClip(10000.0);



                cam.setNearClip(0.6);



                Point start = maze.beginning();



                int y = (int) start.getX();



                int x = (int) start.getY();



                Box begin = cases[x][y];



                double posx = begin.getTranslateX();



                double posz = begin.getTranslateZ();



                cam.setTranslateX(posx);



                cam.setTranslateY(SIZE_BOX);



                cam.setTranslateZ(posz);



                cam.setRotationAxis(Rotate.Y_AXIS);



                if(posz==0.0)cam.setTranslateZ(-1000);



                if(posz==(double) SIZE_BOX*(maze.getHeight()-1)){



                    cam.setTranslateZ(1000+posz);



                    cam.setRotate(180.0);



                }



                if(posx==(double) SIZE_BOX*maze.getWidth()){



                    cam.setTranslateX(1000+posx);



                    cam.setRotate(270.0);



                }



                if(posx == (double) SIZE_BOX ){



                    cam.setTranslateX(-600.0);



                    cam.setRotate(90.0);



                }



                //Box begin2 = cases[y][x];



                System.out.println("x "+begin.getTranslateX()+"/ z "+begin.getTranslateZ()+" / "+cam.getRotate()+" box");



                //System.out.println("x "+begin2.getTranslateX()+"/ z "+begin2.getTranslateZ()+" box 2");



                System.out.println("x "+cam.getTranslateX()+" / z "+cam.getTranslateZ());



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





   /* public void setScene(Scene s){

        scene=s;

    }*/





        //Put a countdown shade, like a transparent one and then start the timer



        //Somewhere here, handle keyboard events

        //Put a depthical panel at the right, to contain the timer and other stuff



        // public abstract void action();







    }
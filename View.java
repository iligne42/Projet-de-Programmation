import javafx.animation.*;
import java.time.LocalDate;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.util.Duration;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class View extends Scene {
    protected StackPane main;
    protected Stage st;
    protected ToolBar tool;
    protected VBox display;
    protected GameVersion game;
    protected TimePane timePane;
    protected MazePane mazePane;
    protected GameControl control;
    protected SubScene mazeScene;
    protected Label timeLabel;
    protected Button save,inv,quit,help,restart,pause,plan;
    protected Group map;

    public View(StackPane root, GameVersion game) {
        super(root);
        this.game = game;
        main=root;
        Rectangle2D screenBounds= Screen.getPrimary().getBounds();
        PerspectiveCamera cam = new PerspectiveCamera(true);
        this.mazePane = new MazePane(cam);
        mazeScene = new SubScene(mazePane, screenBounds.getWidth(), screenBounds.getHeight(), true, SceneAntialiasing.BALANCED);
        mazeScene.setCamera(cam);
       // timeLabel = new Label();
       // timeLabel.textProperty().bind(game.timeSecondsProperty().asString());
        if (game instanceof SoloVersion) timePane = new SoloTimePane();
        else if (game instanceof TimeTrialVersion) timePane = new TimeTrialPane(((TimeTrialVersion) game).timeLimit);
        mazePane.setToolBar();
        main.getChildren().addAll(mazeScene,timePane,tool);
        StackPane.setAlignment(timePane,Pos.TOP_LEFT);
        StackPane.setAlignment(tool,Pos.TOP_RIGHT);
    }

    public void setStage(Stage stage){
      st=stage;
    }

   /* public View(BorderPane root, GameVersion game) {
        super(root);
        this.game = game;
        content = root;
        PerspectiveCamera cam = new PerspectiveCamera(true);
        this.mazePane = new MazePane(cam);
        mazeScene = new SubScene(mazePane, 1490, 860, true, SceneAntialiasing.BALANCED);
        mazeScene.setCamera(cam);
        if (game instanceof SoloVersion) timePane = new SoloTimePane();
        else if (game instanceof TimeTrialVersion) timePane = new TimeTrialPane(((TimeTrialVersion) game).timeLimit);
        content.setCenter(mazeScene);
        content.setLeft(timePane);
    }*/

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



    protected abstract class TimePane extends Pane{//not sure though, it is a layout
        protected Timeline timeLine;
        protected Label timeLabel;
        protected IntegerProperty timeSeconds;



        public TimePane(int time) {
            timeSeconds=new TimeProperty(time);
            timeLabel = new Label();
            timeLine = new Timeline();
            timeLabel.textProperty().bind(timeSeconds.asString());
            timeLabel.setStyle("-fx-alignment:center;-fx-text-fill:crimson;-fx-font-size:50pt");
            this.getChildren().add(timeLabel);
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
        }

        public void pause() {
            timeLine.pause();
        }

        public void start() {
            timeLine.playFromStart();
        }
        public void play(){
          timeLine.play();
        }
    }


    protected class TimeTrialPane extends TimePane {
        protected int timeLimit;

        public TimeTrialPane(int timeLimit) {
            super(timeLimit);
            this.timeLimit = timeLimit;
            timeLine.setCycleCount(timeLimit);
            timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
                game.elapse(1);
                timeSeconds.set(timeLimit - game.getElapsed());

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


    protected class MazePane extends Group {
        protected Group world;
        protected final LinkedList<Maze> floors =game.floors();
        protected PerspectiveCamera camera;
        protected AmbientLight light;
        protected final int MAZE_LENGTH =game.current().getHeight();
        protected final int MAZE_WIDTH = game.current().getWidth();
        protected final int SIZE_BOX = 400;
        protected final int STEP=50;
        protected int posFx,posFz;
        protected PhongMaterial COLOR_WALL = new PhongMaterial();
        protected PhongMaterial COLOR_WAY = new PhongMaterial(Color.DARKGREY);
        protected PhongMaterial COLOR_ENTRY = new PhongMaterial(Color.RED);
        protected PhongMaterial COLOR_END = new PhongMaterial(Color.LIGHTGOLDENRODYELLOW);
        protected PhongMaterial COLOR_DOOR= new PhongMaterial();
        protected PhongMaterial COLOR_STAIRS=new PhongMaterial(Color.WHITESMOKE);
        protected DoubleProperty x=new SimpleDoubleProperty(0);
        protected DoubleProperty z=new SimpleDoubleProperty(0);
        protected DoubleProperty y=new SimpleDoubleProperty(0);
        protected DoubleProperty angle=new SimpleDoubleProperty(0);
        protected Rotate rotateX;
        protected final ArrayList<Node> keyOrBonus=new ArrayList<>();
        protected Vector3D[] coordSwitch=new Vector3D[floors.size()];
        protected Group[] floorGroups=new Group[floors.size()];

        public MazePane(PerspectiveCamera cam) {
            camera = cam;
           // cameraLight=new PointLight(Color.DARKORANGE);
            display = new VBox();
            display.setAlignment(Pos.CENTER);
        }

        public void setToolBar(){
          String style="-fx-background-color:linear-gradient(#686868 0%, #232723 25%, #373837 75%, #757575 100%),linear-gradient(#020b02, #3a3a3a),linear-gradient(#9d9e9d 0%, #6b6a6b 20%, #343534 80%, #242424 100%),linear-gradient(#8a8a8a 0%, #6b6a6b 20%, #343534 80%, #262626 100%),linear-gradient(#777777 0%, #606060 50%, #505250 51%, #2a2b2a 100%);-fx-background-insets: 0,1,4,5,6;-fx-background-radius: 9,8,5,4,3;-fx-padding: 15 30 15 30;-fx-font-size: 18px;-fx-font-weight: bold;-fx-text-fill: white;-fx-effect: dropshadow( three-pass-box , rgba(255,255,255,0.2) , 1, 0.0 , 0 , 1);";
          quit = new Button("Quit");
          quit.setOnMouseClicked(e->{
            st.close();
          });
          plan = new Button("Map");
          plan.setOnMouseClicked(e->{
            if(!main.getChildren().contains(map)){
              main.getChildren().add(map);
              StackPane.setAlignment(map,Pos.BOTTOM_LEFT);
            }
            else{
              main.getChildren().remove(map);
            }
          });
          plan.setDisable(true);
          pause = new Button("Pause");
          pause.setOnMouseClicked(e->{
            if(!main.getChildren().contains(display)){
              timePane.pause();
              display.getChildren().clear();
              Label pau = new Label("PAUSE");
              pau.setStyle("-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.75) , 4,0,0,1 );-fx-text-fill: lightgrey;-fx-font-size: 150px;-fx-font-weight:bold;");
              display.getChildren().add(pau);
              display.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 10;");
              main.getChildren().add(display);
              tool.toFront();
            }
            else{
              timePane.play();
              main.getChildren().remove(display);
            }

          });
          save = new Button("Save");
          save.setOnMouseClicked(e->{
              LocalDate now = LocalDate.now();
              String date[] = now.toString().split("-");
              System.out.println(now);
              try{game.save(date[2]+"/"+date[1]+"/"+date[0].charAt(2)+""+date[0].charAt(3)+"/"+game.player().getName());}
              catch (Exception exc) {}
          });
          inv = new Button("Pocket");
          inv.setOnMousePressed(e->{
            if(!main.getChildren().contains(display)){
              timePane.pause();
              main.getChildren().add(display);
              control.updateInventory();
              tool.toFront();
            }
            else{
              timePane.play();
              main.getChildren().remove(display);
            }
          });
          help = new Button("Help");
          help.setOnMousePressed(e->{
            timePane.pause();
            boolean aide = main.getChildren().contains(display);
            if(!aide)main.getChildren().add(display);
            control.displayHelp();
          });
          help.setOnMouseReleased(e->{
            timePane.play();
            main.getChildren().remove(display);
          });
          Region rg = new Region();
          HBox.setHgrow(rg,Priority.SOMETIMES);
          rg.setFocusTraversable(false);
          tool = new ToolBar(rg,help,inv,save,pause,plan,quit);
          for (Node a : tool.getItems()) {
            if(a instanceof Button){
                a.setStyle(style);
                a.setFocusTraversable(false);
            }
          }
          tool.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
        }
        public boolean test(int a,int b,int c,int d){
          return ((a==b)&&(b==c||b==d));
        }
        public void initMaze() throws IOException{
            Group world=new Group();
            Group floor = new Group();
            Group first=floor;
            Point before = null;
            Box square = null;
<<<<<<< HEAD
            Box firstS;
            Vector3D squareCoor=new Vector3D();
            COLOR_WALL.setBumpMap(new Image("brick.jpg"));
            COLOR_WALL.setDiffuseColor(Color.LIGHTGOLDENRODYELLOW);
=======
            COLOR_WALL.setBumpMap(new Image("images/brick.jpg"));
>>>>>>> c6af39555428b59a3f26fddcdf0a80a6937bceaa
            COLOR_WALL.setSpecularColor(Color.BLACK);
            COLOR_DOOR.setDiffuseMap(new Image("/safe.jpg"));
            COLOR_DOOR.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
            COLOR_ENTRY.setSpecularColor(Color.BLACK);
            COLOR_END.setSpecularColor(Color.WHITE);

            int i=0;
            for (Maze a : floors) {
<<<<<<< HEAD
                createMaze(floor,a,i);
                firstS=square;
                if(before!=null){
=======
                createMaze(floor, a, i);
                if (before != null) {
>>>>>>> c6af39555428b59a3f26fddcdf0a80a6937bceaa
                    int aposx = (int) a.beginning().getX();
                    int aposz = (int) a.beginning().getY();
                    floor.setTranslateX((posFx-aposx)*SIZE_BOX);
                    floor.setTranslateZ((posFz-aposz)*SIZE_BOX);
                    posFx=(int) a.ending().getX()+(posFx-aposx);
                    posFz=(int) a.ending().getY()+(posFz-aposz);
                    if(!test(aposx,(int)before.getX(),MAZE_WIDTH-1,0) && !test(aposz,(int)before.getY(),MAZE_LENGTH-1,0)){
                        System.out.println("normal");
                        setUp(aposx,aposz,MAZE_LENGTH,MAZE_WIDTH,floor,1);
                        setUp((int)before.getX(),(int)before.getY(),MAZE_LENGTH,MAZE_WIDTH,floor,-1);
                        square=new Box(SIZE_BOX,0,SIZE_BOX);
                        square.setTranslateX(aposx*SIZE_BOX);
                        square.setTranslateZ(aposz*SIZE_BOX);
                        if(aposx==0)square.setTranslateX(square.getTranslateX()-SIZE_BOX);
                        else if(aposx==MAZE_LENGTH-1) square.setTranslateX(square.getTranslateX()+SIZE_BOX);
                        else if(aposz==0) square.setTranslateZ(square.getTranslateZ()-SIZE_BOX);
                        else if(aposz==MAZE_WIDTH-1) square.setTranslateZ(square.getTranslateZ()+SIZE_BOX);
                    }
                     else if(test(aposx,(int)before.getX(),MAZE_WIDTH-1,0)){
                        System.out.println("same x");
                          if(aposx==0)System.out.println("0");
                          else System.out.println("taille");
                          floor.setTranslateZ(floor.getTranslateZ()-SIZE_BOX);
                          square = new Box(SIZE_BOX,0,2*SIZE_BOX);
                          square.setTranslateX((aposx-1)*SIZE_BOX);
                          square.setTranslateZ(aposz*SIZE_BOX+SIZE_BOX/2);
                          posFz--;
                    }
                    else {
                        System.out.println("same z");
                        if(aposz==0)System.out.println("0");
                        else System.out.println("taille");
                        floor.setTranslateX(floor.getTranslateX()-SIZE_BOX);
                        square = new Box(2*SIZE_BOX,0,SIZE_BOX);
                        square.setTranslateX(aposx*SIZE_BOX+SIZE_BOX/2);
                        square.setTranslateZ((aposz-1)*SIZE_BOX);
                        posFx--;
                    }
                    square.setMaterial(new PhongMaterial(Color.MAROON));
                    square.setTranslateY((-i+1)*SIZE_BOX+SIZE_BOX/2);
                    floor.getChildren().add(square);
                }
                else {
                    posFx = (int) a.ending().getX();
                    posFz = (int) a.ending().getY();
                    System.out.println("second");
                }
                before=a.ending();
                print(first);
                print(floor);
                print(firstS);
                print(square);
                coordSwitch[i/2]=(new Vector3D(floor.getTranslateX(),floor.getTranslateY(),floor.getTranslateZ()).subtract(new Vector3D(first.getTranslateX(),first.getTranslateY(),first.getTranslateZ()))).multiply(1.0/400);
              //  if(firstS!=null)squareCoor.add(new Vector3D(square.getTranslateX(),square.getTranslateY(),square.getTranslateZ()).subtract(new Vector3D(firstS.getTranslateX(),firstS.getTranslateY(),firstS.getTranslateZ())).multiply(1/400));
                //if(i>0) coordSwitch[i-1]=squareCoor;
               // if(i<coordSwitch.length-1) coordSwitch[i+1]=coordSwitch[i];
                world.getChildren().add(floor);
                floorGroups[i/2]=floor;
                floor = new Group();
                i+=2;

            }
           /* Sphere sphere=new Sphere();
            sphere.setTranslateX(world.getTranslateX());
            sphere.setTranslateY(world.getTranslateY());
            sphere.setTranslateZ(world.getTranslateY());
            sphere.setRadius(coordSwitch[coordSwitch.length-1].norm()*SIZE_BOX);
            sphere.setMaterial(new PhongMaterial(Color.DARKBLUE));*/
            this.getChildren().add (world);
            buildCamera(this);
            for(int a=0;a<coordSwitch.length;a++) System.out.println(coordSwitch[a]);
            System.out.println("end");
        }

        public void print(Node a){
            if(a==null){
                System.out.println(a);
                return;
            }
            System.out.println("X="+a.getTranslateX()+"  Y="+a.getTranslateY()+"  Z="+a.getTranslateZ());
        }

        public void createMaze(Group root,Maze maze,int floor) throws IOException {
            int CASE;
            Box cell;
            for (int i = 0;i<maze.getHeight() ;i++ ){
                for (int j = 0; j <maze.getWidth() ;j++ ) {
                    CASE = maze.getCase(i,j);
                    switch(CASE){
                        case Maze.START:
                            cell=makeFloor(COLOR_ENTRY);
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.END:
                            cell = makeFloor(COLOR_END);
<<<<<<< HEAD
                            setBox(cell,i,j,root,floor,maze);
=======
                            setBox(cell, i, j, root, floor, maze);
>>>>>>> c6af39555428b59a3f26fddcdf0a80a6937bceaa
                            break;

                        case Maze.WAY:
                            cell = makeFloor(COLOR_WAY);
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.DOOR:
                            cell = new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
                            cell.setMaterial(COLOR_DOOR);
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.KEY:
                            drawKey(root,j,i,floor);
                            cell = makeFloor(COLOR_WAY);
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.TELEPORT:
                            cell = makeFloor(COLOR_WAY);
                            drawTeleport(root,i,j,maze,floor);
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.MONSTRE:
                            cell = makeFloor(COLOR_WAY);
                            drawMonster(root,i,j,floor,maze);
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.BONUS:
                            cell = makeFloor(COLOR_WAY);
                            drawBonus(root,i,j,maze,floor);
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.OBSTACLE:
                            cell = makeFloor(COLOR_STAIRS);
                            drawObstacle(root,i,j,floor,maze);
                            //faire drawObstacle, attention à la forme de l'obstacle
                            setBox(cell,i,j,root,floor,maze);
                            break;

                        case Maze.STAIRSUP:
                            drawStair(0,root,i,j,maze,floor);
                            break;

                        case Maze.STAIRSDOWN:
                            drawStair(1,root,i,j,maze,floor);
                            break;

                        case Maze.WALL:
                            cell=new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
                            cell.setMaterial(COLOR_WALL);
                            setBox(cell,i,j,root,floor,maze);
                            break;
                    }
                }
            }
        }

        public Box makeFloor(PhongMaterial color){
            Box cell = new Box(SIZE_BOX,0,SIZE_BOX);
            cell.setMaterial(color);
            cell.setTranslateY(SIZE_BOX/2);
            return cell;
        }

        public void setBox(Box cell,int i, int j, Group root,int floor,Maze maze){
            cell.setTranslateX(j*SIZE_BOX);
            cell.setTranslateZ(i*SIZE_BOX);
            int place = maze.getCase(i,j);
            if(place==Maze.WALL || place== Maze.DOOR || place==Maze.STAIRSUP || place==Maze.STAIRSDOWN)
                cell.setTranslateY((-floor)*SIZE_BOX);
            else
                cell.setTranslateY((-floor)*SIZE_BOX+SIZE_BOX/2);
                setRoof(i,j,root,floor);
            root.getChildren().add(cell);
        }
        public void setRoof(int i, int j, Group root,int floor){
            Box roof = new Box(SIZE_BOX,0,SIZE_BOX);
            roof.setTranslateX(j*SIZE_BOX);
            roof.setTranslateZ(i*SIZE_BOX);
            roof.setTranslateY(-floor*SIZE_BOX-SIZE_BOX/2);
            roof.setMaterial(new PhongMaterial(Color.BLACK));
            root.getChildren().add(roof);
        }
        public void setUp(int posx, int posz,int height,int width,Group floor, int a){
            if(posx==0){
                floor.setTranslateX(floor.getTranslateX()+SIZE_BOX*a);
                posFx +=a;
            }
            else if(posx==height-1){
                floor.setTranslateX(floor.getTranslateX()-SIZE_BOX*a);
                posFx -=a;
            }
            else if(posz==0){
                floor.setTranslateZ(floor.getTranslateZ()+SIZE_BOX*a);
                posFz +=a;
            }
            else if(posz==width-1){
                floor.setTranslateZ(floor.getTranslateZ()-SIZE_BOX*a);
                posFz -=a;
            }
        }
        //Fonctions pour dessiner les différents objets du labyrinthe

        public void drawStair(int dir,Group root,int i,int j,Maze maze,int floor){
            Box step;
            int nbStep=8;
            Group stairs = new Group();
            int size=SIZE_BOX/nbStep,a=0;
            while(nbStep!=a){
                step=new Box(SIZE_BOX,size*(a+1),size);
                step.setMaterial(COLOR_STAIRS);
                step.setTranslateX(j*SIZE_BOX);
                step.setTranslateZ(i*SIZE_BOX+size*a);//+SIZE_BOX/2);//-size);
                step.setTranslateY(SIZE_BOX/2+(size/2)*(-1)*(a+1));
                a++;
                stairs.getChildren().add(step);
            }
            stairs.setRotationAxis(Rotate.Y_AXIS);
            stairs.setTranslateZ(stairs.getTranslateZ()-SIZE_BOX/2+size/2);
            stairs.setTranslateY(stairs.getTranslateY()-floor*SIZE_BOX);
            if(dir==1)stairs.setTranslateY(stairs.getTranslateY()+SIZE_BOX);
            if(j==0){
                //System.out.println("pat");
                stairs.setRotate(-90);
            }
            else if(j==maze.getWidth()-1){
                //  System.out.println("sac");
                stairs.setRotate(90);
            }
            else if(i==maze.getHeight()-1){
                //System.out.println("sec");
                stairs.setRotate(0);
            }
            else if(i==0){
                //System.out.println("zed");
                stairs.setRotate(180);
            }
            if(dir==1)stairs.setRotate(stairs.getRotate()+180);
            root.getChildren().add(stairs);
        }

        public void drawKey(Group root,int posx,int posy,int floor) throws IOException{
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(this.getClass().getResource("key.fxml"));
            MeshView key = fxmlLoader.<MeshView>load();
            key.setRotationAxis(Rotate.Z_AXIS);
            key.setRotate(180.0);
            key.setTranslateX(posx*SIZE_BOX);
            key.setTranslateZ(posy*SIZE_BOX);
            key.setTranslateY(-floor*SIZE_BOX);
            PhongMaterial mat = new PhongMaterial();
            mat.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
            mat.setDiffuseColor(Color.YELLOW);
            key.setMaterial(mat);
            RotateTransition rt = new RotateTransition(Duration.millis(1000));
            rt.setByAngle(360.0);
            rt.setAxis(Rotate.X_AXIS);
            rt.setCycleCount(TranslateTransition.INDEFINITE);
            rt.setAutoReverse(false);
            rt.setInterpolator(Interpolator.LINEAR);
            rt.setNode(key);
            rt.play();
            root.getChildren().add(key);
            keyOrBonus.add(key);
        }

        public void drawTeleport(Group root, int i, int j, Maze maze, int floor) throws IOException{
            Teleporteur last = maze.getTeleport().getLast();
            MeshView teleport = last.initTeleport();
            teleport.setTranslateX(j * SIZE_BOX);
            teleport.setTranslateZ(i * SIZE_BOX);
            teleport.setTranslateY((-floor) * SIZE_BOX - SIZE_BOX/2);
            teleport.setScaleX(teleport.getScaleX()* SIZE_BOX/4);
            teleport.setScaleZ(teleport.getScaleZ()* SIZE_BOX/4);
            teleport.setScaleY(teleport.getScaleY()* SIZE_BOX/4);
            RotateTransition rt = new RotateTransition(Duration.millis(3000));
            rt.setByAngle(360.0);
            rt.setAxis(Rotate.Y_AXIS);
            rt.setCycleCount(TranslateTransition.INDEFINITE);
            rt.setAutoReverse(false);
            rt.setInterpolator(Interpolator.LINEAR);
            rt.setNode(teleport);
            rt.play();
            root.getChildren().add(teleport);
        }

        public void drawMonster(Group root,int i, int j,int floor,Maze maze) throws IOException{
            Monstres last = maze.getMonstres().getLast();
            MeshView ghost = last.initMonster();
            //ghost.setTranslateX(j*SIZE_BOX);
            //ghost.setTranslateZ(i*SIZE_BOX);
            ghost.setTranslateY((-floor)*SIZE_BOX-SIZE_BOX/2);
            root.getChildren().add(ghost);
        }

        public void drawObstacle(Group root,int i, int j,int floor,Maze maze) throws IOException{
            FXMLLoader fxmlLoader = new FXMLLoader();
            float scale = 0,scaleY,scaleZ,posy;
            if(maze.getTypeObstacle().equals("Cercle")) {
                fxmlLoader.setLocation(this.getClass().getResource("Spider.fxml"));
                scale = scaleY=scaleZ =1.5f;
                posy = SIZE_BOX/10;
            }
            else {
                fxmlLoader.setLocation(this.getClass().getResource("gate.fxml"));
                scale = 2.7f;
                scaleY=5;
                scaleZ=3;
                posy = SIZE_BOX / 20;
            }
            Group obs = fxmlLoader.load();
            PhongMaterial mat = new PhongMaterial();
            mat.setSpecularColor(Color.BLACK);
            mat.setDiffuseColor(Color.DARKGREY);
            for ( Node n : obs.getChildren() ) {
                if(n instanceof Shape3D) {
                    ((Shape3D)n).setTranslateX(j*SIZE_BOX);
                    ((Shape3D)n).setTranslateZ(i*SIZE_BOX);
                    ((Shape3D)n).setMaterial(mat);
                    ((Shape3D)n).setScaleX(((Shape3D)n).getScaleX()*scale);
                    ((Shape3D)n).setScaleY(((Shape3D)n).getScaleY()*scaleY);
                    ((Shape3D)n).setScaleZ(((Shape3D)n).getScaleZ()*scaleZ);
                    ((Shape3D)n).setTranslateY(SIZE_BOX/2-posy-(floor*SIZE_BOX));
                }
            }
            root.getChildren().add(obs);
        }

        public void drawBonus(Group root, int i, int j, Maze maze,int floor) throws IOException {
            Bonus last = maze.getBonus().getLast();
            MeshView bonus = last.initBonus();
            RotateTransition rt = new RotateTransition(Duration.millis(3000));
            rt.setByAngle(360.0);
            rt.setAxis(Rotate.Y_AXIS);
            rt.setCycleCount(TranslateTransition.INDEFINITE);
            rt.setAutoReverse(false);
            rt.setInterpolator(Interpolator.LINEAR);
            bonus.setTranslateX(j * SIZE_BOX);
            bonus.setTranslateZ(i * SIZE_BOX);
            bonus.setTranslateY((-floor) * SIZE_BOX + SIZE_BOX/8);
            bonus.setScaleX(bonus.getScaleX()* SIZE_BOX/10);
            bonus.setScaleZ(bonus.getScaleZ()* SIZE_BOX/10);
            bonus.setScaleY(bonus.getScaleY()* SIZE_BOX/10);
            rt.setNode(bonus);
            rt.play();
            root.getChildren().add(bonus);
            keyOrBonus.add(bonus);
        }


        public void remove(Group root,int i, int j){
            int posx = i*SIZE_BOX;
            int posz = j*SIZE_BOX;
            Node removable;
            for ( Node a : keyOrBonus ) {
                if(a.getTranslateX()==posx && a.getTranslateZ()==posz){
                    System.out.println(a.getTranslateX()+"   "+a.getTranslateZ());
                    removable=a;
                    System.out.println(root.getChildren().contains(a));
                    root.getChildren().remove(removable);
                    System.out.println(root.getChildren().contains(a));
                    break;
                }
            }
        }

        public void printMaze(){
            Maze m=game.floors().get(0);
            for(int i=0;i<m.getHeight();i++){
                for(int j=0;j<m.getWidth();j++) System.out.print(m.getCase(i,j)+" ");
                System.out.println();
            }
        }

        public void buildCamera(Group root) {
            //Ici vérifier dans quel sens la début est pour modifier x et z
            //System.out.println("Height " + maze.getHeight() + "   Width  " + maze.getWidth());
            camera.setFarClip(10000);
            camera.setNearClip(0.1);
            //cameraLight.setTranslateY(0);
            Point2D position = game.player().getPosition();
            double yPos=game.player.getY();
            int floor=game.floor();
            System.out.println(yPos);
            System.out.println(position);
            x = new SimpleDoubleProperty((position.getX()+coordSwitch[floor].x()) * SIZE_BOX-SIZE_BOX/2);
            y=new SimpleDoubleProperty(-yPos*SIZE_BOX);
            z = new SimpleDoubleProperty((position.getY() +coordSwitch[floor].z())* SIZE_BOX-SIZE_BOX/2);
            angle = new SimpleDoubleProperty(90 - game.player().orientation());
            camera.translateXProperty().bind(x);
            camera.translateYProperty().bind(y);
            camera.translateZProperty().bind(z);
            camera.rotateProperty().bind(angle);
            camera.setRotationAxis(Rotate.Y_AXIS);
            rotateX=new Rotate();
            rotateX.setAxis(Rotate.X_AXIS);
            camera.getTransforms().add(rotateX);
            //camera.setFieldOfView(100);
            printMaze();
        }

        public void reset() {

        }
        public void moveMap(int size,Circle player){
          player.centerXProperty().set(game.player().getPosition().getX()*size);
          player.centerYProperty().set(game.player().getPosition().getY()*size);
        }
        public void setUpMap(int size){
          map = new Group();
          Canvas mapDraw = new Canvas(600,800);
          GraphicsContext gc = mapDraw.getGraphicsContext2D();
          int x=0,y=0;
          Maze maze = game.current();
          for (int i = 0 ; i< maze.getHeight();i++ ) {
            for(int j = 0; j<maze.getWidth();j++){
              switch (maze.getCase(i,j)) {
                case Maze.WAY: gc.setFill(Color.TRANSPARENT);break;
                case Maze.START: gc.setFill(Color.BLUE);break;
                case Maze.END: gc.setFill(Color.RED);break;
                case Maze.WALL: gc.setFill(Color.LIGHTGREY);break;
                case Maze.STAIRSUP: gc.setFill(Color.BROWN);break;
                case Maze.STAIRSDOWN: gc.setFill(Color.BROWN);break;
                case Maze.OBSTACLE: gc.setFill(Color.GREY);break;
                case Maze.MONSTRE: gc.setFill(Color.WHITE);break;
                case Maze.TELEPORT: gc.setFill(Color.PURPLE);break;
                case Maze.DOOR: gc.setFill(Color.LIGHTBLUE);break;
                case Maze.KEY: gc.setFill(Color.YELLOW);break;
                case Maze.BONUS :gc.setFill(Color.GREEN);break;
              }
              gc.fillRect(x,y,size,size);
              x+=size;
            }
            y+=size;x=0;
          }
          map.getChildren().add(mapDraw);
        }

       /* public void printMaze() {
            for (int i = 0; i < MAZE_LENGTH; i++) {
                for (int j = 0; j < MAZE_WIDTH; j++)
                    System.out.print(maze.getCase(i, j) + "  ");
                System.out.println();
            }
        }*/
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
        protected double mouseXOld;
        protected double mouseYOld;
        protected LongProperty lastUpdate;
        protected AnimationTimer gameTimer;
        public GameControl() throws IOException{
            lastUpdate=new SimpleLongProperty();
            gameTimer=new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (game.gameOver()) {
                        whenIsFinished();
                    } else if (game.player.state() == Player.PlayerState.DEAD) {
                        timePane.stop();
                        final ImageView imv=new ImageView();
                        final Image img=new Image(View.class.getResourceAsStream("glass.png"));
                        imv.setImage(img);
                        main.getChildren().add(imv);
                        View.this.setOnKeyPressed(null);
                        gameTimer.stop();
                        //mettre screen ecran cassé et you died of hnger ou you fell
                    } else if (timePane.timeOver()) {
                        //timePane.setVisible(false);
                        //Print you loose dumbass
                    } else {
                        if (lastUpdate.get() > 0) {
                            double elapsedTime = (now - lastUpdate.get()) / 1000000000.0;
                            game.update(elapsedTime);
                            Point2D pos = game.player().getPosition();
                            double yPos = game.player().getY();
                            int floor = mazePane.floors.indexOf(game.current());
                            //System.out.println(pos);
                            mazePane.x.set((pos.getX() + mazePane.coordSwitch[floor].x()) * mazePane.SIZE_BOX - mazePane.SIZE_BOX / 2);
                            mazePane.z.set((pos.getY() + mazePane.coordSwitch[floor].z()) * mazePane.SIZE_BOX - mazePane.SIZE_BOX / 2);
                            mazePane.y.set(-yPos * mazePane.SIZE_BOX);
                            mazePane.angle.set(90 - game.player().orientation());
                            mazePane.rotateX.setAngle(game.player().orientationX());
                            if(game.player.hasPickedUp()){
                                System.out.println((int)pos.getX()+"  "+(int)pos.getY());
                                mazePane.remove(mazePane.floorGroups[floor],(int)pos.getX(),(int)pos.getY());
                                game.player.pick(false);
                            }
                            else if(game.player.isUnderTeleport()){
                                //if(MazeInterface.confirm("Do you wish to teleport ?")) game.teleport((int)pos.getX(),(int)pos.getY());
                            }
                        }
                        lastUpdate.set(now);

                    }
                }
            };
            handleAction();
        }

        public void displayScore(Pane root) {
            root.getChildren().clear();
            String display = game.scores().getScores();
            String[] splits = display.split("\n");
            for (String s : splits) root.getChildren().add(new Label(s));
        }

        public void displayScores(Scores s){
            ScorePane sp=new ScorePane(s);
            main.getChildren().clear();
            main.getChildren().add(sp);
            sp.printScores();
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

        public void handleAction() throws IOException{
            game.start();
            mazePane.initMaze();
            timePane.start();
            gameTimer.start();
            mazePane.setUpMap(10);
            Circle player = new Circle(4.0,Color.WHITESMOKE);
            mazePane.moveMap(10,player);
            map.getChildren().add(player);
            View.this.setOnKeyPressed(e -> {
                boolean pocket = main.getChildren().contains(display);
                    if(e.getCode()==KeyCode.M){
                        if(!pocket)main.getChildren().add(display);
                        updateInventory();
                        timePane.pause();
                        if(e.isControlDown()){
                            main.getChildren().remove(display);
                            timePane.play();
                        }
                    }
                    else if(!pocket) {
                        if (game.player.state() != Player.PlayerState.JUMPING) {
                            switch (e.getCode()) {
                                case UP:
                                    game.player.up(true);
                                    break;
                                case RIGHT:
                                    if (game.player.state() != Player.PlayerState.STAIRSDOWN && game.player.state() != Player.PlayerState.STAIRSUP)
                                        game.player.right(true);
                                    break;
                                case DOWN:
                                    game.player.down(true);
                                    break;
                                case LEFT:
                                    if (game.player.state() != Player.PlayerState.STAIRSDOWN && game.player.state() != Player.PlayerState.STAIRSUP)
                                        game.player.left(true);
                                    break;
                                case SPACE:
                                    if (game.player.state() != Player.PlayerState.STAIRSDOWN && game.player.state() != Player.PlayerState.STAIRSUP)
                                    game.player.jump(true);
<<<<<<< HEAD
                                    break;
                                case SHIFT:
                                    if (e.isControlDown()) mazePane.rotateX.setAngle(mazePane.rotateX.getAngle() - 0.5);
                                    else mazePane.rotateX.setAngle(mazePane.rotateX.getAngle() + 0.5);
                                    break;
                                case Q:
                                      if(e.isControlDown())st.close();break;
                            }
=======
                                break;
                            case SHIFT:
                                if (e.isControlDown()) game.player.lookDown(true);
                                else game.player.lookUp(true);
                                break;
                            case Q:
                                if (e.isControlDown()) st.close();
                                break;
                            case M:
                                if(!plan.isDisable()){
                                    if(!main.getChildren().contains(map))main.getChildren().add(map);
                                    else main.getChildren().remove(map);
                                }
                                break;
>>>>>>> c6af39555428b59a3f26fddcdf0a80a6937bceaa
                        }
                        mazePane.moveMap(10,player);
                    }
            });
            View.this.setOnKeyReleased(e->{
                switch(e.getCode()){
                    case UP:
                        game.player.up(false);
                        break;
                    case RIGHT:
                        game.player.right(false);
                        break;
                    case DOWN:
                        game.player.down(false);
                        break;
                    case LEFT:
                        game.player.left(false);
                        break;
                    case SPACE:
                        game.player.jump(false);
                        break;
                    case SHIFT:
                        game.player.lookDown(false);
                        game.player.lookUp(false);
                        break;
                    case CONTROL:game.player.lookDown(false);
                    break;
                }
            });
            View.this.addEventHandler(MouseEvent.ANY, evt->{
                if(evt.getEventType()==MouseEvent.MOUSE_DRAGGED || evt.getEventType()==MouseEvent.MOUSE_PRESSED){
                    double mouseXNew=evt.getSceneX();
                    double mouseYNew=evt.getSceneY();
                    if(evt.getEventType()==MouseEvent.MOUSE_DRAGGED){
                        //  double pitchRotate=mazePane.camera.getRotate()+()
                    }
                }
                if(evt.getEventType()==MouseEvent.MOUSE_CLICKED){

                }

            });

        }


        public abstract void whenIsFinished();

        public void displayHelp(){
          display.getChildren().clear();
          String style="-fx-background-color:lightslategrey;-fx-text-fill: white;-fx-font:oblique 15pt cursive;-fx-text-alignment: center;-fx-font-weight:bold;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.75),4,0,0,1);";
          String key = "To open, you need a gold key which is somewhere";
          Label cle = new Label(key);
          configLabel(cle,"key.png",style);
          String time = "Hourglasses are used to save time";
          Label hour = new Label(time);
          configLabel(hour,"hourglass.png",style);
          String coin = "With 5 coins you can buy a map of the maze, with less you save time on your score";
          Label piece = new Label(coin);
          configLabel(piece,"coin.png",style);
          String inv = "You can see what you have with the button pocket or press M";
          Label pock = new Label(inv);
          pock.setStyle(style);
          String keys = "Use arrow keys to move";
          Label touch = new Label(keys);
          touch.setStyle(style);
          String sortie = "The end is red whereas the beginning is blue";
          Label end = new Label(sortie);
          end.setStyle(style);
          display.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 10;");
          display.setSpacing(30.0);
          display.getChildren().addAll(cle,hour,piece,pock,touch,end);
        }
        public void updateInventory(){
            display.getChildren().clear();
            display.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-background-radius: 10;");
            String style="-fx-background-color:lightslategrey;-fx-text-fill: white;-fx-font:oblique 15pt cursive;-fx-text-alignment: center;-fx-font-weight:bold;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.75),4,0,0,1);";
            String styleB= "-fx-background-color:#090a0c,linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),linear-gradient(#20262b, #191d22),radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));-fx-text-fill:white;";
            int time=0,coin=0;
            int key = 7;//game.player().keys().size();
            int bonus = 5;//game.player().getBonus().size();
            if(bonus+key !=0){
                if(bonus!=0){
                    for (Bonus a : game.player().getBonus()) {
                        if(a instanceof TimeBonus)time++;
                        else coin++;
                    }
                    coin=5;time=3;
                    if(coin!=0){
                        Label piece = new Label("Vous avez "+""+coin+" pièce.s");
                        configLabel(piece,"coin.png",style);
                        Button usePiece = new Button("Buy the map");
                        if(coin<5)usePiece.setDisable(true);
                        usePiece.setOnMouseClicked(e->{
                          plan.setDisable(false);
                        });
                        usePiece.setStyle(styleB);
                        HBox panePiece = new HBox(piece,usePiece);
                        panePiece.setAlignment(Pos.CENTER);
                        display.getChildren().add(panePiece);
                    }
                    if(time!=0){
                        Label hourglass = new Label("Vous avez "+""+time+" sablier.s");
                        configLabel(hourglass,"hourglass.png",style);
                        Button useTime = new Button("Use");
                        useTime.setOnMouseClicked(e->{
                          updateInventory();
                        });
                        useTime.setStyle(styleB);
                        HBox paneHour = new HBox(hourglass,useTime);
                        paneHour.setAlignment(Pos.CENTER);
                        display.getChildren().add(paneHour);
                    }
                }
                if(key!=0){
                  Label keys = new Label("Vous avez "+""+key+" clé.s");
                  configLabel(keys,"key.png",style);
                  Button useKey = new Button("Use");
                  useKey.setStyle(styleB);
                  useKey.setOnMouseClicked(e->{
                    updateInventory();
                  });
                  HBox paneKey= new HBox(keys,useKey);
                  paneKey.setAlignment(Pos.CENTER);
                  display.getChildren().add(paneKey);
                }
            }
            else{
                Label nothing = new Label("Vous n'avez aucun objet");
                nothing.setStyle(style);
                display.getChildren().add(nothing);
            }
        }

        public void configLabel(Label txt,String path,String style){
            Image img = new Image(getClass().getResourceAsStream(path));
            txt.setGraphic(new ImageView(img));
            txt.setContentDisplay(ContentDisplay.RIGHT);
            txt.setStyle(style);
        }


   /* public void setScene(Scene s){
        scene=s;
    }*/


        //Put a countdown shade, like a transparent one and then start the timer

        //Somewhere here, handle keyboard events
        //Put a depthical panel at the right, to contain the timer and other stuff

        // public abstract void action();


    }

      /* public void initMaze() {
            Group rotate = new Group();
            ObservableList<Node> childs = rotate.getChildren();
            Box cell;
            Box test = null;
            Box roof = new Box(MAZE_WIDTH * SIZE_BOX, 0, MAZE_LENGTH * SIZE_BOX);
            roof.setMaterial(COLOR_WAY);
            roof.setTranslateY(-SIZE_BOX / 2);
            roof.setTranslateX(SIZE_BOX*(MAZE_WIDTH-1)/2);
            roof.setTranslateZ(SIZE_BOX*(MAZE_LENGTH-1)/2);
          //COLOR_WALL.setDiffuseMap(new Image("brick.jpg"));
           COLOR_WALL.setBumpMap(new Image("brick.jpg"));
           COLOR_WALL.setDiffuseColor(Color.LIGHTGOLDENRODYELLOW);
         COLOR_WALL.setSpecularColor(Color.BLACK);


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
            //rotate.getChildren().add(light);
             //rotate.getChildren().add(roof);
            this.getChildren().add(rotate);
            buildCamera(rotate);
          //light.getScope().add(rotate);
            printMaze();
           if (test != null) System.out.println("Test " + test.getTranslateX() + "   " + test.getTranslateZ());

        }*/




}

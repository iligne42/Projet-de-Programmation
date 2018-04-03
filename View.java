import javafx.animation.*;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.*;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Rectangle2D;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.shape.MeshView;
import javafx.scene.shape.Shape3D;
import javafx.scene.shape.Sphere;
import javafx.scene.transform.Rotate;
import javafx.stage.Screen;
import javafx.util.Duration;
import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.LinkedList;

public class View extends Scene {
    protected StackPane main;
    //protected ToolBar tool;
    protected VBox inventory;
    protected GameVersion game;
    protected TimePane timePane;
    protected MazePane mazePane;
    protected GameControl control;
    protected SubScene mazeScene;
    protected Label timeLabel;

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
        main.getChildren().add(mazeScene);

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
        protected ObservableList<Node> keyOrBonus;
        protected Vector3D[] coordSwitch=new Vector3D[2*floors.size()-1];

        public MazePane(PerspectiveCamera cam) {
            camera = cam;
           // cameraLight=new PointLight(Color.DARKORANGE);
            inventory= new VBox();
            inventory.setStyle("-fx-background-color: rgba(0, 0, 0, 0); -fx-background-radius: 10;");
            inventory.setAlignment(Pos.CENTER);
            inventory.setSpacing(20.0);
        }



        public void initMaze() throws IOException{
            Group world=new Group();
            Group floor = new Group();
            Group first=floor;
            Point before = null;
            Box square = null;
            Box firstS;
            Vector3D squareCoor=new Vector3D();
            COLOR_WALL.setBumpMap(new Image("brick.jpg"));
            COLOR_WALL.setDiffuseColor(Color.LIGHTGOLDENRODYELLOW);
            COLOR_WALL.setSpecularColor(Color.BLACK);
            COLOR_DOOR.setDiffuseMap(new Image("/safe.jpg"));
            COLOR_DOOR.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
            COLOR_ENTRY.setSpecularColor(Color.BLACK);
            COLOR_END.setSpecularColor(Color.WHITE);

            int i=0;
            for (Maze a : floors) {
                createMaze(floor,a,i);
                firstS=square;
                if(before!=null){
                    int aposx = (int) a.beginning().getX();
                    int aposz = (int) a.beginning().getY();
                    floor.setTranslateX((posFx-aposx)*SIZE_BOX);
                    floor.setTranslateZ((posFz-aposz)*SIZE_BOX);
                    posFx=(int) a.ending().getX()+(posFx-aposx);
                    posFz=(int) a.ending().getY()+(posFz-aposz);
                    if(before.getX()!=aposx && before.getY()!=aposz){
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
                     else if(before.getX()==aposx){
                        System.out.println("same x");
                        int coeff=(aposx==0)?1:-1;
                        System.out.println(coeff);
                        floor.setTranslateZ(floor.getTranslateZ()+SIZE_BOX*coeff);
                        square = new Box(SIZE_BOX,0,2*SIZE_BOX);
                        square.setTranslateX((aposx-coeff)*SIZE_BOX);
                        square.setTranslateZ(aposz*SIZE_BOX-(coeff*SIZE_BOX/2));
                        posFz+=coeff;
                    }
                    else {
                        System.out.println(aposz+" / "+before.getY());
                        int coeff=(aposz==0)?1:-1;
                        floor.setTranslateX(floor.getTranslateX()+SIZE_BOX*coeff);
                        square = new Box(2*SIZE_BOX,0,SIZE_BOX);
                        square.setTranslateX(aposx*SIZE_BOX-(coeff*SIZE_BOX/2));
                        square.setTranslateZ((aposz-coeff)*SIZE_BOX);
                        posFx+=coeff;
                    }
                    square.setMaterial(new PhongMaterial(Color.MAROON));
                    square.setTranslateY((-i+1)*SIZE_BOX+SIZE_BOX/2);
                    floor.getChildren().add(square);
                }
                else {
                    posFx = (int) a.ending().getX();
                    posFz = (int) a.ending().getY();
                }
                before=a.ending();
                print(first);
                print(floor);
                print(firstS);
                print(square);
                coordSwitch[i]=(new Vector3D(floor.getTranslateX(),floor.getTranslateY(),floor.getTranslateZ()).subtract(new Vector3D(first.getTranslateX(),first.getTranslateY(),first.getTranslateZ()))).multiply(1.0/400);
              //  if(firstS!=null)squareCoor.add(new Vector3D(square.getTranslateX(),square.getTranslateY(),square.getTranslateZ()).subtract(new Vector3D(firstS.getTranslateX(),firstS.getTranslateY(),firstS.getTranslateZ())).multiply(1/400));
                //if(i>0) coordSwitch[i-1]=squareCoor;
                if(i<coordSwitch.length-1) coordSwitch[i+1]=coordSwitch[i];
                world.getChildren().add(floor);
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
                            setBox(cell,i,j,root,floor,maze);
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
                            cell = makeFloor(COLOR_WAY);
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
        }

        public void drawTeleport(Group root, int i, int j, Maze maze, int floor) throws IOException{
            Teleporteur last = maze.getTeleport().getLast();
            MeshView teleport = last.initTeleport();
            teleport.setTranslateX(j * SIZE_BOX);
            teleport.setTranslateZ(i * SIZE_BOX);
            teleport.setTranslateY((-floor) * SIZE_BOX + SIZE_BOX);
            root.getChildren().add(teleport);
        }

        public void drawMonster(Group root,int i, int j,int floor,Maze maze) throws IOException{
            Monstres last = maze.getMonstres().getLast();
            MeshView ghost = last.initMonster();
            ghost.setTranslateX(j*SIZE_BOX);
            ghost.setTranslateZ(i*SIZE_BOX);
            ghost.setTranslateY((-floor)*SIZE_BOX-SIZE_BOX/2);
            root.getChildren().add(ghost);
        }

        public void drawObstacle(Group root,int i, int j,int floor,Maze maze) throws IOException{
            FXMLLoader fxmlLoader = new FXMLLoader();
            int scale = 0,posy;
            if(maze.getTypeObstacle().equals("Cercle")) {
                fxmlLoader.setLocation(this.getClass().getResource("Spider.fxml"));
                scale = 2;
                posy = SIZE_BOX/10;
            }
            else{
                fxmlLoader.setLocation(this.getClass().getResource("gate.fxml"));
                scale = 3;
                posy = SIZE_BOX/20;
            }
            fxmlLoader.setLocation(this.getClass().getResource("gate.fxml"));
            scale = 4;
            posy = SIZE_BOX/5;
            //fxmlLoader.setLocation(this.getClass().getResource("Spider.fxml"));
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
                    ((Shape3D)n).setScaleY(((Shape3D)n).getScaleY()*scale);
                    ((Shape3D)n).setScaleZ(((Shape3D)n).getScaleZ()*scale);
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
        }


        public void remove(Group root,int i, int j){
            int posz = i*SIZE_BOX;
            int posx = j*SIZE_BOX;
            Node removable;
            for ( Node a : keyOrBonus ) {
                if(a.getTranslateX()==posx && a.getTranslateZ()==posz){
                    removable=a;
                    root.getChildren().remove(removable);
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
           printMaze();
        }

        public void reset() {

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
                            int floor = game.floor();
                            //System.out.println(pos);
                            mazePane.x.set((pos.getX() + mazePane.coordSwitch[floor].x()) * mazePane.SIZE_BOX - mazePane.SIZE_BOX / 2);
                            mazePane.z.set((pos.getY() + mazePane.coordSwitch[floor].z()) * mazePane.SIZE_BOX - mazePane.SIZE_BOX / 2);
                            mazePane.y.set(-yPos * mazePane.SIZE_BOX);
                            mazePane.angle.set(90 - game.player().orientation());
                            // timeSeconds.set(game.getElapsed());
                            if (floor == 2)
                                System.out.println(((mazeScene.getCamera().getTranslateX() / 400) + 0.5) + "     " + (((mazeScene.getCamera().getTranslateY()) / (-400))) + "   " + (((mazeScene.getCamera().getTranslateZ()) / 400) + 0.5));

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
            //game.tStart();
            timePane.start();
            gameTimer.start();
            mazePane.initMaze();
            main.getChildren().add(timePane);
            StackPane.setAlignment(timePane,Pos.TOP_LEFT);
            View.this.setOnKeyPressed(e -> {
                boolean pocket = main.getChildren().contains(inventory);
                    if(e.getCode()==KeyCode.M){
                        if(e.isAltDown()){
                            if(!pocket)main.getChildren().add(inventory);
                            updateInventory();
                            timePane.pause();
                        }
                        else if(e.isControlDown()){
                            main.getChildren().remove(inventory);
                            timePane.play();
                        }
                    }
                    else if(!pocket) {
                        if (game.player.state() != Player.PlayerState.FALLING && game.player.state() != Player.PlayerState.JUMPING) {
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
                                    break;
                                case SHIFT:
                                    if (e.isControlDown()) mazePane.rotateX.setAngle(mazePane.rotateX.getAngle() - 0.5);
                                    else mazePane.rotateX.setAngle(mazePane.rotateX.getAngle() + 0.5);
                                    break;
                            }
                        }
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

        public void updateInventory(){
            inventory.getChildren().clear();
            String style="-fx-text-fill: crimson;-fx-font: oblique 15pt cursive; -fx-text-alignment: center";
            int time=0,coin=0;
            int key = 7;//game.player().keys().size();
            int bonus = 4;//game.player().getBonus().size();
            if(bonus+key !=0){
                if(bonus!=0){
                    for (Bonus a : game.player().getBonus()) {
                        if(a instanceof TimeBonus)time++;
                        else coin++;
                    }
                    coin=4;time=3;
                    if(coin!=0){
                        Label piece = new Label("Vous avez "+""+coin+" pièce.s");
                        configLabel(piece,"coin.png",style,inventory);
                    }
                    if(time!=0){
                        Label hourglass = new Label("Vous avez "+""+time+" sablier.s");
                        configLabel(hourglass,"hourglass.png",style,inventory);
                    }
                }
                if(key!=0){
                    Label keys = new Label("Vous avez "+""+key+" clé.s");
                    configLabel(keys,"key.png",style,inventory);
                }
            }
            else{
                Label nothing = new Label("Vous n'avez aucun objet");
                nothing.setStyle(style);
                inventory.getChildren().add(nothing);
            }
        }

        public void configLabel(Label txt,String path,String style,VBox pan){
            Image img = new Image(getClass().getResourceAsStream(path));
            txt.setGraphic(new ImageView(img));
            txt.setContentDisplay(ContentDisplay.RIGHT);
            txt.setStyle(style);
            pan.getChildren().add(txt);
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

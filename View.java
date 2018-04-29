import javafx.animation.*;
import java.time.LocalDate;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.MapExpression;
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
import java.awt.geom.Point2D;
import java.awt.Point;
import java.io.IOException;
import java.time.LocalDateTime;
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
    protected Pane map;
    protected boolean debug=false;

    public View(StackPane root, GameVersion game) {
        super(root);
        this.game = game;
        main = root;
        Rectangle2D screenBounds = Screen.getPrimary().getBounds();
        PerspectiveCamera cam = new PerspectiveCamera(true);
        this.mazePane = new MazePane(cam);
        mazeScene = new SubScene(mazePane, screenBounds.getWidth(), screenBounds.getHeight(), true, SceneAntialiasing.BALANCED);
        mazeScene.setCamera(cam);
        if (game instanceof SoloVersion) timePane = new SoloTimePane();
        else if (game instanceof TimeTrialVersion) timePane = new TimeTrialPane(((TimeTrialVersion) game).timeLimit);
        main.getChildren().addAll(mazeScene, timePane);
        StackPane.setAlignment(timePane, Pos.TOP_LEFT);
    }

    public void setStage(Stage stage) {
        st = stage;
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
                    return MazeInterface.getT(TimeProperty.this.getValue());
                }
            };


        }
    }

    /**
      Classe pour le timer
    */
    protected abstract class TimePane extends VBox {
        protected Timeline timeLine;
        protected Label timeLabel;
        protected IntegerProperty timeSeconds;


        public TimePane(int time) {
            timeSeconds = new TimeProperty(time);
            timeLabel = new Label();
            timeLine = new Timeline();
            timeLabel.textProperty().bind(timeSeconds.asString());
            timeLabel.setStyle("-fx-alignment:center;-fx-text-fill:crimson;-fx-font-size:50pt");
            this.getChildren().add(timeLabel);
            this.setSpacing(200);
        }


        public String getElapsedTime() {
            return MazeInterface.getT(game.getElapsed());
        }

        public int getElapsedSeconds() {
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

        public void play() {
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

    protected class SoloTimePane extends TimePane {

        public SoloTimePane() {
            super(0);
            timeLine.setCycleCount(Timeline.INDEFINITE);
            timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
                game.elapse(1);
                timeSeconds.set(game.getElapsed());
            }));
        }
    }

    protected class MazePane extends Group {
        protected Group world;
        protected LinkedList<Maze> floors=game.floors();
        protected PerspectiveCamera camera;
        protected AmbientLight light;
        protected final int MAZE_LENGTH = game.current().getHeight();
        protected final int MAZE_WIDTH = game.current().getWidth();
        protected final int SIZE_BOX = 400;
        protected final int STEP = 50;
        protected int posFx, posFz;
        protected PhongMaterial COLOR_WALL = new PhongMaterial();
        protected PhongMaterial COLOR_WAY = new PhongMaterial(Color.DARKGREY);
        protected PhongMaterial COLOR_ENTRY = new PhongMaterial(Color.RED);
        protected PhongMaterial COLOR_END = new PhongMaterial(Color.LIGHTGOLDENRODYELLOW);
        protected PhongMaterial COLOR_DOOR = new PhongMaterial();
        protected PhongMaterial COLOR_STAIRS = new PhongMaterial(Color.WHITESMOKE);
        protected DoubleProperty x = new SimpleDoubleProperty(0);
        protected DoubleProperty z = new SimpleDoubleProperty(0);
        protected DoubleProperty y = new SimpleDoubleProperty(0);
        protected DoubleProperty angle = new SimpleDoubleProperty(0);
        protected Rotate rotateX;
        protected ArrayList<Node> keyOrBonus=new ArrayList<>();
        protected Vector3D[] coordSwitch = new Vector3D[floors.size()];
        protected Group[] floorGroups = new Group[floors.size()];

        public MazePane(PerspectiveCamera cam) {
            camera = cam;
            display = new VBox();
            display.setAlignment(Pos.CENTER);
        }
        public void setLight() {
            DoubleProperty x = camera.translateXProperty();
            DoubleProperty y = camera.translateYProperty();
            DoubleProperty z = camera.translateZProperty();
            DoubleProperty angle=camera.rotateProperty();
            PointLight lightL = new PointLight();
            lightL.translateXProperty().bind(x.add(-SIZE_BOX*Math.cos(Math.toRadians(angle.get()))));
            lightL.translateYProperty().bind(y.subtract(SIZE_BOX/2));
            lightL.translateZProperty().bind(z.add(-SIZE_BOX*Math.sin(Math.toRadians(angle.get()))));
            /*PointLight lightL = new PointLight();
            PointLight lightR = new PointLight();
            PointLight lightU = new PointLight();
            PointLight lightD = new PointLight();
            lightL.translateXProperty().bind(x.subtract(SIZE_BOX));
            lightL.translateYProperty().bind(y);
            lightL.translateZProperty().bind(z);
            lightR.translateZProperty().bind(z);
            lightR.translateYProperty().bind(y);
            lightR.translateZProperty().bind(x.add(SIZE_BOX));
            /*lightU.translateXProperty().bind(x);
            lightU.translateYProperty().bind(y);
            lightU.translateZProperty().bind(z.add(SIZE_BOX));
            lightD.translateXProperty().bind(x);
            lightD.translateYProperty().bind(y);
            lightD.translateZProperty().bind(z.subtract(SIZE_BOX));*/
           /*licht.translateXProperty().bind(x.add(SIZE_BOX));
            licht.translateZProperty().bind(z.add(SIZE_BOX));
            licht.translateYProperty().bind(y);
            lum.translateXProperty().bind(x.subtract(SIZE_BOX));
            lum.translateZProperty().bind(z.subtract(SIZE_BOX));
            lum.translateYProperty().bind(y);*/
            Group groupLight = new Group(lightL,new AmbientLight(Color.BLACK));//,light,new AmbientLight(Color.BLACK));//,new AmbientLight(Color.BLACK),new AmbientLight(Color.BLACK));
            this.getChildren().add(groupLight);
          /*light = new Light.Point();
          light.setColor(Color.LIGHTGOLDENRODYELLOW);
          light.xProperty().bind(x);
          light.yProperty().bind(y);
          light.zProperty().bind(z);
          Lighting lighting = new Lighting(light);
          lighting.setSurfaceScale(8.0);
          main.setEffect(lighting);*/
            //cam.setEffect(lighting);
        }
        /**
          Cette fonction est utilisée pour voir si les coordonées en x ou en z des escaliers
          sont égales à 0 ou à la largeur/longueur - 1
        */
        public boolean test(int a, int b, int c, int d) {
            return ((a == b) && (b == c || b == d));
        }
        /**
          Création par appel successif de createMaze
          À la fin, de chaque createMaze, on effectue des translations sur les étages pour les placer côte à côte selon les escaliers.
        */
        public void initMaze() throws IOException {
            floors = game.floors();
            keyOrBonus = new ArrayList<>();
            Group world = new Group();
            Group floor = new Group();
            Group first = floor;
            Point before = null;
            Box square = null;
            COLOR_WALL.setBumpMap(new Image("images/brick.jpg"));
            COLOR_WALL.setSpecularColor(Color.BLACK);
            COLOR_DOOR.setDiffuseMap(new Image("images/safe.jpg"));
            COLOR_ENTRY.setSpecularColor(Color.BLACK);
            COLOR_END.setSpecularColor(Color.WHITE);
            int i = 0;
            for (Maze a : floors) {
                createMaze(floor, a, i);
                if (before != null) {
                    int aposx = (int) a.beginning().getX();
                    int aposz = (int) a.beginning().getY();
                    floor.setTranslateX((posFx - aposx) * SIZE_BOX);
                    floor.setTranslateZ((posFz - aposz) * SIZE_BOX);
                    posFx = (int) a.ending().getX() + (posFx - aposx);
                    posFz = (int) a.ending().getY() + (posFz - aposz);
                    if (!test(aposx, (int) before.getX(), MAZE_WIDTH - 1, 0) && !test(aposz, (int) before.getY(), MAZE_LENGTH - 1, 0)) {
                        setUp(aposx, aposz, MAZE_LENGTH, MAZE_WIDTH, floor, 1);
                        setUp((int) before.getX(), (int) before.getY(), MAZE_LENGTH, MAZE_WIDTH, floor, -1);
                        square = new Box(SIZE_BOX, 0, SIZE_BOX);
                        square.setTranslateX(aposx * SIZE_BOX);
                        square.setTranslateZ(aposz * SIZE_BOX);
                        if (aposx == 0) square.setTranslateX(square.getTranslateX() - SIZE_BOX);
                        else if (aposx == MAZE_WIDTH - 1) square.setTranslateX(square.getTranslateX() + SIZE_BOX);
                        else if (aposz == 0) square.setTranslateZ(square.getTranslateZ() - SIZE_BOX);
                        else if (aposz == MAZE_LENGTH - 1) square.setTranslateZ(square.getTranslateZ() + SIZE_BOX);
                    } else if (test(aposx, (int) before.getX(), MAZE_WIDTH - 1, 0)) {
                        int coeff = (aposx == 0) ? 1 : -1;
                        floor.setTranslateZ(floor.getTranslateZ() - SIZE_BOX);
                        square = new Box(SIZE_BOX, 0, 2 * SIZE_BOX);
                        square.setTranslateX((aposx - coeff) * SIZE_BOX);
                        square.setTranslateZ(aposz * SIZE_BOX + SIZE_BOX / 2);
                        posFz--;
                    } else {
                        int coeff = (aposz == 0) ? 1 : -1;
                        floor.setTranslateX(floor.getTranslateX() - SIZE_BOX);
                        square = new Box(2 * SIZE_BOX, 0, SIZE_BOX);
                        square.setTranslateX(aposx * SIZE_BOX + SIZE_BOX / 2);
                        square.setTranslateZ((aposz - coeff) * SIZE_BOX);
                        posFx--;
                    }
                    square.setMaterial(new PhongMaterial(Color.MAROON));
                    square.setTranslateY((-i + 1) * SIZE_BOX + SIZE_BOX / 2);
                    floor.getChildren().add(square);
                } else {
                    posFx = (int) a.ending().getX();
                    posFz = (int) a.ending().getY();
                }
                before = a.ending();
                coordSwitch[i / 2] = (new Vector3D(floor.getTranslateX(), floor.getTranslateY(), floor.getTranslateZ()).subtract(new Vector3D(first.getTranslateX(), first.getTranslateY(), first.getTranslateZ()))).multiply(1.0 / 400);
                world.getChildren().add(floor);
                floorGroups[i / 2] = floor;
                floor = new Group();
                i += 2;

            }
            this.getChildren().add(world);
            buildCamera(this);
            setLight();
        }
        /**
          Création de la vue d'un étage de labyrinthe selon l'objet Maze donné en argument
        */
        public void createMaze(Group root, Maze maze, int floor) throws IOException {
            int CASE;
            Box cell;
            for (int i = 0; i < maze.getHeight(); i++) {
                for (int j = 0; j < maze.getWidth(); j++) {
                    CASE = maze.getCase(i, j);
                    switch (CASE) {
                        case Maze.START:
                            cell = makeFloor(COLOR_ENTRY);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.END:
                            cell = makeFloor(COLOR_END);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.WAY:
                            cell = makeFloor(COLOR_WAY);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.DOOR:
                            cell = new Box(SIZE_BOX, SIZE_BOX, SIZE_BOX);
                            cell.setMaterial(COLOR_DOOR);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.KEY:
                            drawKey(root, j, i, floor);
                            cell = makeFloor(COLOR_WAY);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.TELEPORT:
                            cell = makeFloor(COLOR_WAY);
                            drawTeleport(root, i, j, maze, floor);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.MONSTRE:
                            cell = makeFloor(COLOR_WAY);
                            drawMonster(root, i, j, floor, maze);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.BONUS:
                            cell = makeFloor(COLOR_WAY);
                            drawBonus(root, i, j, maze, floor);
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.OBSTACLE:
                            cell = makeFloor(COLOR_STAIRS);
                            drawObstacle(root, i, j, floor, maze);
                            //faire drawObstacle, attention à la forme de l'obstacle
                            setBox(cell, i, j, root, floor, maze);
                            break;

                        case Maze.STAIRSUP:
                            drawStair(0, root, i, j, maze, floor);
                            break;

                        case Maze.STAIRSDOWN:
                            drawStair(1, root, i, j, maze, floor);
                            break;

                        case Maze.WALL:
                            cell = new Box(SIZE_BOX, SIZE_BOX, SIZE_BOX);
                            cell.setMaterial(COLOR_WALL);
                            setBox(cell, i, j, root, floor, maze);
                            break;
                    }
                }
            }
        }

        public Box makeFloor(PhongMaterial color) {
            Box cell = new Box(SIZE_BOX, 0, SIZE_BOX);
            cell.setMaterial(color);
            cell.setTranslateY(SIZE_BOX / 2);
            return cell;
        }
        /*
          Place une Box aux coordonées données aux arguments, et une Box rectangulaire au-dessus de la box crée
        */
        public void setBox(Box cell, int i, int j, Group root, int floor, Maze maze) {
            cell.setTranslateX(j * SIZE_BOX);
            cell.setTranslateZ(i * SIZE_BOX);
            int place = maze.getCase(i, j);
            if (place == Maze.WALL || place == Maze.DOOR || place == Maze.STAIRSUP || place == Maze.STAIRSDOWN)
                cell.setTranslateY((-floor) * SIZE_BOX);
            else
                cell.setTranslateY((-floor) * SIZE_BOX + SIZE_BOX / 2);
            setRoof(i, j, root, floor);
            root.getChildren().add(cell);
        }

        public void setRoof(int i, int j, Group root, int floor) {
            Box roof = new Box(SIZE_BOX, 0, SIZE_BOX);
            roof.setTranslateX(j * SIZE_BOX);
            roof.setTranslateZ(i * SIZE_BOX);
            roof.setTranslateY(-floor * SIZE_BOX - SIZE_BOX / 2);
            roof.setMaterial(new PhongMaterial(Color.BLACK));
            root.getChildren().add(roof);
        }
        /*
          Translate un étage selon la position des escaliers.
        */
        public void setUp(int posx, int posz, int height, int width, Group floor, int a) {
            if (posx == 0) {
                floor.setTranslateX(floor.getTranslateX() + SIZE_BOX * a);
                posFx += a;
            } else if (posx == width - 1) {
                floor.setTranslateX(floor.getTranslateX() - SIZE_BOX * a);
                posFx -= a;
            } else if (posz == 0) {
                floor.setTranslateZ(floor.getTranslateZ() + SIZE_BOX * a);
                posFz += a;
            } else if (posz == height - 1) {
                floor.setTranslateZ(floor.getTranslateZ() - SIZE_BOX * a);
                posFz -= a;
            }
        }
        //Fonctions pour dessiner les différents objets du labyrinthe

        public void drawStair(int dir, Group root, int i, int j, Maze maze, int floor) {
            Box step;
            int nbStep = 8;
            Group stairs = new Group();
            int size = SIZE_BOX / nbStep, a = 0;
            while (nbStep != a) {
                step = new Box(SIZE_BOX, size * (a + 1), size);
                step.setMaterial(COLOR_STAIRS);
                step.setTranslateX(j * SIZE_BOX);
                step.setTranslateZ(i * SIZE_BOX + size * a);//+SIZE_BOX/2);//-size);
                step.setTranslateY(SIZE_BOX / 2 + (size / 2) * (-1) * (a + 1));
                a++;
                stairs.getChildren().add(step);
            }
            stairs.setRotationAxis(Rotate.Y_AXIS);
            stairs.setTranslateZ(stairs.getTranslateZ() - SIZE_BOX / 2 + size / 2);
            stairs.setTranslateY(stairs.getTranslateY() - floor * SIZE_BOX);
            if (dir == 1) stairs.setTranslateY(stairs.getTranslateY() + SIZE_BOX);
            if (j == 0) {
                //System.out.println("pat");
                stairs.setRotate(-90);
            } else if (j == maze.getWidth() - 1) {
                //  System.out.println("sac");
                stairs.setRotate(90);
            } else if (i == maze.getHeight() - 1) {
                //System.out.println("sec");
                stairs.setRotate(0);
            } else if (i == 0) {
                //System.out.println("zed");
                stairs.setRotate(180);
            }
            if (dir == 1) stairs.setRotate(stairs.getRotate() + 180);
            root.getChildren().add(stairs);
        }

        public void drawKey(Group root, int posx, int posy, int floor) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(this.getClass().getResource("fxml/key.fxml"));
            MeshView key = fxmlLoader.<MeshView>load();
            key.setRotationAxis(Rotate.Z_AXIS);
            key.setRotate(180.0);
            key.setTranslateX(posx * SIZE_BOX);
            key.setTranslateZ(posy * SIZE_BOX);
            key.setTranslateY(-floor * SIZE_BOX);
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

        public void drawTeleport(Group root, int i, int j, Maze maze, int floor) throws IOException {
            Teleporteur last = maze.getTeleport().getLast();
            MeshView teleport = last.initTeleport();
            teleport.setTranslateX(j * SIZE_BOX);
            teleport.setTranslateZ(i * SIZE_BOX);
            teleport.setTranslateY((-floor) * SIZE_BOX+SIZE_BOX/4);
            teleport.setScaleX(teleport.getScaleX() * SIZE_BOX / 4);
            teleport.setScaleZ(teleport.getScaleZ() * SIZE_BOX / 4);
            teleport.setScaleY(teleport.getScaleY() * SIZE_BOX / 4);
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

        public void drawMonster(Group root, int i, int j, int floor, Maze maze) throws IOException {
            Monstres last = maze.getMonstres().getLast();
            MeshView ghost = last.initMonster();
            ghost.setTranslateY((-floor) * SIZE_BOX - SIZE_BOX / 2);
            root.getChildren().add(ghost);
        }

        public void drawObstacle(Group root, int i, int j, int floor, Maze maze) throws IOException {
            FXMLLoader fxmlLoader = new FXMLLoader();
            float scale = 0, scaleY, scaleZ, posy;
            Box b = new Box(SIZE_BOX, 0, 0.1 * SIZE_BOX);
            b.setMaterial(COLOR_ENTRY);
            Circle c=new Circle(SIZE_BOX/2,Color.LAVENDER);
            if (maze.getTypeObstacle().equals("Cercle")) {
                fxmlLoader.setLocation(this.getClass().getResource("fxml/Spider.fxml"));
                scale =  2.1f;
                scaleZ = 2.5f;
                scaleY = 1.5f;
                posy = SIZE_BOX / 10;
            } else {
                fxmlLoader.setLocation(this.getClass().getResource("fxml/gate.fxml"));
                scale = 3.8f;
                scaleY = 6;
                scaleZ = 3;
                posy = SIZE_BOX / 20;
            }
            Group obs = fxmlLoader.load();
            PhongMaterial mat = new PhongMaterial();
            mat.setSpecularColor(Color.BLACK);
            mat.setDiffuseColor(Color.DARKGREY);
            for (Node n : obs.getChildren()) {
                if (n instanceof Shape3D) {
                    ((Shape3D) n).setTranslateX(j * SIZE_BOX);
                    ((Shape3D) n).setTranslateZ(i * SIZE_BOX);
                    ((Shape3D) n).setMaterial(mat);
                    ((Shape3D) n).setScaleX(((Shape3D) n).getScaleX() * scale);
                    ((Shape3D) n).setScaleY(((Shape3D) n).getScaleY() * scaleY);
                    ((Shape3D) n).setScaleZ(((Shape3D) n).getScaleZ() * scaleZ);
                    ((Shape3D) n).setTranslateY(SIZE_BOX / 2 - posy - (floor * SIZE_BOX));
                    c.setTranslateX(j * SIZE_BOX);
                    c.setTranslateZ(i * SIZE_BOX);
                    c.setTranslateY(SIZE_BOX / 2);
                }
            }
            root.getChildren().add(c);
            root.getChildren().add(obs);
        }

        public void drawBonus(Group root, int i, int j, Maze maze, int floor) throws IOException {
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
            bonus.setTranslateY((-floor) * SIZE_BOX + SIZE_BOX / 8);
            bonus.setScaleX(bonus.getScaleX() * SIZE_BOX / 10);
            bonus.setScaleZ(bonus.getScaleZ() * SIZE_BOX / 10);
            bonus.setScaleY(bonus.getScaleY() * SIZE_BOX / 10);
            rt.setNode(bonus);
            rt.play();
            root.getChildren().add(bonus);
            keyOrBonus.add(bonus);
        }
        public void remove(Group root, int i, int j) {
            int posx = i * SIZE_BOX;
            int posz = j * SIZE_BOX;
            Node removable;
            for (Node a : keyOrBonus) {
                if (a.getTranslateX() == posx && a.getTranslateZ() == posz) {
                    System.out.println(a.getTranslateX() + "   " + a.getTranslateZ());
                    removable = a;
                    System.out.println(root.getChildren().contains(a));
                    root.getChildren().remove(removable);
                    System.out.println(root.getChildren().contains(a));
                    break;
                }
            }
        }
        public void buildCamera(Group root) {
            camera.setFarClip(10000);
            camera.setNearClip(0.1);
            Point2D position = game.player().getPosition();
            double yPos = game.player.getY();
            int floor = game.floor();
            x = new SimpleDoubleProperty((position.getX() + coordSwitch[floor].x()) * SIZE_BOX - SIZE_BOX / 2);
            y = new SimpleDoubleProperty(-yPos * SIZE_BOX);
            z = new SimpleDoubleProperty((position.getY() + coordSwitch[floor].z()) * SIZE_BOX - SIZE_BOX / 2);
            angle = new SimpleDoubleProperty(90 - game.player().orientation());
            camera.translateXProperty().bind(x);
            camera.translateYProperty().bind(y);
            camera.translateZProperty().bind(z);
            camera.rotateProperty().bind(angle);
            camera.setRotationAxis(Rotate.Y_AXIS);
            rotateX = new Rotate();
            rotateX.setAxis(Rotate.X_AXIS);
            camera.getTransforms().add(rotateX);
        }

        public void reset() throws IOException {
            this.getChildren().clear();
            initMaze();
        }
      }
    /**
      Classe contrôleur
    */
    protected abstract class GameControl {
        protected double mouseXOld;
        protected double mouseYOld;
        protected int action = -1;
        protected LongProperty lastUpdate;
        protected AnimationTimer gameTimer;
        protected Button save, inv, quit, help, restart, pause, plan;

        /**
          Création de la barre de menu avec les boutons sauvegarder, quitter, pause, plan active si le joueur possède plus de 5 pièces
        */
        public void setToolBar(boolean multi) {
            String style = "-fx-background-color: rgba(0, 0, 0, 0);";
            String style1="-fx-background-color: rgba(0, 0, 0, 0);-fx-font-size:23px;-fx-text-fill: white;";
            quit = new Button();
            configButton("images/quit.png", quit);
            quit.setOnMouseClicked(e -> {
                st.close();
            });
            plan = new Button();
            configButton("images/map.png", plan);
            plan.setOnMouseClicked(e -> {
              System.out.println("ouai");
                if (!main.getChildren().contains(map)) {
                    main.getChildren().add(map);
                    StackPane.setAlignment(map, Pos.BOTTOM_LEFT);
                    tool.toFront();
                  }
                else main.getChildren().remove(map);
            });
            plan.setDisable(true);
            pause = new Button();
            configButton("images/pause.png", pause);
            pause.setOnMouseClicked(e -> {
                if (!main.getChildren().contains(display)) {
                    configButton("images/play.png", pause);
                    timePane.pause();
                    display.getChildren().clear();
                    Label pau = new Label("PAUSE");
                    pau.setStyle("-fx-effect: dropshadow(gaussian,rgba(0,0,0,0.75) , 4,0,0,1 );-fx-text-fill: lightgrey;-fx-font-size: 150px;-fx-font-weight:bold;");
                    display.getChildren().add(pau);
                    display.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 10;");
                    main.getChildren().add(display);
                    tool.toFront();
                } else {
                    configButton("images/pause.png", pause);
                    timePane.play();
                    main.getChildren().remove(display);
                }
            });
            IntegerProperty nbPiece = new SimpleIntegerProperty();
            IntegerProperty nbKey = new SimpleIntegerProperty();
            Timeline pocket = new Timeline();
            pocket.setCycleCount(Timeline.INDEFINITE);
            pocket.getKeyFrames().add(new KeyFrame(Duration.millis(500),(evt)->{
              nbPiece.set(game.player().getBonus().size());
              nbKey.set(game.player().keys().size());
            }));
            pocket.playFromStart();
            Label piece = new Label();
            piece.textProperty().bind(nbPiece.asString());
            MazeInterface.configLabel(piece,"/images/coin.png",style1);
            Label key = new Label();
            key.textProperty().bind(nbKey.asString());
            MazeInterface.configLabel(key,"/images/key.png",style1);
            save = new Button();
            configButton("images/save.png", save);
            save.setOnMouseClicked(e -> {
                LocalDate now = LocalDate.now();
                String date[] = now.toString().split("-");
                try {
                    game.save("savings/"+game.player().getName()+"--"+date[2] + "-" + date[1] + "-" + date[0].charAt(2) +date[0].charAt(3));
                } catch (Exception exc) {
                    exc.printStackTrace();
                }
            });
            help = new Button();
            configButton("images/help.png", help);
            help.setOnMouseClicked(e -> {
                if(main.getChildren().contains(display)){
                  if(!multi)timePane.play();
                  main.getChildren().remove(display);
                }
                else {
                  if(!multi)timePane.pause();
                  displayHelp();
                  main.getChildren().add(display);
                  tool.toFront();
              }
            });
            Region rg = new Region();
            HBox.setHgrow(rg, Priority.SOMETIMES);
            rg.setFocusTraversable(false);
            if(multi) tool=new ToolBar(rg,help,piece,plan,quit);
            else tool = new ToolBar(rg, help,piece,save,plan,pause,quit);
            for (Node a : tool.getItems()) {
                if (a instanceof Button) {
                    a.setStyle(style);
                    a.setFocusTraversable(false);
                }
            }
            if(game instanceof TimeTrialVersion)tool.getItems().remove(piece);
            tool.setStyle("-fx-background-color: rgba(0, 0, 0, 0);");
            main.getChildren().add(tool);
            StackPane.setAlignment(tool,Pos.TOP_RIGHT);
        }

        public void configButton(String path, Button button) {
            Image img = new Image(getClass().getResourceAsStream(path), 50, 50, false, false);
            ImageView im = new ImageView(img);
            im.setFitHeight(20);
            im.setFitWidth(20);
            button.setGraphic(new ImageView(img));
        }

        public GameControl(boolean multi) throws IOException {
            lastUpdate = new SimpleLongProperty();
            gameTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (game.gameOver()) {
                        whenIsFinished();
                    } else if (game.player.state() == Player.PlayerState.DEAD) {
                        timePane.stop();
                        final ImageView imv = new ImageView();
                        final Image img = new Image(View.class.getResourceAsStream("images/death.png"));
                        imv.setImage(img);
                        main.getChildren().add(imv);
                        View.this.setOnKeyPressed(null);
                        gameTimer.stop();
                    } else if (timePane.timeOver()) {
                    } else {
                        if (lastUpdate.get() > 0) {
                            double elapsedTime = (now - lastUpdate.get()) / 1000000000.0;
                            game.update(elapsedTime);
                            Point2D pos = game.player().getPosition();
                            double yPos = game.player().getY();
                            int floor = game.player().getMazeIndex();
                            mazePane.x.set((pos.getX() + mazePane.coordSwitch[floor].x()) * mazePane.SIZE_BOX - mazePane.SIZE_BOX / 2);
                            mazePane.z.set((pos.getY() + mazePane.coordSwitch[floor].z()) * mazePane.SIZE_BOX - mazePane.SIZE_BOX / 2);
                            mazePane.y.set(-yPos * mazePane.SIZE_BOX);
                            mazePane.angle.set(90 - game.player().orientation());
                            mazePane.rotateX.setAngle(game.player().orientationX());
                            if (game.player.hasPickedUp()) {
                                System.out.println((int) pos.getX() + "  " + (int) pos.getY());
                                MazeInterface.sounds(0).play();
                                mazePane.remove(mazePane.floorGroups[floor], (int) pos.getX(), (int) pos.getY());
                                game.player.pick(false);
                            }
                        }
                        lastUpdate.set(now);

                    }
                }
            };
            handleAction();
            setToolBar(multi);
        }

        //Fonction de gestion des actions clavier du joueur
        public void handleAction() throws IOException {
            game.start();
            mazePane.initMaze();
            System.out.println(mazePane.floors);
            System.out.println(game.floors());
            gameTimer.start();
            timePane.start();
            int size=500/Math.max(mazePane.MAZE_LENGTH,mazePane.MAZE_WIDTH);
            map = new Pane();
            setUpMap(size);
            View.this.setOnKeyPressed(e -> {
                boolean pane = main.getChildren().contains(display);
                if (!pane) {
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
                        }
                        map.getChildren().clear();
                        setUpMap(size);
                        StackPane.setAlignment(map,Pos.BOTTOM_LEFT);
                    }
                }
            });
            View.this.setOnKeyReleased(e -> {
                switch (e.getCode()) {
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
            View.this.addEventHandler(MouseEvent.ANY, evt -> {
                if (evt.getEventType() == MouseEvent.MOUSE_DRAGGED || evt.getEventType() == MouseEvent.MOUSE_PRESSED) {
                    double mouseXNew = evt.getSceneX();
                    double mouseYNew = evt.getSceneY();
                    if (evt.getEventType() == MouseEvent.MOUSE_DRAGGED) {
                        double pitchRotate = mazePane.rotateX.getAngle() + (mouseYNew - mouseYOld);
                        mazePane.rotateX.setAngle(pitchRotate);
                        double yawRotate = mazePane.angle.get() - (mouseXNew - mouseXOld);
                        mazePane.angle.set(yawRotate);
                    }
                    mouseXOld = mouseXNew;
                    mouseYOld = mouseYNew;
                }
                if (evt.getEventType() == MouseEvent.MOUSE_CLICKED) {

                }

            });

        }

        public void recenterMouse(double x, double y) {
            Rectangle2D screen = Screen.getPrimary().getBounds();
            x = screen.getWidth() / 2;
            y = screen.getHeight() / 2;
        }


        public abstract void whenIsFinished();

        public void moveMap(int size, Circle player) {
            player.centerXProperty().set(game.player().getPosition().getX() * size+10);
            player.centerYProperty().set(game.player().getPosition().getY() * size+300);
        }

        public void makeClip(Circle player,Pane pane){
          Rectangle clip = new Rectangle();
          DoubleProperty height = new SimpleDoubleProperty(400);
          DoubleProperty width = new SimpleDoubleProperty(400);
          clip.widthProperty().bind(height);
          clip.heightProperty().bind(width);
          clip.xProperty().bind(Bindings.createDoubleBinding(
                () -> magic(player.getCenterX() - width.get() / 5, 0, pane.getWidth() - width.get())
                , player.centerXProperty(), width));
                clip.yProperty().bind(Bindings.createDoubleBinding(
                () -> magic(player.getCenterY() - height.get() / 5, 0, pane.getHeight() - height.get()),
                player.centerYProperty(), height));

                pane.setClip(clip);
        }

        public double magic(double value,double min,double max){
          if (value < min) return min ;
          if (value > max) return max ;
          return value ;
        }

        public void setUpMap(int size) {
            Canvas mapDraw = new Canvas(600, 800);
            GraphicsContext gc = mapDraw.getGraphicsContext2D();
            int x = 10, y=300;
            Maze maze = game.current();
            for (int i = 0; i < maze.getHeight(); i++) {
                for (int j = 0; j < maze.getWidth(); j++) {
                    switch (maze.getCase(i, j)) {
                        case Maze.WAY:
                            gc.setFill(Color.TRANSPARENT);
                            break;
                        case Maze.START:
                            gc.setFill(Color.BLUE);
                            break;
                        case Maze.END:
                            gc.setFill(Color.RED);
                            break;
                        case Maze.WALL:
                            gc.setFill(Color.LIGHTGREY);
                            break;
                        case Maze.STAIRSUP:
                            gc.setFill(Color.BROWN);
                            break;
                        case Maze.STAIRSDOWN:
                            gc.setFill(Color.BROWN);
                            break;
                        case Maze.OBSTACLE:
                            gc.setFill(Color.GREY);
                            break;
                        case Maze.MONSTRE:
                            gc.setFill(Color.WHITE);
                            break;
                        case Maze.TELEPORT:
                            gc.setFill(Color.PURPLE);
                            break;
                        case Maze.DOOR:
                            gc.setFill(Color.LIGHTBLUE);
                            break;
                        case Maze.KEY:
                            gc.setFill(Color.YELLOW);
                            break;
                        case Maze.BONUS:
                            gc.setFill(Color.YELLOW);
                            break;
                    }
                    gc.fillRect(x, y, size, size);
                    x += size;
                }
                y += size;
                x = 10;
            }
            Circle player = new Circle(size/4);
            player.setFill(Color.WHITESMOKE);
            map.getChildren().addAll(mapDraw,player);
            moveMap(size,player);
            makeClip(player,map);
        }
        public void displayScore(Pane root) {
            root.getChildren().clear();
            String display = game.scores().getScores();
            String[] splits = display.split("\n");
            for (String s : splits) root.getChildren().add(new Label(s));
        }

        public void displayScores(Scores s) {
            ScorePane sp = new ScorePane(s);
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


        public void displayHelp(){
            int coins=game.maze().getFloor().getFirst().getBonus().size();
            display.getChildren().clear();
            String style = "-fx-text-fill: white;-fx-font:oblique 15pt cursive;-fx-text-alignment: left;-fx-font-weight:bold;-fx-effect:dropshadow(gaussian,rgba(0,0,0,0.75),4,0,0,1);";
            String key = "To open a door, you need a gold key which is hidden somewhere";
            Label cle = new Label(key);
            MazeInterface.configLabel(cle, "images/key.png", style);
            String time = "Hourglasses will get you some extra time";
            Label hour = new Label(time);
            MazeInterface.configLabel(hour, "images/hourglass.png", style);
            String coin = "Look for the coins.With half of them, you can buy a map of the maze, the rest will be extra time on your final score";
            Label piece = new Label(coin);
            MazeInterface.configLabel(piece, "images/coin.png", style);
            String inv = "Pressing M or clicking on the bag icon will show you the objects you possess";
            Label pock = new Label(inv);
            String keys = "Use the arrow keys to move and turn";
            Label touch = new Label(keys);
            String sortie = "You start on the red cell and you need to find the golden cell as fast as you can";
            String monster="Beware of ghosts, if you face them you will start from scratch";
            Label mons=new Label(monster);
            String obs="Try jumping above the spiders. You can try the gates too, you will just get stuck ;)";
            Label obst=new Label(obs);
            Label end = new Label(sortie);
            display.setStyle("-fx-background-color: rgba(0, 0, 0, 0.4); -fx-background-radius: 10; -fx-alignment:center");
            display.setSpacing(30.0);
            display.getChildren().addAll(touch,end, hour,cle, pock,obst,mons);
            if(plan.isDisable()&&!(game instanceof TimeTrialVersion)){
              String styleB = "-fx-background-color:#090a0c,linear-gradient(#38424b 0%, #1f2429 20%, #191d22 100%),linear-gradient(#20262b, #191d22),radial-gradient(center 50% 0%, radius 100%, rgba(114,131,148,0.9), rgba(255,255,255,0));-fx-text-fill:white;";
              Button buyMap = new Button("Buy the map");
              buyMap.setStyle(styleB);
              if(game.player().getBonus().size()<5)buyMap.setDisable(true);
              VBox cost = new VBox(piece,buyMap);
              cost.setAlignment(Pos.CENTER);
              display.getChildren().add(cost);
              buyMap.setOnMouseClicked(e->{
                game.useBonus(5);
                plan.setDisable(false);
                display.getChildren().remove(cost);
              });
            }
            for(Node n:display.getChildren()) n.setStyle(style);
        }
    }
}

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.Group;
import javafx.geometry.Insets;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Box;
import javafx.scene.paint.Color;
import javafx.scene.PerspectiveCamera;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.transform.*;
import javafx.animation.Timeline;
import javafx.event.*;
import java.awt.Point;
import javafx.scene.image.Image;
import javafx.scene.transform.Rotate;
import javafx.scene.control.Slider;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Cylinder;
import javafx.animation.TranslateTransition;
//import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.collections.ObservableList;
import java.util.LinkedList;

public class Test extends Application{
  public Maze maze;
  PerspectiveCamera cam = new PerspectiveCamera(true);
  public int SIZE_MAZE = 15;
  public int SIZE_BOX = 400;
  protected PhongMaterial COLOR_WALL = new PhongMaterial(Color.DARKGREY);
  protected PhongMaterial COLOR_WAY = new PhongMaterial(Color.BLACK);
  protected PhongMaterial COLOR_ENTRY = new PhongMaterial(Color.RED);
  protected PhongMaterial COLOR_END = new PhongMaterial(Color.LIGHTGOLDENRODYELLOW);
  public double mousePosX,mousePosY,mouseOldX,mouseOldY,mouseDeltaX,mouseDeltaY;
  public Rotate rotateZ=new Rotate();
  public Translate tx=new Translate(),ty=new Translate();
  public int STEP = 50,SPEED=1;
  public double angle=5.0;
  public Box[][] cases;


  @Override
  public void start(Stage stage) throws FormatNotSupported{
      Group root = new Group();
      Scene scene = new Scene(root,500,500,true);
      maze= new Maze(SIZE_MAZE,SIZE_MAZE,0,2,0,2,0,0);
      createMaze(root,maze,0);
      //setCamToStart(cam);
      //eventMouse(scene,root);
      buildCamera(cam);
      handleKeyBoard(scene,cam);
      root.getChildren().add(cam);
      scene.setCamera(cam);
      stage.setScene(scene);
      stage.show();

  }

  /*public void doFloor(Group root, MazeFloors maze){
    LinkedList<Maze> floors = maze.getFloor();
    int i=0;
    for (Maze a : floors) {
      createMaze(root,a,i);
    }
  }*/

  public void createMaze(Group root,Maze maze,int floor){
    int CASE;
    Box cell;
    for (int i = 0;i<maze.getHeight() ;i++ ){
      for (int j = 0; j <maze.getWidth() ;j++ ) {
        CASE = maze.getCase(i,j);
        switch(CASE){
          case Maze.START:
          cell=makeFloor(COLOR_ENTRY);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.END:
          cell = makeFloor(COLOR_END);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.WAY:
          cell = makeFloor(COLOR_WAY);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.DOOR:
          cell = new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
          PhongMaterial door= new PhongMaterial();
          door.setDiffuseMap(new Image("/lock.jpg"));
          cell.setMaterial(door);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.KEY:
          cell = makeFloor(COLOR_WAY);
          drawKey(root);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.TELEPORT:
          cell = makeFloor(COLOR_WAY);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.MONSTRE:
          cell = makeFloor(COLOR_WAY);
          drawMonster(root,i,j,floor);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.BONUS:
          cell = makeFloor(COLOR_WAY);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.OBSTACLE:
          break;
          case Maze.STAIRSUP:
          drawStair(4,1,root,i,j,floor);
          break;
          case Maze.STAIRSDOWN:
          drawStair(4,0,root,i,j,floor);
          break;
          default://fais les murs
          cell=new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
          cell.setMaterial(COLOR_WALL);
          //cell.setTranslateY(0);
          setBox(cell,i,j,root,floor);
          break;
        }
      }
    }
    /*Box roof = new Box(maze.getHeight()*SIZE_BOX,0,maze.getWidth()*SIZE_BOX);
    roof.setTranslateX(0);
    roof.setTranslateZ(0);
    roof.setTranslateY();
    root.getChildren().add(roof);
    /*Slider s = new Slider(0,360,0);
    s.setTranslateX(10);
    s.setTranslateY(800);
    root.rotateProperty().bind(s.valueProperty());

    Slider t = new Slider(-10000,10000,0);
    t.setTranslateX(200);
    t.setTranslateY(500);
    root.translateZProperty().bind(t.valueProperty());

    Slider g = new Slider(-10000,10000,0);
    g.setTranslateX(400);
    g.setTranslateY(500);
    root.translateXProperty().bind(g.valueProperty());

    root.getChildren().addAll(s,t,g);*/
  }

  public Box makeFloor(PhongMaterial color){
    Box cell = new Box(SIZE_BOX,0,SIZE_BOX);
    cell.setMaterial(color);
    cell.setTranslateY(SIZE_BOX/2);
    return cell;
  }
  /*public void drawDoor(Group root,Maze maze,int i,int j){
    Box door = new Box(SIZE_BOX,SIZE_BOX,0);
    PhongMaterial COLOR_DOOR = new PhongMaterial();
    COLOR_DOOR.setDiffuseMap(new Image("/door.png"));
    door.setMaterial(COLOR_DOOR);
    door.setTranslateX(j*SIZE_BOX);
    door.setTranslateZ(i*SIZE_BOX);
    if(nearToWall(maze,i+1,j)&&nearToWall(maze,i-1,j))door.setTranslateZ(12);
    else if(nearToWall(maze,i,j+1)&&nearToWall(maze,i,j-1))door.setTranslateX(12);
  }

  public boolean nearToWall(Maze maze, int i, int j){
    try {
      return maze.getCase(i,j)==Maze.WALL;
    }
    catch (Exception e) {
      return false;
    }
  }*/
  public void setBox(Box cell,int i, int j, Group root,int floor){
    cell.setTranslateX((j+floor)*SIZE_BOX);
    cell.setTranslateZ((i+floor)*SIZE_BOX);
    root.getChildren().add(cell);
  }

  public void drawMonster(Group root,int i, int j,int floor){
    Sphere monster = new Sphere(SIZE_BOX/2);
    PhongMaterial face = new PhongMaterial();
    face.setDiffuseMap(new Image("/face.jpg"));
    monster.setMaterial(face);
    monster.setTranslateZ((i+floor)*SIZE_BOX);
    monster.setTranslateX((j+floor)*SIZE_BOX);
    TranslateTransition move = new TranslateTransition(Duration.millis(2000),monster);
    move.setByZ(SIZE_BOX);
    move.setAutoReverse(true);
    move.setCycleCount(TranslateTransition.INDEFINITE);
    move.play();
    root.getChildren().add(monster);
  }
  public void drawKey(Group root){
    Box key = new Box();
    root.getChildren().add(key);
  }

  public void drawStair(int nbStep,int dir,Group root,int i,int j,int floor){
    Box step;
    int size=SIZE_BOX/nbStep,a=0;
    while(nbStep!=i){
      step=new Box(size,size*(a+1),size);
      step.setMaterial(COLOR_WAY);
      step.setTranslateX((j+floor)*SIZE_BOX);
      step.setTranslateZ((i+floor)*SIZE_BOX+size*a);
      if(dir==0)
        step.setTranslateY(size*(-1)*a);
      else
        step.setTranslateY(size*a);
      i++;
      root.getChildren().add(step);
    }
  }

  public void buildCamera(PerspectiveCamera cam){
    cam.setFarClip(10000.0);
    cam.setNearClip(0.6);
    Point start = maze.beginning();
    int x = (int) start.getX();
    int y = (int) start.getY();
    int posx=x*SIZE_BOX;
    int posz=y*SIZE_BOX;
    cam.setTranslateX(posx);
    cam.setTranslateY(0);
    cam.setTranslateZ(posz);
    cam.setRotationAxis(Rotate.Y_AXIS);
    rotateZ.setAxis(Rotate.Z_AXIS);
    cam.getTransforms().addAll(rotateZ,tx,ty);
    if(posz==0.0){
      cam.setTranslateZ(-1000);
      System.out.println("puo");
    }
    if(posz==SIZE_BOX*(maze.getHeight()-1)){
      cam.setTranslateZ(1000+posz);
      cam.setRotate(180.0);
      System.out.println("puoi");
    }
    if(posx==SIZE_BOX*(maze.getWidth()-1)){
      cam.setTranslateX(1000+posx);
      cam.setRotate(270.0);
      System.out.println("puao");
    }
    if(posx == 0){
      cam.setTranslateX(-600.0);
      cam.setRotate(90.0);
      System.out.println("pu");
    }
    //Box begin2 = cases[y][x];
    //System.out.println("x "+begin2.getTranslateX()+"/ z "+begin2.getTranslateZ()+" box 2");
    System.out.println("x "+cam.getTranslateX()+" / z "+cam.getTranslateZ());
  }
  public void spinLeft(PerspectiveCamera cam){
    cam.setRotate((cam.getRotate()-angle)%360);
  }
  public void spinRight(PerspectiveCamera cam){
    cam.setRotate((cam.getRotate()+angle)%360);
  }
  public void handleKeyBoard(Scene scene,PerspectiveCamera cam){
    scene.setOnKeyPressed(e->{
      switch(e.getCode()){
        case UP:
        //spin(cam);
        if(cam.getRotate()>=0 && cam.getRotate()<90)cam.setTranslateZ(cam.getTranslateZ()+STEP);
        else if(cam.getRotate()>=90 && cam.getRotate()<180)cam.setTranslateX(cam.getTranslateX()+STEP);
        else if(cam.getRotate()>=180 && cam.getRotate()<270)cam.setTranslateZ(cam.getTranslateZ()-STEP);
        else cam.setTranslateX(cam.getTranslateX()-STEP);
        break;
        case DOWN:
        if(cam.getRotate()>=0 && cam.getRotate()<90)cam.setTranslateZ(cam.getTranslateZ()-STEP);
        else if(cam.getRotate()>=90 && cam.getRotate()<180)cam.setTranslateX(cam.getTranslateX()-STEP);
        else if(cam.getRotate()>=180 && cam.getRotate()<270)cam.setTranslateZ(cam.getTranslateZ()+STEP);
        else cam.setTranslateX(cam.getTranslateX()+STEP);
        break;
        case LEFT:
        spinLeft(cam);
        System.out.println(cam.getRotate());
        System.out.println("x ="+cam.getTranslateX()+" /"+"z ="+cam.getTranslateZ());
        break;
        case RIGHT:
        spinRight(cam);
        System.out.println(cam.getRotate());
        System.out.println("x ="+cam.getTranslateX()+" /"+"z ="+cam.getTranslateZ());
        break;
        case S:
        if(e.isControlDown())tx.setX(tx.getX()+STEP);
        else if(e.isShiftDown())tx.setX(tx.getX()-STEP);
        break;
        case H:
        if(e.isControlDown())ty.setY(ty.getY()+STEP);
        else if(e.isShiftDown()) ty.setY(ty.getY()-STEP);
        break;
        case V:
        rotateZ.setAngle(rotateZ.getAngle()+angle);
        break;
      }
    });
  }

  /*public void handleKeyboard(Scene scene, PerspectiveCamera cam){
    scene.setOnKeyPressed(evt->{
      switch(evt.getCode()){
        case SPACE:break;
        case UP:break;
        case DOWN:break;
        case LEFT:break;
        case RIGHT:break;
        case H:break;
        case Q:
          if(evt.isControlDown())System.exit(0);
        case V:

      }
    });
  }*/

  /*public void menuInGame(){

  }

  public void eventMouse(Scene scene, Node root){
    scene.setOnMousePressed(evt->{
      mousePosX = evt.getSceneX();
      mousePosY = evt.getSceneY();
      mouseOldX = evt.getSceneX();
      mouseOldY = evt.getSceneY();
    });

    scene.setOnMouseDragged(e->{
      mouseOldX=mousePosX;
      mouseOldY=mousePosY;
      mousePosX=e.getSceneX();
      mousePosY=e.getSceneY();
      mouseDeltaX = mousePosX-mouseOldX;
      mouseDeltaY = mousePosY-mouseOldY;
      if(e.isPrimaryButtonDown()){
        cam.ry.setAngle(cam.ry.getAngle()-mouseDeltaY);
        cam.rx.setAngle(cam.rx.getAngle()-mouseDeltaX);
      }


    });
  }*/

  public static void main(String[] args) {
    launch(args);
  }
}

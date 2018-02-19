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
import javafx.scene.transform.Rotate;
import javafx.scene.control.Slider;
//import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.collections.ObservableList;

public class Test extends Application{
  public Maze maze;
  PerspectiveCamera cam = new PerspectiveCamera(true);
  public int SIZE_MAZE = 15;
  public int SIZE_BOX = 400;
  public PhongMaterial COLOR_WALL= new PhongMaterial(Color.DARKGREY);
  public PhongMaterial COLOR_WAY= new PhongMaterial(Color.BLACK);
  public double mousePosX,mousePosY,mouseOldX,mouseOldY,mouseDeltaX,mouseDeltaY;
  public Rotate rotateZ;
  public Translate tx,ty;
  public int STEP = 50,SPEED=1;
  public double angle=5.0;
  public Box[][] cases;


  @Override
  public void start(Stage stage) throws FormatNotSupported{
      Group root = new Group();
      maze = new Maze(SIZE_MAZE+10,SIZE_MAZE);
      createMaze(root,maze);
      Scene scene = new Scene(root,500,500,true);
      //setCamToStart(cam);
      //eventMouse(scene,root);
      buildCamera(cam);
      handleKeyBoard(scene,cam);
      root.getChildren().add(cam);
      scene.setCamera(cam);
      stage.setScene(scene);
      stage.show();

  }
  public void createMaze(Group root,Maze maze){
    cases = new Box[maze.getHeight()][maze.getWidth()];
    int hori=0,veri=0;
    for (int i = 0;i<maze.getHeight() ;i++ ) {
      for (int j = 0; j <maze.getWidth() ;j++ ) {
        if(maze.getCase(i,j)==0){
          cases[i][j]= new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
          cases[i][j].setMaterial(COLOR_WALL);
          cases[i][j].setTranslateY(400);
        }
        else if (maze.getCase(i,j)==Maze.START) {
          cases[i][j]= new Box(SIZE_BOX,0,SIZE_BOX);
          cases[i][j].setMaterial(new PhongMaterial(Color.YELLOW));
          cases[i][j].setTranslateY(SIZE_BOX+SIZE_BOX/2);
        }
        else{
          cases[i][j]= new Box(SIZE_BOX,0,SIZE_BOX);
          cases[i][j].setMaterial(COLOR_WAY);
          cases[i][j].setTranslateY(SIZE_BOX+SIZE_BOX/2);
        }
        hori+=SIZE_BOX;
        cases[i][j].setTranslateX(hori);
        cases[i][j].setTranslateZ(veri);
        root.getChildren().add(cases[i][j]);
      }
      hori=0;
      veri+=SIZE_BOX;
    }
    /*Slider s = new Slider(0,360,0);
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

    root.getChildren().addAll(rotate,s,t,g);*/
  }

  public void buildCamera(PerspectiveCamera cam){
    cam.setFarClip(10000.0);
    cam.setNearClip(0.6);
    Point start = maze.beginning();
    int x = (int) start.getX();
    int y = (int) start.getY();
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
  }
  public void spinLeft(PerspectiveCamera cam){
    cam.setRotate(cam.getRotate()+angle);
  }
  public void spinRight(PerspectiveCamera cam){
    cam.setRotate(cam.getRotate()-angle);
  }
  public void handleKeyBoard(Scene scene,PerspectiveCamera cam){
    scene.setOnKeyPressed(e->{
      switch(e.getCode()){
        case UP:
        //spin(cam);
        cam.setTranslateZ(cam.getTranslateZ()+STEP);
        System.out.println(cam.getTranslateZ());
        break;
        case DOWN:
        //spin(cam);
        cam.setTranslateZ(cam.getTranslateZ()-STEP);
        System.out.println(cam.getTranslateZ());
        break;
        case LEFT:
        //spin(cam);
        cam.setTranslateX(cam.getTranslateX()-STEP);
        System.out.println(cam.getTranslateX());
        break;
        case RIGHT:
        //spin(cam);
        cam.setTranslateX(cam.getTranslateX()+STEP);
        System.out.println(cam.getTranslateX());
        break;
        case S:
        System.out.println("angle : " +cam.getRotate());
        spinLeft(cam);
        break;
        case V:
        System.out.println("angle : " +cam.getRotate());
        spinRight(cam);
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

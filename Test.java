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
  PerspectiveCamera cam = new PerspectiveCamera();
  public int SIZE_MAZE = 20;
  public int SIZE_BOX = 400;
  public PhongMaterial COLOR_WALL= new PhongMaterial(Color.DARKGREY);
  public PhongMaterial COLOR_WAY= new PhongMaterial(Color.BLACK);
  public double mousePosX,mousePosY,mouseOldX,mouseOldY,mouseDeltaX,mouseDeltaY,SPEED;


  @Override
  public void start(Stage stage) throws FormatNotSupported{
      Group root = new Group();
      maze = new Maze(SIZE_MAZE,SIZE_MAZE);
      createMaze(root,maze);
      Scene scene = new Scene(root,500,500,true);
      //setCamToStart(cam);
      //eventMouse(scene,root);
      scene.setCamera(cam);
      stage.setScene(scene);
      stage.show();

  }

  public void createMaze(Group root,Maze maze){
    Group rotate = new Group();
    ObservableList<Node> childs= rotate.getChildren();
    Box cases = new Box();
    int hori=0,veri=0;
    for (int i = 0;i<maze.getHeight() ;i++ ) {
      for (int j = 0; j <maze.getWidth() ;j++ ) {
        if(maze.getCase(i,j)==0){
          cases = new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
          cases.setMaterial(COLOR_WALL);
          cases.setTranslateY(400);
        }
        else{
          cases = new Box(SIZE_BOX,0,SIZE_BOX);
          cases.setMaterial(COLOR_WAY);
          cases.setTranslateY(SIZE_BOX+SIZE_BOX/2);
        }
        hori+=SIZE_BOX;
        cases.setTranslateX(hori);
        cases.setTranslateZ(veri);
        childs.add(cases);
      }
      hori=0;
      veri+=SIZE_BOX;
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

    root.getChildren().addAll(rotate,s,t,g);
  }
  /*
  public void setCamToStart(PerspectiveCamera cam){
    Point start = maze.beginning();
    int x = (int) start.getX();
    int y = (int) start.getY();
    cam.setTranslateX(x);
    cam.setTranslateZ(y);
  }
  public void eventKeyboard(Scene scene, Node root){
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
  }

  public void menuInGame(){

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

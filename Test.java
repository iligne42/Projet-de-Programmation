import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
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
import java.awt.geom.Point2D;

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
 PerspectiveCamera camera = new PerspectiveCamera(true);
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
      Scene scene = new Scene(root,1000,700,true);
      setCamToStart(camera);
      //eventMouse(scene,root);
     scene.setCamera(camera);
      stage.setScene(scene);
      stage.show();

  }

  /* public void buildCamera(){
        camera.setTranslateY(SIZE_BOX);
        DoubleProperty x=new SimpleDoubleProperty(position.getX()*SIZE_BOX);
        DoubleProperty z=new SimpleDoubleProperty(position.getY()*SIZE_BOX);
        DoubleProperty angle=new SimpleDoubleProperty(game.player().orientation());
        camera.translateXProperty().bind(x);
        camera.translateZProperty().bind(z);
        camera.rotateProperty().bind(angle);
        System.out.println(camera.getTranslateX()+"   "+camera.getTranslateY()+"    "+camera.getTranslateZ());
    }*/

  public void createMaze(Group root,Maze maze){
    Group rotate = new Group();
    ObservableList<Node> childs= rotate.getChildren();
    Box cases;
    int hori=0,veri=0;
    for (int i = 0;i<maze.getHeight() ;i++ ) {
      for (int j = 0; j <maze.getWidth() ;j++ ) {
        if(maze.getCase(i,j)==0){
          cases = new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
          cases.setMaterial(COLOR_WALL);
          cases.setTranslateY(0);
        }
        else{
          cases = new Box(SIZE_BOX,0,SIZE_BOX);
          cases.setMaterial(COLOR_WAY);
          cases.setTranslateY(SIZE_BOX/2);
        }
        hori+=SIZE_BOX;
        cases.setTranslateX(hori);
        cases.setTranslateZ(veri);
        childs.add(cases);
      }
      hori=0;
      veri+=SIZE_BOX;
    }
   /* Slider s = new Slider(0,360,0);
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
*/
    root.getChildren().addAll(rotate,camera);
  }

 public void setCamToStart(PerspectiveCamera cam){
    Point start = maze.beginning();
    int x = (int) start.getX();
    int y = (int) start.getY();
    camera.setTranslateY(0);
    camera.setTranslateX(-900);
    camera.setTranslateZ(y*400);
    camera.setNearClip(0.1);
    camera.setFarClip(10000);
    //camera.setRotate(-90);
   /* cam.setTranslateX(x);
    cam.setTranslateZ(y);*/
  /* Translate translate=new Translate(x*400,0,y*400);
   Translate t=new Translate(0,0,-100);
   Rotate rotate=new Rotate(180,Rotate.Y_AXIS);
   camera.getTransforms().addAll(t,rotate);*/

  }

  /*public void eventKeyboard(Scene scene, Node root){
    scene.setOnKeyPressed(evt->{
      switch(evt.getCode()){
        case SPACE:break;
        case UP:    break;
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
        camera.ry.setAngle(camera.ry.getAngle()-mouseDeltaY);
        camera.rx.setAngle(camera.rx.getAngle()-mouseDeltaX);
      }


    });
  }
*/
  public static void main(String[] args) {
    launch(args);
  }
}

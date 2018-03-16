import javafx.animation.Interpolator;
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
import javafx.scene.control.Label;
import javafx.scene.shape.Sphere;
import javafx.scene.shape.Cylinder;
import javafx.animation.TranslateTransition;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Translate;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import javafx.scene.Node;
import javafx.collections.ObservableList;
import java.util.LinkedList;
import javafx.fxml.FXMLLoader;
import javafx.animation.RotateTransition;
import javafx.scene.shape.Shape3D;
import java.io.IOException;

public class Test extends Application{
  public Maze maze;
  PerspectiveCamera cam = new PerspectiveCamera(true);
  public int SIZE_MAZE = 15;
  public int SIZE_BOX = 400;
  protected PhongMaterial COLOR_WAY = new PhongMaterial(Color.BLACK);
  protected PhongMaterial COLOR_WALL = new PhongMaterial(Color.GREY);
  public double mousePosX,mousePosY,mouseOldX,mouseOldY,mouseDeltaX,mouseDeltaY;
  public Rotate rotateZ=new Rotate();
  public Translate tx=new Translate(),ty=new Translate();
  public int STEP = 50,SPEED=1;
  public double angle=5.0;
  public Box[][] cases;
  private ObservableList<Node> keyOrBonus;

  @Override
  public void start(Stage stage) throws FormatNotSupported,IOException{
      Group root = new Group();
      Scene scene = new Scene(root,500,500,true);
      maze= new Maze(SIZE_MAZE,SIZE_MAZE,0,0,0,5,6,0);
      createMaze(root,maze,1);
      buildCamera(cam);
      handleKeyBoard(scene,cam);
      root.getChildren().add(cam);
      scene.setCamera(cam);
      stage.setScene(scene);
      stage.show();
  }

  public void createMaze(Group root,Maze maze,int floor) throws IOException{
    int CASE;
    Box cell;
    for (int i = 0;i<maze.getHeight() ;i++ ){
      for (int j = 0; j <maze.getWidth() ;j++ ) {
        CASE = maze.getCase(i,j);
        switch(CASE){
          case Maze.START:
          PhongMaterial COLOR_ENTRY = new PhongMaterial();
          COLOR_ENTRY.setSpecularColor(Color.BLACK);
          cell=makeFloor(COLOR_ENTRY);
          setBox(cell,i,j,root,floor);
          drawKey(root,i,j);
          break;
          case Maze.END:
          PhongMaterial COLOR_END = new PhongMaterial();
          //COLOR_END.setDiffuseMap(new Image("/end.jpg"));
          COLOR_END.setSpecularColor(Color.WHITE);
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
          door.setDiffuseMap(new Image("/safe.jpg"));
          door.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
          cell.setMaterial(door);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.KEY:
          System.out.println(i*SIZE_BOX);
          System.out.println(j*SIZE_BOX);
          drawKey(root,j,i);
          cell = makeFloor(COLOR_WAY);
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
          drawBonus(root,i,j);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.OBSTACLE:
          cell = makeFloor(COLOR_WAY);
          drawObstacle(root,i,j);
          setBox(cell,i,j,root,floor);
          break;
          case Maze.STAIRSUP:
          drawStair(4,1,root,i,j,floor);
          break;
          case Maze.STAIRSDOWN:
          drawStair(4,0,root,i,j,floor);
          break;
          case Maze.WALL:
          PhongMaterial COLOR_WALL = new PhongMaterial();
          COLOR_WALL.setDiffuseMap(new Image("brick.jpg"));
          cell=new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
          cell.setMaterial(COLOR_WALL);
          setBox(cell,i,j,root,floor);
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

  public void setBox(Box cell,int i, int j, Group root,int floor){
    cell.setTranslateX(j*SIZE_BOX);
    cell.setTranslateZ(i*SIZE_BOX);
    root.getChildren().add(cell);
  }

  public void drawMonster(Group root,int i, int j,int floor) throws IOException{
    Monstres last = maze.getMonstres().getLast();
    MeshView ghost = last.initMonster();
    ghost.setTranslateX(j*SIZE_BOX);
    ghost.setTranslateZ(i*SIZE_BOX);
    ghost.setTranslateY(-SIZE_BOX/2);
    root.getChildren().add(ghost);
  }
  public void drawKey(Group root,int posx,int posy) throws IOException{
    FXMLLoader fxmlLoader = new FXMLLoader();
    fxmlLoader.setLocation(this.getClass().getResource("bigkey.fxml"));
    Group key = fxmlLoader.<Group>load();
    PhongMaterial mat = new PhongMaterial();
    mat.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
    mat.setDiffuseColor(Color.YELLOW);
    RotateTransition rt = new RotateTransition(Duration.millis(1000));
    rt.setByAngle(360.0);
    rt.setAxis(Rotate.X_AXIS);
    rt.setCycleCount(TranslateTransition.INDEFINITE);
    rt.setAutoReverse(false);
    rt.setInterpolator(Interpolator.LINEAR);
    for ( Node a : key.getChildren() ) {
      if(a instanceof Shape3D) {
          ((Shape3D)a).setMaterial(mat);
          ((Shape3D)a).setTranslateX(posx*SIZE_BOX);
          ((Shape3D)a).setTranslateZ(posy*SIZE_BOX);
          ((Shape3D)a).setRotationAxis(Rotate.Z_AXIS);;
          ((Shape3D)a).setRotate(180.0);
          rt.setNode(a);
      }
      else a.setVisible(false);
    }
    rt.play();
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

  public void drawObstacle(Group root,int i, int j) throws IOException{
    FXMLLoader fxmlLoader = new FXMLLoader();
    if(maze.getTypeObstacle().equals("Cercle")) {
      fxmlLoader.setLocation(this.getClass().getResource("Spider.fxml"));
    }
    else{
      fxmlLoader.setLocation(this.getClass().getResource("gate.fxml"));
    }
    Group obs = fxmlLoader.load();
    PhongMaterial mat = new PhongMaterial();
    mat.setSpecularColor(Color.BLACK);
    mat.setDiffuseColor(Color.DARKGREY);
    for ( Node n : obs.getChildren() ) {
      if(n instanceof Shape3D) {
        ((Shape3D)n).setMaterial(mat);
        ((Shape3D)n).setTranslateX(i*SIZE_BOX);
        ((Shape3D)n).setTranslateZ(j*SIZE_BOX);
        ((Shape3D)n).setTranslateY(SIZE_BOX/2);
        //rt.setNode(n);
      }
      else n.setVisible(false);
    }
    root.getChildren().add(obs);
  }

  public void drawBonus(Group root, int i, int j) throws IOException{
    FXMLLoader fxmlLoader = new FXMLLoader();
    Bonus last = maze.getBonus().getLast();
    PhongMaterial mat = new PhongMaterial();
    if(last.getAvantage().equals("Piece")) {
      fxmlLoader.setLocation(this.getClass().getResource("Coin.fxml")); //mettre pieces ou bonus temps
      mat.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
      mat.setDiffuseColor(Color.YELLOW);
    }else{
      fxmlLoader.setLocation(this.getClass().getResource("sablier4.fxml"));
      mat.setSpecularColor(Color.MAROON);
      mat.setDiffuseColor(Color.BROWN);
    }
    Group bonus=fxmlLoader.load();
    RotateTransition rt = new RotateTransition(Duration.millis(3000));
    rt.setByAngle(360.0);
    rt.setAxis(Rotate.Y_AXIS);
    rt.setCycleCount(TranslateTransition.INDEFINITE);
    rt.setAutoReverse(false);
    rt.setInterpolator(Interpolator.LINEAR);
    for(Node n : bonus.getChildren()) {
      if (n instanceof Shape3D) {
        ((Shape3D)n).setMaterial(mat);
        ((Shape3D)n).setRotationAxis(Rotate.Z_AXIS);
        ((Shape3D)n).setTranslateX(j * SIZE_BOX);
        ((Shape3D)n).setTranslateZ(i * SIZE_BOX);
        //((Shape3D)n).setTranslateY(-SIZE_BOX / 2);
        ((Shape3D)n).setRotationAxis(Rotate.Z_AXIS);
        rt.setNode(n);
      }
      else n.setVisible(false);
    }
    rt.play();
    root.getChildren().add(bonus);
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
      //System.out.println("puo");
    }
    if(posz==SIZE_BOX*(maze.getHeight()-1)){
      cam.setTranslateZ(1000+posz);
      cam.setRotate(180.0);
      //System.out.println("puoi");
    }
    if(posx==SIZE_BOX*(maze.getWidth()-1)){
      cam.setTranslateX(1000+posx);
      cam.setRotate(270.0);
      //System.out.println("puao");
    }
    if(posx == 0){
      cam.setTranslateX(-600.0);
      cam.setRotate(90.0);
      //System.out.println("pu");
    }
    //Box begin2 = cases[y][x];
    //System.out.println("x "+begin2.getTranslateX()+"/ z "+begin2.getTranslateZ()+" box 2");
    //System.out.println("x "+cam.getTranslateX()+" / z "+cam.getTranslateZ());
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
        System.out.println("Z : "+cam.getTranslateZ()+" X : "+ cam.getTranslateX());
        if(cam.getRotate()>=0 && cam.getRotate()<90)cam.setTranslateZ(cam.getTranslateZ()+STEP);
        else if(cam.getRotate()>=90 && cam.getRotate()<180)cam.setTranslateX(cam.getTranslateX()+STEP);
        else if(cam.getRotate()>=180 && cam.getRotate()<270)cam.setTranslateZ(cam.getTranslateZ()-STEP);
        else cam.setTranslateX(cam.getTranslateX()-STEP);
        break;
        case DOWN:
        System.out.println("Z : "+cam.getTranslateZ()+"X : " +cam.getTranslateX());
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

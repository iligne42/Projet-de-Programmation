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
import javafx.util.Duration;
import javafx.scene.transform.Scale;
import javafx.scene.Node;
import javafx.collections.ObservableList;
import java.util.LinkedList;
import javafx.fxml.FXMLLoader;
import javafx.animation.RotateTransition;
import javafx.scene.shape.Shape3D;
import java.io.IOException;
import javafx.animation.Interpolator;

public class Test extends Application{
//  public Maze maze;
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
  public int posFx,posFz;
  private ObservableList<Node> keyOrBonus;


  @Override
  public void start(Stage stage) throws FormatNotSupported,IOException{
      Group root = new Group();
      Scene scene = new Scene(root,500,500,true);
      MazeFloors mazes = new MazeFloors(SIZE_MAZE,SIZE_MAZE,3,0,2,0,2,0,0);
      doFloor(root,mazes);
      //setCamToStart(cam);
      //eventMouse(scene,root);
      buildCamera(cam,mazes);
      handleKeyBoard(scene,cam);
      root.getChildren().add(cam);
      //drawKey(root,maze);
      scene.setCamera(cam);
      stage.setScene(scene);
      stage.show();
  }
  public void doFloor(Group root, MazeFloors maze) throws IOException{
    LinkedList<Maze> floors = maze.getFloor();
    Group floor = new Group();
    Point before = null;
    Box square = null;
    int i=0,height=floors.getFirst().getHeight(),width=floors.getFirst().getWidth();
    for (Maze a : floors) {
      createMaze(floor,a,i);
      if(before!=null){
        int aposx = (int) a.beginning().getX();
        int aposz = (int) a.beginning().getY();
        floor.setTranslateX((posFx-aposx)*SIZE_BOX);
        floor.setTranslateZ((posFz-aposz)*SIZE_BOX);
        posFx=(int) a.ending().getX()+(posFx-aposx);
        posFz=(int) a.ending().getY()+(posFz-aposz);
        if(before.getX()!=aposx && before.getY()!=aposz){
          setUp(aposx,aposz,height,width,floor,1);
          setUp((int)before.getX(),(int)before.getY(),height,width,floor,-1);
          square=new Box(SIZE_BOX,0,SIZE_BOX);
          square.setTranslateX(aposx*SIZE_BOX);
          square.setTranslateZ(aposz*SIZE_BOX);
          if(aposx==0)square.setTranslateX(square.getTranslateX()-SIZE_BOX);
          else if(aposx==height-1) square.setTranslateX(square.getTranslateX()+SIZE_BOX);
          else if(aposz==0) square.setTranslateZ(square.getTranslateZ()-SIZE_BOX);
          else if(aposz==width-1) square.setTranslateZ(square.getTranslateZ()+SIZE_BOX);
        }
        else if(before.getX()==aposx){
          System.out.println("same x");
          int coeff=(aposx==0)?1:-1;
          System.out.println(coeff);
          floor.setTranslateZ(floor.getTranslateZ()+SIZE_BOX*coeff);
          square = new Box(SIZE_BOX,0,2*SIZE_BOX);
          square.setTranslateX((aposx-coeff)*SIZE_BOX);
          square.setTranslateZ(aposz*SIZE_BOX-(coeff*SIZE_BOX/2));
          /*square.setTranslateX((aposx-coeff*2)*SIZE_BOX);
          square.setTranslateZ((aposz-coeff*2)*SIZE_BOX);*/
          posFz+=coeff;
        }
        else {
          System.out.println(aposz+" / "+before.getY());
          int coeff=(aposz==0)?1:-1;
          System.out.println("same z");
          floor.setTranslateX(floor.getTranslateX()+SIZE_BOX*coeff);
          square = new Box(2*SIZE_BOX,0,SIZE_BOX);
          square.setTranslateX(aposx*SIZE_BOX-(coeff*SIZE_BOX/2));
          square.setTranslateZ((aposz-coeff)*SIZE_BOX);
          /*square.setTranslateX((aposx-coeff*2)*SIZE_BOX);
          square.setTranslateZ((aposz-coeff*2)*SIZE_BOX);*/
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
      root.getChildren().add(floor);
      floor = new Group();
      i+=2;
    }
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
  public void createMaze(Group root,Maze maze,int floor) throws IOException{
    int CASE;
    Box cell;
    for (int i = 0;i<maze.getHeight() ;i++ ){
      for (int j = 0; j <maze.getWidth() ;j++ ) {
        CASE = maze.getCase(i,j);
        switch(CASE){
          case Maze.START:
          PhongMaterial COLOR_ENTRY = new PhongMaterial();
          //COLOR_ENTRY.setDiffuseMap();
          COLOR_ENTRY.setSpecularColor(Color.BLACK);
          cell=makeFloor(COLOR_ENTRY);
          setBox(cell,i,j,root,floor,maze);
          break;
          case Maze.END:
          PhongMaterial COLOR_END = new PhongMaterial();
          //COLOR_END.setDiffuseMap(new Image("/end.jpg"));
          COLOR_END.setSpecularColor(Color.WHITE);
          cell = makeFloor(COLOR_END);
          setBox(cell,i,j,root,floor,maze);
          break;
          case Maze.WAY:
          cell = makeFloor(COLOR_WAY);
          setBox(cell,i,j,root,floor,maze);
          break;
          case Maze.DOOR:
          cell = new Box(SIZE_BOX,SIZE_BOX,SIZE_BOX);
          PhongMaterial door= new PhongMaterial();
          door.setDiffuseMap(new Image("/safe.jpg"));
          door.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
          cell.setMaterial(door);
          setBox(cell,i,j,root,floor,maze);
          break;
          case Maze.KEY:
          drawKey(root,j,i,floor);
          cell = makeFloor(COLOR_WAY);
          setBox(cell,i,j,root,floor,maze);
          break;
          case Maze.TELEPORT:
          cell = makeFloor(COLOR_WAY);
          setBox(cell,i,j,root,floor,maze);
          break;
          case Maze.MONSTRE:
          cell = makeFloor(COLOR_WAY);
          drawMonster(root,i,j,floor,maze);
          setBox(cell,i,j,root,floor,maze);
          break;
          case Maze.BONUS:
          cell = makeFloor(COLOR_WAY);
          drawBonus(root,i,j,maze);
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
          PhongMaterial COLOR_WALL = new PhongMaterial();
          COLOR_WALL.setBumpMap(new Image("brick.jpg"));
          COLOR_WALL.setDiffuseColor(Color.LIGHTGOLDENRODYELLOW);
          COLOR_WALL.setSpecularColor(Color.BLACK);
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
  public void setBox(Box cell,int i, int j, Group root,int floor,Maze maze){
    cell.setTranslateX(j*SIZE_BOX);
    cell.setTranslateZ(i*SIZE_BOX);
    int place = maze.getCase(i,j);
    if(place==Maze.WALL || place== Maze.DOOR || place==Maze.STAIRSUP || place==Maze.STAIRSDOWN)
      cell.setTranslateY((-floor)*SIZE_BOX);
    else
      cell.setTranslateY((-floor)*SIZE_BOX+SIZE_BOX/2);
    root.getChildren().add(cell);
  }

  public void drawMonster(Group root,int i, int j,int floor,Maze maze) throws IOException{
    Monstres last = maze.getMonstres().getLast();
    MeshView ghost = last.initMonster();
    ghost.setTranslateX(j*SIZE_BOX);
    ghost.setTranslateZ(i*SIZE_BOX);
    ghost.setTranslateY((-floor)*SIZE_BOX-SIZE_BOX/2);
    root.getChildren().add(ghost);
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

  public void drawStair(int dir,Group root,int i,int j,Maze maze,int floor){
    Box step;
    int nbStep=4;
    Group stairs = new Group();
    int size=SIZE_BOX/nbStep,a=0;
    while(nbStep!=a){
      step=new Box(SIZE_BOX,size*(a+1),size);
      step.setMaterial(new PhongMaterial(Color.BLUE));
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

  public void drawBonus(Group root, int i, int j, Maze maze) throws IOException {
        //FXMLLoader fxmlLoader = new FXMLLoader();
        Bonus last = maze.getBonus().getLast();
        //PhongMaterial mat = new PhongMaterial();
        /*if (last.getAvantage().equals("Piece")) {
            fxmlLoader.setLocation(this.getClass().getResource("Coin.fxml")); //mettre pieces ou bonus temps
            mat.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
            mat.setDiffuseColor(Color.YELLOW);
        } else {
            fxmlLoader.setLocation(this.getClass().getResource("sablier4.fxml"));
            mat.setSpecularColor(Color.MAROON);
            mat.setDiffuseColor(Color.BROWN);
        }
        Group bonus = fxmlLoader.load();*/
        MeshView bonus = last.initBonus();
        RotateTransition rt = new RotateTransition(Duration.millis(3000));
        rt.setByAngle(360.0);
        rt.setAxis(Rotate.Y_AXIS);
        rt.setCycleCount(TranslateTransition.INDEFINITE);
        rt.setAutoReverse(false);
        rt.setInterpolator(Interpolator.LINEAR);
        /*for (Node n : bonus.getChildren()) {
            if (n instanceof Shape3D) {
                ((Shape3D) n).setMaterial(mat);
                ((Shape3D) n).setRotationAxis(Rotate.Z_AXIS);
                ((Shape3D) n).setTranslateX(j * SIZE_BOX);
                ((Shape3D) n).setTranslateZ(i * SIZE_BOX);
                //((Shape3D)n).setTranslateY(-SIZE_BOX / 2);
                ((Shape3D) n).setRotationAxis(Rotate.Z_AXIS);
                rt.setNode(n);
            } else n.setVisible(false);
        }*/
        bonus.setTranslateX(j * SIZE_BOX);
        bonus.setTranslateZ(i * SIZE_BOX);
        //bonus.setTranslateY((-floor) * SIZE_BOX - SIZE_BOX / 2);
        bonus.setScaleX(bonus.getScaleX()* SIZE_BOX/10);
        bonus.setScaleZ(bonus.getScaleZ()* SIZE_BOX/10);
        bonus.setScaleY(bonus.getScaleY()* SIZE_BOX/10);
        rt.setNode(bonus);
        rt.play();
        root.getChildren().add(bonus);
    }
  /*public void drawBonus(Group root, int i, int j,Maze maze) throws IOException{
    Bonus last = maze.getBonus().getLast();
    MeshView bonus = last.initBonus();
    RotateTransition rt = new RotateTransition(Duration.millis(3000));
    rt.setByAngle(360.0);
    rt.setAxis(Rotate.Y_AXIS);
    rt.setCycleCount(TranslateTransition.INDEFINITE);
    rt.setAxis(Rotate.Y_AXIS);
    rt.setAutoReverse(false);
    rt.setInterpolator(Interpolator.LINEAR);
    bonus.setTranslateX(j*SIZE_BOX);
    bonus.setTranslateZ(i*SIZE_BOX);
    bonus.setTranslateY(-SIZE_BOX/2);
    bonus.setRotationAxis(Rotate.Z_AXIS);
    rt.setNode(bonus);
    rt.play();
    root.getChildren().add(bonus);
  }*/

  public void buildCamera(PerspectiveCamera cam,MazeFloors mazes){
    cam.setFarClip(10000.0);
    cam.setNearClip(0.6);
    Maze maze = mazes.getFloor().getFirst();
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
    }
    if(posz==SIZE_BOX*(maze.getHeight()-1)){
      cam.setTranslateZ(1000+posz);
      cam.setRotate(180.0);
    }
    if(posx==SIZE_BOX*(maze.getWidth()-1)){
      cam.setTranslateX(1000+posx);
      cam.setRotate(270.0);
    }
    if(posx == 0){
      cam.setTranslateX(-600.0);
      cam.setRotate(90.0);
    }
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

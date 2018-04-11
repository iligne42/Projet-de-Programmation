import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.scene.shape.MeshView;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.fxml.FXMLLoader;
import java.io.IOException;

public class Monstres extends Divers{
    //public Maze maze;
    //public Point2D position;
    private DoubleProperty x,y,rX,rY;
    private IntegerProperty orientation;
   // private MonsterAnimation animation;
    private final float speed=2f;


    public Monstres(Maze m){
        super(m);
        put();
        orientation= new SimpleIntegerProperty(0);
        x=new SimpleDoubleProperty(p.getX());
        y=new SimpleDoubleProperty(p.getY());
        rX=new SimpleDoubleProperty(p.getX()*400);
        rY=new SimpleDoubleProperty(p.getY()*400);
    }

    public Monstres(Maze m, Point2D position){
        super(m,position);
        x=new SimpleDoubleProperty(position.getX());
        y=new SimpleDoubleProperty(position.getY());
        rX=new SimpleDoubleProperty(position.getX()*400);
        rY=new SimpleDoubleProperty(position.getY()*400);
        //this.position=position;
        orientation= new SimpleIntegerProperty(0);
    }

    /*private int getX(){
        return (int)p.getX();
    }

    private int getY(){
        return (int)p.getY();
    }*/

    /*public Point2D getPosition(){
        return p;
    }*/

    public DoubleProperty getX(){
      return x;
    }

    public DoubleProperty getY(){
      return y;
    }

    public IntegerProperty getDirec(){
      return orientation;
    }

    public void change(){
      x.set(p.getX());
      y.set(p.getY());
      rX.set(p.getX()*400);
      rY.set(p.getY()*400);
    }

    public void move(double elapsedSeconds){
        double x=p.getX()+Math.cos(Math.toRadians(orientation.get()))*elapsedSeconds;
        double y=p.getY()+Math.sin(Math.toRadians(orientation.get()))*elapsedSeconds;
        Point2D point=new Point2D.Double(x,y);
        if(y<maze.getHeight() && x<maze.getWidth()){
            if(maze.getCase(point)== Maze.WAY){
                maze.free((int)p.getY(),(int)p.getX());
                p=point;
                change();
                maze.fill(Maze.MONSTRE,p);
            }
            else{
                Random rand=new Random();
                int o=rand.nextInt(4);
                if(o==1) orientation.set(90);
                else if(o==2) orientation.set(180);
                else if(o==3) orientation.set(270);
            }
        }
    }

   /* public void move(){
        Random rand=new Random();
        int i=rand.nextInt(3);
        int j=0;
        if(i==0){
            j=rand.nextInt(2);
            if(j==0) j=-1;
        }else if(i==2) i=-1;
        double x=p.getX()+j;
        double y=p.getY()+i;
        Point2D point=new Point2D.Double(x,y);

        int o=rand.nextInt(4);
        if(o==1) orientation.set(90);
        else if(o==2) orientation.set(180);
        else if(o==3) orientation.set(270);
    }

    protected interface MonsterAnimation extends Player.PlayerAnimation{
        void animate(double elapsedSeconds);

        boolean isOver();
    }

    protected class TurningTo implements MonsterAnimation{
        protected int destination;

        public TurningTo(int destination){
            destination=destination;
        }

        public void animate(double elapsedSeconds){
            if(destination>orientation.get() && Math.abs(destination-orientation.get())<=180){
                turnLeft(elapsedSeconds);
            }
        }

        public void turnLeft(double elapsedSeconds){
            orientation.set(orientation+)
        }

        public void turnLeft(double elapsedSeconds){
            angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
            if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
            //orientation=(orientation+angularVelocity*elapsedSeconds)%360;
            orientation=(float)(orientation+orientationSpeed)%360;
            System.out.println("Angle :"+orientation);
            setAcceleration();
        }
    }


    protected class TurningTo implements PlayerAnimation{
        protected int destination;

        public TurningTo(int destination){
            this.destination=destination;
            orientationSpeed=1;
            velocity.setTo(0,0,0);
            System.out.println("Destination: "+destination);
        }

        public void animate(double elapsedSeconds){
            System.out.println(orientation);
            if(destination>orientation && Math.abs(destination-orientation)<=180){
                System.out.println("hmmm");
                turnLeft(elapsedSeconds);
            }
            else turnRight(elapsedSeconds);
        }

        public boolean isOver(){
            return (int)orientation==destination;
        }

    }


    public void move(){
        Random rand=new Random();
        int i=rand.nextInt(3);
        int j=0;
        if(i==0){
            j=rand.nextInt(2);
            if(j==0) j=-1;
        }else if(i==2) i=-1;
        double x=p.getX()+j;
        double y=p.getY()+i;
        Point2D point=new Point2D.Double(x,y);
        if(y<maze.getHeight() && x<maze.getWidth()){
            if(maze.getCase(point)== Maze.WAY){
                maze.free((int)y-i,(int)x-j);
                p=point;
                change();
                maze.fill(Maze.MONSTRE,p);
            }
        }
        int o=rand.nextInt(4);
        if(o==1) orientation.set(90);
        else if(o==2) orientation.set(180);
        else if(o==3) orientation.set(270);
    }*/


    public MeshView initMonster() throws IOException{
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(this.getClass().getResource("ghost.fxml"));
      MeshView ghost = fxmlLoader.<MeshView>load();
      PhongMaterial mat = new PhongMaterial();
      mat.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
      mat.setDiffuseColor(Color.WHITE);
      ghost.setMaterial(mat);
      ghost.setRotationAxis(Rotate.Y_AXIS);
      ghost.translateXProperty().bind(rX);
      ghost.translateZProperty().bind(rY);
      ghost.rotateProperty().bind(orientation);
      return ghost;
    }
}

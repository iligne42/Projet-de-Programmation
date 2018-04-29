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
    private DoubleProperty x,y,rX,rY;
    private IntegerProperty orientation,rOrientation;
    private boolean debug=false;


    public Monstres(Maze m){
        super(m);
        put();
        orientation= new SimpleIntegerProperty(0);
        rOrientation=new SimpleIntegerProperty(90-orientation.get());
        x=new SimpleDoubleProperty(p.getX());
        y=new SimpleDoubleProperty(p.getY());
        rX=new SimpleDoubleProperty(p.getX()*400-200);
        rY=new SimpleDoubleProperty(p.getY()*400-200);
    }

    public Monstres(Maze m, Point2D position){
        super(m,position);
        x=new SimpleDoubleProperty(position.getX());
        y=new SimpleDoubleProperty(position.getY());
        rX=new SimpleDoubleProperty(position.getX()*400-200);
        rY=new SimpleDoubleProperty(position.getY()*400-200);
        //this.position=position;
        orientation= new SimpleIntegerProperty(0);
        rOrientation=new SimpleIntegerProperty(90-orientation.get());
    }

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
      rX.set(p.getX()*400-200);
      rY.set(p.getY()*400-200);
    }

    public void move(double elapsedSeconds){
        double x=p.getX()+Math.cos(Math.toRadians(orientation.get()))*elapsedSeconds;
        double y=p.getY()+Math.sin(Math.toRadians(orientation.get()))*elapsedSeconds;
        if(debug) System.out.println(x+"    "+y);
        if(y<maze.getHeight() && x<maze.getWidth()){
            Point2D point=new Point2D.Double(x,y);
            Point2D rBegin=new Point2D.Double(p.getX()+(int)Math.cos(Math.toRadians(orientation.get())),p.getY()+(int)Math.sin(Math.toRadians(orientation.get())));
            if(debug) System.out.println(rBegin);
           if(!checkCollision(point,rBegin,0.5f,1,1)){
                maze.free((int)p.getY(),(int)p.getX());
                p=point;
                change();
                maze.fill(Maze.MONSTRE,p);
            }
            else {
               if(debug) System.out.println("Collision, je tourne");
                Random rand=new Random();
                int o=rand.nextInt(4);
                if(o==0) orientation.set(0);
                else if(o==1) orientation.set(90);
                else if(o==2) orientation.set(180);
                else if(o==3) orientation.set(270);
                if(debug) System.out.println("Ma nouvelle orientation est "+orientation.get());
               rOrientation.set(90-orientation.get());
            }
        }
        else{
            Random rand=new Random();
            int o=rand.nextInt(4);
            if(o==0) orientation.set(0);
            else if(o==1) orientation.set(90);
            else if(o==2) orientation.set(180);
            else if(o==3) orientation.set(270);
            if(debug) System.out.println("je ne suis pas dans le labyrinthe, je tourne "+orientation.get());
            rOrientation.set(90-orientation.get());
        }
    }

    public boolean checkCollision(Point2D center,Point2D rPoint,float radius,float width,float height) {
                if (isInBounds(rPoint) && maze.getCase(rPoint) != Maze.WAY) {
                    if(debug) System.out.println("Test de collision");
                    Point2D rBeginning=new Point2D.Double(Math.floor(rPoint.getX()),Math.floor(rPoint.getY()));
                    double deltaX = center.getX() - Math.max(rBeginning.getX(), Math.min(center.getX(), rBeginning.getX() + width));
                    double deltaY = center.getY() - Math.max(rBeginning.getY(), Math.min(center.getY(), rBeginning.getY() + height));
                    if (((float) (deltaX * deltaX + deltaY * deltaY)) < (radius * radius)) return true;
                }
                if(debug) System.out.println("Pas de collision");
        return false;
    }

    public boolean isInBounds(Point2D p) {
        return  p.getX() >= 0 && p.getY() >= 0 && p.getX() < maze.getWidth() && p.getY() < maze.getHeight();
    }



    public MeshView initMonster() throws IOException{
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(this.getClass().getResource("fxml/ghost.fxml"));
      MeshView ghost = fxmlLoader.<MeshView>load();
      PhongMaterial mat = new PhongMaterial();
      mat.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
      mat.setDiffuseColor(Color.WHITE);
      ghost.setMaterial(mat);
      ghost.setRotationAxis(Rotate.Y_AXIS);
      ghost.translateXProperty().bind(rX);
      ghost.translateZProperty().bind(rY);
      ghost.rotateProperty().bind(rOrientation);
      return ghost;
    }
}

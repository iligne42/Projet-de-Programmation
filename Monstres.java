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
    private DoubleProperty x,y;
    private IntegerProperty orientation;


    public Monstres(Maze m){
        super(m);
        put();
        orientation= new SimpleIntegerProperty(0);
    }

    public Monstres(Maze m, Point2D position){
        super(m,position);
        x=new SimpleDoubleProperty(position.getX());
        y=new SimpleDoubleProperty(position.getY());
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
    }

    public MeshView initMonster() throws IOException{
      FXMLLoader fxmlLoader = new FXMLLoader();
      fxmlLoader.setLocation(this.getClass().getResource("ghost.fxml"));
      MeshView ghost = fxmlLoader.<MeshView>load();
      PhongMaterial mat = new PhongMaterial();
      mat.setSpecularColor(Color.LIGHTGOLDENRODYELLOW);
      mat.setDiffuseColor(Color.WHITE);
      ghost.setMaterial(mat);
      ghost.setRotationAxis(Rotate.Y_AXIS);
      return ghost;
    }
}

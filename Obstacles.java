import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Random;
//import javafx.scene.shape.TriangleMesh;

public class Obstacles extends Divers{
    private Shape s;

    public Obstacles(Maze m, String type){
        super(m);
        if(type.equals("Rectangle")){
            s=new Rectangle(0.5,0.5);
        }else{
            s=new Circle(0.5);
        }
        put();
    }

    public String getShape(){
        if(s instanceof Rectangle) return "Rectangle";
        else return "Cercle";
    }

    public Shape shape(){
        return s;
    }

    /*private void put(Point2D p){
        Random rand=new Random();
        int i=0; int j=0;
        while(maze.getCase(i,j)!=Maze.WAY ) {
            i = rand.nextInt(maze.getHeight());
            j = rand.nextInt(maze.getWidth());
        }
        double k=centrer(i);
        double l=centrer(j);
        p.setLocation(l,k);
    }*/


}

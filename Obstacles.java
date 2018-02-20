import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;

import java.awt.geom.Point2D;
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

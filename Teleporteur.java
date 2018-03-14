import javafx.scene.shape.Circle;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
import java.lang.Double;
public class Teleporteur extends Divers{
    //private Maze maze;
    //private static Point2D start; p==start
    private static Point2D end;
    private Circle circleS;
    private Circle circleE;

    public Teleporteur(Maze m){
        super(m);
        //start=new Point2D.Double();
        end=new Point2D.Double();
        put();
        put(end);
        while(end.equals(p)) {
            put(end);
        }
        circleS=new Circle();
        circleE=new Circle();
        doCircle(p,end);
    }

    public Teleporteur(Maze m, Point2D start, Point2D end){
        super(m);
        this.p=end;
        this.end=start;
    }

    public Point2D getStart() {
        return p;
    }

    public Point2D getEnd() {
        return end;
    }

    private void put(Point2D p){
        Random rand=new Random();
        int i=0; int j=0;
        while(maze.getCase(i,j)!=Maze.WAY) {
            j = rand.nextInt(maze.getWidth());
            i = rand.nextInt(maze.getHeight());
        }
        double k=centrer(i);
        double l=centrer(j);
        p.setLocation(l,k);
    }

    private void doCircle(Point2D s, Point2D e){
        circleS.setCenterX(s.getX());
        circleS.setCenterY(s.getY());
        circleS.setRadius(0.5);
        circleE.setCenterX(e.getX());
        circleE.setCenterY(e.getY());
        circleE.setRadius(0.5);
    }
}

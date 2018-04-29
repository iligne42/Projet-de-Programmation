import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Key extends Divers{
    private Door door;

    public Key(Maze m, Door door){
        super(m);
        this.door=door;
        beforeDoor();
    }

    public Door getDoor(){
        return this.door;
    }

    public int getX(){return (int)p.getX();}
    public int getY(){return (int)p.getY();}
    //public Point2D getPlace(){return p;}

    private static int inBetween(int x, int y){
        Random rand=new Random();
        return ((x==y)?x:rand.nextInt(max(x,y)-min(x,y))+min(x,y));
    }

    public void beforeDoor() {
        int i = 0;
        int j = 0;
        Point b=maze.beginning();
        int yb=(int)b.getY();
        int xb=(int)b.getX();
        do{
            i=inBetween(yb,door.getY());
            j=inBetween(xb,door.getX());
        } while(maze.getCase(i,j)!=Maze.WAY || (i==door.getY() && j==door.getX()));
        double k=centrer(i);
        double l=centrer(j);
        p.setLocation(l,k);
    }

    public static void main(String[]args){
    }
}

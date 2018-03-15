import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

import static java.lang.Math.abs;
import static java.lang.Math.max;
import static java.lang.Math.min;

public class Key extends Divers{
    //private Maze maze;
    private Door door;
    //private Point2D p;

    public Key(Maze m, Door door){
        super(m);
        this.door=door;
        //p=new Point2D.Double();
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
        return rand.nextInt(max(x,y)-min(x,y))+min(x,y);
    }

    public void beforeDoor() {
        //Random rand = new Random();
        int i = 0;
        int j = 0;
        Point b=maze.beginning();
        int yb=(int)b.getY();
        int xb=(int)b.getX();
        //int incr=0;
        //System.out.println(yb+","+xb);
        do{
            //System.out.println(door.getX());
            i=inBetween(yb,door.getY());
            //System.out.println("Le x est:"+i);
            j=inBetween(xb,door.getX());
            //System.out.println("Le y est:"+j);
            //incr++;
        } while(maze.getCase(i,j)!=Maze.WAY || (i==door.getY() && j==door.getX()));
        //System.out.println("La clé:"+j+","+i);
        double k=centrer(i);
        double l=centrer(j);
        p.setLocation(l,k);        
    }

    public static void main(String[]args){
        /*System.out.println(inBetween(6,1));
        System.out.println(inBetween(6,1));
        System.out.println(inBetween(6,1));
        System.out.println(inBetween(6,1));
        System.out.println(inBetween(6,1));*/
    }
}

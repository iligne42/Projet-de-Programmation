import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.*;
public class Monstres extends Divers{
    //public Maze maze;
    //public Point2D position;
    private int orientation;


    public Monstres(Maze m){
        super(m);
        put();
        orientation=0;
    }

    public Monstres(Maze m, Point2D position){
        super(m,position);
        //this.position=position;
        orientation=0;
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
                maze.fill(Maze.MONSTRE,p);
            }
        }
        int o=rand.nextInt(4);
        if(o==1) orientation+=90;
        else if(o==2) orientation+=180;
        else if(o==3) orientation+=270;
    }
}
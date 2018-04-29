import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class Door extends Divers{
    private Key key;

    public Door(Maze m){
        super(m);
        place();
        key=new Key(maze,this);
    }

    public Door(Maze m, Point p){
        super(m,p);
        key=new Key(maze,this);
    }

    public int getX(){return (int)p.getX();}
    public int getY(){return (int)p.getY();}
    public Point2D getKeyPlace(){return key.getPosition();}
    public Key getKey(){return key;}

    public boolean isTheKey(Point2D point){
        p=getKeyPlace();
        return p.getX()==point.getX() && p.getY()==point.getY();
    }

    public boolean isTheKey(int i, int j){
       return (key.getX()==i && key.getY()==j);
    }


    private void place(){
        Random rand=new Random();
        int i=0; int j=0;
        while(maze.getCase(i,j)!=Maze.WAY || tooClose(i,j)) {
            i = rand.nextInt(maze.getHeight());
            j = rand.nextInt(maze.getWidth());
        }
        double k=centrer(i);
        double l=centrer(j);
        p.setLocation(l,k);
    }

    public void forget(){
        p=null;
    }

    private boolean tooClose(int i, int j){
        Point b=maze.beginning();
        int x=(int)b.getX();
        int y=(int)b.getY();
        if(x==j || x+1==j || x-1==j) return true;
        if(y==i || y+1==i || y-1==i) return true;
        return false;
    }
}

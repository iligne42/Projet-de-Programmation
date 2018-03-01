import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

public class Door extends Divers{
    //private Maze maze;
    private Key key;
    //private Point2D p;

    public Door(Maze m){
        //System.out.println("on est dans le constructeur de porte");
        super(m);
        //p=new Point();
        place();
        key=new Key(maze,this);
    }

    public Door(Maze m, Point p){
        super(m,p);
        //this.p=p;
        key=new Key(maze,this);
    }

    public int getX(){return (int)p.getX();}
    public int getY(){return (int)p.getY();}
    //public Point2D getPlace(){return p;}
    public Point2D getKeyPlace(){return key.getPosition();}
    public Key getKey(){return key;}

    public boolean isTheKey(Point2D point){
      return (getKeyPlace().equals(point));
    }
    private void place(){
        Random rand=new Random();
        int i=0; int j=0;
        while(maze.getCase(i,j)!=Maze.WAY || tooClose(i,j)) {
            i = rand.nextInt(maze.getHeight());
            j = rand.nextInt(maze.getWidth());
        }
        //System.out.println("La porte:"+j+","+i);
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

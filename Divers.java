import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.Random;

public abstract class Divers implements Serializable{
    protected Maze maze;
    protected Point2D p;

    public Divers(Maze m){
        maze=m;
        p=new Point2D.Double();
    }

    public Divers(Maze m, Point2D p){
        maze=m;
        this.p=new Point2D.Double(centrer((int)p.getX()),centrer((int)p.getY()));
    }

    protected double centrer(int i){
        return (double) i+0.5;
    }

    public Point2D getPosition(){
        return p;
    }

    protected void put(){
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

}

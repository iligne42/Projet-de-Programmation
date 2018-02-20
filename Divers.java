import java.awt.geom.Point2D;
import java.util.Random;

public abstract class Divers {
    public Maze maze;
    public Point2D p;

    public Divers(Maze m){
        maze=m;
        p=new Point2D.Double();
    }

    public Divers(Maze m, Point2D p){
        maze=m;
        this.p=p;
    }

    public double centrer(int i){
        return (double) i+0.5;
    }


    public Point2D getPosition(){
        return p;
    }


    public void put(){ //en faire un boolean au cas où il n'y a plus de possibilité?
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

   /* public boolean obstacle(){return false;}
    public boolean door(){return false;}
    public boolean key(){return false;}
    public boolean telepot(){return false;}
    public boolean montre(){return false;}*/
   //String avec le type de divers??
}

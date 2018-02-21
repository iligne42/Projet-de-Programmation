import java.awt.geom.Point2D;

public abstract class Bonus extends Divers{
    protected String avantage;

    public Bonus(Maze m, String a){
        super(m);
        avantage=a;
        put();
    }

    public String getAvantage(){return avantage;}
}

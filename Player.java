import javafx.geometry.Point2D;

import java.io.Serializable;


public class Player implements Serializable{
    protected final String name;
    protected Point2D position;
    protected int orientation;

    public Player(String name){
        this.name=name;
        position=null;
       // position=new Point(0,0);
    }

    public void setPosition(Point2D p,int ori){
        position=p;
        orientation=ori;
    }

    public int orientation(){
        return orientation;
    }

    public Point2D getPosition(){
        return position;
    }
}
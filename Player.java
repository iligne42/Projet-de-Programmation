import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Cylinder;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
public class Player implements Serializable{
	private String name;
	private Point2D position;
	//private Cylinder body;
	private Circle shape;
	private int orientation;
	private float speed;
	private int orientationSpeed;
	private LinkedList<Bonus> bonus;
	private LinkedList<Key> keys;

	public Player(String name){
		this.name=name;
		position=null;
		orientation=0;
		speed=(float)0.5;
		orientationSpeed=5;
		bonus=new LinkedList<>();
		keys=new LinkedList<>();
		shape=new Circle(0.1);
		//body=new Cylinder()
		//shape.setFill(Color.BLUE);
	}

	public double radius(){
		return shape.getRadius();
	}

	public void setPosition(Point2D position, int ori){
		this.position=position;
		this.orientation=ori;
		shape.setCenterX(position.getX());
		shape.setCenterY(position.getY());
	}

	public void setSpeed(float s){
		speed=s;
	}

	public String getName(){ return name;}
	public Point2D getPosition(){ return position;}
	public int orientation(){ return orientation;}

	public void moveForward(){
		position.setLocation(position.getX()+Math.cos(Math.toRadians(orientation))*speed, position.getY()+Math.sin(Math.toRadians(orientation))*speed);
		//System.out.println(Math.cos(Math.PI/2)*speed);
		//System.out.println(position.getX()+"   "+position.getY());
		//System.out.println(position);
	}

	public void moveBackward(){
		position.setLocation(position.getX()+Math.cos(Math.toRadians(orientation))*(-speed), position.getY()+Math.sin(Math.toRadians(orientation))*(-speed));
	}

	public void moveLeft(){
		orientation=(orientation+orientationSpeed)%360;
	}

	public void moveRight(){
		orientation=(orientation+360-orientationSpeed)%360;
	}

    public void pickUp(Key key){
        keys.add(key);
    }

    public void pickUp(Bonus bonuss){
        bonus.add(bonuss);
    }

    public boolean openDoor(Point2D point){
        if(maze.getCase(point)!=Maze.DOOR)return true;
        else{
            Door door = maze.getDoor(point);
            for ( Key a : keys){
                if(door.getKey().equals(a)) return true;
            }
            return false;
        }
    }

	public static void main(String[] args) {
		Player p=new Player("Pierre");
		p.setPosition(new Point2D.Float(1.F,1.F),45);
		p.moveForward();
		//p.moveBackward();
		p.moveLeft();
		p.moveRight();
		System.out.println( p.getPosition());
		System.out.println(p.orientation());
	}
}
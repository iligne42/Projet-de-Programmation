import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
public class Player implements Serializable{
	private String name;
	private Point2D position;
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
		orientationSpeed=45;
		bonus=new LinkedList<>();
		keys=new LinkedList<>();
	}

	public void setPosition(Point2D position, int ori){
		this.position=position;
		this.orientation=ori;
	}

	public void setSpeed(float s){
		speed=s;
	}

	public String getName(){ return name;}
	public Point2D getPosition(){ return position;}
	public int orientation(){ return orientation;}

	public void moveForward(){
		position.setLocation(position.getX()+Math.cos(Math.toRadians(orientation))*speed, position.getY()+Math.sin(Math.toRadians(orientation))*speed);
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
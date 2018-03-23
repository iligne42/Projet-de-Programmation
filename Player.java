import javafx.scene.shape.Circle;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;

public class Player implements Serializable {
	private String name;
	private double posX, posZ;
	private Vector3D velocity,acceleration;
	//représenter la pente de l'escalier par un vecteur et ajouter peu à peu au joueur, enfin à sa vitesse je crois

	private float maxSpeed,accConstant,angularVelocity,orientationSpeed,orientation;
	private Circle shape;
	private boolean up,down,left,right;
	private LinkedList<Bonus> bonus;
	private LinkedList<Key> keys;

	public Player(String name){
		this.name=name;
		orientation=0;
		posX=posZ=0;
		velocity=new Vector3D();
		acceleration=new Vector3D();
		maxSpeed=1.5f;
		accConstant=3;
		orientationSpeed=2;
		bonus=new LinkedList<>();
		keys=new LinkedList<>();
		shape=new Circle(0.15);
		//body=new Cylinder()
		//shape.setFill(Color.BLUE);
	}

	public float radius(){
		return (float)shape.getRadius();
	}

	public void setPosition(Point2D position, float ori){
		//this.position=position;
		posX=position.getX();
		posZ=position.getY();
		this.orientation=ori;
		shape.setCenterX(posX);
		shape.setCenterY(posZ);
	}

	public void setMaxSpeed(float s){
		maxSpeed=s;
	}

	public String getName(){ return name;}
	public Point2D getPosition(){
		return new Point2D.Double(posX,posZ);
	}
	//public Point2D getPosition(){ return position;}
	public float orientation(){ return orientation;}

	public LinkedList<Key> keys() {
		return keys;
	}

	public LinkedList<Bonus> getBonus() {
		return bonus;
	}

	public void down(boolean b) {
		down = b;
	}

	public void up(boolean b) {
		up = b;
	}

	public void left(boolean b) {
		left = b;
	}

	public void right(boolean b) {
		right = b;
	}

	public void setAcceleration(){
		acceleration.setTo(Math.cos(Math.toRadians(orientation))*accConstant,0,Math.sin(Math.toRadians(orientation))*accConstant);
	}

	public void updatePosition(double elapsedSeconds){
		if(up) {
			velocity=velocity.add(acceleration.multiply(elapsedSeconds));
			if(velocity.norm()>maxSpeed) velocity=velocity.posColinear(maxSpeed);
		}
		else if(down) {
			velocity=velocity.subtract(acceleration.multiply(elapsedSeconds));
			if(velocity.norm()>maxSpeed) velocity=velocity.posColinear(maxSpeed);
		}
		else if(left) {
			angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
			if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
			orientation=(orientation+angularVelocity)%360;
			setAcceleration();
		}
		else if(right) {
			angularVelocity+=(float)(360-orientationSpeed*elapsedSeconds);
			if(angularVelocity<360-orientationSpeed)angularVelocity=(float)(360-orientationSpeed);
			orientation=(orientation+angularVelocity)%360;
			setAcceleration();
		}
		else {
			velocity.setTo(0,0,0);
			angularVelocity=0;
		}
		posX+=velocity.x()*elapsedSeconds;
		posZ+=velocity.z()*elapsedSeconds;
		//System.out.println("x="+posX+" y="+posZ);

	}


	public void pickUp(Key key){
		keys.add(key);
	}

	public void pickUp(Bonus bonuss){
		bonus.add(bonuss);
	}



/*import javafx.scene.paint.Color;
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
	private float orientation;
	private float maxSpeed;
	private int orientationSpeed;
	private LinkedList<Bonus> bonus;
	private LinkedList<Key> keys;

	public Player(String name){
		this.name=name;
		position=null;
		orientation=0;
		maxSpeed=(float)0.5;
		orientationSpeed=5;
		bonus=new LinkedList<>();
		keys=new LinkedList<>();
		shape=new Circle(0.15);
		//body=new Cylinder()
		//shape.setFill(Color.BLUE);
	}

	public float radius(){
		return (float)shape.getRadius();
	}

	public void setPosition(Point2D position, float ori){
		this.position=position;
		this.orientation=ori;
		shape.setCenterX(position.getX());
		shape.setCenterY(position.getY());
	}

	public void setSpeed(float s){
		maxSpeed=s;
	}

	public String getName(){ return name;}
	public Point2D getPosition(){ return position;}
	public float orientation(){ return orientation;}

	public LinkedList<Key> keys() {
		return keys;
	}

	public LinkedList<Bonus> getBonus() {
		return bonus;
	}



	public void moveForward(){
		position.setLocation(position.getX()+Math.cos(Math.toRadians(orientation))*maxSpeed, position.getY()+Math.sin(Math.toRadians(orientation))*maxSpeed);
		//System.out.println(Math.cos(Math.PI/2)*maxSpeed);
		//System.out.println(position.getX()+"   "+position.getY());
		//System.out.println(position);
	}

	public void moveBackward(){
		position.setLocation(position.getX()+Math.cos(Math.toRadians(orientation))*(-maxSpeed), position.getY()+Math.sin(Math.toRadians(orientation))*(-maxSpeed));
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
*/

		/* import javafx.scene.shape.Circle;
		 import java.awt.geom.Point2D;
		 import java.io.Serializable;
		 import java.util.*;
public class Player implements Serializable {
	private String name;
	private Point2D position;
	private float posX;
	private float posZ;
	private float velocityX;
	private float velocityZ;
	private float accelerationX;
	private float accelerationZ;
	private float angularVelocity;
	private float angularVelocityX;
	private float angularVelocityZ;
	private float angularAccX;
	private float angularAccZ;
	private Circle shape;
	private float orientation;
	private float maxSpeed;
	private float maxAcc;
	private int angAcc;
	private int orientationSpeed;
	private boolean up,down,left,right;
	private LinkedList<Bonus> bonus;
	private LinkedList<Key> keys;

	public Player(String name){
		this.name=name;
		position=null;
		orientation=0;
		velocityX=velocityZ=0;
		accelerationX=accelerationZ=0;
		angularVelocityZ=angularVelocityX=0;
		maxSpeed=(float)0.5;
		maxAcc=0.5f;
		orientationSpeed=5;
		bonus=new LinkedList<>();
		keys=new LinkedList<>();
		shape=new Circle(0.15);
		//body=new Cylinder()
		//shape.setFill(Color.BLUE);
	}

	public float radius(){
		return (float)shape.getRadius();
	}

	public void setPosition(Point2D position, float ori){
		//this.position=position;
		posX=(float)position.getX();
		posZ=(float)position.getY();
		this.orientation=ori;
		shape.setCenterX(position.getX());
		shape.setCenterY(position.getY());
	}

	public void setSpeed(float s){
		maxSpeed=s;
	}

	public String getName(){ return name;}
	public Point2D getPosition(){
		return new Point2D.Double(posX,posZ);
	}
	//public Point2D getPosition(){ return position;}
	public float orientation(){ return orientation;}

	public LinkedList<Key> keys() {
		return keys;
	}

	public LinkedList<Bonus> getBonus() {
		return bonus;
	}

	public void down(boolean b) {
		down = b;
	}

	public void up(boolean b) {
		up = b;
	}

	public void left(boolean b) {
		left = b;
	}

	public void right(boolean b) {
		right = b;
	}

	public void moveForward(){
		velocityX=(float)Math.cos(Math.toRadians(orientation))*maxSpeed;
		velocityZ=(float)Math.sin(Math.toRadians(orientation))*maxSpeed;
		angularVelocity=0;
	}

	public void moveBackward(){
		velocityX=(float)Math.cos(Math.toRadians(orientation))*(-maxSpeed);
		velocityZ=(float)Math.sin(Math.toRadians(orientation))*(-maxSpeed);
		angularVelocity=0;
	}

	public void moveLeft(){
		angularVelocity=orientationSpeed;
		velocityX=velocityZ=0;
	}
	public void moveRight(){
		angularVelocity=360-orientationSpeed;
		velocityX=velocityZ=0;
	}

	public void updatePosition(double elapsedSeconds){
		accelerationX=(float)Math.cos(Math.toRadians(orientation))*maxAcc;
		accelerationZ=(float)Math.sin(Math.toRadians(orientation))*maxAcc;
		if(up) {
			velocityX+=accelerationX*elapsedSeconds;
			velocityZ+=accelerationZ*elapsedSeconds;

		}
		else if(down) {
			velocityX += (-accelerationX) * elapsedSeconds;
			velocityZ += (-accelerationZ) * elapsedSeconds;

			/*velocityX = -velocityX;
			velocityZ = -velocityZ;


		}
		else if(left) {
			angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
			System.out.println(left);
			/*angularAccX=(float)Math.cos(Math.toRadians(orientation))*angAcc;
			angularAccZ=(float)Math.sin(Math.toRadians(orientation))*angAcc;
		}
		else if(right) {
			angularVelocity+=(float)(360-orientationSpeed*elapsedSeconds);
			/*angularAccX=-angularAccX;
			angularAccZ=-angularAccZ;
		}
		else {
			//accelerationX = -accelerationX;
			//accelerationZ = -accelerationZ;
			//velocityX+=accelerationX*elapsedSeconds;
			//velocityZ+=accelerationZ*elapsedSeconds;
			velocityZ=velocityX=0;
			angularVelocity=0;
		}
		float a=posX;
		float b=posZ;
		posX+=velocityX*elapsedSeconds;
		posZ+=velocityZ*elapsedSeconds;
		if(up) System.out.println("X="+(posX-a)+" Y="+(posZ-b));
		orientation=(float)(orientation+angularVelocity)%360;

		//position.setLocation(position.getX()+velocityX*elapsedSeconds,position.getY()+velocityZ*elapsedSeconds);
		//orientation=(float)(orientation+angularVelocity*elapsedSeconds)%360;
	}


	public void pickUp(Key key){
		keys.add(key);
	}

	public void pickUp(Bonus bonuss){
		bonus.add(bonuss);
	}
*/

}






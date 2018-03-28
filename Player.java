import javafx.geometry.Point3D;
import javafx.scene.shape.Circle;

import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;

public class Player implements Serializable {
	private String name;
	private double posX,posY, posZ;
	private int ground;
	private PlayerState state;
	private PlayerState previousState;
	private Vector3D velocity,acceleration;
	//représenter la pente de l'escalier par un vecteur et ajouter peu à peu au joueur, enfin à sa vitesse je crois

	private float maxSpeed,accConstant,angularVelocity,gravity,friction,orientationSpeed,orientation;
	private Circle shape;
	private boolean up,down,left,right,space;
	private LinkedList<Bonus> bonus;
	private LinkedList<Key> keys;

	public enum PlayerState{
		 BETWEEN,GROUND,STAIRSUP,STAIRSDOWN,DEAD;
	}

	public Player(String name){
		this.name=name;
		orientation=0;
		posX=posZ=posY=0;
		ground=0;
		state=PlayerState.GROUND;
		velocity=new Vector3D();
		acceleration=new Vector3D();
		maxSpeed=3f;
		accConstant=3;
		gravity=1.5f;
		friction=3;
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

	public void setPosition(Point2D position, double y,float ori){
		posX=position.getX();
		posY=y;
		posZ=position.getY();
		this.orientation=ori;
		shape.setCenterX(posX);
		shape.setCenterY(posZ);
	}


	public void setPosition(Point2D position, float ori){
		setPosition(position,posY,ori);
	}

	public void setMaxSpeed(float s){
		maxSpeed=s;
	}

	public String getName(){ return name;}
	public Point2D getPosition(){
		return new Point2D.Double(posX,posZ);
	}


	public double getY(){
		return posY;
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

	public void jump(boolean b){
		space=b;
	}

	public void changeState(PlayerState s){
	    previousState=state;
		state=s;
		setAcceleration();
	}

	public void reverseState(PlayerState s){
		switch(s){
			case STAIRSUP:changeState(PlayerState.STAIRSDOWN);
			break;

			case STAIRSDOWN:changeState(PlayerState.STAIRSUP);
			break;

			default:break;
		}
	}

	public PlayerState state() {
		return state;
	}

    public PlayerState previousState() {
        return previousState;
    }

    public void setAcceleration(){
		switch(state){

			case STAIRSUP:acceleration.setTo(Math.cos(Math.toRadians(orientation)),1,Math.sin(Math.toRadians(orientation)));
				break;

			case STAIRSDOWN:acceleration.setTo(Math.cos(Math.toRadians(orientation)),-1,Math.sin(Math.toRadians(orientation)));
				break;

            case BETWEEN:acceleration.setTo(Math.cos(Math.toRadians(orientation)),0,Math.sin(Math.toRadians(orientation)));
                break;

			default:acceleration.setTo(Math.cos(Math.toRadians(orientation))*accConstant,0,Math.sin(Math.toRadians(orientation))*accConstant);
				break;
		}

	}

	public void jump(){
		acceleration.setTo(0,-accConstant,0);
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
		else if(space){
			jump();
			velocity=velocity.add(acceleration.multiply(elapsedSeconds));
			if(velocity.norm()>maxSpeed) velocity=velocity.posColinear(maxSpeed);
		}
		else {
				//velocity=velocity.add(new Vector3D(0,gravity,0).multiply(elapsedSeconds));
				/*if(velocity.y()<ground){
					velocity.setTo(velocity.x(),0,velocity.z());
					changeState(previousState);
					//posY=position du sol
				}

			}*/
			velocity.setTo(0,0,0);

			angularVelocity=0;
		}
		posX+=velocity.x()*elapsedSeconds;
		posY+=velocity.y()*elapsedSeconds;
		posZ+=velocity.z()*elapsedSeconds;
		//System.out.println("x="+posX+" y="+posZ);

	}


	public void pickUp(Key key){
		keys.add(key);
	}

	public void pickUp(Bonus bonuss){
		bonus.add(bonuss);
	}




}






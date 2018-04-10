import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MeshView;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;

public class Player implements Serializable {
	private String name;
	private double posX,posY, posZ;
	private int ground;
	private int currentMaze;
	private PlayerState state;
	private PlayerState previousState;
	private PlayerAnimation animation;
	private Vector3D velocity,acceleration;
	//représenter la pente de l'escalier par un vecteur et ajouter peu à peu au joueur, enfin à sa vitesse je crois

	private float maxSpeed,accConstant,angularVelocity,gravity,friction,orientationSpeed,orientation,jumpVelocity,orientationX;
	private float radius;
	private boolean up,down,left,right,space,pickedUp;
	private LinkedList<Bonus> bonus;
	private LinkedList<Key> keys;

	public enum PlayerState{
		 BETWEEN,GROUND,STAIRSUP,STAIRSDOWN,DEAD,JUMPING,FALLING;
	}

	protected interface PlayerAnimation{
            void animate(double elapsedSeconds);

            boolean isOver();
    }

    protected class Falling implements PlayerAnimation{

	    public Falling(){
            setGround(0);
            state=null;
            System.out.println("Pb");

        }

	    public void animate(double elapsedSeconds){
	        applyGravity(elapsedSeconds);
            if (posY < ground) {
                posY=ground;
                velocity.setTo(0, 0, 0);
                changeState(PlayerState.DEAD);
            }
        }

        public boolean isOver(){
	        return state==PlayerState.DEAD;
        }

    }

    protected class TurningTo implements PlayerAnimation{
	    protected int destination;

	    public TurningTo(int destination){
	        this.destination=destination;
            orientationSpeed=1;
            velocity.setTo(0,0,0);
            System.out.println("Destination: "+destination);
        }

        public void animate(double elapsedSeconds){
	        System.out.println(orientation);
	        if(destination>orientation && Math.abs(destination-orientation)<=180){
	            System.out.println("hmmm");
	            turnLeft(elapsedSeconds);
            }
	        else turnRight(elapsedSeconds);
        }

        public boolean isOver(){
	        return (int)orientation==destination;
        }

    }

    protected class LookingTo implements PlayerAnimation{
	    protected int destination;

	    public LookingTo(int destination){
            this.destination=destination;
            velocity.setTo(0,0,0);
            System.out.println("Destination: "+destination);
        }

        public void animate(double elapsedSeconds){
            System.out.println(orientationX);
            if(destination>orientationX && Math.abs(destination-orientationX)<=180) lookUp(elapsedSeconds);
            else lookDown(elapsedSeconds);
        }

        public boolean isOver(){
            return (int)orientationX==destination;
        }
    }

    protected class MovingTo implements PlayerAnimation{
	    protected double destX,destZ;

	    public MovingTo(double destX,double destZ){
	        this.destX=destX;
	        this.destZ=destX;
        }

        public void animate(double elapsedSeconds){

        }

        public boolean isOver(){
	        return posX==destX && posZ==destZ;
        }

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
		accConstant=5;
		gravity=1.5f;
		friction=3;
		orientationSpeed=1.5f;
		bonus=new LinkedList<>();
		keys=new LinkedList<>();
		radius=0.15f;
		currentMaze=0;
		jumpVelocity=jumpVelocity(0.5f);
		animation=null;
		//body=new Cylinder()
		//shape.setFill(Color.BLUE);
	}

	public float radius(){
		return radius;
	}

	public void setPosition(Point2D position, double y,float ori){
		posX=position.getX();
		posY=y;
		posZ=position.getY();
		this.orientation=ori;
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
    public int getGround(){return ground;}
    public int getMazeIndex(){return currentMaze;}

    public void setMaze(int m){
	    currentMaze=m;
    }


    public PlayerState state() {
        return state;
    }

    public PlayerState previousState() {
        return previousState;
    }

    public PlayerAnimation animation() {
        return animation;
    }

    public void setAnimation(PlayerAnimation anim){
	    animation=anim;
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

	public void pick(boolean b){pickedUp=b;}

	public boolean jumping(){
		return space;
	}

	public boolean hasPickedUp(){
	    return pickedUp;
    }

    public void updatePosition(double elapsedSeconds){
	    if(animation==null) {
            if (left) {
                turnLeft(elapsedSeconds);
                if (up) moveForward(elapsedSeconds);
                else if (down) moveBackward(elapsedSeconds);
            } else if (right) {
                turnRight(elapsedSeconds);
                if (up) moveForward(elapsedSeconds);
                else if (down) moveBackward(elapsedSeconds);
            } else if (up) {
                if (space) jump(elapsedSeconds);
                else if (left) turnLeft(elapsedSeconds);
                else if (right) turnRight(elapsedSeconds);
                moveForward(elapsedSeconds);

            } else if (down) {
                if (space) jump(elapsedSeconds);
                else if (left) turnLeft(elapsedSeconds);
                else if (right) turnRight(elapsedSeconds);
                moveBackward(elapsedSeconds);
            } else if (space && state!=PlayerState.JUMPING) {
                jump();
                changeState(PlayerState.JUMPING);
            } else {
                if (state != PlayerState.STAIRSUP && state != PlayerState.STAIRSDOWN) {
                    applyGravity(elapsedSeconds);
                    velocity = velocity.add(new Vector3D(0, -gravity, 0).multiply(elapsedSeconds));

                   /* else if(posY>ground+1){
                        posY=ground+1;
                    }*/
                } else velocity.setTo(0, 0, 0);
                angularVelocity = 0;
            }
        }
        else{
	        if(!animation.isOver()) animation.animate(elapsedSeconds);
	        else {
                animation=null;
                angularVelocity=0;
                orientationSpeed=2;
                velocity.setTo(0,0,0);
            }

        }

        posX+=velocity.x()*elapsedSeconds;
        posY+=velocity.y()*elapsedSeconds;
        posZ+=velocity.z()*elapsedSeconds;
        System.out.println(posY);
        if (posY < ground) {
            posY = ground;
            velocity.setTo(0, 0, 0);

            //posY=position du sol
        }

    }


    public void turnLeft(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        //orientation=(orientation+angularVelocity*elapsedSeconds)%360;
        orientation=(float)(orientation+orientationSpeed)%360;
        System.out.println("Angle :"+orientation);
        setAcceleration();
    }

    public void turnRight(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        orientation=(orientation+(360-angularVelocity))%360;
        System.out.println("Angle :"+orientation);
        setAcceleration();
    }

    public void lookUp(double elapsedSeconds){

    }

    public void lookDown(double elapsedSeconds){

    }


    public void moveForward(double elapsedSeconds){
        velocity=velocity.add(acceleration.multiply(elapsedSeconds));
        if(velocity.norm()>maxSpeed) velocity=velocity.posColinear(maxSpeed);
    }


    public void moveBackward(double elapsedSeconds){
        velocity=velocity.subtract(acceleration.multiply(elapsedSeconds));
        if(velocity.norm()>maxSpeed) velocity=velocity.posColinear(maxSpeed);
    }

    public void jump(double elapsedSeconds){
        velocity=velocity.add(new Vector3D(0,accConstant,0).multiply(elapsedSeconds));
    }

    public void jump(){
	    velocity=new Vector3D(0,jumpVelocity,0);
    }

    public float jumpVelocity(float jumpHeight){
	    return (float)Math.sqrt(2*gravity*jumpHeight);
    }

    public void applyGravity(double elapsedSeconds){
        velocity = velocity.add(new Vector3D(0, -gravity, 0).multiply(elapsedSeconds));
        posY+=velocity.y()*elapsedSeconds;
        if (posY < ground) {
            posY=ground;
            velocity.setTo(0, 0, 0);
            if(state==PlayerState.JUMPING) changeState(previousState);
            //posY=position du sol
        }
    }


    public void changeState(PlayerState s){
        if(state!=PlayerState.JUMPING) previousState=state;
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

    public void setGround(int x){
        ground=x;
    }

    public float orientationX(){
	    return orientationX;
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

    public void pickUp(Key key){
		keys.add(key);
		pickedUp=true;
	}

	public void pickUp(Bonus bonuss){
		bonus.add(bonuss);
		pickedUp=true;
	}
    public MeshView initPlayer() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(this.getClass().getResource("User.fxml"));
        PhongMaterial mat = new PhongMaterial();
        mat.setSpecularColor(Color.SNOW);
        mat.setDiffuseColor(Color.YELLOW);
        MeshView player = fxmlLoader.<MeshView>load();
        player.setMaterial(mat);
        return player;
    }


    public void useBonus(int i){
        for(int a=0;a<i;i++) bonus.remove();
    }







}






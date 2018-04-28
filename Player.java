import javafx.fxml.FXMLLoader;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Circle;
import javafx.scene.shape.MeshView;
import javafx.scene.transform.Rotate;

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
	private float maxSpeed,accConstant,angularVelocity,gravity,friction,orientationSpeed,orientation,jumpVelocity,orientationX;
	private float radius;
	private boolean up,down,left,right,space,pickedUp;
	private LinkedList<Bonus> bonus;
	private LinkedList<Key> keys;

	//Les états du joueur
	public enum PlayerState{
		 BETWEEN,GROUND,STAIRSUP,STAIRSDOWN,DEAD,JUMPING
	}

	//Les animations du joueur
	protected interface PlayerAnimation{
            void animate(double elapsedSeconds);

            boolean isOver();
    }

    protected class Falling implements PlayerAnimation{

	    public Falling(){
            setGround(0);
            state=null;
            MazeInterface.sounds(3).play();
            //add scream sound

        }

	    public void animate(double elapsedSeconds){
	        System.out.println(posY);
	        applyGravity(elapsedSeconds);
            if (posY <= ground) {
                System.out.println("I'm dead !");
                posY=ground;
                velocity.setTo(0, 0, 0);
                MazeInterface.sounds(3).stop();
                changeState(PlayerState.DEAD);
                //add crash sound
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
           // System.out.println("Destination: "+destination);
        }

        public void animate(double elapsedSeconds){
            if(Math.abs(destination-orientation)<=180){
                if(orientation<destination) turnLeft(elapsedSeconds);
                else turnRight(elapsedSeconds);
            }
            else{
                if(orientation<destination) turnRight(elapsedSeconds);
                else turnLeft(elapsedSeconds);
            }
            if(isOver()) orientation=destination;
        }
        public boolean isOver(){
	        return (int)orientation==destination;
        }

    }

    protected class LookingTo implements PlayerAnimation{
	    protected int destination;

	    public LookingTo(int destination){
            this.destination=destination;
            orientationSpeed=1;
            velocity.setTo(0,0,0);
        }

        public void animate(double elapsedSeconds){
            if(destination>orientationX && Math.abs(destination-orientationX)<=180 || Math.abs(destination-orientationX)>180 && destination<orientationX) lookUp(elapsedSeconds);
            else lookDown(elapsedSeconds);
            if(isOver()) orientationX=destination;
        }

        public boolean isOver(){
            return (int)orientationX==destination;
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
		accConstant=3.5f;
		gravity=1.5f;
		friction=3;
		orientationSpeed=2f;
		bonus=new LinkedList<>();
		keys=new LinkedList<>();
		radius=0.15f;
		currentMaze=0;
		jumpVelocity=jumpVelocity(0.4f);
		animation=null;
	}

	/******Les getters******/
	public float radius(){
		return radius;
	}
    public float orientationX(){
        return orientationX;
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
    public boolean hasPickedUp(){
        return pickedUp;
    }
    public int getGround(){return ground;}
    public int getMazeIndex(){return currentMaze;}
    public PlayerState state() {
        return state;
    }
    public PlayerState previousState() {
        return previousState;
    }
    public PlayerAnimation animation() {
        return animation;
    }
/************************************************/

    /******Les setters******/
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
    public void setMaze(int m){
	    currentMaze=m;
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
    public void setAnimation(PlayerAnimation anim){
	    animation=anim;
    }
    public void changeState(PlayerState s){
        if(state!=PlayerState.JUMPING) previousState=state;
        state=s;
        setAcceleration();
        setLookUp();
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

    public void setAcceleration(){
        switch(state){

            case STAIRSUP:acceleration.setTo((int)Math.cos(Math.toRadians(orientation)),1,(int)Math.sin((Math.toRadians(orientation))));
                System.out.println(Math.cos(Math.toRadians(180))+"   "+Math.sin(Math.toRadians(180)));

                break;

            case STAIRSDOWN:acceleration.setTo((int)Math.cos(Math.toRadians(orientation)),-1,(int)Math.sin(Math.toRadians(orientation)));
                break;

            case BETWEEN:acceleration.setTo(Math.cos(Math.toRadians(orientation)),0,Math.sin(Math.toRadians(orientation)));
                break;

            default:acceleration.setTo(Math.cos(Math.toRadians(orientation))*accConstant,0,Math.sin(Math.toRadians(orientation))*accConstant);
                break;
        }
    }

    public void setLookUp(){
        switch(state){
            case STAIRSUP:if((int)orientationX!=20) setAnimation(new LookingTo(20));
                break;

            case STAIRSDOWN:if((int)orientationX!=340) setAnimation(new LookingTo(340));
                break;

            case BETWEEN:
                posY=ground;
                if((int)orientationX!=340) setAnimation(new LookingTo(340));
                break;

            case GROUND:
                posY=ground;
                if(orientationX!=0) setAnimation(new LookingTo(0));
                break;
        }
    }
    /************************************************/




    //Fonction de gestion des déplacements du joueur
    public void updatePosition(double elapsedSeconds){
	    if(animation==null) {
            if (state != PlayerState.JUMPING) {
                if (left) {
                    turnLeft(elapsedSeconds);
                    if (up) moveForward(elapsedSeconds);
                    else if (down) moveBackward(elapsedSeconds);
                } else if (right) {
                    turnRight(elapsedSeconds);
                    if (up) moveForward(elapsedSeconds);
                    else if (down) moveBackward(elapsedSeconds);
                } else if (up) {
                    if (space) {
                        jump();
                        changeState(PlayerState.JUMPING);
                    } else if (left) turnLeft(elapsedSeconds);
                    else if (right) turnRight(elapsedSeconds);
                    moveForward(elapsedSeconds);

                } else if (down) {
                    if (space) {
                        jump();
                        changeState(PlayerState.JUMPING);
                    }
                    else if (left) turnLeft(elapsedSeconds);
                    else if (right) turnRight(elapsedSeconds);
                    moveBackward(elapsedSeconds);
                } else if (space) {
                    jump();
                    changeState(PlayerState.JUMPING);
                } else {
                    velocity.setTo(0, 0, 0);
                    angularVelocity = 0;
                }

            } else applyGravity(elapsedSeconds);
        }
        else{
	        if(!animation.isOver()) animation.animate(elapsedSeconds);
	        else {
	            System.out.println(velocity);
	            System.out.println(acceleration);
	            System.out.println("problem");
                animation=null;
                angularVelocity=0;
                orientationSpeed=2;
                velocity.setTo(0,0,0);
            }

        }
        posX+=velocity.x()*elapsedSeconds;
        posY+=velocity.y()*elapsedSeconds;
        if (posY < ground && state!=PlayerState.STAIRSDOWN && state!=PlayerState.STAIRSUP ) {
            posY=ground;
            velocity.setTo(0, 0, 0);
            if(state==PlayerState.JUMPING) changeState(previousState);
            //posY=position du sol
        }
        posZ+=velocity.z()*elapsedSeconds;

    }

    //Pour tourner à gauche
    public void turnLeft(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        orientation=(orientation+angularVelocity)%360;
        setAcceleration();
    }

    public void turnRight(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        orientation=(orientation+(360-angularVelocity))%360;
        setAcceleration();
    }

    //Pour regarder vers le haut
    public void lookUp(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        orientationX=(orientationX+angularVelocity)%360;
    }

    public void lookDown(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        orientationX=(orientationX+(360-angularVelocity))%360;
    }

    //Pour aller vers l'avant
    public void moveForward(double elapsedSeconds){
        velocity=velocity.add(acceleration.multiply(elapsedSeconds));
        if(velocity.norm()>maxSpeed) velocity=velocity.posColinear(maxSpeed);
    }


    public void moveBackward(double elapsedSeconds){
        velocity=velocity.subtract(acceleration.multiply(elapsedSeconds));
        if(velocity.norm()>maxSpeed) velocity=velocity.posColinear(maxSpeed);
    }


    //Pour sauter
    public void jump(){
	    velocity=velocity.add(new Vector3D(0,jumpVelocity,0));
        System.out.println(velocity);
    }

    public float jumpVelocity(float jumpHeight){
	    return (float)Math.sqrt(2*gravity*jumpHeight);
    }

    public void applyGravity(double elapsedSeconds){
        velocity = velocity.add(new Vector3D(0, -gravity, 0).multiply(elapsedSeconds));
    }


    public void pickUp(Key key){
		keys.add(key);
		pickedUp=true;
	}

	public void pickUp(Bonus bonuss){
		bonus.add(bonuss);
		pickedUp=true;
	}
    public void useBonus(){
        bonus.remove();
    }

    public void useBonus(int i){
        for(int a=0;a<i;a++) bonus.remove();
    }


    //La représentation graphique du joueur
    public MeshView initPlayer() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(this.getClass().getResource("fxml/User.fxml"));
        PhongMaterial mat = new PhongMaterial();
        mat.setSpecularColor(Color.SNOW);
        mat.setDiffuseColor(Color.YELLOW);
        MeshView player = fxmlLoader.<MeshView>load();
        player.setMaterial(mat);
        Rotate r=new Rotate();
        r.setAxis(Rotate.X_AXIS);
        r.setAngle(r.getAngle()-90);
        player.getTransforms().add(r);
        return player;
    }

}






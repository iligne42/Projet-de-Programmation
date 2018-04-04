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
		 BETWEEN,GROUND,STAIRSUP,STAIRSDOWN,DEAD,JUMPING,FALLING;
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
		accConstant=4;
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


    public PlayerState state() {
        return state;
    }

    public PlayerState previousState() {
        return previousState;
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

	public boolean jumping(){
		return space;
	}

    public void updatePosition(double elapsedSeconds){
	    if(left){
	        turnLeft(elapsedSeconds);
            if(up) moveForward(elapsedSeconds);
            else if(down) moveBackward(elapsedSeconds);
        }

        else if(right){
	        turnRight(elapsedSeconds);
            if(up) moveForward(elapsedSeconds);
            else if(down) moveBackward(elapsedSeconds);
        }
        else if(up){
	        if(space) jump(elapsedSeconds);
	        else if(left) turnLeft(elapsedSeconds);
	        else if(right) turnRight(elapsedSeconds);
	        moveForward(elapsedSeconds);

        }

        else if(down){
            if(space) jump(elapsedSeconds);
            else if(left) turnLeft(elapsedSeconds);
            else if(right) turnRight(elapsedSeconds);
	        moveBackward(elapsedSeconds);
        }
        else if(space){
	        jump(elapsedSeconds);
        }
        else{
            if(state!=PlayerState.STAIRSUP && state!=PlayerState.STAIRSDOWN) {
                velocity = velocity.add(new Vector3D(0, -gravity, 0).multiply(elapsedSeconds));
                posY+=velocity.y()*elapsedSeconds;
                if (posY < ground && state!=PlayerState.FALLING) {
                    posY=ground;
                    velocity.setTo(0, 0, 0);
                    return;
                    //posY=position du sol
                }
                else if(state==PlayerState.FALLING){
                    if(velocity.y()<-2*gravity) state=PlayerState.DEAD;
                }
            }
            else velocity.setTo(0, 0, 0);
            angularVelocity=0;
        }

        posX+=velocity.x()*elapsedSeconds;
        posY+=velocity.y()*elapsedSeconds;
        posZ+=velocity.z()*elapsedSeconds;
    }


    public void turnLeft(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        orientation=(orientation+angularVelocity)%360;
        //orientation=(float)(orientation+orientationSpeed*elapsedSeconds)%360;
        setAcceleration();
    }

    public void turnRight(double elapsedSeconds){
        angularVelocity+=(float)(orientationSpeed*elapsedSeconds);
        if(angularVelocity>orientationSpeed)angularVelocity=(float)(orientationSpeed);
        orientation=(orientation+(360-angularVelocity))%360;
        setAcceleration();
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
	}

	public void pickUp(Bonus bonuss){
		bonus.add(bonuss);
	}

    public void useBonus(int i){
        for(int a=0;a<i;i++) bonus.remove();
    }

    public MeshView initPlayer() throws IOException{
        FXMLLoader fxmlLoader = new FXMLLoader();
        fxmlLoader.setLocation(this.getClass().getResource("teleport.fxml"));
        PhongMaterial mat = new PhongMaterial();
        mat.setSpecularColor(Color.HOTPINK);
        mat.setDiffuseColor(Color.PURPLE);
        MeshView player = fxmlLoader.<MeshView>load();
        player.setMaterial(mat);
        return player;
    }





}






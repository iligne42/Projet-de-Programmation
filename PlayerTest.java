import javafx.animation.AnimationTimer;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.scene.shape.Circle;

import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.LinkedList;

public class PlayerTest {
    private float posX;
    private float posY;
    private float posZ;
    private float velocityX;
    private float velocityY;
    private float velocityZ;
    private float accelerationX;
    private float accelerationY;
    private float accelerationZ;
    private static final float gravity = 0.2f;
    private String name;
    private Point2D position;
    private Point2D velocity;
    //private Cylinder body;
    private Circle shape;
    private int orientation;
    private float speed;
    private int orientationSpeed;
    private LinkedList<Bonus> bonus;
    private LinkedList<Key> keys;

    public PlayerTest(String name) {
        this.name = name;
        position = null;
        orientation = 0;
        speed = (float) 0.5;
        orientationSpeed = 5;
        bonus = new LinkedList<>();
        keys = new LinkedList<>();
        shape = new Circle(0.15);
        //body=new Cylinder()
        //shape.setFill(Color.BLUE);
    }

    public float radius() {
        return (float) shape.getRadius();
    }

    public void setPosition(Point2D position, int ori) {
        this.position = position;
        this.orientation = ori;
        shape.setCenterX(position.getX());
        shape.setCenterY(position.getY());
    }

    public void update(long elapsedTime) {
        //only if player is jumping
        velocityY += gravity * elapsedTime;
        posX += velocityX * elapsedTime;
        posY += velocityX * elapsedTime;
        posZ += velocityX * elapsedTime;
    }

    /* public void update(long elapsedTime){
         position.setLocation(position.getX()+velocity.getX()*elapsedTime,position.getY()+velocity.getX()*elapsedTime);

     }
 */
    public void setSpeed(float s) {
        speed = s;
    }

    public String getName() {
        return name;
    }

    public Point2D getPosition() {
        return position;
    }

    public Point2D getVelocity() {
        return velocity;
    }

    public int orientation() {
        return orientation;
    }

    public LinkedList<Key> keys() {
        return keys;
    }

    public LinkedList<Bonus> getBonus() {
        return bonus;
    }


    public void moveForward(long elapsedTime) {
        position.setLocation(position.getX() + Math.cos(Math.toRadians(orientation)) * speed, position.getY() + Math.sin(Math.toRadians(orientation)) * speed);
        //System.out.println(Math.cos(Math.PI/2)*speed);
        //System.out.println(position.getX()+"   "+position.getY());
        //System.out.println(position);
    }

    public void moveBackward() {
        position.setLocation(position.getX() + Math.cos(Math.toRadians(orientation)) * (-speed), position.getY() + Math.sin(Math.toRadians(orientation)) * (-speed));
    }

    public void moveLeft() {
        orientation = (orientation + orientationSpeed) % 360;
    }

    public void moveRight() {
        orientation = (orientation + 360 - orientationSpeed) % 360;
    }

    public void pickUp(Key key) {
        keys.add(key);
    }

    public void pickUp(Bonus bonuss) {
        bonus.add(bonuss);
    }

    final LongProperty lastUpdateTime = new SimpleLongProperty();
    final AnimationTimer gameAnimation = new AnimationTimer() {
        @Override
        public void handle(long now) {
            if (lastUpdateTime.get() > 0) {
                final double elapsedSeconds = (now - lastUpdateTime.get()) / 1000000000.0;
                final double deltaX = elapsedSeconds * velocityX;

            }
        }
    };
}

   /* import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Cylinder;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.Serializable;
import java.util.*;
    public class Player implements Serializable {
        private String name;
        private Point2D position;
        private float velocityX;
        private float velocityZ;
        private float angularVelocity;
        //private Cylinder body;
        private Circle shape;
        private float orientation;
        private float speed;
        private int orientationSpeed;
        private LinkedList<Bonus> bonus;
        private LinkedList<Key> keys;

        public Player(String name){
            this.name=name;
            position=null;
            orientation=0;
            velocityX=velocityZ=0;
            angularVelocity=0;
            speed=(float)0.5;
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
            speed=s;
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
            velocityX=(float)Math.cos(Math.toRadians(orientation))*speed;
            velocityZ=(float)Math.sin(Math.toRadians(orientation))*speed;
            angularVelocity=0;
        }

        public void moveBackward(){
            velocityX=(float)Math.cos(Math.toRadians(orientation))*(-speed);
            velocityZ=(float)Math.sin(Math.toRadians(orientation))*(-speed);
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
            position.setLocation(position.getX()+velocityX*elapsedSeconds,position.getY()+velocityZ*elapsedSeconds);
            orientation=(float)(orientation+angularVelocity*elapsedSeconds)%360;
        }


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





        }*/
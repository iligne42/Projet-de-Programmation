
import javafx.geometry.Point3D;
import java.awt.geom.Point2D;
import java.io.Serializable;

public class Vector3D implements Serializable{
    double x;
    double y;
    double z;

    public Vector3D(Point3D v){
        this.x=v.getX();
        this.y=v.getY();
        this.z=v.getZ();
    }

    public Vector3D(Point2D v){
        this.x=v.getX();
        this.y=0;
        this.z=v.getY();
    }


    public Vector3D(double x, double y, double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public Vector3D(){
        this(0,0,0);
    }



    public double x(){
        return x;
    }

    public double y(){
        return y;
    }

    public double z(){
        return z;
    }

    public double norm(){
        return Math.sqrt(this.dotProduct(this));
    }

    public Vector3D multiply(double alpha){
        return new Vector3D(x*alpha,y*alpha,z*alpha);
    }

    public boolean isNulVector(){
        return x==0 && y==0 && z==0;
    }


    public Vector3D normalize(){
        return (isNulVector())?this:this.multiply(1/norm());
    }

    public void setTo(double x,double y, double z){
        this.x=x;
        this.y=y;
        this.z=z;
    }

    public String toString(){
        return ("x="+x+" y="+y+" z="+z);
    }




    public Vector3D add(Vector3D f){
        return new Vector3D(x+f.x(),y+f.y(),z+f.z());
    }

    public Vector3D subtract(Vector3D f){
        return this.add(f.multiply(-1));
    }

    public double dotProduct(Vector3D f){
        return x()*f.x()+y()*f.y()+z()*f.z();
    }

    public Vector3D crossProduct(Vector3D f){
        double a=y*f.z()-z*f.y();
        double b=f.x()*z-f.z()*x;
        double c=x*f.y()-y*f.x();
        Point3D p=new Point3D(a,b,c);
        return new Vector3D(p);
    }

    public Vector3D posColinear(double norm){
        return isNulVector()?this:multiply(norm/this.norm());
    }

    public Vector3D negColinear(double norm){
        return posColinear(-norm);
    }

    public double distance(Vector3D f){
        return this.subtract(f).norm();
    }




}
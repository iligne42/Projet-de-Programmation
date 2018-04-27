import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.*;
import javafx.scene.transform.Rotate;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.io.Serializable;
import java.util.Random;

public class Obstacles extends Divers{
    private String s;

    public Obstacles(Maze m, String type){
        super(m);
        s=type;
        put();
    }
    public Obstacles(Maze m, Point2D p,String type){
      super(m,p);
      s=type;
    }
    public String getShape(){
        return s;
    }

}

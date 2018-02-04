import javafx.application.Application;
import javafx.scene.shape.*;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.Group;
import java.util.Random;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.LinkedList;

public class Maze2D extends Application{
    static int delta = 60;
    static LinkedList<Box> cas= new LinkedList<Box>();
    public static void randArray(){
        int a=0,b=0,taille=60;
        for (int i = 0;i<20 ;i++ ) {
            for (int j = 0;j<20 ; j++) {
                cas.add(new Box(taille,taille,0));
                a+=taille;
            }
            b+=taille;
            a=0;
        }
    }

    public void drawShape(){
        Random alea= new Random();
        for (int i=0;i<20 ;i++ ) {
            for (int j =0;j<20 ;j++ ) {
                if(alea.nextInt(2)==0)cas.get(i*10+j).setFill(Color.BLUE);
                else cas.get(i*10+j).setFill(Color.YELLOW);
            }
        }
    }
    @Override
    public void start(Stage stage){
        stage.setTitle("Maze2D");
        Group root = new Group();
        randArray();
        drawShape();
        Circle circ= createMove();
        root.getChildren().add(circ);
        for(Rectangle a: cas){
            root.getChildren().add(a);
        }
        Scene scene = new Scene(root,900,900);
        scene.setOnKeyPressed(e->{
                switch(e.getCode()){
                case UP:    circ.setCenterY(circ.getCenterY() - delta); break;
                case RIGHT: circ.setCenterX(circ.getCenterX() + delta); break;
                case DOWN:  circ.setCenterY(circ.getCenterY() + delta); break;
                case LEFT:  circ.setCenterX(circ.getCenterX() - delta); break;
                }
                System.out.println("ululu");
            });
        stage.setScene(scene);
        stage.show();
    }
    public Circle createMove(){
        Circle circ= new Circle(200.0,150.0,20.0,Color.RED);
        circ.setOpacity(0.7);
        return circ;
    }
    public static void main(String[ ] args) {
        launch(args);
    }
}

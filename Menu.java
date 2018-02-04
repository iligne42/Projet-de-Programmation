import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Alert;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;

public class Menu extends Application{
  @Override
  public void start(Stage stage){
    VBox mode = new VBox();
    mode.setPrefSize(500.0,500.0);
    VBox level = new VBox();
    level.setPrefSize(500.0,500.0);
    VBox menu = new VBox();
    menu.setPrefSize(500.0,500.0);
    StackPane stack = new StackPane();
    stack.getChildren().addAll(mode,level,menu);
    /*Panneau pour le mode*/
    mode.setSpacing(10);
    Button solo = new Button("Solo");
    solo.setOnMouseClicked(e->{

    });
    Button chro= new Button("Against the clock");
    chro.setOnMouseClicked(e->{

    });
    Button multi=new Button("Multiplayer");
    multi.setOnMouseClicked(e->{

    });
    Button backToLevel= new Button("BACK");
    backToLevel.setOnMouseClicked(e->{
      changePanel(stack,level);
    });
    mode.getChildren().addAll(solo,chro,multi,backToLevel);
    /*Panneau pour les niveaux*/
    level.setSpacing(10);
    Button easy = new Button("Easy");
    easy.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });
    Button normal = new Button("Normal");
    normal.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });
    Button hard = new Button("Hard");
    hard.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });
    Button backToMenu = new Button("BACK");
    backToMenu.setOnMouseClicked(e->{
      changePanel(stack,menu);
    });
    level.getChildren().addAll(easy,normal,hard,backToMenu);
    /*Panneau pour le menu principal*/
    menu.setSpacing(10);
    Button quit = new Button("QUIT");
    quit.setOnMouseClicked(e->{
      System.exit(0);
    });
    Button play = new Button("PLAY");
    play.setOnMouseClicked(e->{
      changePanel(stack,level);
    });
    Button cred = new Button("CREDITS");
    cred.setOnMouseClicked(e->{
      Alert dev = new Alert(Alert.AlertType.INFORMATION);
      dev.setTitle("Developpers of 3DMaze");
      dev.setContentText("Game made by Pierre Méjane,Iman Lignel"+"\r\n"+"Faridah Akinotcho,Frédéric Francine");
      dev.show();
    });
    Button rank = new Button("RANKING");
    rank.setOnMouseClicked(e->{

    });
    stage.setTitle("Menu of 3DMaze");
    menu.getChildren().addAll(play,rank,cred,quit);
    level.setVisible(false);
    mode.setVisible(false);
    Scene scene = new Scene(stack);
    stage.setScene(scene);
    stage.show();
  }
  public static void changePanel(StackPane stack,VBox panel){
    ObservableList<Node>childs=stack.getChildren();
    int now=childs.indexOf(panel);
    Node mtn = childs.get(now);
    Node past = childs.get(2);//On prend le panel au plus haut de la pile
    past.toBack();//puis on le met au bas de la pile
    past.setVisible(false);
    mtn.toFront();//on met au dessus de la pile, le panel qui nous intéresse
    mtn.setVisible(true);//puis on l'affiche
  }
  public static void main(String[] args) {
    launch(args);
  }
}

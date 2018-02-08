import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.css.*;

import java.io.File;
import java.util.Optional;


public class Menu extends Application{


  @Override
  public void start(Stage stage){
   VBox mode = new VBox();
    mode.setPrefSize(500.0,500.0);
      mode.getStyleClass().add("vbox");

      HBox level = new HBox();
    level.setPrefSize(450.0,450.0);
      level.getStyleClass().add("hbox");
    VBox menu = new VBox();
    menu.setPrefSize(500.0,500.0);
    menu.getStyleClass().add("vbox");

    View view=null;

    VBox game=new VBox();
    game.setPrefSize(500.0,500.0);
    game.getStyleClass().add("vbox");

    VBox maze=new VBox();
    maze.setPrefSize(500.0,500.0);
    maze.getStyleClass().add("vbox");

    StackPane stack = new StackPane();
    stack.getChildren().addAll(mode,maze,game,menu);


    /*Panneau pour les niveaux*/


    /*Panneau pour charger le jeu*/
    Button newG=new Button("New Game");
    newG.setOnMouseClicked(e->{
      changePanel(stack,maze);
    });

    Button cont=new Button("Continue");
    cont.setOnMouseClicked(e->{

    });

    game.getChildren().addAll(newG,cont);


    /* Panneau pour générer le labyrinthe*/
    Button random=new Button("Random maze");
    random.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });

    Button load=new Button("Load maze file");
    load.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });

    maze.getChildren().addAll(random,load);



    /*Panneaux pour les modes*/
    ToggleGroup gameType = new ToggleGroup();
    ToggleGroup difficulty = new ToggleGroup();


      RadioButton solo= new RadioButton("Solo");
      solo.setSelected(true);
      solo.setToggleGroup(gameType);

      RadioButton chro= new RadioButton("Against the clock");
    /*chro.setOnMouseClicked(e->{
     view=new View(new TimeTrialVersion());
      changePanel(stack,view);
    });*/
      chro.setToggleGroup(gameType);

      RadioButton multi=new RadioButton("Multiplayer");
    /*multi.setOnMouseClicked(e->{
      view=new View(new MultiPlayerVersion());
      changePanel(stack,view);
    });*/
      multi.setToggleGroup(gameType);
      VBox type=new VBox();
      type.getChildren().addAll(new Label("Game Types"),solo,chro,multi);
      type.getStyleClass().add("vbox");
      type.setId("v1");


      RadioButton easy = new RadioButton("Easy");
    /*easy.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });*/
      easy.setToggleGroup(difficulty);

      RadioButton normal = new RadioButton("Normal");
      normal.setSelected(true);
    /*normal.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });*/
      normal.setToggleGroup(difficulty);

      RadioButton hard = new RadioButton("Hard");
    /*hard.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });*/
      hard.setToggleGroup(difficulty);

      RadioButton superhard = new RadioButton("Super Hard");
    /*hard.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });*/
      superhard.setToggleGroup(difficulty);

      RadioButton personalized = new RadioButton("Personalized");
    /*hard.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });*/
      personalized.setToggleGroup(difficulty);


      level.getChildren().addAll(type);
      Button backToLevel= new Button("BACK");
      /*backToLevel.setOnMouseClicked(e->{
          changePanel(stack,level);
      });*/

      //mode.getChildren().addAll(solo,chro,multi,backToLevel);
      /*Panneau pour les niveaux*/



      Button backToMenu = new Button("BACK");
      backToMenu.setOnMouseClicked(e->{
          changePanel(stack,menu);
      });
      backToMenu.getStyleClass().add("back");

      Button playGame = new Button("Ok");
      playGame.setOnMouseClicked(e->{
          String dif=((RadioButton)difficulty.getSelectedToggle()).getText();
          String gType=((RadioButton)gameType.getSelectedToggle()).getText();
         // view=MazeInterface.getView(MazeInterface.getSize(dif),MazeInterface.getSize(dif),gType);
      });
      playGame.setId("play");

      VBox niveaux=new VBox();
      niveaux.getChildren().addAll(new Label("Difficulties"),easy,normal,hard,superhard,personalized);
      niveaux.getStyleClass().add("vbox");
      niveaux.setId("v2");
      level.getChildren().addAll(niveaux);

      mode.getChildren().addAll(level,playGame,backToMenu);

    /*Button backToLevel= new Button("BACK");
    backToLevel.setOnMouseClicked(e->{
      changePanel(stack,level);
    });*/





    /*Panneau pour le menu principal*/
    menu.setSpacing(10);
      Label label=new Label("Maze");
    Button quit = new Button("QUIT");
    quit.setOnMouseClicked(e->{
      System.exit(0);
    });

    Button play = new Button("PLAY");
    play.setOnMouseClicked(e->{
      changePanel(stack,game);
      //changePanel(stack,level);
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
    //level.setVisible(false);
    mode.setVisible(false);
    game.setVisible(false);
    maze.setVisible(false);
    if(view!=null) view.setVisible(false);
    Scene scene = new Scene(stack);
    scene.getStylesheets().add("menu.css");
    stage.setScene(scene);
    stage.show();
  }

  public static void changePanel(StackPane stack,Parent panel){
    ObservableList<Node> children=stack.getChildren();
    int now=children.indexOf(panel);
    Node mtn = children.get(now);
    Node past = children.get(children.size()-1);//On prend le panel au plus haut de la pile
    past.toBack();//puis on le met au bas de la pile
    past.setVisible(false);
    mtn.toFront();//on met au dessus de la pile, le panel qui nous intéresse
    mtn.setVisible(true);//puis on l'affiche
  }



  public static void main(String[] args) {
    launch(args);
  }

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("View Files");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    }



  /*


   openButton.setOnAction(
            new EventHandler<ActionEvent>() {
                @Override
                public void handle(final ActionEvent e) {
                    configureFileChooser(fileChooser);
                    File file = fileChooser.showOpenDialog(stage);
                    if (file != null) {
                        openFile(file);
                    }
                }
            });
private class BackButton extends Button{
    public BackButton(String s, Parent p){
      super(s);
      this.setOnMouseClicked(e->{
        changePanel(stack,p);
      });
    }
  }

  //.addAll(new BackButton("",));
*/

}
  /*Panneau pour le mode
    mode.setSpacing(10);
    Button solo = new Button("Solo");
    solo.setOnMouseClicked(e->{
      //déterminer le bouton sur lequel on a cliqué précédemment pour obtenir les param du jeu
     // view=new View(new SoloVersion(...));

    });

    Button chro= new Button("Against the clock");
    chro.setOnMouseClicked(e->{
      view=new View(new TimeTrialVersion());
      changePanel(stack,view);
    });

            Button multi=new Button("Multiplayer");
            multi.setOnMouseClicked(e->{
     view=new View(new MultiPlayerVersion());
      changePanel(stack,view);
    });*/

    //level.setSpacing(10);
    /*Button easy = new Button("Easy");
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
    level.getChildren().addAll(easy,normal,hard,backToMenu);*/

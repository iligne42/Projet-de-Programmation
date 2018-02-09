import javafx.collections.FXCollections;
import javafx.geometry.Pos;
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
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.util.Optional;




public class Menu extends Application{
    String type;
    String dif;
    File fic;

  @Override
  public void start(Stage stage) throws FormatNotSupported,IOException{
   VBox mode = new VBox();
    mode.setPrefSize(600.0,500.0);
      mode.getStyleClass().add("vbox");

      HBox settings = new HBox();
      settings.getStyleClass().add("hbox");

      VBox menu = new VBox();
    menu.setPrefSize(500.0,500.0);
    menu.getStyleClass().add("vbox");



    VBox game=new VBox();
    game.setPrefSize(500.0,500.0);
    game.getStyleClass().add("vbox");

    VBox maze=new VBox();
    maze.setPrefSize(500.0,500.0);
    maze.getStyleClass().add("vbox");

    VBox settings2=new VBox();
    settings2.setPrefSize(500,500);
    settings2.getStyleClass().add("vbox");



    StackPane stack = new StackPane();
    stack.getChildren().addAll(settings2,mode,maze,game,menu);


    /*Panneau pour les level*/


    /*Panneau pour charger le jeu*/
    Button newG=new Button("New Game");
    newG.setOnMouseClicked(e->{
      changePanel(stack,maze);
    });


      ObservableList<String> options= FXCollections.observableArrayList();

      ComboBox cont=new ComboBox(options);
      cont.setPromptText("Continue a previous game");
      cont.setOnAction(e->{
          String ser=cont.getSelectionModel().getSelectedItem().toString();
      });
     /* cont.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
          @Override
          public ListCell call(ListView param) {
              return null;
          }
      });*/
      Button backToMenu = new Button("Back");
      backToMenu.setOnMouseClicked(e->{
          changePanel(stack,menu);
      });
      //backToMenu.getStyleClass().add("back");


      game.getChildren().addAll(newG,cont,backToMenu);


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
      type.getChildren().addAll(new Label("Game Type"),solo,chro,multi);
      type.getStyleClass().add("vbox");
      type.getStyleClass().add("v1");


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


      settings.getChildren().add(type);
      Button backToLevel= new Button("Back");
      /*backToLevel.setOnMouseClicked(e->{
          changePanel(stack,level);
      });*/

      //mode.getChildren().addAll(solo,chro,multi,backToLevel);
      /*Panneau pour les level*/



      Button backToMaze = new Button("Back");
      backToMaze.setOnMouseClicked(e->{
          changePanel(stack,maze);
      });

      Button playGame = new Button("Go !");
      playGame.setOnMouseClicked(e->{
          String dif=((RadioButton)difficulty.getSelectedToggle()).getText();
          String gType=((RadioButton)gameType.getSelectedToggle()).getText();
          View view;
          try {
             view=MazeInterface.getView(MazeInterface.getSize(dif),MazeInterface.getSize(dif),gType);
              Stage st=new Stage();
              Scene sc=new Scene(view);
              //sc.getStylesheets().add("");
              st.setScene(sc);
              st.setFullScreen(true);
              st.show();
          } catch (FormatNotSupported formatNotSupported) {
              formatNotSupported.printStackTrace();
          } catch (IOException e1) {
              e1.printStackTrace();
          }

          // view=MazeInterface.getView(MazeInterface.getSize(dif),MazeInterface.getSize(dif),gType);
      });

      VBox level=new VBox();
      level.getChildren().addAll(new Label("Level"),easy,normal,hard,superhard,personalized);
      level.getStyleClass().add("vbox");
      level.setId("v2");
        settings.getChildren().add(level);
      mode.getChildren().addAll(settings,playGame,backToMaze);


    /* Panneau pour générer le labyrinthe*/
    Button random=new Button("Random maze");
    random.setOnMouseClicked(e->{
      changePanel(stack,mode);
    });

    Button load=new Button("Load maze file");
    load.setOnMouseClicked(e->{
     changePanel(stack,settings2);

        /*if(file!=null){
            HBox h=new HBox();
            Button ok=new Button("Set");
            ok.setOnMouseClicked(e->{
                String type=((RadioButton)gameType.getSelectedToggle()).getText();});
            h.getChildren().addAll(solo,chro,multi);
            view=MazeInterface.getView(new Maze(file),type);
        }*/

    });

      Button backToGame = new Button("Back");
      backToGame.setOnMouseClicked(e->{
          changePanel(stack,game);
      });
      //backToMenu.getStyleClass().add("back");

    maze.getChildren().addAll(random,load,backToGame);

    FileChooser fileChooser=new FileChooser();

      ToggleGroup typeG = new ToggleGroup();
      RadioButton solo2= new RadioButton("Solo");
      solo2.setSelected(true);
      solo2.setToggleGroup(typeG);

      RadioButton chro2= new RadioButton("Against the clock");
      chro2.setToggleGroup(typeG);

      RadioButton multi2=new RadioButton("Multiplayer");
      multi2.setToggleGroup(typeG);

      VBox opt=new VBox();
      opt.getChildren().addAll(solo2,chro2,multi2);
      opt.getStyleClass().add("v3");

      VBox type2=new VBox();
      type2.getChildren().addAll(new Label("Choose a game type"),opt);
      type2.getStyleClass().add("vbox");
      //type2.setStyle("-fx-alignment:top-center;");
      //type2.getStyleClass().add("v1");


      Button backToMaze2 = new Button("Back");
      backToMaze2.setOnMouseClicked(e->{
          changePanel(stack,maze);
      });

      Button choose=new Button("Upload a maze file");
      choose.setOnMouseClicked(e-> {
          configureFileChooser(fileChooser);
          File file = fileChooser.showOpenDialog(stage);
          String gType = ((RadioButton) typeG.getSelectedToggle()).getText();
          if (file != null) {
              View view;
              try {
                     view=MazeInterface.getView(new Maze(file), gType);
                  Stage st=new Stage();
                  Scene sc=new Scene(view);
                  //sc.getStylesheets().add("");
                  st.setScene(sc);
                  st.setFullScreen(true);
                  st.show();
              } catch (FormatNotSupported formatNotSupported) {
                  formatNotSupported.printStackTrace();
              } catch (IOException e1) {
                  e1.printStackTrace();
              }

          }
      });

      settings2.getChildren().addAll(type2,choose,backToMaze2);

    /*Panneau pour le menu principal*/
      Label label=new Label("Maz3D");
      label.setStyle("-fx-font-size: 58; -fx-text-fill:#bdbbb6; -fx-alignment:top-center;");
      label.setId("mazd");
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
      dev.setTitle("Developpers of Maz3D");
      dev.setContentText("Game made by Faridah Akinotcho, Frédéric Francine"+"\r\n"+"Iman Lignel, Pierre Méjane");
      dev.show();
      addCss(dev);
    });

    Button rank = new Button("RANKING");
    rank.setOnMouseClicked(e->{

    });

    stage.setTitle("Menu of Maz3D");
    menu.getChildren().addAll(label,play,rank,cred,quit);
    //level.setVisible(false);
    mode.setVisible(false);
    game.setVisible(false);
    maze.setVisible(false);
    settings2.setVisible(false);
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

  public static void addCss(Dialog a){
      DialogPane dialogPane=a.getDialogPane();
      dialogPane.getStylesheets().add("alert.css");
      a.setHeaderText(null);
      a.setGraphic(null);
  }



  public static void main(String[] args) {
    launch(args);
  }

    private static void configureFileChooser(final FileChooser fileChooser){
        fileChooser.setTitle("View Files");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
    fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Text", "*.txt")
            //new FileChooser.ExtensionFilter("JPG", "*.jpg")
    )      ;
  }




  /*



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

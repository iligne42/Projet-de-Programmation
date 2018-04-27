import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.css.*;
import javafx.util.Callback;
import javafx.event.EventHandler;
import java.io.File;
import java.io.IOException;
import javafx.scene.input.MouseEvent;

//ICI RAJOUTER /MODIFIER DU CODE QUAND LE RESEAU SERA FINI POUR LE METTRE A JOUR ET TOUT RELIER


public class Menu extends Application {
    String gType;
    String dif;
    String name;
    MazeFloors mazeM;
    View view;
    boolean[] supp;
    int selected;
    IntegerProperty value;
    boolean debug=false;
    //Parent previous;

    protected class Counter extends VBox{

        protected Label val;
        protected Button plus,minus;

        public Counter(){
            value=new SimpleIntegerProperty(1);
            val=new Label();
            val.setId("cl");
            //val.setMinSize(20,12);
            val.textProperty().bind(value.asString());
            plus=new Button("+");
            minus=new Button("-");
            plus.getStyleClass().add("counter");
            minus.getStyleClass().add("counter");
            plus.setOnMouseClicked(e->{
                value.set(value.get()+1);
            });
            minus.setOnMouseClicked(e->{
                if(value.get()>1) value.set(value.get()-1);
            });
            this.getChildren().addAll(plus,minus);
            this.getStyleClass().add("countbox");
        }

        public Label getLabel(){
            return val;
        }
    }

    public void setButton(Button button, int a, VBox pan, String path,StackPane stack,VBox menu){
      	button.setOnMouseClicked(e->{
      		pan.getChildren().clear();
      		pan.setStyle("-fx-spacing:2;");
                try{
                    Scores sco = new Scores(path,a);
                    String [] sc = (sco.getScores()).split("\n");
                    if(debug)System.out.println(sc.length);
                    for(String sct : sc){
                        Label res = new Label(sct);
                        if(debug)System.out.println(sct);
                        pan.getChildren().add(res);
                    }
                    Button prev = new Button("Back");
                    prev.setOnMouseClicked(evt->{
                            changePanel(stack,menu);
                            pan.getChildren().clear();
                            initRank(stack,pan,menu);
			});
                    pan.getChildren().add(prev);
                }
                catch(Exception exc){
              if(debug)      exc.printStackTrace();}
    	});
    }
    public void config(VBox ranking,String path,StackPane stack,VBox menu){
        ranking.getChildren().clear();
        Button fac = new Button("Easy");
        Button norm = new Button("Normal");
        Button diff = new Button("Hard");
        Button hdiff = new Button("Super Hard");
        setButton(fac,0,ranking,path,stack,menu);
        setButton(norm,1,ranking,path,stack,menu);
        setButton(diff,2,ranking,path,stack,menu);
        setButton(hdiff,3,ranking,path,stack,menu);
        ranking.getChildren().addAll(fac,norm,diff,hdiff);
    }
    public void initRank(StackPane stack,VBox ranking,VBox menu){
      Button solos = new Button("Solo");
      solos.setOnMouseClicked(e->{
              config(ranking,"txt/bestSolos.txt",stack,menu);
          });
      Button trial = new Button("Against the clock");
      trial.setOnMouseClicked(e->{
              config(ranking,"txt/bestRaces.txt",stack,menu);
          });
      ranking.getChildren().addAll(solos,trial);
    }
    @Override
    public void start(Stage stage) throws FormatNotSupported, IOException {
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
                Platform.exit();
                System.exit(0);
            }
        });

        VBox menu = new VBox();
        menu.setPrefSize(500.0, 500.0);
        menu.getStyleClass().add("vbox");

        VBox mode = new VBox();
        mode.setPrefSize(600.0, 500.0);
        mode.getStyleClass().add("vbox");

        HBox settings = new HBox();
        settings.getStyleClass().add("hbox");

        VBox game = new VBox();
        game.setPrefSize(500.0, 500.0);
        game.getStyleClass().add("vbox");

        VBox maze = new VBox();
        maze.setPrefSize(500.0, 500.0);
        maze.getStyleClass().add("vbox");

        VBox settings2 = new VBox();
        settings2.setPrefSize(500, 500);
        settings2.getStyleClass().add("vbox");

        VBox ranking = new VBox();
        ranking.setPrefSize(500,500);
        ranking.getStyleClass().add("vbox");

        final hostMenu hostmenu = new hostMenu();


        StackPane stack = new StackPane();
        stack.getChildren().addAll(settings2, mode, maze, game, hostmenu,ranking, menu);

        initRank(stack,ranking,menu);
        /*Panneau pour le réseau*/
        Button go=new Button("Go !");
        go.setOnMouseClicked(e->{
            try {
                Stage st = new Stage();
                view = MazeInterface.getView(mazeM,gType,name,MazeInterface.getTime(dif));
                view.setStage(st);
                st.setScene(view);
                st.setFullScreen(true);
                st.show();
            }
            catch(Exception ex){
         if(debug)       ex.printStackTrace();
            }
        });

        Button backToSet=new Button("Back");
        backToSet.setOnMouseClicked(e->{
            changePanel(stack,settings);
        });
        hostmenu.getStyleClass().add("vbox");



        /*Panneau pour charger le jeu*/
        Button newG = new Button("New Game");
        newG.setOnMouseClicked(e -> {
            changePanel(stack, maze);
        });


        ObservableList<String> options = FXCollections.observableArrayList();
        File folder=new File("savings/");
        for(File f:folder.listFiles()){
            if(f.getName().endsWith(".ser")) options.add(f.getName());
        }

        ComboBox cont = new ComboBox(options);
        cont.setPromptText("Continue a previous game");
        cont.setOnAction(e -> {
            String ser = cont.getSelectionModel().getSelectedItem().toString();
            try {
                GameVersion g = MazeInterface.load(ser);
                view = new SingleView(g);
                Stage st = new Stage();
                view.setStage(st);
                st.setScene(view);    
                st.setFullScreen(true);
                st.show();

            }
            catch(Exception ex){
         if(debug)       ex.printStackTrace();
            }
        });
        Button backToMenu = new Button("Back");
        backToMenu.setOnMouseClicked(e -> {
            changePanel(stack, menu);
        });
        //backToMenu.getStyleClass().add("back");


        game.getChildren().addAll(newG, cont, backToMenu);


        /*Panneaux pour les modes*/
        ToggleGroup gameType = new ToggleGroup();
        ToggleGroup difficulty = new ToggleGroup();

        //Types de jeu
        RadioButton solo = new RadioButton("Solo");
        solo.setSelected(true);
        solo.setToggleGroup(gameType);

        RadioButton chro = new RadioButton("Against the clock");
        chro.setToggleGroup(gameType);

        RadioButton multi = new RadioButton("Multiplayer Local");
        RadioButton net = new RadioButton("Multiplayer In Network");

        net.setToggleGroup(gameType);

        multi.setToggleGroup(gameType);

        for(Toggle t:gameType.getToggles()){
            RadioButton r=(RadioButton)t;
            r.setOnMouseClicked(evt->{
                gType=r.getText();
            });
        }

        VBox type = new VBox();
        type.getChildren().addAll(new Label("Game Type"), solo, chro, multi,net);
        type.getStyleClass().add("vbox");
        type.getStyleClass().add("v1");

        //Les options
        VBox option=new VBox();
        supp=new boolean[5];
        CheckBox[] opt=new CheckBox[5];
        opt[0]=new CheckBox("Obstacles");
        opt[1]=new CheckBox("Doors");
        opt[2]=new CheckBox("Monsters");
        opt[3]=new CheckBox("Teleporters");
        opt[4]=new CheckBox("Bonus");

        class OptionAction implements EventHandler<MouseEvent>{
            private int pos;

            public OptionAction(int a){
                super();
                pos=a;
            }
            @Override
            public void handle(MouseEvent event){
                selected=numberSelected(opt);
                CheckBox ch=(CheckBox)event.getSource();
                if(ch.isSelected()){
                    supp[pos]=true;
                    switch (dif){
                        case "Easy":
                            if(selected==1)for(CheckBox c:opt) if(!c.isSelected()) c.setDisable(true);
                            break;
                        case "Normal":
                            if(selected==2)
                                for(CheckBox c:opt) if(!c.isSelected()) c.setDisable(true);
                            break;
                        case "Hard":
                            if(selected==3)
                                for(CheckBox c:opt) if(!c.isSelected()) c.setDisable(true);
                            break;

                    }
                }
                else{
                    supp[pos]=false;
                    for(CheckBox c:opt) c.setDisable(false);
                }


            }
        }

        option.getChildren().add(new Label("Options"));
        for(int i=0;i<opt.length;i++){
            CheckBox c=opt[i];
            c.setOnMouseClicked(new OptionAction(i));
            option.getChildren().add(c);
        }
        HBox floors=new HBox();
        floors.setId("ffl");
        Label fl=new Label("Floors");
        fl.setId("fl");
        Counter co=new Counter();
        floors.getChildren().addAll(co,fl,co.val);
        option.getChildren().add(floors);

        option.getStyleClass().add("v2");
        option.setVisible(false);

        //Les difficultés
        RadioButton easy = new RadioButton("Easy");
        easy.setToggleGroup(difficulty);

        RadioButton normal = new RadioButton("Normal");
        normal.setSelected(true);
        normal.setToggleGroup(difficulty);

        RadioButton hard = new RadioButton("Hard");
         hard.setToggleGroup(difficulty);

        RadioButton superhard = new RadioButton("Super Hard");
        superhard.setToggleGroup(difficulty);

        RadioButton personalized = new RadioButton("Personalized");
        personalized.setToggleGroup(difficulty);

        for(Toggle t:difficulty.getToggles()){
            RadioButton r=(RadioButton)t;
            r.setOnMouseClicked(evt->{
                dif=r.getText();
                deSelectAll(opt);
                option.setVisible(true);

            });
        }



        Button backToMaze = new Button("Back");
        backToMaze.setOnMouseClicked(e -> {
            changePanel(stack, maze);
            option.setVisible(false);
            deSelectAll(opt);
        });

        Button playGame = new Button("Go !");
        playGame.setOnMouseClicked(e -> {
           // previous=mode;
            if(dif==null)dif = ((RadioButton) difficulty.getSelectedToggle()).getText();
            if(gType==null)gType = ((RadioButton) gameType.getSelectedToggle()).getText();


            if(gType.equals("Multiplayer In Network")){
                        try {
                        name = MazeInterface.readInput("What's your name ?");
                        mazeM=MazeInterface.getMaze(MazeInterface.getSize(dif),MazeInterface.getSize(dif),value.get(),0,supp,MazeInterface.nbExtra(dif));
                        hostmenu.initHost(name,mazeM);
                       changePanel(stack, hostmenu);
                    } catch (Exception exception) {

                    }
            }
            else {
                try {
                    mazeM = MazeInterface.getMaze(MazeInterface.getSize(dif), MazeInterface.getSize(dif),value.get(),MazeInterface.typeBonus(gType),supp,MazeInterface.nbExtra(dif));
                    view = MazeInterface.getView(mazeM, gType,MazeInterface.getTime(dif));
                    Stage st = new Stage();
                    view.setStage(st);
                    st.setScene(view);
                   st.setFullScreen(true);
                    st.show();
                } catch (Exception formatNotSupported) {
                    if(debug)formatNotSupported.printStackTrace();
                }
            }

        });

        VBox level = new VBox();
        level.getChildren().addAll(new Label("Level"), easy, normal, hard, superhard, personalized);
        level.getStyleClass().add("v2");
        settings.getChildren().addAll(type,option,level);
        mode.getChildren().addAll(settings, playGame, backToMaze);



        /* Panneau pour générer le labyrinthe*/
        Button random = new Button("Random maze");
        random.setOnMouseClicked(e -> {
           // previous=random.getParent();
            changePanel(stack, mode);
        });

        Button load = new Button("Load maze file");
        load.setOnMouseClicked(e -> {
                    changePanel(stack, settings2);
                });
        Button backToGame = new Button("Back");
        backToGame.setOnMouseClicked(e -> {
            changePanel(stack, game);
        });
        /*if(file!=null){
            HBox h=new HBox();
            Button ok=new Button("Set");
            ok.setOnMouseClicked(e->{
                String type=((RadioButton)gameType.getSelectedToggle()).getText();});
            h.getChildren().addAll(solo,chro,multi);
            view=MazeInterface.getView(new Maze(file),type);
        }*/




        maze.getChildren().addAll(random, load, backToGame);

        FileChooser fileChooser = new FileChooser();

       /*Panneau pour les choix avec un chargement de fichier*/
        ToggleGroup typeG = new ToggleGroup();
        RadioButton solo2 = new RadioButton("Solo");
        solo2.setSelected(true);
        solo2.setToggleGroup(typeG);

        RadioButton chro2 = new RadioButton("Against the clock");
        chro2.setToggleGroup(typeG);

        RadioButton multi2 = new RadioButton("Multiplayer Local");
        multi2.setToggleGroup(typeG);

        RadioButton net2=new RadioButton("Multiplayer In Network");
        net2.setToggleGroup(typeG);

        VBox op = new VBox();
        op.getChildren().addAll(solo2, chro2, multi2,net2);
        op.getStyleClass().add("v3");

        VBox type2 = new VBox();
        type2.getChildren().addAll(new Label("Choose a game type"), op);
        type2.getStyleClass().add("vbox");
        //type2.setStyle("-fx-alignment:top-center;");
        //type2.getStyleClass().add("v1");


        Button backToMaze2 = new Button("Back");
        backToMaze2.setOnMouseClicked(e -> {
            changePanel(stack, maze);
        });

        Button choose = new Button("Upload a maze file");
        choose.setOnMouseClicked(e -> {
            configureFileChooser(fileChooser);
            File file = fileChooser.showOpenDialog(stage);
            String gType = ((RadioButton) typeG.getSelectedToggle()).getText();
            if(dif==null)dif = ((RadioButton) difficulty.getSelectedToggle()).getText();
            if (file != null) {
                try {
                    mazeM = new MazeFloors(new Maze(file,gType));
                    if (gType.equals("MultiPlayer In Network")) {
                            name = MazeInterface.readInput("What's your name ?");
                            hostmenu.initHost(name, mazeM);
                        }
                     else {
                            view = MazeInterface.getView(mazeM, gType,MazeInterface.getTime(dif));
                            Stage st = new Stage();
                            st.setScene(view);
                            view.setStage(st);
                            st.setFullScreen(true);
                            st.show();
                        }
                    }
                    catch (Exception formatNotSupported) {
                        if(debug)formatNotSupported.printStackTrace();
                        }



                }
        });

        settings2.getChildren().addAll(type2, choose, backToMaze2);

        /*Panneau pour le menu principal*/
        Label label = new Label("A maz3D");
        label.setStyle("-fx-font-size: 58; -fx-text-fill:#bdbbb6; -fx-alignment:top-center;");
        label.setId("mazd");
        Button quit = new Button("QUIT");
        quit.setOnMouseClicked(e -> {
            System.exit(0);
        });

        Button play = new Button("PLAY");
        play.setOnMouseClicked(e -> {
            //previous=play.getParent();
            changePanel(stack, game);
            //changePanel(stack,level);
        });

        Button jplay=new Button("JOIN A GROUP");
        jplay.setOnMouseClicked(e->{
            //previous=jplay.getParent();
            try {
                    name = MazeInterface.readInput("What's your name ?");
                    hostmenu.initClient(name);
                    changePanel(stack, hostmenu);
                } catch (Exception exception) {
                    if(debug)exception.printStackTrace();
                }
            });


        Button cred = new Button("CREDITS");
        cred.setOnMouseClicked(e -> {
           // previous=cred.getParent();
            Alert dev = new Alert(Alert.AlertType.INFORMATION);
            dev.setTitle("Developpers of Maz3D");
            dev.setContentText("Game made by Faridah Akinotcho, Frédéric Francine" + "\r\n" + "Iman Lignel, Pierre Méjane");
            dev.show();
            addCss(dev);
        });

        Button rank = new Button("RANKING");
        rank.setOnMouseClicked(e -> {
            changePanel(stack,ranking);
        });


        stage.setTitle("Menu of Maz3D");
        menu.getChildren().addAll(label, play, jplay,rank, cred, quit);
        mode.setVisible(false);
        game.setVisible(false);
        maze.setVisible(false);
        settings2.setVisible(false);
        hostmenu.setVisible(false);
        ranking.setVisible(false);
        Scene scene = new Scene(stack);
        scene.getStylesheets().add("css/menu.css");
        stage.setScene(scene);
        stage.show();
    }



    public static void changePanel(StackPane stack, Parent panel) {
        ObservableList<Node> children = stack.getChildren();
        int now = children.indexOf(panel);
        Node mtn = children.get(now);
        Node past = children.get(children.size() - 1);//On prend le panel au plus haut de la pile
        past.toBack();//puis on le met au bas de la pile
        past.setVisible(false);
        mtn.toFront();//on met au dessus de la pile, le panel qui nous intéresse
        mtn.setVisible(true);//puis on l'affiche
    }

    public static int numberSelected(CheckBox[] tab){
        int res=0;
        for(CheckBox c:tab){
            if(c!=null && c.isSelected()) res ++;
        }
        return res;
    }

    public static void deSelectAll(CheckBox[] tab){
        for(CheckBox c:tab) if(c!=null){
            c.setDisable(false);
            c.setSelected(false);
        }
    }

    public static void addCss(Dialog a) {
        DialogPane dialogPane = a.getDialogPane();
        dialogPane.getStylesheets().add("css/alert.css");
        a.setHeaderText(null);
        a.setGraphic(null);

    }


    public static void main(String[] args) {
        launch(args);

    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Files");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Text", "*.txt")
                //new FileChooser.ExtensionFilter("JPG", "*.jpg")
        );
    }

}

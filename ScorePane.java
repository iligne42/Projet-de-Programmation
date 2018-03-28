import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import java.util.Scanner;

public class ScorePane extends Pane {
    private Scores score;
    private Label affichage;
    private VBox messages;

    public ScorePane(Scores s){
        super();
        getStyleClass().add("root");
        setPrefSize(500,500);
        score=s;
        affichage=new Label();
        messages=new VBox();
        //messages.getStyleClass().add("messages");
        messages.setPrefSize(500,500);
        this.getChildren().add(messages);
        //messages.setAlignment(Pos.CENTER);
        getStylesheets().add("chat.css");
    }

    public void printScores(){
        //messages.getChildren().add(affichage);
        //affichage.setText(score.toString());
        //System.out.println("Je rentre dans printlScores");
        /*for(int i=0;i<score.length();i++){
            messages.getChildren().add(new Label("Nom "+i));
            System.out.println("J'essaie d'ajouter les messages");
            Pair<String,Integer> p=score.get(i);
            Label deb=new Label();
            deb.setText(p.getKey());
            deb.getStyleClass().add("deb");
            Label end=new Label();
            end.setText(p.getValue()+"");
            end.getStyleClass().add("end");
            HBox align=new HBox();
            align.getChildren().addAll(deb,end);
            messages.getChildren().add(align);
            }*/
            String debut="Tableau des scores";
            Label deb=new Label(debut);
            deb.getStyleClass().add("deb");
            messages.getChildren().add(deb);
            Scanner sc=new Scanner(score.toString());
            while(sc.hasNextLine())
                styleMessage(sc.nextLine());

    }

    public void styleMessage(String str){
        String debut="";
        String fin="";
        boolean flag = true;
        for(int i=0; i<str.length(); i++){
            if(flag){
                if(str.charAt(i)=='-') flag=false;
                debut+=str.charAt(i)+"";
            }else fin+=str.charAt(i)+"";
        }
        Label deb=new Label(debut);
        deb.getStyleClass().add("deb");
        Label end=new Label(fin);
        end.getStyleClass().add("end");
        HBox align=new HBox();
        align.getChildren().addAll(deb,end);
        align.getStyleClass().add("align");
        messages.getChildren().add(align);
    }
}

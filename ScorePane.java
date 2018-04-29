import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.util.Pair;

import java.util.Scanner;

public class ScorePane extends Pane {
    private Scores score;
    private VBox messages;

    public ScorePane(Scores s){
        super();
        getStyleClass().add("root");
        score=s;
        messages=new VBox();
        String sc="Scores";
        Label l=new Label(sc);
        l.getStyleClass().add("l");
        messages.getChildren().add(l);

        Rectangle2D r= Screen.getPrimary().getBounds();
        messages.setPrefSize(r.getWidth(),r.getHeight());
        messages.getStyleClass().add("mes");
        this.getChildren().add(messages);
        getStylesheets().add("css/chat.css");
    }

    public void printScores(){
            Scanner sc=new Scanner(score.toString());
            while(sc.hasNextLine())
                styleMessage(sc.nextLine());
            if(score.current!=null) {
                String scoreC = "Your score is : " + score.getCurrentScore();
                Label la = new Label(scoreC);
                la.getStyleClass().add("la");
                messages.getChildren().add(la);
            }
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

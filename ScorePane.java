import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

public class ScorePane extends VBox{
    private Scores score;
    private Label affichage;
    private VBox messages;

    public ScorePane(Scores s){
        super();
        setPrefSize(500,500);
        score=s;
        affichage=new Label();
        this.getChildren().add(affichage);
        affichage.setAlignment(Pos.CENTER);
        getStylesheets().add("chat.css");
    }

    public void printScores(){
        //affichage.setText(score.toString());
        for(int i=0;i<score.length();i++){
            Pair<String,Integer> p=score.get(i);
            Label deb=new Label();
            deb.setText(p.getKey());
            deb.getStyleClass().add("deb");
            Label end=new Label();
            end.setText(p.getValue()+"");
            end.getStyleClass().add("end");
            HBox align=new HBox();
            align.getChildren().addAll(deb,end);
            getChildren().add(align);
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
        messages.getChildren().add(align);
    }
}

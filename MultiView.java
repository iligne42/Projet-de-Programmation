
import javafx.beans.property.IntegerProperty;
import javafx.scene.Camera;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.util.Stack;

public class MultiView extends View {
    protected MultiPlayerVersion multi;
    // protected Stack<Player> players;

    public MultiView(MultiPlayerVersion multi) {
        super(new BorderPane(),multi.getGame());
        this.multi = multi;
        control = new MultiControl();

    }

    protected class MultiControl extends GameControl {
        //protected Scores scores;
        protected IntegerProperty timeToBeat;

        public MultiControl() {
            scores = new Scores();
            Label label = new Label();
            timeToBeat = new TimeProperty(0);
            label.textProperty().bind(timeToBeat.asString());
            label.setVisible(false);
            timePane.getChildren().add(label);
            game.start();
            if (game.gameOver()) {
                timePane.stop();
                scores.addToScoresList(game.player.getName(),timePane.getElapsedSeconds());
                timeToBeat.set(scores.get(0).getValue());
                //Get time to beat, it is the top of the list
                //Get to the next player and labyrinth
                multi.next();
                if (!multi.gameOver()) {
                    mazePane = new MazePane();
                    timePane = new SoloTimePane();
                    timePane.setVisible(true);
                } else {
                    //displayScore(MultiView.this);
                }
            } else {
                //read events
            }

        }

        public void displayScore(Pane root) {
            root.getChildren().clear();
            String display = scores.getScores();
            String[] splits = display.split("\n");
            for (String s : splits) root.getChildren().add(new Label(s));
        }

    }
}


   /* public void action(){
        int i;
        if(game.gameOver()){
            //remove le temps
            //afficher sur un joptionpane to print congratulations, you win
            //Switch to print of 10 bestScores with name;
            timePane.stop();
            timePane.setVisible(false);//Maybe
            //game.addToScores(timePane.getElapsedTime());
            //File file=new Scores(game.scoresFile()).addToScoresFile(game.player().getName(),timePane.getElapsedSeconds());
            //Show the node with the best scores;
        }
        else if(timePane.timeOver()){
            timePane.setVisible(false);
            //remove timePanel
            //print you loose, dumb assgame
        }
        else{
            //readKeyEvent
            //game.move(i);
            //   KeyEvent(null,null,KeyEvent.KEY_RELEASED,KeyEvent.CHAR_UNDEFINED,)

        }
        //remove the panels with the maze and time and switch to score viewing
        //if(game.timeOver()) remove the panel with time and print tome over, only in time trial mode though
    }


}

*/


    //Multiplayer Mode
//Faire une classe qui étend vue spéciale pour ça avec comme attributs, des listes de vues banales;
   /* public void actionM(){
        int i;
        if(game.gameOver()){
        Scores scores=new Scores();
        scores.addToList(timePane.getElapsedSeconds());
            //remove le temps
            //afficher sur un joptionpane to print congratulations, you win
            //Switch to print of 10 bestScores with name;
            timePane.timeLine.stop();
            timePane.setVisible(false);//Maybe
            ArrayList<Integer> scores=new ArrayList<>();
            for(ViewG v:viewList) scores.add(v.timePane.getElapsedTime());
            this.print(game.ranking(scores));
            //Show the node with the best scores;
        }
        else if(timePane.timeOver()){
            timePane.setVisible(false);
            //remove timePanel
            //print you loose, dumb ass
        }
        else{
            //readKeyEvent
            //game.move(i);
            //   KeyEvent(null,null,KeyEvent.KEY_RELEASED,KeyEvent.CHAR_UNDEFINED,)

        }
        //remove the panels with the maze and time and switch to score viewing
        //if(game.timeOver()) remove the panel with time and print tome over, only in time trial mode though
    }*/


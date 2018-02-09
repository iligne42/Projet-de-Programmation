import javafx.scene.layout.VBox;

import java.io.IOException;

public class SingleView extends View{
   protected GameControl control=new SoloControl();

    public SingleView(GameVersion game) throws IOException{
        super(game);
        control=new SoloControl();
    }

    protected class SoloControl extends GameControl{

        public SoloControl() throws IOException{
            scores=new Scores(game.scoresFile());
            game.start();
            timePane.start();
            if (game.gameOver()) {
                timePane.stop();
                scores.addToScoresFile(game.player().getName(),timePane.getElapsedSeconds());
                displayScore(SingleView.this);
            } else if (timePane.timeOver()) {
                timePane.setVisible(false);
                //Print you loose dumbass
            } else {

            }
        }

        public void displayScore(VBox root) {

        }
    }


    public void action(){
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

//
        //Use a path transition to slide the player
    }

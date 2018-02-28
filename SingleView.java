
import javafx.scene.Camera;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.awt.geom.Point2D;
import java.io.IOException;

public class SingleView extends View{

    public SingleView(GameVersion game) throws IOException{
        super(new BorderPane(),game);
        control=new SoloControl();
    }

    protected class SoloControl extends GameControl{

        public SoloControl() throws IOException{
            game.start();
            timePane.start();
            mazePane.initMaze(0);
            SingleView.this.setOnKeyPressed(e->{
                System.out.println(e.getCode());
                    switch (e.getCode()){
                            case UP:    game.move(1); break;
                            case RIGHT: game.move(4); break;
                            case DOWN:  game.move(2); break;
                            case LEFT:  game.move(3); break;
                           // case S: mazePane.rotateY.setAngle(mazePane.rotateY.getAngle()+5);
                        }
                        Point2D pos=game.player().getPosition();
                        SingleView.this.mazePane.x.set(pos.getX()*SingleView.this.mazePane.SIZE_BOX);
                        SingleView.this.mazePane.z.set(pos.getY()*SingleView.this.mazePane.SIZE_BOX);
                        SingleView.this.mazePane.angle.set(90-game.player().orientation());
                System.out.println(SingleView.this.getCamera().getTranslateX()+"     "+SingleView.this.getCamera().getTranslateZ()+"   "+SingleView.this.getCamera().getRotate());

                       // System.out.println(game.player().getPosition().getX()+"    "+game.player.getPosition().getY());

                //SingleView.this.mazePane.printMaze();
                    if (game.gameOver()) {
                        timePane.stop();
                        //game.addToScores();
                        game.addToScoresFile();
                        //displayScore(SingleView.this);
                    } else if (timePane.timeOver()) {
                        timePane.setVisible(false);
                        //Print you loose dumbass
                    }


                });

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

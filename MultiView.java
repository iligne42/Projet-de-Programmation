
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Stack;

public class MultiView extends View {
    protected MultiPlayerVersion multi;
    // protected Stack<Player> players;

    public MultiView(MultiPlayerVersion multi) throws IOException,FormatNotSupported{
        //super(new BorderPane(),multi.getGame());
        super(new StackPane(),multi.getGame());
        this.multi = multi;
        control = new MultiControl();

    }

    protected class MultiControl extends GameControl {
        //protected Scores scores;
        protected IntegerProperty timeToBeat;
        protected Label nameLabel;
        protected Label label;

        public MultiControl() throws IOException, FormatNotSupported {
            super();
            label = new Label();
            nameLabel=new Label("Current Player : "+game.player().getName());
            timeToBeat = new TimeProperty(0);
            label.textProperty().bind(timeToBeat.asString());
            main.getChildren().add(nameLabel);
            StackPane.setAlignment(nameLabel, Pos.BOTTOM_CENTER);

            //timePane.getChildren().add(label);

        }

        public void whenIsFinished() {
           // game.stop();
            timePane.stop();
            gameTimer.stop();
            game.addToScoresList();
            timeToBeat.set(game.scores().get(0).getValue());
            //Get time to beat, it is the top of the list
            //Get to the next player and labyrinth
            if (!multi.gameOver()) {
                try {
                    game=multi.next();
                    mazePane.getChildren().clear();
                    handleAction();
                  /*  main.getChildren().add(label);
                    nameLabel.setText(game.player().getName());
                    StackPane.setAlignment(label, Pos.BOTTOM_LEFT);*/
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (FormatNotSupported formatNotSupported) {
                    formatNotSupported.printStackTrace();
                }
                catch(CloneNotSupportedException e){
                    e.printStackTrace();
                }

               // timePane.setVisible(true);
            } else {
                displayScores(game.scores());
                //displayScores(MultiView.this);
            }
        }
    }


        public void displayScore(Pane root) {
            root.getChildren().clear();
            String display = game.scores().getScores();
            String[] splits = display.split("\n");
            for (String s : splits) root.getChildren().add(new Label(s));
        }

    }

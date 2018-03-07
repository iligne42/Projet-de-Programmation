
import javafx.beans.property.IntegerProperty;
import javafx.scene.Camera;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;

import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.Stack;

public class MultiView extends View {
    protected MultiPlayerVersion multi;
    // protected Stack<Player> players;

    public MultiView(MultiPlayerVersion multi) throws IOException,FormatNotSupported{
        super(new BorderPane(),multi.getGame());
        this.multi = multi;
        control = new MultiControl();

    }

    protected class MultiControl extends GameControl {
        //protected Scores scores;
        protected IntegerProperty timeToBeat;

        public MultiControl() throws IOException, FormatNotSupported {
            super();
            Label label = new Label();
            timeToBeat = new TimeProperty(0);
            label.textProperty().bind(timeToBeat.asString());
            label.setVisible(false);
            timePane.getChildren().add(label);

        }

        public void whenIsFinished() {
            timePane.stop();
            game.addToScoresList();
            timeToBeat.set(game.scores().get(0).getValue());
            //Get time to beat, it is the top of the list
            //Get to the next player and labyrinth
            if (!multi.gameOver()) {
                try {
                    multi.next();
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (FormatNotSupported formatNotSupported) {
                    formatNotSupported.printStackTrace();
                }
                mazePane.reset();
                timePane = new SoloTimePane();
                timePane.setVisible(true);
            } else {
                //displayScore(MultiView.this);
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


import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.beans.property.IntegerProperty;
import javafx.geometry.Pos;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Stack;

public class MultiView extends View {
    protected MultiPlayerVersion multi;
    // protected Stack<Player> players;

    public MultiView(MultiPlayerVersion multi) throws IOException, FormatNotSupported {
        //super(new BorderPane(),multi.getGame());
        super(new StackPane(), multi.getGame());
        this.multi = multi;
        control = new MultiControl();

    }

    protected class MultiControl extends GameControl {
        //protected Scores scores;
        protected IntegerProperty timeToBeat;
        protected Label nameLabel;
        protected HBox toBeat = new HBox();
        protected Label label;

        public MultiControl() throws IOException, FormatNotSupported {
            super(false);
            label = new Label();
            nameLabel = new Label(game.player().getName());
            timeToBeat = new TimeProperty(0);
            label.textProperty().bind(timeToBeat.asString());
            label.setStyle("-fx-alignment:center;-fx-text-fill:crimson;-fx-font-size:50pt");
            nameLabel.setStyle("-fx-alignment:center;-fx-text-fill:white;-fx-font-size:25pt");
            toBeat.getChildren().addAll(new Label("Time to beat : "), label);
            tool.getItems().add(nameLabel);
            //StackPane.setAlignment(nameLabel, Pos.BOTTOM_CENTER);

            //timePane.getChildren().add(label);

        }

        protected class EndPane extends TimePane {
            public EndPane() {
                super(0);
                timeLine.setCycleCount(Timeline.INDEFINITE);
                timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(0.2), (event) -> {
                    game.convertBonus();
                    timePane.timeSeconds.set(game.getElapsed());
                    if (game.player.getBonus().size() == 0) {
                        game.addToScoresList();
                        timeToBeat.set(game.scores().get(0).getValue());
                        //Get time to beat, it is the top of the list
                        //Get to the next player and labyrinth
                        if (!multi.gameOver()) {
                            mazePane.printMaze();
                            try {
                                game = multi.next();
                                mazePane.getChildren().clear();
                                handleAction();
                                timePane.getChildren().add(toBeat);
                                nameLabel.setText(game.player().getName());
                  /*  main.getChildren().add(label);
                    StackPane.setAlignment(label, Pos.BOTTOM_LEFT);*/
                            } catch (IOException e1) {
                                e1.printStackTrace();
                            } catch (FormatNotSupported formatNotSupported) {
                                formatNotSupported.printStackTrace();
                            } catch (CloneNotSupportedException e) {
                                e.printStackTrace();
                            }

                            // timePane.setVisible(true);
                        } else {
                            displayScores(game.scores());
                            //displayScores(MultiView.this);
                        }
                        timeLine.stop();
                    }
                }));
            }
        }

        public void whenIsFinished() {
            timePane.stop();
            gameTimer.stop();
            MultiView.this.setOnKeyPressed(null);
            new EndPane().play();

        }


    }
}

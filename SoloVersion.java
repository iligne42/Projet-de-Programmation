
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;

import javax.swing.*;
import java.io.IOException;
import java.text.Normalizer;
import java.time.Duration;

public class SoloVersion extends GameVersion {

	public SoloVersion(int length, int width, int nbFloors,int nbObstacles,int nbMonstres,int nbTeleport,int nbDoors,int nbBonus,int typeBonus,String name, Scores score) throws FormatNotSupported, IOException {
		super(length, width,nbFloors,nbObstacles,nbMonstres,nbTeleport,nbDoors,nbBonus,typeBonus, name, score);
		timeLine.setCycleCount(Timeline.INDEFINITE);
		timeLine.getKeyFrames().add(new KeyFrame(javafx.util.Duration.seconds(1), (event) -> {
			elapsed ++;
			timeSeconds.set(elapsed);

		}));

	}

	public SoloVersion(MazeFloors maze, Player player, Scores scores) throws FormatNotSupported, IOException {
		super(maze, player, scores);
		timeLine.setCycleCount(Timeline.INDEFINITE);
		timeLine.getKeyFrames().add(new KeyFrame(javafx.util.Duration.seconds(1), (event) -> {
			elapsed ++;
			timeSeconds.set(elapsed);

		}));
	}

	public SoloVersion(MazeFloors maze, String name, Scores scores) throws FormatNotSupported, IOException {
		super(maze, name, scores);
		timeLine.setCycleCount(Timeline.INDEFINITE);
		timeLine.getKeyFrames().add(new KeyFrame(javafx.util.Duration.seconds(1), (event) -> {
			elapsed ++;
			timeSeconds.set(elapsed);

		}));
	}


	public SoloVersion(MazeFloors maze, Player player) throws FormatNotSupported, IOException {
		this(maze, player, new Scores("bestSolos.txt"));

	}

	public SoloVersion(MazeFloors maze, String name) throws FormatNotSupported, IOException {
		this(maze, name, new Scores("bestSolos.txt"));
	}

	public String scoresFile() {
		return "bestSolos.txt";
	}

}
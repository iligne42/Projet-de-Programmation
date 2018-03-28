
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.util.Duration;

import java.io.IOException;

public class TimeTrialVersion extends GameVersion {
	public final int timeLimit;


	public TimeTrialVersion(int length, int width,  int nbFloors,int nbObstacles,int nbMonstres,int nbTeleport,int nbDoors,int nbBonus,int typeBonus,String name, int time) throws FormatNotSupported,IOException{
		super(length,width,nbFloors,nbObstacles,nbMonstres,nbTeleport,nbDoors,nbBonus,typeBonus,name,new Scores("bestRaces.txt"));
		timeLimit=time;
		timeLine.setCycleCount(timeLimit);
		timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
			elapsed ++;
			timeSeconds.set(timeLimit-elapsed);

		}));
	}

    public TimeTrialVersion(MazeFloors maze, Player player, int time) throws FormatNotSupported,IOException{
        super(maze,player,new Scores("bestRaces.txt"));
        timeLimit=time;
		timeLine.setCycleCount(timeLimit);
		timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
			elapsed ++;
			timeSeconds.set(timeLimit-elapsed);

		}));
    }

    public TimeTrialVersion(MazeFloors maze, String name, int time) throws FormatNotSupported,IOException{
	    super(maze,name,new Scores("bestRaces.txt"));
	    timeLimit=time;
		timeLine.setCycleCount(timeLimit);
		timeLine.getKeyFrames().add(new KeyFrame(Duration.seconds(1), (event) -> {
			elapsed ++;
			timeSeconds.set(timeLimit-elapsed);

		}));
    }

	public String scoresFile(){
		return "bestRaces.txt";
	}





}


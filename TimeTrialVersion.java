import java.io.IOException;

public class TimeTrialVersion extends GameVersion {
	public final int timeLimit;


	public TimeTrialVersion(int length, int width, String name, int time) throws FormatNotSupported,IOException{
		super(length,width,name,new Scores("bestRaces.txt"));
		timeLimit=time;
	}

    public TimeTrialVersion(Maze maze, Player player, int time) throws FormatNotSupported,IOException{
        super(maze,player,new Scores("bestRaces.txt"));
        timeLimit=time;
    }

    public TimeTrialVersion(Maze maze, String name, int time) throws FormatNotSupported,IOException{
	    super(maze,name,new Scores("bestRaces.txt"));
	    timeLimit=time;
    }

	public String scoresFile(){
		return "bestRaces.txt";
	}





}

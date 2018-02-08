public class TimeTrialVersion extends GameVersion {
	public final int timeLimit;


	public TimeTrialVersion(int length, int width, String name, int time) throws FormatNotSupported{
		super(length,width,name);
		timeLimit=time;
	}

    public TimeTrialVersion(Maze maze, Player player, int time){
        super(maze,player);
        timeLimit=time;
    }

    public TimeTrialVersion(Maze maze, String name, int time){
	    super(maze,name);
	    timeLimit=time;
    }

	public String scoresFile(){
		return "bestRaces.txt";
	}

	public void addToScores(String score){
		//super.addToScores(score,"bestRaces.txt");
	}



}

import javax.swing.*;
import java.text.Normalizer;

public class SoloVersion extends GameVersion{



	public SoloVersion(int length, int width, String name) throws FormatNotSupported{
      	super(length,width,name);
	}

	public SoloVersion(Maze maze, Player player){
	    super(maze,player);
    }

    public SoloVersion(Maze maze, String name){
	    super(maze,name);
    }

	public String scoresFile(){
		return "bestSolos.txt";
	}

	/*public void addToScores(String score){
	    super.addToScores(score, "bestSolos.txt");
    }*/




}
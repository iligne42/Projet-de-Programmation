import javax.swing.*;
import java.io.IOException;
import java.text.Normalizer;

public class SoloVersion extends GameVersion{

	public SoloVersion(int length, int width, String name,Scores score) throws FormatNotSupported,IOException{
		super(length,width,name,score);
	}

	public SoloVersion(Maze maze, Player player,Scores scores) throws FormatNotSupported,IOException{
		super(maze,player,scores);
	}

	public SoloVersion(Maze maze, String name,Scores scores) throws FormatNotSupported,IOException{
		super(maze,name,scores);
	}

	public SoloVersion(int length, int width, String name) throws FormatNotSupported,IOException{
      	this(length,width,name,new Scores("bestSolos.txt"));
	}



	public SoloVersion(Maze maze, Player player) throws FormatNotSupported,IOException{
	    this(maze,player,new Scores("bestSolos.txt"));
    }

    public SoloVersion(Maze maze, String name) throws FormatNotSupported,IOException{
	    this(maze,name,new Scores("bestSolos.txt"));
    }

	public String scoresFile(){
		return "bestSolos.txt";
	}




}
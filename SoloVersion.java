import javax.swing.*;
import java.text.Normalizer;

public class SoloVersion extends GameVersion{


	public SoloVersion(int length, int width, String name) throws FormatNotSupported{
      	super(length,width,name);
		timer=new Timer(1000, e->{
			if(!gameOver()) timerLabel.setText(SoloVersion.this.getTime(elapsedSeconds++));
		});

	}

	public void start(){
		player.setPosition(maze.beginning(),90);
		//Add beginning in maze that gives a point with(0,0) as the beginning of the maze
		timer.start();
	}




}
import javafx.event.ActionEvent;
import javax.swing.*;
import java.text.Normalizer;
import java.util.Timer;

public class TimeTrialVersion extends GameVersion {
	protected boolean timeOver;
	public final int timeLimit;

	//Timer timer = new Timer(1000, new Chrono());

		/*private class Chrono implements ActionListener{
    		int elapsedSeconds = 120;

    		public void actionPerformed(ActionEvent evt){
        		elapsedSeconds--;
        	//timerLabel.setText(elapsedSeconds)
        	if(elapsedSeconds <= 0){
            	timer.stop();
            	wrong();
            // fill'er up here...
        }*/



	public TimeTrialVersion(int length, int width, String name, int time) throws FormatNotSupported{
		super(length,width,name);
	    timeLimit=time;
		timer=new Timer(1000,e->{
				timerLabel.setText(TimeTrialVersion.this.getTime(timeLimit-(elapsedSeconds++)));
				//timerLabel.setText(this.getRemainingTime(remainingSeconds --));
				if(elapsedSeconds==timeLimit){
				    timeOver = true;
				    timer.stop();
                }
				//timerLabel.setText()
			});
	}

	public void modifyTime(){
		elapsedSeconds --;
	}

	public void start(){
	    player.setPosition(maze.beginning(),90);
	    timer.start();
    }

	public boolean timeOver(){
	    if(timeOver){
	        //GUI.print("Oups, time over. You loose !");
	        return true;
        }

        return false;
	   // return elapsedSeconds==timeLimit;
    }

    public String getTime(){
		return getTime(timeLimit-elapsedSeconds);
	}




}

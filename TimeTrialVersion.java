public class TimeTrialVersion extends GameVersion{
	protected Timer timer;
	protected boolean timeOver;

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



	public TimeTrialVersion(String name, int time){
		timer=new Timer(time, new ActionListener(){
			int remainingSeconds=0;

			public void actionPerformed(ActionEvent evt){
				remainingSeconds ++;
				if(remainingSeconds==0){
					timeOver=true;
				}
				//timerLabel.setText()
			} 

		});
	}

	public boolean gameOver(){
		return timeOver || maze.lastOccupied();
	}


}
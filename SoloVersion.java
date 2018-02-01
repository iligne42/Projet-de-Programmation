public class SoloVersion extends GameVersion{
	Timer timer=new Timer(1000, new ActionListener(){
		private int elapsedSeconds=0;

		public void actionPerformed(ActionEvent e){
			if(!gameOver) System.out.println(elapsedSeconds++);

		}
	});

	public SoloVersion(String name){
		player=new Player(name);
		maze=new Maze();
	}

	public boolean gameOver(){
		return maze.lastOccupied();
	}


}
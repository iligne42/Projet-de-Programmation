import java.awt.Point;
import java.util.*;
public class testMaze extends maze{
	Maze maze;

	public testMaze(Maze m){
		maze=m;
		mazeTest=new boolean[m.getHeight()][m.getWidth()];
		for(boolean[] b:mazeTest)
			Arrays.fill(b,false);
	}

	public void createMazeTest(){
		Point deb = maze.beggining();

	}

	public setCase(int i,int j, boolean val){mazeTest[i][j]=val;}
}
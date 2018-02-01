import java.io.File;
import java.util.*;
public class Maze{
	private int[][] maze;
	public final static int WALL = 0;
	public final static int WAY = 1;

	public Maze(int L, int l){
		maze=new int[L][l];
		randomMaze(L,l);
	}
	
	public Maze(File fic) throw FileNotFoundException{
		Scanner sc = new Scanner(fic);
		ArrayList<String> tmp = new ArrayList<String>();
		while(sc.hasNextLine())
			tmp.add(sc.nextLine());
		int max = maxLength(tmp);
		maze = new int[tmp.size()][max];
		for(int i=0;i<tmp.size();i++){
			for(int j=0;j<max;j++){
				maze[i][j]=(tmp.get(i).charAt(j)==' '?WAY:WALL);
			}
		}
	}

	private static int maxLength(ArrayList<String> t){
		int res =0;
		for(String i:t){
			if(i.length()>res)
				res=i.length();
		}
		return res;
	}

	private void print(){
		for(int i=0;i<maze.length;i++){
			for(int j=0;j<maze[i].length;j++){
				System.out.print((maze[i][j]==WAY?' ':'0'));
			}
			System.out.println();
		}
	}

	private void randomMaze(int L,int l){

    }
}
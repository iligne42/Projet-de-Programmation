import java.io.File;
import java.util.*;
public Maze{
	private int[][] maze;
	public final static int WALL = 0;
	public final static int WAY = 1;

	public Maze(int L, int l){
	}

	public Maze(File fic){
		Scanner sc = new Scanner(fic);
		ArrayList<String> tmp = new ArrayList<String>();
		while(sc.hasNextLine())
			tmp.add(sc.nextLine());
		int max = max(tmp);
		maze = new int[tmp.size()][max];
		for(int i=0;i<si)

	}

	private static int max(int ... t){
		int res = t[0];
		for(int i:t){
			if(i>res)
				res=i;
		}
		return res;
	}
}
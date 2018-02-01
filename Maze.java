import java.io.*;
import java.util.*;
public class Maze{
	private int[][] maze;
	public final static int WALL = 0;
	public final static int WAY = 1;
	public final static int START = 2;
	public final static int END = 3;

	public Maze(int L, int l){
		maze=new int[L][l];
		randomMaze(L,l);
	}

	public Maze(File fic) throws FileNotFoundException{
		Scanner sc = new Scanner(fic);
		ArrayList<String> tmp = new ArrayList<String>();
		while(sc.hasNextLine())
			tmp.add(sc.nextLine());
		int max = maxLength(tmp);
		maze = new int[tmp.size()][max];
		for(int i=0;i<tmp.size();i++){
			for(int j=0;j<max;j++){
				switch (tmp.get(i).charAt(j)){
					case 'S':
					maze[i][j]=START;break;
					case ' ':
					maze[i][j]=WAY;break;
					default:
					maze[i][j]=WALL;break;
				}
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

	public static boolean sameLength(ArrayList<String> t){
		int tmp = t.get(0).length();
		for(String i:t){
			if(i.length()!=tmp)
				return false;
		}
		return true;
	}

	private void print(){
		for(int i=0;i<maze.length;i++){
			for(int j=0;j<maze[i].length;j++){
				String tmp="";
				switch(maze[i][j]){
					case WAY: tmp=" ";break;
					case START: tmp=" ";break;
					case END: tmp=" ";break;
					case WALL: tmp="\\";break;
				}
				System.out.print(tmp);
			}
			System.out.println( );
		}
	}

	private void randomMaze(int L,int l){

    }

    public static void main(String[] args){
    	try{
    		File fic = new File("labiTest.txt");
    		Maze m = new Maze(fic);
    		m.print();
    	}catch(Exception e){
    		//e.printStackTraceException();
    	}
    }
}
import javafx.geometry.Point2D;

import java.io.*;
import java.util.*;
import java.awt.*;
public class Maze{
	private int[][] maze;
	public final static int WALL = 0;
	public final static int WAY = 1;
	public final static int START = 2;
	public final static int END = 3;

	public Maze(int L, int l) throws FormatNotSupported{
		if(L>3 && l>3) {
            maze = new int[L][l];
            randomMaze();
        }else{
		    throw new FormatNotSupported("Le labyrinthe est trop petit");
        }
	}

	public Maze(File fic) throws FileNotFoundException, FormatNotSupported{
		Scanner sc = new Scanner(fic);
		ArrayList<String> tmp = new ArrayList<String>();
		while(sc.hasNextLine())
			tmp.add(sc.nextLine());
		if(!sameLength(tmp)) 
			throw new FormatNotSupported("Le labyrinthe n'est pas carré");
		int max = maxLength(tmp);
		maze = new int[tmp.size()][max];
		for(int i=0;i<tmp.size();i++){
			for(int j=0;j<max;j++){
				switch (tmp.get(i).charAt(j)){
					case 'S':
					maze[i][j]=START;break;
					case ' ':
					if(i==0||j==0||i==maze.length-1||j==maze[i].length-1)
						maze[i][j]=END;
					else maze[i][j]=WAY;
					break;
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
					case START: tmp="S";break;
					case END: tmp="E";break;
					case WALL: tmp="\\";break;
				}
				System.out.print(tmp);
			}
			System.out.println( );
		}
	}

	private void randomMaze(){
        createBorder();
    }

    private void createBorder(){
	    for(int i=0; i<maze.length; i++){
	        for(int j=0; j<maze[i].length; j++){
	            if(i==0||j==0||i==maze.length-1||j==maze[i].length-1) maze[i][j]=WALL;
            }
        }
        Point Start=createStartEnd();
	    setStartEnd(Start,START);
	    Point End=createStartEnd();
	    while(Start.equals(End)) End=createStartEnd();
	    setStartEnd(End,END);
    }

    private Point createStartEnd(){
	    Random rand=new Random();
	    int VH=rand.nextInt(2);
	    int i=0; int j=0;
	    if(VH==0){
	        i=rand.nextInt(2);
	        if(i==1) i=maze.length-1;
	        j=rand.nextInt(maze[i].length);
        }else{
	        i=rand.nextInt(maze.length);
	        j=rand.nextInt(2);
	        if(j==1) j=maze[i].length-1;
        }
        return new Point(i,j);
    }

    private void setStartEnd(Point p, int val){
	    int i=(int)p.getX();
	    int j=(int)p.getY();
	    maze[i][j]=val;
    }

    private void createInside(){
	    //Appeler les fonctions de création de murs vertical et horizontal
    }

    private void horizontal(int h){ //Commencer avec v=-1;
	    Random rand=new Random();
	    int i=rand.nextInt(maze.length-h);
	    if(onAWall(i)) {
            for (int j = 0; j < maze[i].length; j++) {
                int tmp = rand.nextInt(maze[i].length - h);
                if (j != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else horizontal(h);
    }

    private void vertical(int v){
	    Random rand=new Random();
	    int j=rand.nextInt(v);//v?
        for(int i=0; i<maze.length; i++){
            int tmp=rand.nextInt(maze.length);
            if(i!=tmp) maze[i][j]=WALL;
            else maze[i][j]=WAY;
        }
    }

    private boolean onAWall(int i){//vérifie qu'on créer le nouveau mur sur un mur et pas un "trou"
	    if(maze[i][0]==WALL && maze[i][maze[i].length-1]==WALL) return true;
	    else return false;
    }

    public static void main(String[] args){
    	try{
    		File fic = new File("labiTest.txt");
    		Maze m = new Maze(fic);
    		m.print();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }

    public int get(int i, int j){
		return maze[i][j];
	}
	public Point2D beginning(){
		return new Point2D(0,0);
	}

	public Point2D ending(){
		return new Point2D(0,0);
	}
}
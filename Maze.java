import java.io.*;
import java.util.*;
import java.awt.*;
public class Maze{
	private int[][] maze;
	public final static int WALL = 0;
	public final static int WAY = 1;
	public final static int START = 2;
	public final static int END = 3;
	Random rand=new Random();

	public Maze(int L, int l) throws FormatNotSupported{
		if(L>3 && l>3) {
            maze = new int[L][l];
            randomMaze();
        }else{
		    throw new FormatNotSupported("Le labyrinthe est trop petit");
        }
	}
	

	public Maze(File fic) throws FileNotFoundException, FormatNotSupported{
		int nbEnter=0;
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
					maze[i][j]=START;nbEnter++;break;
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
		if(nbEnter!=1)
			throw new FormatNotSupported("Le labyrinthe possede "+nbEnter+" entrée et doit en posséder 1.");
		
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
					case WALL: tmp=""+(char)9608;break;
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

    private void createInside(int x1, int x2, int y1, int y2, int width, int height){ // attribut de délimitations, pour vertical et pour horizontal
	    //Appeler les fonctions de création de murs vertical et horizontal
        int orientation=orientation(width,height);
        if(orientation==0) horizontal(1);
        else vertical(2);
    }

    private void horizontal(int h){ //Commencer avec v=-1;
	    Random rand=new Random();
	    int i=rand.nextInt(maze.length-h);
	    if(onAWall(i,0)) {
            for (int j = 0; j < maze[i].length; j++) {
                int tmp = rand.nextInt(maze[i].length - h)+1;//il ne faut pas que tmp soit sur une extrémité du mur si on repasse sur un mur déjà existant
                if (j != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else horizontal(h);
    }

    private void vertical(int v){
	    Random rand=new Random();
	    int j=rand.nextInt(v);//v?
        if(onAWall(0,j)) {
            for (int i = 0; i < maze.length; i++) {
                int tmp = rand.nextInt(maze.length)+1;
                if (i != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }
    }

    private boolean onAWall(int i, int j){//vérifie qu'on créer le nouveau mur sur un mur et pas un "trou"
	    if(maze[i][0]==WALL && maze[i][maze[i].length-1]==WALL) return true;
	    else if(maze[0][j]==WALL && maze[maze.length-1][j]==WALL) return true;
	    else return false;
    }

    private int orientation(int width, int height){
	    if(width<height) return 0; //0 pour horizontal
        else if(height<width) return 1; //1 pour vertical
        else return rand.nextInt(2);
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
}
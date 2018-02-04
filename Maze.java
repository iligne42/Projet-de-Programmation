import java.io.*;
import java.util.*;
import java.awt.*;
public class Maze{
	private int[][] maze;
	public final static int WALL = 0;
	public final static int WAY = 1;
	public final static int START = 2;
	public final static int END = 3;
	static Random rand=new Random();

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
        createInside(0,maze.length-1,0,maze[0].length-1);
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

    private void createInside(int x1, int x2, int y1, int y2){ // attribut de délimitations, pour vertical et pour horizontal
	    //Appeler les fonctions de création de murs vertical et horizontal
        int width=Math.abs(x2-x1);
        int height=Math.abs(y2-y1);
        if (height<maze.length && width<maze[height].length)
        if(width<2 || height<2) return; // si on a plus de mur à créer on arrêt la fonction
        int orientation=orientation(width,height);
        if(orientation==0) {
            int Y=horizontal(1,x1,x2,y1,y2);
            createInside(x1,x2,y1,Y);
            createInside(x1,x2,Y,y2);
        }
        else {
            int X=vertical(1,x1,x2,y1,y2);
            createInside(x1,X,y1,y2);
            createInside(X,x2,y1,y2);
        }
    }

    private int horizontal(int h, int x1, int x2, int y1, int y2){ //Commencer avec v=-1;
	    int i=rand.nextInt(y2-y1-1)+y1;//y2=bas de la délimitation, y1=haut de la délimitation;
	    if(onAWall(i,x1,x2)) {
            for (int j = x1; j < x2; j++) {
                int tmp = rand.nextInt(x2-x1-1)+x1;//il ne faut pas que tmp soit sur une extrémité du mur si on repasse sur un mur déjà existant
                if (j != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else horizontal(h,x1,x2,y1,y2);
	    return i;
    }

    private int vertical(int v,int x1, int x2, int y1, int y2){
	    int j=rand.nextInt(x2-x1-1)+x1;//v?
        if(onAWall(j, y1, y2)) {
            for (int i = y1; i < y2; i++) {
                int tmp = rand.nextInt(y2-y1-1)+y1;
                if (i != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else vertical(v,x1,x2,y1,y2);
        return j;
    }

    private boolean onAWall(int a, int b, int c){//vérifie qu'on créer le nouveau mur sur un mur et pas un "trou"
	    if(maze[a][b]==WALL && maze[a][c]==WALL) return true;
	    else if(maze[b][a]==WALL && maze[c][a]==WALL) return true;
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
    		Maze m = new Maze(10,10);
    		m.print();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
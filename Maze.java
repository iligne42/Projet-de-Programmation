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
		if(L>5 && l>5) {
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
			throw new FormatNotSupported("Le labyrinthe n'est pas rectangle");
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

	public Point beggining(){
	    Point p=new Point();
	    for(int i=0; i<maze.length; i++){
	        for(int j=0; j<maze[i].length; j++){
	            if(maze[i][j]==START) p.move(i,j);
            }
        }
        return p;
    }

    public int getWidth(){return maze[0].length;}
    public int getHeight(){return maze.length;}
    public int getCase(Point p){return getCase((int)p.getX(),(int)p.getY());}
    public int getCase(int i, int j){return maze[i][j];}

    public Point ending(){
        Point p=new Point();
        for(int i=0; i<maze.length; i++){
            for(int j=0; j<maze[i].length; j++){
                if(maze[i][j]==END) p.move(i,j);
            }
        }
        return p;
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
	    String RED="\u001B[31m";
	    String Normal="\u001B[0m";
		for(int i=0;i<maze.length;i++){
			for(int j=0;j<maze[i].length;j++){
				String tmp="";
				switch(maze[i][j]){
					case WAY: tmp="  ";break;
					case START: tmp=RED+"SS"+Normal;break;
					case END: tmp=RED+"EE"+Normal;break;
					case WALL: tmp=""+(char)9608+(char)9608;break;
				}
				System.out.print(tmp);
			}
			System.out.println( );
		}
		System.out.println();
	}

	private void randomMaze(){
        do{
            createBorder();
            createInside(0,maze[0].length-1,0,maze.length-1);
        }while(!possibleMaze());
    }

    private void createBorder(){
	    for(int i=0; i<maze.length; i++){
	        for(int j=0; j<maze[i].length; j++){
	            if(i==0||j==0||i==maze.length-1||j==maze[i].length-1) maze[i][j]=WALL;
	            else maze[i][j]=WAY;
            }
        }
        Point Start=createStartEnd();
	    setStartEnd(Start,START);
	    Point End=createStartEnd();
	    while(Start.getX()==End.getX()||Start.getY()==End.getY()) End=createStartEnd();
	    setStartEnd(End,END);
    }

    private Point createStartEnd(){
	    int VH=rand.nextInt(2);
	    int i=0; int j=0;
	    if(VH==0){
	        i=rand.nextInt(2);
	        if(i==1) i=maze.length-1;
	        j=rand.nextInt(maze[i].length-2)+1;
        }else{
	        i=rand.nextInt(maze.length-2)+1;
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
        boolean flag=false;
        int width=Math.abs(x2-x1);
        int height=Math.abs(y2-y1);
        //System.out.println("width="+width+ " height="+height);
        int incr=0;
        do {
            if (height < maze.length && width < maze[height].length) {
                if (width < 4 || height < 4) return; // si on a plus de mur à créer on arrêt la fonction
                int orientation = orientation(width, height);
                if (orientation == 0) {
                    int Y = horizontal(x1, x2, y1, y2);
                    if (Y == -1){
                        flag = true;

                    }
                    //return;
                    else {
                        //print();
                        createInside(x1, x2, y1, Y);
                        createInside(x1, x2, Y, y2);
                    }
                } else {
                    int X = vertical(x1, x2, y1, y2);
                    if (X == -1){
                        flag = true;

                    }
                    //return;
                    else {
                        //print();
                        createInside(x1, X, y1, y2);
                        createInside(X, x2, y1, y2);
                    }
                }
            }
            incr++;
        }while(flag && incr<3);
    }

    private static int entreDeux(int x, int y){
	    return rand.nextInt(Math.abs(x-y)-1)+Math.min(x,y)+1;
    }

    private static int entreDeuxMurs(int x, int y){
	    return entreDeux(Math.min(x,y)+1, Math.max(x,y)-1);
    }

    private int horizontal(int x1, int x2, int y1, int y2){
	    if(Math.abs(x1-x2)<=3) return -1;
	    int i=entreDeuxMurs(y1,y2);//y2=bas de la délimitation, y1=haut de la délimitation;
        for(int val1=x1+1;val1<x2;val1++)
            if(maze[i][val1]!=WAY) return -1;
	    if(onAWall(i,x1,x2, true)) {
	        //System.out.println("mur horizontal en "+i);
            int tmp = entreDeux(x1,x2);
            //System.out.println("trou en"+tmp);
            for (int j = x1+1; j < x2; j++) {
                if (j != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else return -1;
	    return i;
    }

    private int vertical(int x1, int x2, int y1, int y2){
	    if(Math.abs(y1-y2)<=3) return -1;
	    int j=entreDeuxMurs(x1,x2);
	    for(int val1=y1+1;val1<y2;val1++)
	        if(maze[val1][j]!=WAY) return -1;
        if(onAWall(j, y1, y2, false)) {
            //System.out.println("mur vertical en "+j);
            int tmp = entreDeux(y1,y2);
            //System.out.println("trou en"+tmp);
            for (int i = y1+1; i < y2; i++) {
                if (i != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else return -1;
        return j;
    }

    private boolean onAWall(int a, int b, int c, boolean d){//vérifie qu'on créer le nouveau mur sur un mur et pas un "trou"
	    if(d && maze[a][b]==WALL && maze[a][c]==WALL) return true;
	    else if(!d && maze[b][a]==WALL && maze[c][a]==WALL) return true;
	    else return false;
    }

    private int orientation(int width, int height){
	    if(width<height) return 0; //0 pour horizontal
        else if(height<width) return 1; //1 pour vertical
        else return rand.nextInt(2);
    }

    private boolean possibleMaze(){
	    int[][] t=copieMaze();
	    Point b=beggining();
	    visiter(b,t);
	    Point e=ending();
	    if(t[(int)e.getX()][(int)e.getY()]==-1){ return true;}
	    else return false;
    }

    private int[][] copieMaze(){
	    int[][]res=new int[maze.length][maze[0].length];
	    for(int i=0; i<maze.length; i++){
	        for(int j=0; j<maze[i].length; j++){
	            res[i][j]=maze[i][j];
            }
        }
        return res;
    }

    /*private boolean possiblePath(Point p){
	    if(p==ending()) return true;
	    if(maze[(int)p.getX()-1][(int)p.getY()]==WAY){
            p.translate(-1,0);
	        return possiblePath(p);
        }if(maze[(int)p.getX()+1][(int)p.getY()]==WAY){
            p.translate(1,0);
            return possiblePath(p);
        }if(maze[(int)p.getX()][(int)p.getY()-1]==WAY){
	        p.translate(0,-1);
	        return possiblePath(p);
        }if(maze[(int)p.getX()][(int)p.getY()+1]==WAY){
	        p.translate(0,1);
	        return possiblePath(p);
        }
        else return false;
    }*/

    private void visiter(Point b, int[][] test){
	    int vue=-1;
	    int X = (int)b.getX();
	    int Y = (int)b.getY();
	    //int[][] test=this.maze;
	    //Point b=beggining();
	    test[X][Y]=vue;
        if(X-1>=0 && X-1<test.length && (test[X-1][Y]==WAY||test[X-1][Y]==END)){
            visiter(new Point(X-1,Y),test);
        }if(X+1>=0 && X+1<test.length && (test[X+1][Y]==WAY||test[X+1][Y]==END)){
        	visiter(new Point(X+1,Y),test);
        }if(Y-1>=0 && Y-1<test[X].length && (test[X][Y-1]==WAY||test[X][Y-1]==END)){
        	visiter(new Point(X,Y-1),test);
        }if(Y+1>=0 && Y+1<test[X].length && (test[X][Y+1]==WAY||test[X][Y+1]==END)) {
			visiter(new Point(X, Y + 1), test);
		}
    }


    public static void main(String[] args){
	    //System.out.println(entreDeuxMurs(2,6));
        //System.out.println(entreDeuxMurs(2,6));
        //System.out.println(entreDeuxMurs(2,6));
        //System.out.println(entreDeuxMurs(2,5));
    	try{
    		File fic = new File("labiTest.txt");
    		Maze m = new Maze(80,60);
    		m.print();
    		//System.out.println(m.beggining());
    		//System.out.println(m.ending());
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
import java.awt.geom.Point2D;
import java.io.*;
import java.util.*;
import java.awt.*;
public class Maze implements Serializable{
	private int[][] maze;
	public final static int WALL = 0;
	public final static int WAY = 1;
	public final static int START = 2;
	public final static int END = 3;
	private static Random rand=new Random();

	public Maze(int L, int l) throws FormatNotSupported{ //constructeur pour un labyrinthe aléatoire
		if(L>5 && l>5) {
            maze = new int[L][l];
            randomMaze();
        }else{
		    throw new FormatNotSupported("The maze is too small");
        }
	}
	

	public Maze(File fic) throws FileNotFoundException, FormatNotSupported{ //constructeur qui créer un labyrinthe à partir d'un fichier
		int nbEnter=0;
		Scanner sc = new Scanner(fic);
		ArrayList<String> tmp = new ArrayList<String>();
		while(sc.hasNextLine())
			tmp.add(sc.nextLine());
		if(!sameLength(tmp)) 
			throw new FormatNotSupported("The maze isn't rectangular");
		int max = tmp.get(0).length();
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
			throw new FormatNotSupported("The maze has "+nbEnter+" starts and must only have 1.");
		
	}

	public Point beginning(){ //renvoie les coordonnées de la case de départ
	    Point p=new Point();
	    for(int i=0; i<maze.length; i++){
	        for(int j=0; j<maze[i].length; j++){
	            if(maze[i][j]==START) p.move(j,i);
            }
        }
        return p;
    }

    public int getWidth(){return maze[0].length;}
    public int getHeight(){return maze.length;}
    public int getCase(Point2D p){return getCase((int)p.getY(),(int)p.getX());}
    public int getCase(int i, int j){return maze[i][j];}

    public Point ending(){ //renvoie les coordonnées de la case d'arrivée
        Point p=new Point();
        for(int i=0; i<maze.length; i++){
            for(int j=0; j<maze[i].length; j++){
                if(maze[i][j]==END) p.move(j,i);
            }
        }
        return p;
    }

	public static boolean sameLength(ArrayList<String> t){ //vérifie que toutes les chaînes de caractères d'une ArrayList font la même taille
		int tmp = t.get(0).length();
		for(String i:t){
			if(i.length()!=tmp)
				return false;
		}
		return true;
	}

	private void print(){ //affiche le labyrinthe dans le terminal
	    String RED="\u001B[45m";
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

	private void randomMaze(){ //crée le labyrinthe aléatoire
        do{
            createBorder();
            createInside(0,maze[0].length-1,0,maze.length-1);
        }while(!possibleMaze());
    }

    private void createBorder(){ //crée la "base" du labyrinthe(contour)
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

    private Point createStartEnd(){ //crée les coordonnées pour les cases de départ et d'arrivée
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

    private void setStartEnd(Point p, int val){ //met à jour une case du labyrinthe en fonction des coordonnées du point et de la valeur donnée
	    int i=(int)p.getX();
	    int j=(int)p.getY();
	    maze[i][j]=val;
    }

    private void createInside(int x1, int x2, int y1, int y2){ //crée les Murs du labyrinthe(intérieur)
        boolean flag=false;
        int width=Math.abs(x2-x1);
        int height=Math.abs(y2-y1);
        int incr=0;
        do {
            if (height < maze.length && width < maze[height].length) {
                if (width < 4 || height < 4) return; // si l'espace est trop petit pour créer un mur, on arrête la fonction
                int orientation = orientation(width, height);
                if (orientation == 0) {
                    int Y = horizontal(x1, x2, y1, y2);
                    if (Y == -1){
                        flag = true;

                    }
                    else {
                        createInside(x1, x2, y1, Y);
                        createInside(x1, x2, Y, y2);
                    }
                } else {
                    int X = vertical(x1, x2, y1, y2);
                    if (X == -1){
                        flag = true;

                    }
                    else {
                        createInside(x1, X, y1, y2);
                        createInside(X, x2, y1, y2);
                    }
                }
            }
            incr++;
        }while(flag && incr<3);
    }

    private static int entreDeux(int x, int y){ //renvoit un entier compris entre x et y exclus
	    return rand.nextInt(Math.abs(x-y)-1)+Math.min(x,y)+1;
    }

    private static int entreDeuxMurs(int x, int y){ // renvoit un entier compris entre x-1 et y-1 exclus
	    return entreDeux(Math.min(x,y)+1, Math.max(x,y)-1);
    }

    private int horizontal(int x1, int x2, int y1, int y2){ //crée un mur horizontal
	    if(Math.abs(x1-x2)<=3) return -1;
	    int i=entreDeuxMurs(y1,y2);//y2=bas de la délimitation, y1=haut de la délimitation;
        for(int val1=x1+1;val1<x2;val1++)
            if(maze[i][val1]!=WAY) return -1;
	    if(onAWall(i,x1,x2, true)) {
            int tmp = entreDeux(x1,x2);
            for (int j = x1+1; j < x2; j++) {
                if (j != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else return -1;
	    return i;
    }

    private int vertical(int x1, int x2, int y1, int y2){ //crée un mur vertical
	    if(Math.abs(y1-y2)<=3) return -1;
	    int j=entreDeuxMurs(x1,x2);
	    for(int val1=y1+1;val1<y2;val1++)
	        if(maze[val1][j]!=WAY) return -1;
        if(onAWall(j, y1, y2, false)) {
            int tmp = entreDeux(y1,y2);
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

    private int orientation(int width, int height){ //renvoit 0 si height est plus grand, 1 si width est plus grand et un nombre aléatoire entre 0 et 1 si ils font la même taille
	    if(width<height) return 0; //0 pour horizontal
        else if(height<width) return 1; //1 pour vertical
        else return rand.nextInt(2);
    }

    private boolean possibleMaze(){ //vérifie que le labyrinthe peut se réaliser
	    int[][] t=copyMaze();
	    Point begin=beginning();
	    Point b=new Point((int)begin.getY(),(int)begin.getX());
	    visit(b,t);
	    Point end=ending();
	    Point e=new Point((int)end.getY(),(int)end.getX());
	    return (t[(int)e.getX()][(int)e.getY()]==-1);
    }

    private int[][] copyMaze(){ //copie le tableau du labyrinthe dans un autre tableau
	    int[][]res=new int[maze.length][maze[0].length];
	    for(int i=0; i<maze.length; i++){
	        for(int j=0; j<maze[i].length; j++){
	            res[i][j]=maze[i][j];
            }
        }
        return res;
    }

    private void visit(Point b, int[][] test){ //change toutes les cases accessibles par la valeur de vue
	    int vue=-1;
	    int X = (int)b.getX();
	    int Y = (int)b.getY();
	    test[X][Y]=vue;
        if(X-1>=0 && X-1<test.length && (test[X-1][Y]==WAY||test[X-1][Y]==END)){
            visit(new Point(X-1,Y),test);
        }if(X+1>=0 && X+1<test.length && (test[X+1][Y]==WAY||test[X+1][Y]==END)){
        	visit(new Point(X+1,Y),test);
        }if(Y-1>=0 && Y-1<test[X].length && (test[X][Y-1]==WAY||test[X][Y-1]==END)){
        	visit(new Point(X,Y-1),test);
        }if(Y+1>=0 && Y+1<test[X].length && (test[X][Y+1]==WAY||test[X][Y+1]==END)) {
			visit(new Point(X, Y + 1), test);
		}
    }



    public static void main(String[] args){
    	try{
    		File fic = new File("labiTest.txt");
    		Maze m = new Maze(80,80);
    		m.print();
    	}catch(Exception e){
    		e.printStackTrace();
    	}
    }
}
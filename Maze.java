import java.awt.geom.Point2D;
import java.io.*;
import java.text.Normalizer;
import java.util.*;
import java.awt.*;
import javafx.util.*;
public class Maze implements Serializable{
    private int[][] maze;
    private LinkedList<Obstacles> obstacles;
    private LinkedList<Monstres> monstres;
    private LinkedList<Pair<Teleporteur,Teleporteur>> teleport;
    private LinkedList<Door> doors;
    private LinkedList<Bonus> bonus;
    public final static int WALL = 0;
    public final static int WAY = 1;
    public final static int START = 2;
    public final static int END = 3;
    public final static int OBSTACLE = 4;
    public final static int STAIRSUP=5;
    public final static int STAIRSDOWN=6;
    public final static int MONSTRE=7;
    public final static int TELEPORT=8;
    public final static int DOOR=9;
    public final static int KEY=10;
    public final static int BONUS=11;
    private static Random rand=new Random();

    public Maze(int L, int l)throws FormatNotSupported{ //constructeur pour un labyrinthe aléatoire
        if(L>5 && l>5) {
            maze = new int[L][l];
            randomMaze();
        }else{
            throw new FormatNotSupported("The maze is too small");
        }
    }

    public Maze(int L, int l, int nbObstacles, int nbMonstres, int nbTeleport , int nbDoors, int nbBonus ,int typeBonus) throws FormatNotSupported{
        if(L<5 || l<5) throw new FormatNotSupported("The maze is too small");
        maze = new int[L][l];
        if(nbObstacles==0){
            obstacles=null;
            randomMaze();
        }else {
            obstacles = new LinkedList<>();
            int typeObs=rand.nextInt(2);
            String shapeObs="";
            if(typeObs==0) shapeObs="Rectangle";
            else shapeObs="Cercle";
            randomMaze(nbObstacles, shapeObs);
        }
        if(nbMonstres==0) monstres=null;
        else{
            monstres=new LinkedList<>();
            addMonstres(nbMonstres); //attention à la place
        }
        if(nbTeleport==0) teleport=null;
        else{
            teleport=new LinkedList<>();
            addTeleport(nbTeleport); //attention à la place
        }
        if(nbDoors==0) doors=null;
        else{
            doors=new LinkedList<>();
            //System.out.println("on va ajouter les portes");
            addDoor(nbDoors); //attention à la place
        }if(nbBonus==0 || (typeBonus!=0 && typeBonus!=1)){
            bonus=null;
        }
        else{
            bonus=new LinkedList<>();
            addBonus(nbBonus,typeBonus);
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

    public static boolean sameLength(ArrayList<String> t){ //vérifie que toutes les chaînes de caractères d'une ArrayList font la même taille
        int tmp = t.get(0).length();
        for(String i:t){
            if(i.length()!=tmp)
                return false;
        }
        return true;
    }

    public int getWidth(){return maze[0].length;}
    public int getHeight(){return maze.length;}
    public int getCase(Point2D p){return getCase((int)p.getY(),(int)p.getX());}
    public int getCase(int i, int j){return maze[i][j];}
    public LinkedList<Monstres> getMonstres() { return monstres; }
    public LinkedList<Obstacles> getObstacles(){ return obstacles;}
    public String getTypeObstacle(){return obstacles.getFirst().getShape();}
    public LinkedList<Bonus> getBonus(){return bonus;}
    public String detTypeBonus(){return bonus.getFirst().getAvantage();}
    public void free(int i, int j){ maze[i][j]=WAY;}

    public Point specialPlaces(int value){
        Point p=new Point();
        for(int i=0; i<maze.length; i++){
            for(int j=0; j<maze[i].length; j++){
                if(maze[i][j]==value) p.move(j,i);
            }
        }
        return p;
    }

    public Point beginning(){ //renvoie les coordonnées de la case de départ
        if(haveValue(START)) return specialPlaces(START);
        else return specialPlaces(STAIRSDOWN);
    }

    public Point ending(){ //renvoie les coordonnées de la case d'arrivée
        if(haveValue(END)) return specialPlaces(END);
        else return specialPlaces(STAIRSUP);
    }

    public boolean haveValue(int value){
        for(int i=0; i<maze.length; i++){
            for(int j=0; j<maze[i].length; j++){
                if(maze[i][j]==value) return true;
            }
        }
        return false;
    }

    public void print(){ //affiche le labyrinthe dans le terminal
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
                    case STAIRSUP: tmp=RED+"SU"+Normal;break;
                    case STAIRSDOWN: tmp=RED+"SD"+Normal;break;
                    case OBSTACLE: tmp=RED+"OO"+Normal;break;
                    case MONSTRE: tmp=RED+"MM"+Normal;break;
                    case TELEPORT: tmp=RED+"TT"+Normal;break;
                    case DOOR: tmp=RED+"DD"+Normal;break;
                    case KEY: tmp=RED+"KK"+Normal;break;
                    case BONUS :tmp=RED+"BB"+Normal;break;
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

    private void randomMaze(int nbObstacles, String typeObstacle){ //crée le labyrinthe aléatoire avec obstacles
        randomMaze();
        int tmp=0;
        while(nbObstacles!=0 && tmp!=5){
            //Point p=addObstacle();
            Obstacles o=new Obstacles(this, typeObstacle);
            obstacles.add(o);
            fill(OBSTACLE, o.getPosition());
            if(possibleMaze()) nbObstacles--;
            else {
                removeObstacle(o.getPosition());
                obstacles.removeLast();
                tmp++;
            }
        }
        if(tmp==5) System.out.println("Nous ne pouvions pas mettre autant d'obstacles. Il y en manque donc "+nbObstacles+".");
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
            if(i==1){ i=maze.length-1;}
            j=rand.nextInt(maze[i].length-3)+1;
        }else{
            i=rand.nextInt(maze.length-3)+1;
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

    private void createInside(int x1, int x2, int y1, int y2){ //crée les murs du labyrinthe(intérieur)
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

    private static int betweenTwo(int x, int y){ //renvoit un entier compris entre x et y exclus
        return rand.nextInt(Math.abs(x-y)-1)+Math.min(x,y)+1;
    }

    private static int betweenTwoWalls(int x, int y){ // renvoit un entier compris entre x-1 et y-1 exclus
        return betweenTwo(Math.min(x,y)+1, Math.max(x,y)-1);
    }

    private int horizontal(int x1, int x2, int y1, int y2){ //crée un mur horizontal
        if(Math.abs(x1-x2)<=3) return -1;
        int i=betweenTwoWalls(y1,y2);//y2=bas de la délimitation, y1=haut de la délimitation;
        for(int val1=x1+1;val1<x2;val1++)
            if(maze[i][val1]!=WAY) return -1;
        if(onAWall(i,x1,x2, true)) {
            int tmp = betweenTwo(x1,x2);
            for (int j = x1+1; j < x2; j++) {
                if (j != tmp) maze[i][j] = WALL;
                else maze[i][j] = WAY;
            }
        }else return -1;
        return i;
    }

    private int vertical(int x1, int x2, int y1, int y2){ //crée un mur vertical
        if(Math.abs(y1-y2)<=3) return -1;
        int j=betweenTwoWalls(x1,x2);
        for(int val1=y1+1;val1<y2;val1++)
            if(maze[val1][j]!=WAY) return -1;
        if(onAWall(j, y1, y2, false)) {
            int tmp = betweenTwo(y1,y2);
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
        Point e=new Point((int)end.getY(), (int)end.getX());
        if(t[(int)e.getX()][(int)e.getY()]==-1){ return true;}
        else return false;
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
        if(X-1>=0 && X-1<test.length && (test[X-1][Y]==WAY||test[X-1][Y]==END)){ //ajouter les portes,les clés,les bonus,les monstres
            visit(new Point(X-1,Y),test);
        }if(X+1>=0 && X+1<test.length && (test[X+1][Y]==WAY||test[X+1][Y]==END)){ //ou sinon juste différent de murs et obstacle
            visit(new Point(X+1,Y),test);
        }if(Y-1>=0 && Y-1<test[X].length && (test[X][Y-1]==WAY||test[X][Y-1]==END)){ //si on a des portes, vérifier qu'on a bien pris la clé associée
            visit(new Point(X,Y-1),test);
        }if(Y+1>=0 && Y+1<test[X].length && (test[X][Y+1]==WAY||test[X][Y+1]==END)) {
            visit(new Point(X, Y + 1), test);
        }
    }


    public void changeEnding(){
        Point p=ending();
        int i=(int)p.getX();
        int j=(int)p.getY();
        maze[j][i]=STAIRSUP;
    }

    public void changeBeginning(){
        Point p=beginning();
        int i=(int)p.getX();
        int j=(int)p.getY();
        maze[j][i]=STAIRSDOWN;
    }

    /*private Point fill(int value){
        int i=0;
        int j=0;
        while(maze[i][j]!=WAY){
            i=rand.nextInt(maze.length);
            j=rand.nextInt(maze[i].length);
        }
        maze[i][j]=value;
        return new Point(i,j);
    }*/
    public Key getKey(Point2D point){
      for (Door a : doors)
        if(a.isTheKey(point))return a.getKey();
      return null;
    }

    public Bonus getBonus(Point2D point){
      for (Bonus a : bonus)
        if(a.getPosition().equals(point))return a;
      return null;
    }

    public void fill(int value, Point2D p){
        maze[(int)p.getY()][(int)p.getX()]=value;
    }

    /*private Point addObstacle(){
        return fill(OBSTACLE);
    }*/

    private void removeObstacle(Point2D p){
        int i=(int)p.getX();
        int j=(int)p.getY();
        maze[j][i]=WAY;
    }

    private void addMonstres(int nb){
        while(nb!=0){
            Monstres m=new Monstres(this);
            monstres.add(m);
            fill(MONSTRE,m.getPosition());
            nb--;
        }
    }


    /*public boolean moveMonstre(Monstres m, int i, int j){
        Point2D p=m.getPosition();
        if(maze[i][j]==WAY) {
            removeObstacle(p);
            maze[i][j] = MONSTRE;
            return true;
        }
        return false;
    }*/

    private void addTeleport(int nb){
        while(nb!=0) {
            Teleporteur t1 = new Teleporteur(this);
            while(existTeleport(t1)){ t1=new Teleporteur(this);}
            Teleporteur t2 = new Teleporteur(this, t1.getStart(), t1.getEnd());
            teleport.add(new Pair<>(t1, t2));
            fill(TELEPORT, t1.getStart());
            //System.out.println(t1.getStart().getY()+" "+t1.getStart().getX());
            fill(TELEPORT, t1.getEnd());
            //System.out.println(t1.getEnd().getY()+" "+t1.getEnd().getX());
            nb--;
        }
    }

    private boolean existTeleport(Teleporteur t){
        for(int i=0; i<teleport.size(); i++){
            if(teleport.get(i).getKey().equals(t) || teleport.get(i).getValue().equals(t)) return true;
        }
        return false;
    }

    private void addDoor(int nb){
        while(nb!=0){
            //System.out.println("on est dans la fonction ajouter porte");
            Door d=new Door(this);
            while(existDoor(d)){ d=new Door(this);}
            doors.add(d);
            fill(DOOR, d.getPosition());
            fill(KEY, d.getKeyPlace());
            nb--;
        }
    }

    private boolean existDoor(Door d){
        for(int i=0; i<doors.size(); i++){
            if(doors.get(i).equals(d)) return true;
        }
        return false;
    }

    public Door getDoor(Point2D point){
      for (Door a : doors){
        if(a.getPosition().equals(point))
          return a;
      }
      return null;    
    }

    private void addBonus(int nb, int type){
        while(nb!=0){
            if(type==0){
                TimeBonus tb=new TimeBonus(this);
                bonus.add(tb);
                fill(BONUS, tb.getPosition());
            }else{
                Piece p=new Piece(this);
                bonus.add(p);
                fill(BONUS, p.getPosition());
            }
            nb--;
        }
    }

    public static void main(String[] args){
        try{
            File fic = new File("labiTest.txt");
            Maze m = new Maze(fic);
            m.print();
            /*Maze m = new Maze(30,30,0,1,0,0,0,0);
            m.print();
            LinkedList<Monstres> monstres=m.getMonstres();
            System.out.println(monstres.getFirst().getPosition());
            int bouge=30;
            while(bouge!=0) {
                for (int i = 0; i < monstres.size(); i++) {
                    monstres.get(i).move();
                }
                m.print();
                bouge--;
            }
            System.out.println(monstres.getFirst().getPosition());*/
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}

import java.io.*;
import java.util.*;
import java.awt.*;
public class MazeFloors{
    private LinkedList<Maze> mazes;
    private int nbFloors;

    public MazeFloors(int L, int l, int nb)throws FormatNotSupported{
        this(L,l,nb,0,0,0,0);
    }

    public MazeFloors(int L, int l, int nb, int nbObstacles, int nbMonstres,int nbTeleport,int nbDoors) throws FormatNotSupported{
        mazes=new LinkedList<Maze>();
        nbFloors=nb;
        while(nb!=0){
            mazes.add(new Maze(L,l,nbObstacles,nbMonstres,nbTeleport,nbDoors)); //problèmes dans getcase pour teleporteur et porte
            nb--;
        }
        for(int i=0; i<mazes.size(); i++){
            Maze m=mazes.get(i);
            if(i==0) m.changeEnding();
            else if(i==mazes.size()-1) m.changeBeginning();
            else{
                m.changeBeginning();
                m.changeEnding();
            }
        }
    }

    private void print(){ //affiche le labyrinthe dans le terminal
        for(int i=0; i<mazes.size(); i++){
            mazes.get(i).print();
            System.out.println("Next Floor!!");
        }
    }

    public static void main(String []args) throws FormatNotSupported{
        MazeFloors mf=new MazeFloors(20,10,5,0,0,0,1);
        mf.print();
    }
}
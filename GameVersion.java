import javafx.geometry.Point2D;
import java.io.*;
import java.util.ArrayList;

public abstract class GameVersion implements Serializable{
  protected Player player;
  protected Maze maze;



  public GameVersion(int length, int width, String name) throws FormatNotSupported{
      player=new Player(name);
      maze=new Maze(length,width);
  }

  public GameVersion(Maze maze, String name){
      this.maze=maze;
      player=new Player(name);
  }

  public GameVersion(Maze maze, Player player){
      this.maze=maze;
      this.player=player;
  }

  /*public GameVersion(int length, int width, String name) throws FormatNotSupported{
      this(new Maze(length,width),name);
  }*/

  public Maze maze(){
      return maze;
  }

    public Player player() {
        return player;
    }

    //Serialisation
    public void save(String file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file+".ser");
        ObjectOutputStream oos=new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }

    public void start(){
        player.setPosition(maze.beginning(),90);
    }


    public boolean gameOver(){
      Point2D pos=player.getPosition();
      Point2D end=maze.ending();
      return (pos.getX()==end.getX() && pos.getY()==end.getY());
  }


  public boolean validMove(){
      Point2D point = player.getPosition();
      return maze.get((int)point.getY(),(int)point.getX())!=Maze.WALL;
  }

  public void retreat(Point2D prev){
      Point2D fin=this.WallOnTheSameLine(prev, player.getPosition());
      player.setPosition(fin, player.orientation());
  }


  public Point2D WallOnTheSameLine(Point2D prev, Point2D act){
      double a=(act.getY()-prev.getY())/(act.getX()-prev.getX());// coefficient directeur
      double b=prev.getY()-(a*prev.getX());//ordonnée à l'origine
      //On cherche quel point du mur est sur la droite entre les deux points
      double x=((int)Math.max(prev.getX(),act.getX()));
      double y=((int)Math.max(prev.getY(),act.getY()));

      if(x>=Math.min(prev.getX(),act.getX()) && y<Math.min(prev.getY(),act.getY())) y=a*x+b;
      if(y>=Math.min(prev.getY(),act.getY()) && x<Math.min(prev.getX(), act.getX())) x=(b-y)/a;
      //On le fait reculer de combien il a traversé le mur
      return new Point2D(2*x-act.getX(),2*y-act.getY());
  }

  /*public void move(int direction){
      Point2D p=player.getPosition();
      switch(direction){
          case 1: player.moveForward();;
          break;
          case 2: player.moveBackward();
              break;
          case 3: player.moveLeft();
              break;

          case 4: player.moveRight();
              break;
      }
      if(!validMove()) this.retreat(p);
  }*/

    public abstract String scoresFile();

   /* public abstract void addToScores(String score);

  public void addToScores(String score,String fic){
      try{
          FileWriter fw;
          File file=new File(fic);
          if(!file.exists()) file.createNewFile();
          FileReader fr=new FileReader(file);
          BufferedReader br=new BufferedReader(fr);
          String line=null;
          ArrayList<String> best=tenBestScores();
          while((line=br.readLine())!=null){
              best.add(line);
          }
          best.add(player.name+":"+score);
          fw=new FileWriter(fic);
          String scores="";
          for (String s:best) scores+=s+"\n";
          fw.write(scores);
          fw.close();
          fr.close();
          br.close();
      }
      catch(IOException ex){
          ex.printStackTrace();
      }

  }




  public ArrayList<String> tenBestScores(){
      return new ArrayList<String>(){

          public boolean add(String s){
              int i=0;
              String line=this.get(i).split(":")[1];
              while(line!=null){
                  if(getSeconds(s.split(":")[1])<getSeconds(line)){
                      super.add(i,s);
                      break;
                  }
                  line=this.get(++i).split(":")[1];
              }
              if(this.size()>10) this.remove(this.size()-1);
              return true;
          }
      };
  }


    public String getTime(int t){
        int minutes=t/60;
        int seconds=t%60;
        int hours=minutes/60;
        minutes%=60;
        return ((hours<10)?"0":"")+hours+" : "+((minutes<10)?"0":"")+minutes+" : "+((seconds<10)?"0":"")+seconds;
    }

    public int getSeconds(String s){
        int t=0;
        String[] tab=s.split(":");
        for(int i=1;i<=s.length();i++) t+=Integer.parseInt(tab[tab.length-i])*Math.pow(60,i);
        return t;
    }*/

    }





import javax.swing.*;
import javafx.geometry.Point2D;
import java.io.*;
import java.util.ArrayList;

public abstract class GameVersion implements Serializable{
  protected Player player;
  protected Maze maze;
  protected Timer timer;
  protected int elapsedSeconds;


  public GameVersion(int length, int width, String name) throws FormatNotSupported{
      player=new Player(name);
      maze=new Maze(length,width);
      elapsedSeconds=0;
  }


  //Serialisation
    public void save() throws IOException {
        FileOutputStream fos = new FileOutputStream("Save.ser");
        ObjectOutputStream oos=new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }

    public abstract void start();

  public boolean gameOver(){
      Point2D pos=player.getPosition();
      Point2D end=maze.ending();
      if(pos.getX()==end.getX() && pos.getY()==end.getY()){
        timer.stop();
  	    //ending must return a Point that design the last corner of the lastCell, like(4,4) for a 4x4 matrix
       // GUI.print("Well done, you have reached the end of this maze !!");
        //ajouter au classement des 10 meilleurs score
          this.addToScores();
  	    return true;
      }
      //return timeOver();
      return false;
  }


    public boolean timeOver(){
      return false;
    }

    public void modifyTime(){
      elapsedSeconds ++;
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

  public void move(int direction){
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
  }

  public void addToScores(String score){
      try{
          FileWriter fw;
          File file=new File("bestScores.txt");
          if(!file.exists()) file.createNewFile();
          FileReader fr=new FileReader(file);
          BufferedReader br=new BufferedReader(fr);
          String line=null;
          ArrayList<String> best=tenBestScores();
          while((line=br.readLine())!=null){
              best.add(line);
          }
          best.add(score);
          fw=new FileWriter("bestScores.txt");
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

  public void addToScores(){
      //On parcourt le fichier dès qu'on trouve un plus petit on saute une ligne et on rajoute et on efface la dernière ligne!
    try{
        FileWriter fw;
        File file=new File("bestScores.txt");
        if(!file.exists()) file.createNewFile();
        FileReader fr=new FileReader(file);
        BufferedReader br=new BufferedReader(fr);
        String line=null;
        ArrayList<String> best=tenBestScores();
        while((line=br.readLine())!=null){
           best.add(line);
        }
        best.add(getTime(elapsedSeconds));
        fw=new FileWriter("bestScores.txt");
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
              String line=this.get(i);
              while(line!=null){
                  if(getSeconds(s)<getSeconds(line)){
                      super.add(i,s);
                      break;
                  }
                  line=this.get(++i);
              }
              if(this.size()>10) this.remove(this.size()-1);
              return true;
          }
      };
  }

    public String getTime(){
      return getTime(elapsedSeconds);
    }

    public String getTime(int t){
        int minutes=t/60;
        int seconds=t%60;
        int hours=minutes/60;
        minutes%=60;
        return ((hours<10)?"0":"")+hours+" : "+(minutes<10)?"0":"")+minutes+" : "+(seconds<10)?"0":"")+seconds;
    }

    public int getSeconds(String s){
        int t=0;
        String[] tab=s.split(":");
        for(int i=1;i<=s.length();i++) t+=Integer.parseInt(tab[tab.length-i])*Math.pow(60,i);
        return t;
    }

    public int getSeconds(){
      return elapsedSeconds;
    }







  /*public void play() {
      if (!gameOver()) {
          Point point=player.move();
          if (validMove(point)) player.setPosition(point);
      }
      else{
          //ajouter au classement des 10 meilleurs scores
      }
  }*/

   /* public static class GUI {

        public GUI() {
        }

        public String readInput(String input) {
            String s = JOptionPane.showInputDialog(((ViewGUI) view).getFrame(), input);
            while (onlySpaces(s)) {
                print("This is not a valid answer. Try again ! ");
                s = readInput(input);
            }
            return s;
        }

        public boolean onlySpaces(String line) {
            if (line == null || line.equals("")) return true;
            for (int i = 0; i < line.length(); i++) {
                char c = line.charAt(i);
                if (c != ' ') return false;
            }
            return true;
        }

        public int readInt(String s) {
            int res = 0;
            try {
                res = Integer.parseInt(readInput(s));
            } catch (Exception e) {
                print("This is not a valid number.Try again !");
                res = readInt(s);
            }
            return res;
        }

        public void print(String s) {
            JOptionPane.showMessageDialog(((ViewGUI) view).getFrame(), s, "Information ", JOptionPane.INFORMATION_MESSAGE);
        }*/

    }





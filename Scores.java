import javafx.util.Pair;
import java.io.*;
import java.util.ArrayList;

public class Scores implements Serializable{
    protected ScoreList list;
    protected File file;

    private class ScoreList extends ArrayList<Pair<String,Integer>>{

        public boolean add(String name,int score){
            int i=0;
            if(this.size()==0) return super.add(new Pair<>(name,score));
            Pair<String,Integer> pair=this.get(i);
            int val;
            while(i<this.size()){
                val=pair.getValue();
                if(score<val){
                    super.add(i,new Pair<>(name,score));
                    break;
                }
                pair=this.get(++i);
            }
            if(this.size()>10) this.remove(this.size()-1);
            return true;
        }

        public String toString(){
            String scores="";
            for(Pair<String,Integer> p: this) scores+=p.getKey()+"-"+MazeInterface.getT(p.getValue())+"\n";
            return scores;
        }

    }

    public Scores(){
        list=new ScoreList();
    }

    public Scores(String path) throws IOException{
        list=new ScoreList();
        file=new File(path);
        if(!file.exists()) file.createNewFile();
        fillList();
    }

    public File getFile(){
        return file;
    }

    public ScoreList getList() {
        return list;
    }

    public Pair<String,Integer> get(int i){
        return list.get(i);
    }


    public void fillList(){
        try{
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);
            String line=null;
            while((line=br.readLine())!=null){
                String[] tab=line.split("-");
                list.add(tab[0],MazeInterface.getSeconds(tab[1]));
            }
            fr.close();
            br.close();

        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void addToScoresFile(String name, int score) {
        list.add(name, score);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(list.toString());
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public void addToScoresList(String name, int score){
        list.add(name,score);
    }

    public String getScores(){
        return list.toString();
    }


}
import javafx.util.Pair;
import java.io.*;
import java.util.ArrayList;

public class Scores implements Serializable{
    protected ScoreList[] lists;
    protected File file;
    protected int dif=0;
    protected Pair<String,Integer> current;

    private class ScoreList extends ArrayList<Pair<String,Integer>>{

        public boolean add(String name,int score) {
            int i = 0;
            boolean added=false;
            if (this.size() == 0) return super.add(new Pair<>(name, score));
            Pair<String, Integer> pair;
            int val;
            while (i < this.size()) {
                pair = this.get(i);
                val = pair.getValue();
                if (score < val) {
                    super.add(i, new Pair<>(name, score));
                    added=true;
                    break;
                }
                i++;
            }
            if(this.size()<10 && !added){
                super.add(new Pair<>(name,score));
                return true;
            }
            if (this.size() > 10) this.remove(this.size() - 1);
            return true;
        }

        public boolean add(Scores score){
            ScoreList l=score.lists[dif];
            for(Pair<String,Integer> p:l){
                this.add(p.getKey(),p.getValue());
            };
            return true;
        }

        public String toString(){
            String scores="";
            for(Pair<String,Integer> p: this) scores+=p.getKey()+"-"+MazeInterface.getT(p.getValue())+"\r\n";
            return scores;
        }

        public String toStringR(){
            String scores="";
            for(Pair<String,Integer> p: this) if(p.getKey()!=current.getKey() && p.getValue()!=current.getValue()) scores+=p.getKey()+"-"+MazeInterface.getT(p.getValue())+"\r\n";
            return scores;
        }

    }

    public Scores(){
        lists=new ScoreList[1];
        for(int i=0;i<lists.length;i++) lists[i]=new ScoreList();

    }

    public Scores(String path,int dif) throws IOException{
        lists=new ScoreList[4];
        for(int i=0;i<lists.length;i++) lists[i]=new ScoreList();
        this.dif=dif;
        file=new File(path);
        if(!file.exists()) file.createNewFile();
        fillList();
    }

    public File getFile(){
        return file;
    }

    public ScoreList getList() {
        return lists[dif];
    }

    public Pair<String,Integer> get(int i){
        return lists[dif].get(i);
    }

    public int length(){
        return  lists[dif].size();
    }


    public void fillList(){
        try{
            FileReader fr=new FileReader(file);
            BufferedReader br=new BufferedReader(fr);
            String line=null;
            int i=-1;
            while((line=br.readLine())!=null){
                if(line.equals("**")) i++;
                else {
                    String[] tab = line.split("-");
                    lists[i].add(tab[0], MazeInterface.getSeconds(tab[1]));
                }
            }
            fr.close();
            br.close();

        }
        catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void addToScoresFile(String name, int score) {
        current=new Pair<>(name,score);
        lists[dif].add(name, score);
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(listsString());
            fw.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public boolean add(Scores score){
        return lists[dif].add(score);
    }

    public void addToScoresList(String name, int score){
        lists[dif].add(name,score);
    }

    public String getScores(){
        return lists[dif].toString();
    }

    public String listsString(){
        String s="";
        for(int i=0;i<lists.length;i++){
            s+="**\r\n"+lists[i].toString();
        }
        return s;
    }

    public String toString(){
        return lists[dif].toString();
    }

    public String getCurrentScore(){
        if(current!=null) return current.getKey()+"-"+MazeInterface.getT(current.getValue())+"\r\n";
        return "";
    }


}
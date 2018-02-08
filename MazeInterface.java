import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;

public interface MazeInterface{

    static String getTime(int t){
        int minutes=t/60;
        int seconds=t%60;
        int hours=minutes/60;
        minutes%=60;
        return ((hours<10)?"0":"")+hours+" : "+((minutes<10)?"0":"")+minutes+" : "+((seconds<10)?"0":"")+seconds;
    }

    static int getSeconds(String s){
        int t=0;
        String[] tab=s.split(":");
        for(int i=1;i<=s.length();i++) t+=Integer.parseInt(tab[tab.length-i])*Math.pow(60,i);
        return t;
    }

    /*static String getInput(String s){
        while(notValid())
        return new Alert(AlertType.In)

    }*/

    static boolean notValid(String line){
        if (line == null || line.equals("")) return true;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c != ' ' &&  c!='\n') return false;
        }
        return true;
    }


    static String[] setMulti(){
        String[] res=new String[nbPlayer()];
        for(int i=0;i<res.length;i++){
            res[i]=readInput("Name of the player "+(i+1)+": ");
        }
        return res;
    }

    static  String readInput(String input) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Settings");
        //dialog.setHeaderText("Confirm the number of players");
        dialog.setContentText(input);
        Optional<String> result = dialog.showAndWait();
        String s = String.valueOf(result);
        while (MazeInterface.notValid(s)) {
            s=readInput(input);
        }
        return s;
    }

    static int readInt(String s) {
        int res = 0;
        try {
            res = Integer.parseInt(readInput(s));
        } catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Not a number");
            error.setContentText("Your input is not a number, try again");
            Optional<ButtonType> button = error.showAndWait();
            if(button.get() == ButtonType.OK) res = readInt(s);
        }
        return res;
    }

    public static int nbPlayer(){
        int res=0;
        TextInputDialog dialog = new TextInputDialog("2");
        dialog.setTitle("MultiPlayer initialisation");
        dialog.setHeaderText("Confirm the number of players");
        dialog.setContentText("Please enter the number of players :");
        Optional<String> result = dialog.showAndWait();
        try {
            res = Integer.parseInt(result.get());
        }
        catch (Exception e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Not a number");
            error.setContentText("Your input is not a number, try again");
            Optional<ButtonType> button = error.showAndWait();
            if(button.get() == ButtonType.OK) return nbPlayer();
        }
        return res;
    }

   /* static View getView(int L, int l, String ty){
        //int size=getSize(lev);
        int time=0;
        int type=0;
            if(ty.equals("Solo")) {
                if (l == -1)
                    return new SingleView(new SoloVersion(readInt("Choose the length"), readInt("Choose the width"), readInput("What's your name")));
                else return new SingleView(new SoloVersion(L, l, readInput("What's your name ?")));
            }
            else if(ty.equals("Against the clock")){
                if(l==-1) return new SingleView(new TimeTrialVersion(readInt("Choose the length"), readInt("Choose the width"),readInput("What's your name")), time);
                    else return new SingleView(new TimeTrialVersion(L,l,readInput("What's your name ?"),time))
                }
            else{
                if(l==-1) return new MultiView(new MultiPlayerVersion(setMulti(),new Maze(readInt("Choose the length"), readInt("Choose the width"))));
                    else return new MultiView(new MultiPlayerVersion(setMulti(),new Maze(L,l)));
                }
        }

    static int getSize(String lev){
        int size=-1;
        switch(lev){
            case "Easy": size=rand(10,20);

                break;

            case "Normal":size=rand(20,30);
                break;

            case "Hard": size=rand(30,51);
                break;

            case "Super Hard": size=100;
                break;
        }
        return size;
    }

    static String getInput(String s){

    }

    static int rand(int a, int b) {
        double x = Math.random() * (b - a) + a;
        return (int) x;
    }

    */
}
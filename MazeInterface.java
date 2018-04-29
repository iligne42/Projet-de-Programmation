import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextInputDialog;
import javafx.scene.media.AudioClip;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.control.Label;
import javafx.scene.control.ContentDisplay;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;

public interface MazeInterface {
    ArrayList<AudioClip> sounds=new ArrayList<>();

    //Focntion de chargement des sons
    static void initSounds() {
        File folder = new File("sounds/");
        for (File f : folder.listFiles()) {
            AudioClip a = new AudioClip(f.toURI().toString());
            a.setCycleCount(1);
            sounds.add(a);
        }
        for(AudioClip a:sounds) a.play(0);
    }

    static AudioClip sounds(int index){
        return sounds.get(index);
    }
    
    static void configLabel(Label txt, String path, String style) {
        Image img = new Image(path);
        txt.setGraphic(new ImageView(img));
        txt.setContentDisplay(ContentDisplay.RIGHT);
        txt.setStyle(style);
    }

    static String getT(int t) {
        int minutes = t / 60;
        int seconds = t % 60;
        int hours = minutes / 60;
        minutes %= 60;
        return ((hours < 10) ? "0" : "") + hours + ":" + ((minutes < 10) ? "0" : "") + minutes + ":" + ((seconds < 10) ? "0" : "") + seconds;
    }

    static int getSeconds(String s) {
        int t = 0;
        String[] tab = s.split(":");
        for (int i = 1; i <= tab.length; i++) t += Integer.parseInt(tab[tab.length - i]) * Math.pow(60, i-1);
        return t;
    }

    static boolean notValid(String line) {
        if (line == null || line.equals("")) return true;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (c != ' ' && c != '\n') return false;
        }
        return true;
    }


    static String[] setMulti() throws FormatNotSupported {
        String[] res = new String[nbPlayer()];
        for (int i = 0; i < res.length; i++) {
            res[i] = readInput("Name of the player " + (i + 1) + ": ");
        }
        return res;
    }

    //Popup de confirmation
    static int confirm(String input,Window stage){
        Alert alert=new Alert(Alert.AlertType.CONFIRMATION);
        alert.initOwner(stage);
        alert.setTitle("You need to confirm ...");
        alert.setContentText(input);
        ButtonType bYes=new ButtonType("YES"),bNo=new ButtonType("NO");
        alert.getButtonTypes().setAll(bYes,bNo);
        Menu.addCss(alert);
        //Platform.runLater(alert::showAndWait);
        Optional<ButtonType> result=alert.showAndWait();
        if(result.get()==bYes) return 0;
        else return 1;
    }

    static void warning(String warn){
        Alert alert=new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Hmmm ...");
        alert.setContentText(warn);
        Menu.addCss(alert);
        alert.showAndWait();
    }

    static String readInput(String input) throws FormatNotSupported {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Settings");
        //dialog.setHeaderText("Confirm the number of players");
        dialog.setContentText(input);
        Menu.addCss(dialog);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            String s = result.get();
            if (notValid(s)) return readInput(input);
            return s;
        }
        throw new FormatNotSupported("No value ");
    }


    static int readInt(String s) throws FormatNotSupported{
        int res = 0;
        try {
            res = Integer.parseInt(readInput(s));
        } catch (NumberFormatException e) {
            Alert error = new Alert(Alert.AlertType.ERROR);
            error.setTitle("Not a number");
            error.setContentText("Your input is not a number, try again");
            Menu.addCss(error);
            Optional<ButtonType> button = error.showAndWait();
            if (button.get() == ButtonType.OK) res = readInt(s);
        }
        return res;
    }

    //Fonction pour charger un fichier de sauvegarde
    static GameVersion load(String file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("savings/"+file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        GameVersion g = (GameVersion) ois.readObject();
        ois.close();
        return g;
    }

    static MultiPlayerVersion loadM(String file) throws IOException, ClassNotFoundException {
        FileInputStream fis = new FileInputStream("savings/"+file);
        ObjectInputStream ois = new ObjectInputStream(fis);
        MultiPlayerVersion g = (MultiPlayerVersion) ois.readObject();
        ois.close();
        return g;
    }

    static int nbPlayer() throws FormatNotSupported{
        int res = 0;
        TextInputDialog dialog = new TextInputDialog("2");
        dialog.setTitle("MultiPlayer initialisation");
        dialog.setHeaderText("Confirm the number of players");
        dialog.setContentText("Please enter the number of players :");
        Menu.addCss(dialog);
        Optional<String> result = dialog.showAndWait();
        if (result.isPresent()) {
            try {
                res = Integer.parseInt(result.get());
            }
            catch(NumberFormatException e){
                Alert error = new Alert(Alert.AlertType.ERROR);
                error.setTitle("Not a number");
                error.setContentText("Your input is not a number, try again");
                Menu.addCss(error);
                Optional<ButtonType> button = error.showAndWait();
                if (button.get() == ButtonType.OK) res = nbPlayer();
            }
            return res;
        }
        throw new FormatNotSupported("No value ");
    }


    static Maze getMaze(int L, int l ) throws FormatNotSupported,IOException{
        initSounds();
        if(l==-1){
            L=readInt("Choose the length");
            l=readInt("Choose the width");
        }
        return new Maze(L,l);
    }

    static MazeFloors getMaze(int L,int l,int f,int typeB,boolean[] sup) throws Exception{
        initSounds();
        int[] extra=new int[sup.length];
        int nb=getSelected(sup);
        boolean no=false;
        if(l==-1){
            L=readInt("Choose the length");
            l=readInt("Choose the width");
        }
        int extras=L*l/30;

        for(int i=0;i<extra.length;i++){
            extra[i]=0;
            if(sup[i] ){
                if(i!=4) {
                    extra[i] = extras / nb;
                    extras -= extra[i];
                    nb--;
                    if(extra[i]==0) no=true;
                }
                else{
                    extra[i]=(typeB==0)?(L*l)/5:(L*l)/10;
                    nb --;
                }
            }
        }
        if(no) warning("Sorry, we couldn't put everything you requested :'(");
        return new MazeFloors(L,l,f,extra[0],extra[2],extra[3],extra[1],extra[4],typeB);
    }

    static int getSelected(boolean[] s){
        int r=0;
        for(boolean b:s){
            r=r+((b)?1:0);
        }
        return r;
    }

    static View getView(MazeFloors m, String ty, String name) throws FormatNotSupported, IOException {
        int time=m.getFloor().getFirst().getHeight()*m.getFloor().getFirst().getWidth();
        if (ty.equals("Solo"))
            return new SingleView(new SoloVersion(m, name));

        else if (ty.equals("Against the clock"))
            return new SingleView(new TimeTrialVersion(m, name, time));
        else return new MultiView(new MultiPlayerVersion(setMulti(), m));
    }

    static View getView(MazeFloors m, String ty) throws FormatNotSupported, IOException {
        int time=m.getFloor().getFirst().getHeight()*m.getFloor().getFirst().getWidth();
        if (ty.equals("Solo"))
            return new SingleView(new SoloVersion(m, readInput("What's your name ?")));

        else if (ty.equals("Against the clock"))
            return new SingleView(new TimeTrialVersion(m, readInput("What's your name ?"), time));

        else return new MultiView(new MultiPlayerVersion(setMulti(), m));
    }

    static int getSize(String lev) {
        int size = -1;
        switch (lev) {
            case "Easy":
                size = rand(10, 20);

                break;

            case "Normal":
                size = rand(20, 30);
                break;

            case "Hard":
                size = rand(30, 51);
                break;

            case "Super Hard":
                size = 100;
                break;
        }
        return size;
    }

    static int getDifficulty(int length,int width){
        if(length>=5 && length<20 && width>=5 && width<20) return 0;
        if(length>=20 && length<30 && width>=20 && width<30) return 1;
        if(length>=30 && length<=99 && width>=30 && width<=99) return 2;
        else return 3;
    }

    static int typeBonus(String ty){
        if(ty.equals("Against the clock")) return 1;
        return 0;
    }


    static int rand(int a, int b) {
        double x = Math.random() * (b - a) + a;
        return (int) x;
    }

}

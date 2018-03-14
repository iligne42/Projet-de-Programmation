
import javafx.scene.Camera;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.awt.geom.Point2D;
import java.io.IOException;

import java.net.*;
import java.util.ArrayList;

public class netView extends SingleView{

    public netView(GameVersion game, Socket me) throws IOException{
        super(game);
        control=new NetControl(me);
    }

    protected class NetControl extends GameControl{
        private Socket me;
        public NetControl(Socket me) throws IOException{
            super();
            this.me=me;
        }

        public void whenIsFinished(){
            Scores sc = game.scores();
            timePane.stop();
            netView.this.setOnKeyPressed(null);
            game.addToScoresList();
            
            //setVisible(false);
            try{
                System.out.println("ERRORRRRRR -----------------------------");
                System.out.println(me);
                System.out.println(sc);
                System.out.println("FIN ERRORRRRRR -------------------------");
                netFunc.sendObject(me,sc);
            }catch(IOException e){}
            while(true){
                try{
                    Object tmp = netFunc.readObject(me);
                    if(tmp instanceof Scores)
                        System.out.println((Scores) tmp);
                    else
                        System.exit(1);
                }catch(IOException e){

                }catch(ClassNotFoundException e){}
            }
        }
    }

    private void printPlayer(ArrayList<String> list){
        System.out.println("Les joueurs ayant fini sont :");
        for(String str:list)
            System.out.println("  - "+str);
    }
}
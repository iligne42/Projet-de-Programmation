
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.scene.Camera;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.MeshView;

import java.awt.*;
import java.awt.geom.Point2D;
import java.io.IOException;

import java.net.*;
import java.util.ArrayList;
import java.util.LinkedList;

public class netView extends View{
    ArrayList<Player> players=new ArrayList<Player>();
    private static boolean debug = false;

    public netView(GameVersion game, Socket me) throws IOException{
        super(new StackPane(), game);
        control=new NetControl(me);
    }

    protected class NetControl extends GameControl{
        private Socket me;
        private sendPos sp;
        private getPos gp;
        public NetControl(Socket me) throws IOException{
            super();
            this.me=me;
            sp = new sendPos();
            sp.start();
            gp=new getPos();
            gp.start();
        }

        public void whenIsFinished(){
            Scores sc = game.scores();
            timePane.stop();
            sp.arret();
            netView.this.setOnKeyPressed(null);
            game.addToScoresList();
            
            try{
                netFunc.sendObject(me,sc);
                if(debug)
                    System.out.println("J'envoie le score");
            }catch(IOException e){}
        }

        private class sendPos extends Thread{
            private volatile boolean end;
            public sendPos(){
                super();
                end=false;
            }

            public void run(){
                if(debug)
                    System.out.println("Je peux aller la");
                while(!end){
                    try{
                        netFunc.sendObject(me,game.player());
                        if(true)
                            System.out.println("Voici ma position");
                        Thread.sleep(200);
                    }catch(Exception e){
                        e.printStackTrace();
                        if(debug)
                            System.out.println("Je peux pas envoyer ma pos");
                    }
                }
            }
    
            public void arret(){
                if(debug)
                    System.out.println("Je modifie end");
                end =true;
           }
        }

        public class getPos extends Thread{
            private volatile boolean end;
            public getPos(){
                super();
                end=false;
            }

            public void run(){
                while(!end){
                    try{
                        Object tmp=netFunc.readObject(me);
                        if(tmp instanceof ArrayList){
                            if(((ArrayList)tmp).get(0) instanceof Player){
                                if(debug)
                                    System.out.println("Je recois une list de pos");
                                players = (ArrayList<Player>)tmp;
                                if(debug)
                                    drawPlayer(mazePane);
                            }
                        }else if(tmp instanceof Scores)
                            displayScores((Scores) tmp);
                    }catch(Exception e){}
                }
            }

            public void arret(){
                end=true;
            }
        }
    }


    private void printPlayer(ArrayList<String> list){
        System.out.println("Les joueurs ayant fini sont :");
        for(String str:list)
            System.out.println("  - "+str);
    }

    public void drawPlayer(Group root) throws IOException{
        for(int i=0; i<players.size(); i++) {
            Player p=players.get(i);
            MeshView player = p.initPlayer();
            int g=p.getGround();
            /*x=new SimpleDoubleProperty(p.getPosition().getX()+mazePane.coordSwitch[g].x()*mazePane.SIZE_BOX-mazePane.SIZE_BOX/2);
            z=new SimpleDoubleProperty(p.getPosition().getY()+mazePane.coordSwitch[g].z()*mazePane.SIZE_BOX-mazePane.SIZE_BOX/2);
            y=new SimpleDoubleProperty(-p.getY()*mazePane.SIZE_BOX);
            player.translateXProperty().bind(x);
            player.translateYProperty().bind(y);
            player.translateZProperty().bind(z);*/
            player.setTranslateX(p.getPosition().getX()+mazePane.coordSwitch[g].x()*mazePane.SIZE_BOX-mazePane.SIZE_BOX/2);
            player.setTranslateZ(p.getPosition().getY()+mazePane.coordSwitch[g].z()*mazePane.SIZE_BOX-mazePane.SIZE_BOX/2);
            player.setTranslateY(-p.getY()*mazePane.SIZE_BOX);
            player.setScaleX(player.getScaleX()* mazePane.SIZE_BOX);
            player.setScaleZ(player.getScaleZ()* mazePane.SIZE_BOX);
            player.setScaleY(player.getScaleY()* mazePane.SIZE_BOX);
            root.getChildren().add(player);
        }
    }
}
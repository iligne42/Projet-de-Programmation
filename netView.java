
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
            this.me=me;
            game.start();
            timePane.start();
            mazePane.initMaze();
            setOnKeyPressed(e->{
                System.out.println(e.getCode());
                    switch (e.getCode()){
                            case UP:    game.move(1); break;
                            case RIGHT: game.move(4); break;
                            case DOWN:  game.move(2); break;
                            case LEFT:  game.move(3); break;
                           // case S: mazePane.rotateY.setAngle(mazePane.rotateY.getAngle()+5);
                        }
                        Point2D pos=game.player().getPosition();
                        netView.this.mazePane.x.set(pos.getX()*netView.this.mazePane.SIZE_BOX);
                        netView.this.mazePane.z.set(pos.getY()*netView.this.mazePane.SIZE_BOX);
                        netView.this.mazePane.angle.set(90-game.player().orientation());
                System.out.println(netView.this.getCamera().getTranslateX()+"     "+netView.this.getCamera().getTranslateZ()+"   "+netView.this.getCamera().getRotate());

                       // System.out.println(game.player().getPosition().getX()+"    "+game.player.getPosition().getY());

                //netView.this.mazePane.printMaze();
                    if (game.gameOver()) {
                        timePane.stop();
                        whenIsFinished();
                    } else if (timePane.timeOver()) {
                        timePane.setVisible(false);
                        //Print you loose dumbass
                    }


                });

            }

        private void whenIsFinished(){
            Scores sc = game.scores();
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
                    if(tmp instanceof ArrayList)
                        printPlayer((ArrayList<String>) tmp);
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
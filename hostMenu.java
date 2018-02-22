import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.application.*;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.*;
import javafx.css.*;
import javafx.util.Callback;
import java.net.*;
import java.util.*;

import java.io.File;
import java.io.IOException;
import java.util.Optional;


public class hostMenu extends VBox{
	private boolean hote;
	private ArrayList<Socket> sockets;
	private ArrayList<String> names;
	private ServerSocket socServ;
	private waitClients waitCli;
	private Socket me;
	private TilePane Players;
	private Maze maze;
	private String myName;

	public hostMenu(){
		super();
		Players=new TilePane();
		names = new ArrayList<String>();
	}

	public void initHost(String name){
		try{
			maze=new Maze(10,10);
		}catch(Exception e){}
		clear();
		hote=true;
		getChildren().addAll(new Label("Vous êtes l'hôte."),Players);
		try{
		socServ = new ServerSocket(netFunc.PORT);
		sockets = new ArrayList<Socket>();
		initMe(name,"localHost");
		getClient();
		}catch(Exception e){e.printStackTrace();}
	}

	public void initClient(String name){
		clear();
		VBox waitIP= new VBox();
		Label lab= new Label("Entrer l'addresse IP de l'hôte.");
		TextField text= new TextField();
		Button but = new Button("Valider");
		waitIP.getChildren().addAll(lab,text,but);
		waitIP.getStyleClass().add("vbox");
		but.setOnMouseClicked(e->{
			clear();
			initMe(name,text.getText());
		});
		this.getChildren().add(waitIP);
	}

	public void initMe(String name, String addr){
		System.out.println("J'ai l'addr "+addr);
		try{
			me=new Socket(addr,netFunc.PORT);
			myName=name;
			netFunc.sendString(me,name);
			waitInfo();
			Button play = new Button("Play");
			play.setDisable(!hote);
			play.setOnMouseClicked(e->{
				waitCli.stop();
				sendMaze();
			});
			getChildren().add(play);
			printPlayer();
		}catch(Exception e){e.printStackTrace();}
	}

	public void getClient(){
		waitCli = new waitClients();
		waitCli.start();
	}

	private void printPlayer(){
		this.getChildren().remove(Players);
		Players =new TilePane();
		//Players.setSpacing(30);
		for(String str:names)
			Players.getChildren().add(new Label(str));
		Players.getStyleClass().add("hbox");
		this.getChildren().add(getChildren().size()-1,Players);
	}

	private void waitInfo(){
		waitInfo tmp = new waitInfo();
		tmp.start();
	}

	public void stopGetClient(){
		waitCli.Stop();
	}

	public void clear(){
		getChildren().clear();
	}

	public void sendMaze(){
		for (Socket tmp : sockets) {
			try{
				netFunc.sendObject(tmp,maze);
			}catch(IOException e){
				names.remove(sockets.indexOf(tmp));
				System.out.println("erreur de connexion avec une socket.");
			}
		}
	}

	class waitClients extends Thread{
		private volatile boolean exit = false;

		public void run(){
			System.out.println("Attente de clients...");
			while(!exit){
				try{
					Socket tmp = socServ.accept();
					String str = netFunc.readString(tmp);
					if(!exit){
						sockets.add(tmp);
						names.add(str);
						System.out.println("Un nouveau client a été ajouté.");
						for(Socket soc:sockets){
							try{
								netFunc.sendObject(soc,names);
							}catch(IOException e){
								names.remove(sockets.indexOf(soc));
								System.out.println("erreur de connexion avec une socket.");
							}
						}
						System.out.println("J'envoie les listes");
					}else
					tmp.close();
				}catch(Exception e){}
			}
		}

		public void Stop(){
			exit=true;
		}
	}

	class waitInfo extends Thread{
		public void run(){
			while(true){
			try{
				Object tmp=netFunc.readObject(me);
				if(tmp instanceof ArrayList){
					names=(ArrayList<String>)tmp;
					Platform.runLater(() -> printPlayer());
				}else if(tmp instanceof Maze){
					SoloVersion sv=new SoloVersion((Maze)tmp,myName);
					SingleView SV=new SingleView(sv);
					break;
				}else{
					break;
				}
			}catch(Exception e){}
			}
		}
	}

}
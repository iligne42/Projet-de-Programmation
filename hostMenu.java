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
	boolean hote;
	ArrayList<Socket> sockets;
	ArrayList<String> names;
	ServerSocket socServ;
	waitClients waitCli;
	Socket me;
	TilePane Players;

	public hostMenu(){
		super();
		Players=new TilePane();
		names = new ArrayList<String>();
	}

	public void initHost(String name){
		hote=true;
		getChildren().addAll(new Label("Vous êtes l'hôte."),Players);
		try{
		socServ = new ServerSocket(netFunc.PORT);
		sockets = new ArrayList<Socket>();
		initClient(name,"localHost");
		getClient();
		}catch(Exception e){e.printStackTrace();}
	}

	public void initClient(String name, String addr){
		try{
		me=new Socket(addr,netFunc.PORT);
		netFunc.sendString(me,name);
		printPlayer();
		waitInfo();
		}catch(Exception e){}
	}

	public void getClient(){
		waitCli = new waitClients();
		waitCli.start();
	}

	private void printPlayer(){
		this.getChildren().remove(Players);
		Players =new TilePane();
		for(String str:names)
			Players.getChildren().add(new Label(str));
		Players.getStyleClass().add("hbox");
		this.getChildren().add(Players);
	}

	private void waitInfo(){
		waitInfo tmp = new waitInfo();
		tmp.start();
	}

	public void stopGetClient(){
		waitCli.Stop();
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
				}else
					break;
			}catch(Exception e){}
			}
		}
	}

}
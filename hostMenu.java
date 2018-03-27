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
import javafx.stage.*;
import javafx.event.*;

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
	private MazeFloors maze;
	private String myName;
	private Stage stageChat;

	public hostMenu(){
		super();
		Players=new TilePane();
		names = new ArrayList<String>();
	}

	public void initHost(String name,MazeFloors m){
		System.out.println("je creer un hote");
		maze=m;
		clear();
		hote=true;
		getChildren().addAll(new Label("You are the host."),Players);
		try{
		socServ = new ServerSocket(netFunc.PORT);
		sockets = new ArrayList<Socket>();
		initMe(name,"localHost");
		getClient();
		makeChat();
		}catch(Exception e){e.printStackTrace();}
	}

	public void makeChat(){
		chatPane chat=new chatPane();
		chat.initHost(myName);
		makeStage(chat);
	}

	public void makeChat(String addr){
		chatPane chat=new chatPane();
		chat.initClient(myName,addr);
		makeStage(chat);
	}

	public void makeStage(chatPane chat){
		stageChat = new Stage();
		stageChat.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent t) {
            	chat.close();
                stageChat.close();
            }
        });
		stageChat.setTitle("Chat");
		StackPane layout = new StackPane();
		layout.getChildren().add(chat);
		Scene scene=new Scene(layout,500,500);
		stageChat.setScene(scene);
		stageChat.setResizable(false);
		stageChat.show();
	}

	public void initClient(String name){
		clear();
		VBox waitIP= new VBox();
		Label lab= new Label("Put the IP adress of the host.");
		TextField text= new TextField();
		Button but = new Button("Validate");
		waitIP.getChildren().addAll(lab,text,but);
		waitIP.getStyleClass().add("vbox");
		but.setOnMouseClicked(e->{
			clear();
			initMe(name,text.getText());
			makeChat(text.getText());
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

	public void lancerLabi(MazeFloors tmp){
		try{
			Stage stage=new Stage();
			SoloVersion sv=new SoloVersion(tmp,myName);
			System.out.println("reussi 1");
			netView NV=new netView(sv,me);
			stage.setScene(NV);
			System.out.println("reussi 2");
			stage.setFullScreen(true);
			stage.show();
			if(hote){
				Server server = new Server(socServ,names, sockets);
			}
		}catch(FormatNotSupported e){

		}catch(IOException e){}

	}

	public <K> K[] toTab(ArrayList<K> list){
		K[] tab =(K[]) new Object[list.size()];
		for(int i=0;i<tab.length;i++){
			tab[i]=list.get(i);
		}
		return tab;
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
					((Maze)tmp).print();
					Platform.runLater(()-> lancerLabi((MazeFloors) tmp));
					break;
				}else{
					break;
				}
			}catch(Exception e){}
			}
		}
	}

}
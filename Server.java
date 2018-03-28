import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
	private ServerSocket socServ;
	private ArrayList<Player> players;
	private ArrayList<Socket> sockets;
	private Scores score;
	private ArrayList<Triplet> arrivedList;

	public Server(ServerSocket socServ, ArrayList<String> names, ArrayList<Socket> sockets){
		this.socServ=socServ;
		players=new ArrayList();
		for(String str:names){
			players.add(new Player(str));
		}
		this.sockets=sockets;
		score=new Scores();
		arrivedList=new ArrayList<Triplet>();
		for(int i=0;i<names.size();i++){
			waitScore ws= new waitScore(sockets.get(i),players.get(i));
			ws.start();
		}
		System.out.println("Server en marche");
	}

	private void sendScoreFinished(){
		ArrayList<String> tmp = new ArrayList<String>();
		for(Triplet t:arrivedList)
			try{
				netFunc.sendObject(t.getSocket(),score);
			}catch(IOException e){
				arrivedList.remove(t);
			}
	}

	private void sendEnd(){
		for(Triplet t:arrivedList)
			try{
				netFunc.sendObject(t.getSocket(),false);
			}catch(IOException e){
				arrivedList.remove(t);
			}
	}

	private void sendPosPlayers(){
		for(Socket soc:sockets){
			try{
				netFunc.sendObject(soc,players);
			}catch(IOException e){}
		}
	}

	class waitScore extends Thread{
		private Socket soc;
		private Player player;

		public waitScore(Socket s, Player p){
			super();
			soc=s;
			player =p;
		}

		public void run(){
			Object tmp=netFunc.readObject(soc);
			if(tmp==null){
				netFunc.closeSocket(soc);
			}else if(tmp instanceof Scores){
				System.out.println(player.getName()+" est arrivé.");
				Triplet t=new Triplet(soc,(Scores)tmp,player);
				score.add((Scores)tmp);
				arrivedList.add(t);
				players.remove(player);
				sockets.remove(soc);
				sendScoreFinished();
			}else if(tmp instanceof Player){
				Player p=(Player)tmp;
				player.setPosition(p.getPosition(),p.orientation());
				sendPosPlayers();
			}
			if(sockets.isEmpty())
				sendEnd();
		}
	}



}
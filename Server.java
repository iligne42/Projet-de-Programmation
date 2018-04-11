import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
	private ServerSocket socServ;
	private ArrayList<Player> players;
	private ArrayList<Socket> sockets;
	private Scores score;
	private ArrayList<Triplet> arrivedList;
	private final boolean debug = false;

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
		if(debug)
			System.out.println("Server en marche");
		sendPos sp2=new sendPos();
		sp2.start();
	}

	private void sendScoreFinished(){
		ArrayList<String> tmp = new ArrayList<String>();
		for(Triplet t:arrivedList)
				netFunc.sendObject(t.getSocket(),score);
	}

	private void sendEnd(){
		for(Triplet t:arrivedList)
				netFunc.sendObject(t.getSocket(),false);
	}

	private void sendPosPlayers(){
		for(Socket soc:sockets){
				netFunc.sendObject(soc,players);
		}
	}

	private void printPlayer(){
		for(Player p:players){
			System.out.println(p.getName()+" :"+p.getPosition().toString());
		}
	}

	class waitScore extends Thread {
		private Socket soc;
		private Player player;

		public waitScore(Socket s, Player p) {
			super();
			soc = s;
			player = p;
		}

		public void run() {
			while(true) {
				Object tmp = netFunc.readObject(soc);
				if (debug)
					System.out.println("Je recois un objet");
				if (tmp == null) {
					//netFunc.closeSocket(soc);
				} else if (tmp instanceof Scores) {
					if (debug)
						System.out.println(player.getName() + " est arrivé.");
					Triplet t = new Triplet(soc, (Scores) tmp, player);
					score.add((Scores) tmp);
					arrivedList.add(t);
					players.remove(player);
					sockets.remove(soc);
					sendScoreFinished();
				} else if (tmp instanceof Player) {
					Player p = (Player) tmp;
					player.setPosition(p.getPosition(), p.orientation());
					if (debug) {
						System.out.println("--------------------------------------------");
						System.out.println("J'ai recu info Position de " + p.getName());
						System.out.println(p.getPosition());
						System.out.println(player.getPosition());
					}
				}
				if (sockets.isEmpty())
					sendEnd();
			}
		}
	}

	public class sendPos extends Thread{
		public sendPos(){
			super();
		}
			public void run(){
			while(true){
				sendPosPlayers();
				if(true)
					printPlayer();
				try {
					Thread.sleep(200);
				}catch(InterruptedException e){
					e.printStackTrace();
				}
			}
		}
	}



}
import java.net.*;
import java.io.*;
import java.util.*;

public class Server{
	private ServerSocket socServ;
	private ArrayList<String> names;
	private ArrayList<Socket> sockets;

	private ArrayList<Triplet> arrivedList;

	public Server(ServerSocket socServ, ArrayList<String> names, ArrayList<Socket> sockets){
		this.socServ=socServ;
		this.names=names;
		this.sockets=sockets;
		arrivedList=new ArrayList<Triplet>();
		for(int i=0;i<names.size();i++){
			waitScore ws= new waitScore(sockets.get(i),names.get(i));
			ws.start();
		}
		System.out.println("Server en marche");
	}

	private void sendScoreFinished(){
		ArrayList<String> tmp = new ArrayList<String>();
		for(Triplet t:arrivedList)
			tmp.add(t.getC());
		for(Triplet t:arrivedList)
			try{
				netFunc.sendObject(t.getA(),tmp);
			}catch(IOException e){
				arrivedList.remove(t);
			}
	}

	private void sendEnd(){
		for(Triplet t:arrivedList)
			try{
				netFunc.sendObject(t.getA(),false);
			}catch(IOException e){
				arrivedList.remove(t);
			}
	}

	class waitScore extends Thread{
		private Socket soc;
		private String name;

		public waitScore(Socket s, String n){
			super();
			soc=s;
			name=n;
		}

		public void run(){
			try{
				Object tmp=netFunc.readObject(soc);
				System.out.println(name+" est arrivé.");
				Triplet t=new Triplet(soc,(Scores)tmp,name);
				arrivedList.add(t);
				names.remove(name);
				sockets.remove(soc);
				sendScoreFinished();
				if(sockets.isEmpty())
					sendEnd();
			}catch(Exception e){}
		}
	}



}
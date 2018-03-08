import java.net.*;
import java.util.*;
import java.io.*;
public class netFunc{
	public static final int PORT=1234;
	public static String getIP(){
		try{
    	java.net.URL URL = new java.net.URL("http://bot.whatismyipaddress.com");
 		java.net.HttpURLConnection Conn = (HttpURLConnection)URL.openConnection();
		java.io.InputStream InStream = Conn.getInputStream();
		java.io.InputStreamReader Isr = new java.io.InputStreamReader(InStream);
		java.io.BufferedReader Br = new java.io.BufferedReader(Isr);
		 
		return Br.readLine();
    	}catch(Exception e){e.printStackTrace();}
		return "error";
	}

	public static String readString(Socket socket){
		try{
		BufferedReader tmp = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		return tmp.readLine();
		}catch(IOException e){
			return null;
		}
	}

	public static void sendString(Socket socket, String s) throws IOException{
		PrintWriter tmp =new PrintWriter(socket.getOutputStream());
		tmp.println(s);
		tmp.flush();
	}

	public static Object readObject(Socket socket) throws IOException, ClassNotFoundException{
		try{
		ObjectInputStream tmp=new ObjectInputStream(socket.getInputStream());
		return tmp.readObject();
		}catch(IOException e){
			return null;
		}catch(ClassNotFoundException e){
			return null;
		}
	}

	public static <K extends Serializable> void sendObject(Socket socket, K obj) throws IOException{
		ObjectOutputStream tmp = new ObjectOutputStream(socket.getOutputStream());
		tmp.writeObject(obj);
	}

}
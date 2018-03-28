import java.net.Socket;
public class Triplet{
	private Socket A;
	private Scores B;
	private Player C;

	public Triplet(Socket A, Scores B, Player C){
		this.A=A;
		this.B=B;
		this.C=C;
	}

	public Socket getSocket(){return A;}
	public Scores getScore(){return B;}
	public Player gatPlayer(){return C;}
	public String getName(){return C.getName();}
}
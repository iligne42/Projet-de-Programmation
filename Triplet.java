import java.net.Socket;
public class Triplet{
	private Socket A;
	private Scores B;
	private String C;

	public Triplet(Socket A, Scores B, String C){
		this.A=A;
		this.B=B;
		this.C=C;
	}

	public Socket getA(){return A;}
	public Scores getB(){return B;}
	public String getC(){return C;}
}
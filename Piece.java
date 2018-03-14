public class Piece extends Bonus{
    private int montant;

    public Piece(Maze m){
        super(m,"Piece");
        montant=10;
    }

    public int getMontant(){return montant;}
}

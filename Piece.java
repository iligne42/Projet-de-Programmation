public class Piece extends Bonus{
    private int montant;

    public Piece(Maze m){
        super(m,"Vous avez gagné de l'argent.");
        montant=10;
    }

    public int getMontant(){return montant;}
}

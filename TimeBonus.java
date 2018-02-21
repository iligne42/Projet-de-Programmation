public class TimeBonus extends Bonus{
    private int time;

    public TimeBonus(Maze m){
        super(m,"Vous gagné du temps: 10 secondes.");
        time=10;
    }

    public int getTime(){return time;}
}

public class TimeBonus extends Bonus{
    private int time;

    public TimeBonus(Maze m){
        super(m,"Time");
        time=10;
    }

    public int getTime(){return time;}
}

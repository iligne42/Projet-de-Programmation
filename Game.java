public abstract class GameVersion{
  protected Player player;
  protected Maze maze;
  protected boolean gameOver;

  public abstract boolean gameOver(){
  	return gameOver;
  }

  public abstract void play();


}

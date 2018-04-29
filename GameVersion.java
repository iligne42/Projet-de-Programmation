import javafx.scene.media.AudioClip;
import javafx.util.Pair;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;



public abstract class GameVersion implements Serializable {
    protected Player player;
    protected MazeFloors mazeFloors;
    protected LinkedList<Maze> floors;
    protected int floor;
    protected Maze current;
    protected Scores scores;
    protected int elapsed = 0;
    protected boolean debug=true;



    public GameVersion(int length, int width, int nbFloors,int nbObstacles,int nbMonstres,int nbTeleport,int nbDoors,int nbBonus,int typeBonus, String name, Scores score) throws FormatNotSupported {
        player = new Player(name);
        mazeFloors = new MazeFloors(length,width,nbFloors,nbObstacles,nbMonstres,nbTeleport,nbDoors,nbBonus,typeBonus);
        floors=mazeFloors.getFloor();
        floor=0;
        current=floors.get(floor);
        scores = score;


    }

    public GameVersion(MazeFloors mazeF, String name, Scores score) throws FormatNotSupported {
        player = new Player(name);
        this.mazeFloors=mazeF;
        floors=mazeFloors.getFloor();
        floor=0;
        current=floors.get(floor);
        scores = score;
    }


    public GameVersion(MazeFloors mazeF, Player player, Scores score) {
        this.mazeFloors = mazeF;
        this.player = player;
        floors=mazeFloors.getFloor();
        floor=0;
        current=floors.get(floor);
        scores = score;
    }


    public MazeFloors maze() {
        return mazeFloors;
    }

    public Maze current(){
        return current;
    }

    public LinkedList<Maze> floors() {
        return floors;
    }

    public Player player() {
        return player;
    }

    public void setElapsed(int x) {
        elapsed = x;
    }

    public int getElapsed() {
        return elapsed;
    }

    public String getElapsedTime(){
        return MazeInterface.getT(elapsed);
    }

    public void elapse(int added) {
        elapsed += added;
    }

    public abstract String scoresFile();

    public Scores scores() {
        return scores;
    }

    public void addToScoresFile() {
        scores.addToScoresFile(player.getName(), elapsed);
    }

    public void addToScoresList() {
        scores.addToScoresList(player.getName(), elapsed);
    }

    public String getScores() {
        return scores.getScores();
    }

    public int floor(){
        return floor;
    }


    //Serialisation
    public void save(String file) throws IOException {
        File f=new File(file+".ser");
        if(!f.exists()){
            f.createNewFile();
        }
        if(debug) System.out.println(f.getAbsolutePath());
        FileOutputStream fos = new FileOutputStream(f);
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }

    public void reset(){
        startInFloor(floors.getFirst().beginning(),0);
        elapsed=0;

    }


    //Initialisation de la position et de l'orientation du joueur
    public void start(){
        if(player.getPosition().getX()==0 && player.getPosition().getY()==0 && player.getY()==0) startF(0);
    }


    public void startF(int i) {
        if(i==0)startInFloor(current.beginning(),0);
        else if(i==1)startInFloor(current.beginning(),1);
        else startInFloor(current.ending(),2);

    }

    public void startInFloor(Point b, int i){
            Point2D begin = new Point2D.Double(b.getX(), b.getY());
            int orient = orientPlayer(b);
            begin = (i == 0) ? new Point2D.Double(begin.getX() + 0.5, begin.getY() + 0.5) : firstPosition(b);
            player.setPosition(begin, orientPlayer(b));
        player.setAcceleration();

    }
    //Pour placer le joueur après la montée ou la descente d'un escalier
    public Point2D firstPosition(Point2D begin){
        Point2D first=null;
        int x = (int) begin.getX();
        int y = (int) begin.getY();
        if (y == 0) first=new Point2D.Double(x+0.5,y+1.5);
        else if (x == 0) first=new Point2D.Double(x+1.5,y+0.5);
        else if (x == current.getWidth() - 1) first=new Point2D.Double(x-0.5,y+0.5);
        else if (y == current.getHeight() - 1) first=new Point2D.Double(x+0.5,y-0.5);
        return first;
    }


    public int orientPlayer(Point begin){
        int orientation = 90;
        int x = (int) begin.getX();
        int y = (int) begin.getY();
        if (y == 0) orientation = 90;
        else if (x == 0) orientation = 0;
        else if (x == current.getWidth() - 1) orientation = 180;
        else if (y == current.getHeight() - 1) orientation = 270;
        return orientation;
    }

    public int orientPlayerReverse(Point begin){
        return (orientPlayer(begin)+180)%360;
    }

    //Pour passer au labyrinthe suivant
    public void next(){
        current=floors.get((++floor)/2);
        player.setGround(floor);
        player.setMaze(floor/2);
        if(debug) System.out.println(floor);
        startF(1);
    }

    public void prev(){
       current=floors.get((--floor)/2);
        player.setGround(floor);
        player.setMaze(floor/2);
        startF(2);
    }

    public boolean gameOver() {
        return current.equals(floors.getLast()) && isInBounds(player.getPosition(),current) && current.getCase(player.getPosition()) == Maze.END ;
    }

    public boolean isInBounds(Point2D p,Maze current) {
        return  p.getX() >= 0 && p.getY() >= 0 && p.getX() < current.getWidth() && p.getY() < current.getHeight();
    }

   public void update(double timeElapsed){
        if(elapsed>=14400) player.changeState(Player.PlayerState.DEAD);
        else {
            Point2D p = (Point2D) player.getPosition().clone();
            float angle = player.orientation();
            double y = player.getY();
            player.updatePosition(timeElapsed);
            this.handleMove(p, y, angle,timeElapsed);
        }
    }

//Fonction de gestion du déroulement du jeu et de toutes les actions du joueur et du monde
    public void handleMove(Point2D start,double yPos1,float angle1,double timeElapsed) {
        Point2D goal = player.getPosition();
        double yPos = player.getY();
        float angle = player.orientation();
        float radius = player.radius();
        if(start.getX() != goal.getX() || start.getY() != goal.getY()) {
            //sounds.get(0).play();
            if(debug) System.out.println();
            if(debug) System.out.println("Start : " + start);
            if(debug) System.out.println("Goal : " + goal);
            if(debug) System.out.println(yPos);
            if(debug) System.out.println(player.state());

            if (player.state() == Player.PlayerState.BETWEEN) inBetween(goal, start, angle);
            else if (player.state() == Player.PlayerState.STAIRSUP) {
                if (Math.floor(yPos) >  floor) {
                    if(debug) System.out.println(player.previousState());
                    if(floor%2==1){
                        next();
                        player.changeState(Player.PlayerState.GROUND);
                    }
                    else{
                        floor++;
                        player.setGround(floor);
                        player.changeState(Player.PlayerState.BETWEEN);
                    }
                }
                else if(Math.ceil(yPos)==floor){
                    if(floor%2==0) player.changeState(Player.PlayerState.GROUND);
                    else player.changeState(Player.PlayerState.BETWEEN);
                }
            }
            else if (player.state() == Player.PlayerState.STAIRSDOWN) {
                if (Math.ceil(yPos) < floor) {
                    if(floor%2==0){
                        floor--;
                        player.setGround(floor);
                        player.changeState(Player.PlayerState.BETWEEN);
                    }
                    else{
                        prev();
                        player.changeState(Player.PlayerState.GROUND);
                    }
                }
                else if(Math.floor(yPos)==floor){
                   if(floor%2==0) player.changeState(Player.PlayerState.GROUND);
                    else player.changeState(Player.PlayerState.BETWEEN);
                }
                if(player.state()!=Player.PlayerState.STAIRSDOWN && debug)System.out.println(player.state());
            }

            else {
                    if (!isInBounds(goal, current))player.setPosition(start, angle);
                    else {
                        current.updateGame(timeElapsed);
                        if(debug) System.out.println(current.getCase(goal));
                        ArrayList<Pair<Integer, Object>> near = closestStuff(goal);
                        Point2D wallB = checkCollision(goal, radius, closestWalls(near), 1, 1);
                        Point2D doorB = checkCollision(goal, radius, closestDoors(near), 1, 1);
                        if (wallB != null) slide(start, goal, wallB, radius, angle);
                        else if (doorB != null) openDoor(doorB, start, goal, radius, angle);
                        else {
                            for (Pair<Integer, Object> p : near) {
                                if (p.getKey() == Maze.MONSTRE) {
                                    if (monsterInFront((Monstres) p.getValue(), start, goal, angle)) {
                                        MazeInterface.sounds(2).play();
                                        this.startF(0);
                                        return;
                                    }
                                }
                            }
                            int x = (int) goal.getX();
                            int y = (int) goal.getY();
                            switch (current.getCase(y, x)) {
                                case Maze.OBSTACLE:
                                    Obstacles o = current.getObstacle(x, y);
                                    Point2D pos = o.getPosition();
                                    if (o.getShape().equals("Cercle")) {
                                       // Circle c = (Circle) o.shape();
                                        if (circleCircleCollision(goal, o.getPosition(), radius,0.5f)) {
                                            if(yPos<=player.getGround()){
                                                System.out.println("testR");
                                                player.setPosition(start,yPos1,angle);
                                            }
                                        }
                                    } else {
                                      float width = 0.1f;
                                      float height = 1;
                                        Point2D oBegin = new Point2D.Double(pos.getX() - width / 2, pos.getY() - width / 2);
                                        if (circleRectangleCollision(goal, oBegin, radius, width, height)) {
                                            player.setPosition(start,yPos1,angle);
                                        }
                                    }
                                    break;
                                case Maze.MONSTRE:
                                    MazeInterface.sounds(2).play();
                                    this.startF(0);
                                    break;

                                case Maze.BONUS:
                                    pickBonus(x, y);
                                     System.out.println("Bonus :"+player.getBonus().size());
                                    break;
                                case Maze.KEY:
                                    pickKey(x, y);
                                    break;
                                case Maze.TELEPORT:
                                    if(current.getCase(start)!=Maze.TELEPORT) //player.teleport(true);
                                  teleport(x, y);
                                    break;
                                case Maze.STAIRSUP:
                                    goUpStairs(start, goal, yPos, angle);
                                    break;

                                case Maze.STAIRSDOWN:
                                    goDownStairs(start, goal, yPos, angle);
                                    break;
                            }
                        }
                    }
                }
            }
        }


    //Renvoie le coin supérieur gauche des murs et portes et les monstres à proximité
    public ArrayList<Pair<Integer,Object>> closestStuff(Point2D center){
        ArrayList<Pair<Integer,Object>> neighbours=new ArrayList<>();
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                Point2D p = new Point2D.Double(center.getX() + i, center.getY() + j);
                if (isInBounds(p,current)){
                    int a=(int)p.getX();
                    int b=(int)p.getY();
                    if(current.getCase(p) == Maze.WALL)  neighbours.add(new Pair(Maze.WALL,new Point2D.Double(a,b)));
                    else if(current.getCase(p)==Maze.MONSTRE) neighbours.add(new Pair(Maze.MONSTRE,current.getMonster(a,b)));
                    else if(current.getCase(p)==Maze.DOOR){
                        neighbours.add(new Pair(Maze.DOOR,new Point2D.Double(a,b)));
                        System.out.println(a+"   "+b);
                    }
                }
            }
        return neighbours;
    }

    //Test de collision
    public Point2D checkCollision(Point2D center, float radius,ArrayList<Point2D> closestWalls,float width,float height) {
        Point2D wallBeginning = null;
        for (int i = 0; i < closestWalls.size(); i++) {
            wallBeginning = closestWalls.get(i);
            //if(debug) System.out.println(wallBeginning);
            if(circleRectangleCollision(center,wallBeginning,radius,width,height)){
                if(debug) System.out.println("Colliding wall " + wallBeginning);
                return wallBeginning;
            }
        }
        if(debug) System.out.println("No collision");
        return null;
    }

    //Les murs à proximité
    public ArrayList<Point2D> closestWalls(ArrayList<Pair<Integer,Object>> close) {
        ArrayList<Point2D> neighbours = new ArrayList<>();
        for (final Iterator<Pair<Integer, Object>> it = close.iterator(); it.hasNext(); ) {
            Pair<Integer, Object> p = it.next();
            if (p.getKey() == Maze.WALL) {
                neighbours.add((Point2D) p.getValue());
                //if(debug) System.out.println("Point:::"+p);
                it.remove();
            }
        }
        return neighbours;
    }

    public ArrayList<Point2D> closestDoors(ArrayList<Pair<Integer,Object>> close) {
        ArrayList<Point2D> neighbours = new ArrayList<>();
        for (final Iterator<Pair<Integer, Object>> it = close.iterator(); it.hasNext(); ) {
            Pair<Integer, Object> p = it.next();
            if (p.getKey() == Maze.DOOR) {
                neighbours.add((Point2D) p.getValue());
                it.remove();
            }
        }
        return neighbours;
    }


    /*---------------------------------------------------------Actions après déplacement-------------------------------------------------*/

   //Fonction de glissement le long du mur
    public void slide(Point2D start,Point2D goal,Point2D wallB,float radius,float angle){
        Line2D wallLine = wallSegment(start, wallB, goal, radius,1,1);
        if(debug) System.out.println(wallLine.getP1() + "    " + wallLine.getP2());
        Point2D collide = getCollisionCenter(wallLine, start, goal, radius);
        if(debug) System.out.println("Center :" + collide);
        Vector3D mvt = new Vector3D(collide).subtract(new Vector3D(goal));
        Vector3D wallNormal = wallNormal(collide, wallLine);
        if(debug) System.out.println("WallNormal: " + wallNormal.x() + "   " + wallNormal.y() + "   " + wallNormal.z());
        double correctionLength = mvt.dotProduct(wallNormal);
        if(debug) System.out.println("Push out of:" + correctionLength);
        double slideX = goal.getX() + correctionLength * wallNormal.x();
        double slideY = goal.getY() + correctionLength * wallNormal.z();
        player.setPosition(new Point2D.Double(slideX, slideY), angle);
        if(debug) System.out.println("New position: " + player.getPosition());
        Point2D wall;
        if ((wall=checkCollision(player.getPosition(), radius,closestWalls(closestStuff(player.getPosition())),1,1))!=null) slide(start,player.getPosition(),wall,radius,angle);
    }

    //Lorsqu'on arrive devant une porte
    public void openDoor(Point2D c,Point2D start,Point2D goal,float radius,float angle) {
        Door door = current.getDoor((int)c.getX(),(int)c.getY());
        if (door != null) {
            boolean b=false;
            for (Key k : player.keys()) {
                if (door.getKey().equals(k)) {
                    //  player.here animation
                    return;
                }
            }
            player.setPosition(start,angle);
        }
    }

    //Lorsqu'on rencontre un monstre et qu'on lui fait face
    public boolean monsterInFront(Monstres monster, Point2D start, Point2D goal,float angle) {
        int mons=monster.getDirec().get();
        double product=new Vector3D(goal).subtract(new Vector3D(start)).dotProduct(new Vector3D(monster.getPosition()).subtract(new Vector3D(start)));
        return (angle>=(mons+165)%360 && angle<=(mons+195)%360 && product>0);
    }

    //Pour ramasser une clé
    public void pickKey(int x, int y){
        Key key=current.getKey(x,y);
        System.out.println(key);
        if(key!=null){
            player.pickUp(key);
            current.free(y,x);
        }
    }

    //Pour ramasser un bonus
    public void pickBonus(int x, int y){
        Bonus bonus=current.getBonus(x,y);
        System.out.println(bonus);
        if(bonus!=null){
            player.pickUp(bonus);
            current.free(y,x);
        }
    }

    //Téléportation
    public void teleport(int x, int y){
        Teleporteur t=current.getTeleport(x,y);
        if(t!=null) player.setPosition(t.getEnd(), player.orientation());


    }

    //Lorsqu'on arrive sur une case d'escalier, on regarde vers le haut, on se met face à l'escalier puis on passe à l'état STAIRSUP
    public void goUpStairs(Point2D start, Point2D goal,double y,float angle) {
        Point upS = current.ending();
        int orient1 = orientPlayerReverse(upS);
       if((int)player.orientationX()!=20) player.setAnimation(player.new LookingTo(20));
       if ((int) angle != orient1){
           player.setPosition(BorderOnTheSameLine(start,goal),angle);
           System.out.println(player.getPosition());
           player.setAnimation(player.new TurningTo(orient1));
       }
       else{
           player.setPosition(start,angle);
           player.changeState(Player.PlayerState.STAIRSUP);
       }
    }



    //Lorsqu'on arrive sur une case escalier pour descendre
    public void goDownStairs(Point2D start, Point2D goal,double y,float angle) {
        if(debug) System.out.println(angle);
        Point downS = current.beginning();
        int orient1 = orientPlayerReverse(downS);
        //if((int)player.orientationX()!=340)player.setAnimation(player.new LookingTo(340));
        if (Math.floor(angle) != orient1){
            player.setPosition(BorderOnTheSameLine(start,goal),angle);
            player.setAnimation(player.new TurningTo(orient1));
        }
        else{
            player.setPosition(start,angle);
            player.changeState(Player.PlayerState.STAIRSDOWN);
        }
    }


/*Lorsqu'on est sur la passerelle
    Si on traverse un bord, on fait l'action appropriée: monter, descendre ou tomber dans le vide
 */
    public void inBetween(Point2D goal,Point2D start,float angle) {
        Player.PlayerState s = player.previousState();
        Point beg=null;
        boolean middle=false;
        int orient=0, orientR=0;
        if (s == Player.PlayerState.STAIRSDOWN) {
            beg = current.beginning();
            orient = orientPlayerReverse(beg);
            orientR = orientPlayer(beg);
        }
        else if (s == Player.PlayerState.STAIRSUP) {
            beg = current.ending();
            orient = orientPlayerReverse(beg);
            orientR = orientPlayer(beg);
        }
        if (Math.floor(goal.getX()) == Math.floor(beg.getX()) && Math.floor(goal.getY()) == Math.floor(beg.getY())) {
            if(debug) System.out.println("Sur un bord");
            if ((int)angle == orientR){
                if(s==Player.PlayerState.STAIRSDOWN){
                    floor++;
                }
                else floor--;
                player.reverseState(s);
            }
            else{
                if (Math.floor(angle) != orient) player.setAnimation(player.new TurningTo(orient));
                else{
                    if(s==Player.PlayerState.STAIRSUP) floor--;
                    else if(s== Player.PlayerState.STAIRSDOWN) floor++;
                    player.changeState(s);
                    player.setGround(floor);
                }
            }
        }
        else {
            Point nextStair=null;
            Point2D wayEnd;
            if (s == Player.PlayerState.STAIRSUP) nextStair = floors.get((floor + 1) / 2).beginning();
            else if (s == Player.PlayerState.STAIRSDOWN) nextStair = floors.get((floor - 1) / 2).ending();
            int orient2 = orientPlayer(nextStair), orient3=0;
            Point2D way = new Point2D.Double(beg.getX() + (int)Math.cos(Math.toRadians(orient)), beg.getY() + (int)Math.sin(Math.toRadians(orient)));
            if (orient == (orient2 + 180) % 360) {
                if (s == Player.PlayerState.STAIRSDOWN) orient3 = (orient + 90) % 180;
                else if (s == Player.PlayerState.STAIRSUP) orient3 = (180+(orient +90) % 180)%360;
                Point2D mid=new Point2D.Double(way.getX()+(int)Math.cos(Math.toRadians(orient3)),way.getY()+(int)Math.sin(Math.toRadians(orient3)));
                if(Math.floor(goal.getX())==Math.floor(mid.getX()) && Math.floor(goal.getY())==Math.floor(mid.getY())) middle=true;
                wayEnd=new Point2D.Double(beg.getX()+(int)Math.cos(Math.toRadians(orient3)),beg.getY()+(int)Math.sin(Math.toRadians(orient3)));
            }
            else wayEnd = new Point2D.Double(way.getX() + (int)Math.cos(Math.toRadians(orient2)), way.getY() + (int)Math.sin(Math.toRadians(orient2)));
            if (Math.floor(goal.getX()) == Math.floor(wayEnd.getX()) && Math.floor(goal.getY()) == Math.floor(wayEnd.getY())) {
                if((int)angle!=orient2 && player.animation()==null) player.setAnimation(player.new TurningTo(orient2));
                else  player.changeState(s);
            }
            else if ((Math.floor(goal.getX())!=Math.floor(way.getX()) || Math.floor(goal.getY())!=Math.floor(way.getY())) && !middle) {
                if(debug) System.out.println("J'ai traversé un mauvais côté, je tombe.");
                player.setAnimation(player.new Falling());
            }
        }
    }

    public void convertBonus(){
        if(player.getBonus().size()!=0) {
            player.useBonus();
            elapsed -= 2;
            if (elapsed <= 0) elapsed += 2;
        }
    }

    public void useBonus(){
        player.useBonus();
        elapsed-=30;
    }

   public void useBonus(int i) {
        if(player.getBonus().getFirst() instanceof TimeBonus)elapsed -= 30 * i;                
        player.useBonus(i);
   }
    /*------------------------------------------------------------------------------------------------------------------------*/




    /*---------------------------------------------Fonctions pour le glissement contre les murs------------------------------------------------*/
    /*Renvoie le centre de la hitbox au moment exact de la collision avec le mur
    -On trace le triangle rectangle formé par le point d'arrivée, le point de départ et le point de contact avec le segment de mur.
    -On détermine l'hypothénuse de ce triangle qui sera la distance entre le point de départ et la position du centre au moment de la collision(trigonométrie et produit vectoriel)

     */
    public Point2D getCollisionCenter(Line2D wall, Point2D start, Point2D goal, float radius) {
        Point2D intersection = segmentIntersection(wall, start, goal);
        Vector3D interDirection = new Vector3D(start).subtract(new Vector3D(intersection));
        Vector3D lineD = new Vector3D(wall.getP2()).subtract(new Vector3D(wall.getP1())).normalize();
        Vector3D interWall = lineD.posColinear(interDirection.dotProduct(lineD));
        double sinInvAngle = (interWall.norm() * interDirection.norm()) / interWall.crossProduct(interDirection).norm();
        double length = radius * ((java.lang.Double.isNaN(sinInvAngle)) ? 1 : sinInvAngle);
        Vector3D collision = new Vector3D(start).subtract(new Vector3D(intersection)).normalize().posColinear(length);
        return new Point2D.Double(intersection.getX() + collision.x(), intersection.getY() + collision.z());

    }

    //La normale au segment de mur dirigée vers le centre de la hitbox
    public Vector3D wallNormal(Point2D collide, Line2D wall) {
        Vector3D direction = new Vector3D(wall.getP2()).subtract(new Vector3D(wall.getP1()));
        Vector3D center = new Vector3D(collide).subtract(new Vector3D(wall.getP1()));
        Vector3D wallNormal = (direction.crossProduct(center).crossProduct(direction)).normalize();
        return wallNormal;
    }

    /*--------------------------*----------------------------------------------------------------------------------------------------------*/

    /*--------------------------------------Fonctions pour obtenir le point d'intersection-------------------------------*/
    public Point2D segmentIntersection(Line2D seg,Point2D start,Point2D goal){
        double segX1=seg.getX1();
        double segX2=seg.getX2();
        double segY1=seg.getY1();
        double segY2=seg.getY2();
        if(start.getX()!=goal.getX()){
            double a = (goal.getY() - start.getY()) / (goal.getX() - start.getX());
            double b = start.getY() - (a * start.getX());
            if(start.getY()==goal.getY() && segY1==segY2) return new Point2D.Double(((Math.min(start.getX(),goal.getX())<=segX1))?segX1:segX2,segY1);
            return(segX1==segX2)?new Point2D.Double(segX1,a*segX1+b):new Point2D.Double((segY1-b)/a,segY1);
        }
        return (segY1==segY2)?new Point2D.Double(goal.getX(),segY1):((Math.min(start.getY(),goal.getY())<=segY1)?new Point2D.Double(segX1,segY1):new Point2D.Double(segX1,segY2));
    }

    //Renvoie le segment de mur, de porte ou d'obstacle entré en collision
    public Line2D wallSegment(Point2D start, Point2D wallBeginning, Point2D goal, float radius,float width,float height) {
        Point2D wallX2 = new Point2D.Double(wallBeginning.getX() + width, wallBeginning.getY());
        Point2D wallY1 = new Point2D.Double(wallBeginning.getX(), wallBeginning.getY() + height);
        Point2D wallY2 = new Point2D.Double(wallBeginning.getX() + width, wallBeginning.getY() + height);
        if (start.getX() <= wallBeginning.getX()) {

            if (start.getY() <= wallBeginning.getY()) {
                Point2D prev1=new Point2D.Double(wallBeginning.getX(),wallBeginning.getY()-height);
                Point2D prev2=new Point2D.Double(wallBeginning.getX()-width,wallBeginning.getY());
                if(!isInBounds(prev1,current) || current.getCase(prev1)==Maze.WALL) return new Line2D.Double(wallBeginning,wallY1);
                if(!isInBounds(prev2,current) || current.getCase(prev2)==Maze.WALL) return new Line2D.Double(wallBeginning,wallX2);
                return circleSegmentCollision(wallBeginning, wallX2, goal, radius) ? new Line2D.Double(wallBeginning, wallX2) : new Line2D.Double(wallBeginning, wallY1);
            }
            else if (start.getY() <= wallY1.getY()) return new Line2D.Double(wallBeginning, wallY1);
            else {
                Point2D next1=wallY1;
                Point2D next2=new Point2D.Double(wallBeginning.getX()-width,wallBeginning.getY());
                if(!isInBounds(next1,current) || current.getCase(next1)==Maze.WALL) return new Line2D.Double(wallBeginning,wallY1);
                if(!isInBounds(next2,current) || current.getCase(next2)==Maze.WALL) return new Line2D.Double(wallY1,wallY2);
                return (circleSegmentCollision(wallY1, wallY2, goal, radius) ? new Line2D.Double(wallY1, wallY2) : new Line2D.Double(wallBeginning, wallY1));
            }
        } else {
            if (start.getY() <= wallBeginning.getY()) {
                if (start.getX() <= wallX2.getX()) {
                    return new Line2D.Double(wallBeginning, wallX2);
                } else {
                    Point2D prev1=new Point2D.Double(wallBeginning.getX(),wallBeginning.getY()-height);
                    Point2D next2=wallX2;
                    if(!isInBounds(prev1,current) || current.getCase(prev1)==Maze.WALL) return new Line2D.Double(wallX2,wallY2);
                    if(!isInBounds(next2,current) || current.getCase(next2)==Maze.WALL) return new Line2D.Double(wallBeginning,wallX2);
                    return (circleSegmentCollision(wallBeginning, wallX2, goal, radius)) ? new Line2D.Double(wallBeginning, wallX2) : new Line2D.Double(wallX2, wallY2);
                }
            } else {
                if (start.getY() <= wallY2.getY()) {
                    return new Line2D.Double(wallX2, wallY2);
                } else {
                    if (start.getX() <= wallY2.getX()) {
                        return new Line2D.Double(wallY1, wallY2);
                    } else{
                        Point2D next1=wallX2;
                        Point2D next2=wallY1;
                        if(!isInBounds(next1,current) || current.getCase(next1)==Maze.WALL) return new Line2D.Double(wallY1,wallY2);
                        if(!isInBounds(next2,current) || current.getCase(next2)==Maze.WALL) return new Line2D.Double(wallX2,wallY2);
                        return (circleSegmentCollision(wallY1, wallY2, goal, radius)) ? new Line2D.Double(wallY1, wallY2) : new Line2D.Double(wallX2, wallY2);
                    }

                }
            }
        }
    }

    //Renvoie le point d'intersection avec une segment orienté selon les axes
    public Point2D.Double BorderOnTheSameLine(Point2D prev, Point2D act){
        double x=Math.floor(Math.max(prev.getX(),act.getX()));
        double y=Math.floor(Math.max(prev.getY(),act.getY()));
        if(prev.getX()==act.getX()) x=prev.getX();
        else {
            double a =(act.getY() - prev.getY()) / (act.getX() - prev.getX());// coefficient directeur
            double b = prev.getY() - (a * prev.getX());//ordonnée à l'origine
            if (x >= Math.min(prev.getX(), act.getX()) && y < Math.min(prev.getY(), act.getY())) y = a * x + b;
            if (y >= Math.min(prev.getY(), act.getY()) && x < Math.min(prev.getX(), act.getX())){
                x = (y-b) / a;
            }
        }
        return new Point2D.Double(x,y);
    }


    /*-----------------------------Fonctions de test de collision--------------------------*/

    public boolean circleRectangleCollision(Point2D center,Point2D rBeginning,float radius,float width,float height){
        double deltaX = center.getX() - Math.max(rBeginning.getX(), Math.min(center.getX(), rBeginning.getX() + width));
        double deltaY = center.getY() - Math.max(rBeginning.getY(), Math.min(center.getY(), rBeginning.getY() + height));
        if(debug) System.out.println("Where : "+((float) (deltaX * deltaX + deltaY * deltaY))+"  Radius : "+(radius * radius));
        return (((float) (deltaX * deltaX + deltaY * deltaY)) < (radius * radius));
    }

    public boolean circleCircleCollision(Point2D center,Point2D center2,float radius,float radius2){
        double x1=center.getX();
        double x2=center2.getX();
        double y1=center.getY();
        double y2=center2.getY();
        return Math.abs((x1-x2)*(x1-x2)+(y1-y2)*(y1-y2))<(radius+radius2)*(radius+radius2);
    }

    public boolean circleSegmentCollision(Point2D wall1, Point2D wall2, Point2D circle, float radius) {
        Vector3D line = new Vector3D(wall2).subtract(new Vector3D(wall1));
        Vector3D lineD=line.normalize();
        Vector3D center = new Vector3D(circle).subtract(new Vector3D(wall1));
        double project = center.dotProduct(lineD);
        Vector3D pDepth = center.subtract(lineD.multiply(project));
        if (project < 0) pDepth =center;
        if (project > line.norm()) pDepth = new Vector3D(circle).subtract(new Vector3D(wall2));
        if(debug) System.out.println("Norm: "+pDepth.norm());
        return (float) pDepth.norm() < radius;
    }
}





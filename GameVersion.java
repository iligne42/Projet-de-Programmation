import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.geometry.Point3D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.*;
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

    /*public GameVersion(int length, int width, String name, Scores score) throws FormatNotSupported {
        player = new Player(name);
        current = new Maze(length, width);
        scores = score;
    }


    public GameVersion(Maze current, Player player, Scores score) {
        this.current = current;
        this.player = player;
        scores = score;
    }

    public GameVersion(Maze current, String name, Scores score) {
        this.current = current;
        player = new Player(name);
        scores = score;
    }

    public Maze current() {
        return current;
    }*/

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


    //Serialisation
    public void save(String file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file + ".ser");
        ObjectOutputStream oos = new ObjectOutputStream(fos);
        oos.writeObject(this);
        oos.close();
    }


    //Initialisation de la position et de l'orientation du joueur
    public void start() {
        startInFloor(current.beginning());

    }

    public void startInFloor(Point b){
        Point2D begin = new Point2D.Double(b.getX(), b.getY());
        int orient=orientPlayer(b);
        begin =(floor==0)?new Point2D.Double(begin.getX() + 0.5, begin.getY() + 0.5):firstPosition(b);
        player.setPosition(begin,orientPlayer(b));
        player.setAcceleration();

    }

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

    public void next(){
        current=floors.get((++floor)/2);
        player.setGround(floor);
        start();
       /* Point2D real=firstPosition(player.getPosition());
        Point2D relative=firstPosition(current.beginning());
        coordinateSystem=new Vector3D(real).subtract(new Vector3D(relative));*/
    }

    public int floor(){
        return floor;
    }

    public void prev(){
       current=floors.get((--floor)/2);
        player.setGround(floor);
        start();
        /*Point2D real=firstPosition(player.getPosition());
        Point2D relative=firstPosition(current.beginning());
        coordinateSystem=new Vector3D(real).subtract(new Vector3D(relative));*/
    }

    public boolean gameOver() {
        return current.equals(floors.getLast()) && current.getCase(player.getPosition()) == Maze.END ;
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
            this.handleMove(p, y, angle);
        }
    }


    public void handleMove(Point2D start,double yPos1,float angle1) {
        Point2D goal = player.getPosition();
        double yPos = player.getY();
        float angle = player.orientation();
        float radius = player.radius();
        if(start.getX() != goal.getX() || start.getY() != goal.getY()) {
            System.out.println();
            System.out.println("Start : " + start);
            System.out.println("Goal : " + goal);
            System.out.println(yPos);
            System.out.println(player.state());
        }

            if (player.state() == Player.PlayerState.BETWEEN) inBetween(goal, start, angle);
            else if (player.state() == Player.PlayerState.STAIRSUP) {
                if ((int) yPos >  floor) {
                    System.out.println(player.previousState());
                    if(floor%2==1){
                        System.out.println("Yaya");
                        player.changeState(Player.PlayerState.GROUND);
                        next();
                    }
                    else{
                        player.changeState(Player.PlayerState.BETWEEN);
                        floor++;
                        player.setGround(floor);
                    }

                    player.setGround(floor);
                }
                else if(Math.ceil(yPos)==floor){
                    if(floor%2==0) player.changeState(Player.PlayerState.GROUND);
                    else player.changeState(Player.PlayerState.BETWEEN);
                }
                else if(player.orientation()!=angle1) player.setPosition(start,angle1);

            }
            else if (player.state() == Player.PlayerState.STAIRSDOWN) {
                if (Math.ceil(yPos) < floor) {
                    if(floor%2==0){
                        player.changeState(Player.PlayerState.BETWEEN);
                        floor--;
                        player.setGround(floor);
                    }
                    else{
                        player.changeState(Player.PlayerState.GROUND);
                        prev();
                    }
                   /* if (player.previousState() == Player.PlayerState.BETWEEN) {

                    } else if (player.previousState() == Player.PlayerState.GROUND)*/
                }
                else if((int)yPos==floor){
                    if(floor%2==0) player.changeState(Player.PlayerState.GROUND);
                    else player.changeState(Player.PlayerState.BETWEEN);
                }
                else if(player.orientation()!=angle1) player.setPosition(start,angle1);
            }

            else if (player.state() == Player.PlayerState.GROUND) {
                if (start.getX() != goal.getX() || start.getY() != goal.getY()) {
                    if (!isInBounds(goal, current)) player.setPosition(start, angle);
                    else {
                        System.out.println(current.getCase(goal));
                        ArrayList<Pair<Integer, Object>> near = closestStuff(goal);
                        Point2D wallB = checkCollision(goal, radius, closestWalls(near), 1, 1);
                        Point2D doorB = checkCollision(goal, radius, closestDoors(near), 1, 1);
                        if (wallB != null) slide(start, goal, wallB, radius, angle);
                        else if (doorB != null) openDoor(doorB, start, goal, radius, angle);
                        else {
                            for (Pair<Integer, Object> p : near) {
                                if (p.getKey() == Maze.MONSTRE) {
                                    if (monsterInFront((Monstres) p.getValue(), start, goal, angle)) {
                                        this.start();
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
                                    if (o.equals("Cercle")) {
                                        Circle c = (Circle) o.shape();
                                        if (circleCircleCollision(goal, o.getPosition(), radius, (float) c.getRadius())) {
                                            Point2D obIntersection = circleIntersection(goal, pos, radius, (float) c.getRadius());
                                            retreat(start, goal, obIntersection, angle, radius);
                                        }
                                    } else {
                                        Rectangle r = (Rectangle) o.shape();
                                        float width = (float) r.getWidth();
                                        float height = (float) r.getHeight();
                                        Point2D oBegin = new Point2D.Double(pos.getX() - width / 2, pos.getY() - width / 2);
                                        if (circleRectangleCollision(goal, oBegin, radius, width, height)) {
                                            Point2D obIntersection = segmentIntersection(wallSegment(start, oBegin, goal, radius, width, height), start, goal);
                                            retreat(start, goal, obIntersection, angle, radius);
                                        }
                                    }
                                    break;
                                case Maze.BONUS:
                                    pickBonus(x, y);
                                    System.out.println(player.getBonus().size());
                                    break;
                                case Maze.KEY:
                                    pickKey(x, y);
                                    break;
                                case Maze.TELEPORT:
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
            else{

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
                    else if(current.getCase(p)==Maze.DOOR) neighbours.add(new Pair(Maze.DOOR,new Point2D.Double(a,b)));
                }
            }
        return neighbours;
    }

    //Renvoie le coin supérieur gauche du mur ou de la porte avec lequel il y a eu la collision, null sinon
   /* public Point2D checkCollision(Point2D center, float radius,ArrayList<Point2D> closestWalls,float width,float height) {
        Point2D wallBeginning = null;
        float dist=Integer.MAX_VALUE;
        for (int i = 0; i < closestWalls.size(); i++) {
            Point2D wallB = closestWalls.get(i);
            float d=distanceR(center,wallB,width,height);
            //System.out.println(wallBeginning);
            if(circleRectangleCollision(d,radius) && d<dist){
                dist=d;
                wallBeginning=wallB;
            }
        }
         System.out.println("Colliding wall " + wallBeginning);
        return wallBeginning;
    }*/

    public Point2D checkCollision(Point2D center, float radius,ArrayList<Point2D> closestWalls,float width,float height) {
        Point2D wallBeginning = null;
        for (int i = 0; i < closestWalls.size(); i++) {
            wallBeginning = closestWalls.get(i);
            //System.out.println(wallBeginning);
            if(circleRectangleCollision(center,wallBeginning,radius,width,height)){
                System.out.println("Colliding wall " + wallBeginning);
                return wallBeginning;
            }
        }
        System.out.println("No collision");
        return null;
    }

    public ArrayList<Point2D> closestWalls(ArrayList<Pair<Integer,Object>> close) {
        ArrayList<Point2D> neighbours = new ArrayList<>();
        for (final Iterator<Pair<Integer, Object>> it = close.iterator(); it.hasNext(); ) {
            Pair<Integer, Object> p = it.next();
            if (p.getKey() == Maze.WALL) {
                neighbours.add((Point2D) p.getValue());
                //System.out.println("Point:::"+p);
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


    public void slide(Point2D start,Point2D goal,Point2D wallB,float radius,float angle){
        Line2D wallLine = wallSegment(start, wallB, goal, radius,1,1);
        System.out.println(wallLine.getP1() + "    " + wallLine.getP2());
        Point2D collide = getCollisionCenter(wallLine, start, goal, radius);
        System.out.println("Center :" + collide);
           /* Vector3D mvt=new Vector3D(goal).subtract(new Vector3D(collide));
            Vector3D wallDir=new Vector3D(wallLine.getP2()).subtract(new Vector3D(wallLine.getP1())).normalize();
            Vector3D correctionVector=wallDir.multiply(wallDir.dotProduct(mvt));
            player.setPosition(new Point2D.Double(collide.getX()+correctionVector.x(),collide.getY()+correctionVector.y()),angle);
*/
        Vector3D mvt = new Vector3D(collide).subtract(new Vector3D(goal));
        Vector3D wallNormal = wallNormal(collide, wallLine);
        System.out.println("WallNormal: " + wallNormal.x() + "   " + wallNormal.y() + "   " + wallNormal.z());
        double correctionLength = mvt.dotProduct(wallNormal);
        System.out.println("Push out of:" + correctionLength);
        double slideX = goal.getX() + correctionLength * wallNormal.x();
        double slideY = goal.getY() + correctionLength * wallNormal.z();
        player.setPosition(new Point2D.Double(slideX, slideY), angle);
        System.out.println("New position: " + player.getPosition());
        if (checkCollision(player.getPosition(), radius,closestWalls(closestStuff(player.getPosition())),1,1)!=null) {
            //player.setPosition(start, angle);
            player.setPosition(collide, angle);
            System.out.println(collide);
            if (checkCollision(player.getPosition(), radius,closestWalls(closestStuff(player.getPosition())),1,1)!=null) {
                System.out.println("Problem");
            }
            System.out.println("New position2: " + player.getPosition());
        }
    }

    public void retreat(Point2D start,Point2D goal,Point2D intersection,float ori,float radius){
        Vector3D radiusMvt=new Vector3D(goal).subtract(new Vector3D(start)).negColinear(radius);
        Vector3D interMvt=new Vector3D(intersection).subtract(new Vector3D(goal));
        Vector3D pDepth=radiusMvt.add(interMvt);
        player.setPosition(new Point2D.Double(goal.getX()+pDepth.x(),goal.getY()+pDepth.z()),ori);
        //player.setPosition(new Point2D.Double(2*intersection.getX()-goal.getX(),2*intersection.getY()-goal.getY()),ori);
    }

    public void openDoor(Point2D c,Point2D start,Point2D goal,float radius,float angle) {
        Door door = current.getDoor((int)c.getX(),(int)c.getY());
        if (door != null) {
            boolean b=false;
            for (Key k : player.keys()) {
                if (door.getKey().equals(k)) {
                    //  player.
                    return;
                }
            }
            Point2D doorIntersection=segmentIntersection(wallSegment(start,c,goal,radius,1,1),start,goal);
            retreat(start,goal,doorIntersection,angle,radius);
        }
    }

    public boolean monsterInFront(Monstres monster, Point2D start, Point2D goal,float angle) {
        return monster.getDirec().get()==(angle+180)%360 && new Vector3D(goal).subtract(new Vector3D(start)).dotProduct(new Vector3D(monster.getPosition()).subtract(new Vector3D(start)))>0;
    }


    public void pickKey(int x, int y){
        Key key=current.getKey(x,y);
        if(key!=null){
            player.pickUp(key);
            current.free(y,x);
        }
    }

    public void pickBonus(int x, int y){
        Bonus bonus=current.getBonus(x,y);
        if(bonus!=null){
            player.pickUp(bonus);
            current.free(y,x);
        }
    }


    public void teleport(int x, int y){
        Teleporteur t=current.getTeleport(x,y);
        player.setPosition(t.getEnd(),player.orientation());

    }

    public void goUpStairs(Point2D start, Point2D goal,double y,float angle) {
        System.out.println(angle);
        Point upS = current.ending();
        int orient1 = orientPlayerReverse(upS);
        if ((int) angle != orient1) player.setPosition(start, angle);
        else player.changeState(Player.PlayerState.STAIRSUP);
    }




    public void goDownStairs(Point2D start, Point2D goal,double y,float angle) {
        System.out.println(angle);
        Point downS = current.beginning();
        int orient1 = orientPlayerReverse(downS);
        if ((int) angle != orient1) player.setPosition(start, angle);
        else player.changeState(Player.PlayerState.STAIRSDOWN);
    }



    public void inBetween(Point2D goal,Point2D start,float angle) {
        Player.PlayerState s = player.previousState();
        Point beg=null;
        int orient=0, orientR=0;
        if (s == Player.PlayerState.STAIRSDOWN) {
            beg = current.beginning();
            orient = orientPlayerReverse(beg);
            orientR = orientPlayer(beg);
        } else if (s == Player.PlayerState.STAIRSUP) {
            beg = current.ending();
            orient = orientPlayerReverse(beg);
            orientR = orientPlayer(beg);
        }
        if ((int) goal.getX() == (int) beg.getX() && (int) goal.getY() == (int) beg.getY()) {
            if ((int) angle == orientR){
                if(s==Player.PlayerState.STAIRSDOWN){
                    floor++;
                }
                else floor--;
                player.reverseState(s);
            }
            else if((int)angle==orient){
                if(s==Player.PlayerState.STAIRSUP) floor--;
                else if(s== Player.PlayerState.STAIRSDOWN) floor++;
                player.changeState(s);
                player.setGround(floor);
            }
            else player.setPosition(start, angle);
        } else {
            Point nextStair=null;
            Point2D way, wayEnd;
            if (s == Player.PlayerState.STAIRSUP) nextStair = floors.get((floor + 1) / 2).beginning();
            else if (s == Player.PlayerState.STAIRSDOWN) nextStair = floors.get((floor - 1) / 2).ending();
            int orient2 = orientPlayer(nextStair), orient3=0;
            if (orient == (orient2 + 180) % 360) {
                if (s == Player.PlayerState.STAIRSDOWN) orient3 = (orient - 90) % 180;
                else if (s == Player.PlayerState.STAIRSUP) orient3 = (orient + 90) % 180;
                way = beg;
            } else {
                orient3 = orient2;
                way = new Point2D.Double(beg.getX() + Math.cos(Math.toRadians(orient)), beg.getY() + Math.sin(Math.toRadians(orient)));
            }
            wayEnd = new Point2D.Double(way.getX() + Math.cos(Math.toRadians(orient3)), way.getY() + Math.sin(Math.toRadians(orient3)));
            if (((int) goal.getX() == (int)wayEnd.getX() && (int) goal.getY() == (int) wayEnd.getY())) {
                System.out.println(angle);
                if ((int) angle == orient2) player.changeState(s);
                else player.setPosition(start, angle);
            } else if ((int) goal.getX() != (int) start.getX() || (int) goal.getY() != (int) start.getY())
                player.changeState(Player.PlayerState.FALLING);
        }
    }


   public void useBonus(int i) {
        if(player.getBonus().getFirst() instanceof TimeBonus){
            player.useBonus(i);
            elapsed -= 30 * i;
        }
        else{

        }

   }
    /*------------------------------------------------------------------------------------------------------------------------*/




    /*---------------------------------------------Fonctions pour le glissement contre les murs------------------------------------------------*/
    //Renvoie le centre de la hitbox au moment exact de la collision avec le mur
    public Point2D getCollisionCenter(Line2D wall, Point2D start, Point2D goal, float radius) {
        Point2D intersection = segmentIntersection(wall, start, goal);
        System.out.println("Intersection :" + intersection);
        Vector3D interDirection = new Vector3D(start).subtract(new Vector3D(intersection));
        System.out.println(start + "      " + intersection);
        System.out.println("PS " + interDirection.x() + "   " + interDirection.y() + "   " + interDirection.z());
        Vector3D lineD = new Vector3D(wall.getP2()).subtract(new Vector3D(wall.getP1())).normalize();
        Vector3D interWall = lineD.posColinear(interDirection.dotProduct(lineD));
        System.out.println("PA " + interWall.x() + "  " + interWall.y() + "   " + interWall.z());
        double sinInvAngle = (interWall.norm() * interDirection.norm()) / interWall.crossProduct(interDirection).norm();
        double length = radius * ((java.lang.Double.isNaN(sinInvAngle)) ? 1 : sinInvAngle);
        System.out.println("Center to wall :" + length);
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

    /*------------------------------------------------------------------------------------------------------------------------------------*/

    /*--------------------------------------Fonctions pour obtenir le point d'intersection-------------------------------*/
    public Point2D segmentIntersection(Line2D seg,Point2D start,Point2D goal){
        double segX1=seg.getX1();
        double segX2=seg.getX2();
        double segY1=seg.getY1();
        double segY2=seg.getY2();
        if(start.getX()!=goal.getX()){
            double a = (goal.getY() - start.getY()) / (goal.getX() - start.getX());
            double b = start.getY() - (a * start.getX());
            return(segX1==segX2)?new Point2D.Double(segX1,a*segX1+b):new Point2D.Double((segY1-b)/a,segY1);
        }
        return (segY1==segY2)?new Point2D.Double(goal.getX(),segY1):((Math.min(start.getY(),goal.getY())<=segY1)?new Point2D.Double(segX1,segY1):new Point2D.Double(segX1,segY2));
    }



    public Point2D circleIntersection(Point2D goal,Point2D center,float r1,float r2){
        return new Point2D.Double(((goal.getX()*r2)+(center.getX()*r1))/(r1+r2),((goal.getY()*r2)+(center.getY()*r1))/(r1+r2));
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








    /*-----------------------------Fonctions de test de collision--------------------------*/


    public boolean circleRectangleCollision(Point2D center,Point2D rBeginning,float radius,float width,float height){
        double deltaX = center.getX() - Math.max(rBeginning.getX(), Math.min(center.getX(), rBeginning.getX() + width));
        double deltaY = center.getY() - Math.max(rBeginning.getY(), Math.min(center.getY(), rBeginning.getY() + height));
        System.out.println("Where : "+((float) (deltaX * deltaX + deltaY * deltaY))+"  Radius : "+(radius * radius));
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
        System.out.println("Norm: "+pDepth.norm());
        return (float) pDepth.norm() < radius;
    }

    /*-------------------------------------------------------------------------------*/









   //Unused so far-----------------------------------------------------------------------------------------------------------------------------------------
    public void pickObject() {
        Point2D point = player.getPosition();
        if (current.getCase(point) == Maze.KEY) {
            Key key = current.getKey(point);
            player.pickUp(key);
            current.free((int) point.getY(), (int) point.getX());
        } else if (current.getCase(point) == Maze.BONUS) {
            Bonus bonus = current.getBonus(point);
            player.pickUp(bonus);
            current.free((int) point.getY(), (int) point.getX());
        }
    }

    public Point2D.Double BorderOnTheSameLine(Point2D prev, Point2D act){
        double x=((int)Math.max(prev.getX(),act.getX()));
        double y=((int)Math.max(prev.getY(),act.getY()));
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
        // return new Point2D.Double(2*x-act.getX(),2*y-act.getY());
    }

    public Point2D circleIntersection(Point2D goal,Point2D center,float r2){
        Vector3D dist=new Vector3D(goal).subtract(new Vector3D(center)).posColinear(r2);
        return new Point2D.Double(center.getX()+dist.x(),center.getY()+dist.z());
    }



   /* public void inBetween(Point2D goal,Point2D start,float angle){
        if (player.previousState() == Player.PlayerState.STAIRSDOWN) {
            Point beg = current.beginning();
            int orient = orientPlayerReverse(beg);
            int orientR = orientPlayer(beg);
            if ((int) angle == orientR && (int) goal.getX() == (int)beg.getX() && (int) goal.getY() == (int) beg.getY())
                player.changeState(Player.PlayerState.STAIRSUP);
            else {
                Point nextStair = floors.get((floor - 1) / 2).ending();
                int orient2 = orientPlayer(nextStair);
                Point2D way = new Point2D.Double(beg.getX() + Math.cos(Math.toRadians(orient)), beg.getY() + Math.sin(Math.toRadians(orient)));
                Point2D wayEnd = new Point2D.Double(way.getX() + Math.cos(Math.toRadians(orient2)), way.getY() + Math.sin(Math.toRadians(orient2)));
                if(orient==(orient2+180)%360) wayEnd=new Point2D.Double(beg.getX()+Math.cos(Math.toRadians((orient-90)%180)),beg.getY()+Math.sin(Math.toRadians((orient-90)%180)));
                if (((int) goal.getX() == wayEnd.getX() && (int) goal.getY() == (int) wayEnd.getY() || (int) goal.getX() == way.getX() && (int) goal.getY() == (int) way.getY())){
                    if((int)angle==orient2)  player.changeState(Player.PlayerState.STAIRSDOWN);
                    else player.setPosition(start,angle);
                }
                else if ((int) goal.getX() != (int) start.getX() || (int) goal.getY() != (int) start.getY())
                    player.changeState(Player.PlayerState.JUMP);
            }
        } else if (player.previousState() == Player.PlayerState.STAIRSUP) {
            Point end = current.ending();
            int orient = orientPlayerReverse(end);
            int orientR = orientPlayer(end);
            if ((int) angle == orientR && (int) goal.getX() == (int) end.getX() && (int) goal.getY() == (int) end.getY())
                player.changeState(Player.PlayerState.STAIRSDOWN);
            else {
                Point nextStair = floors.get((floor + 1) / 2).beginning();
                int orient2 = orientPlayer(nextStair);
                Point2D way = new Point2D.Double(end.getX() + Math.cos(Math.toRadians(orient)), end.getY() + Math.sin(Math.toRadians(orient)));System.out.println(way);
                Point2D wayEnd = new Point2D.Double(way.getX() + Math.cos(Math.toRadians(orient2)), way.getY() + Math.sin(Math.toRadians(orient2)));System.out.println(wayEnd);
                if(orient==(orient2+180)%360) wayEnd=new Point2D.Double(end.getX()+Math.cos(Math.toRadians((orient+90)%180)),end.getY()+Math.sin(Math.toRadians((orient+90)%180)));
                if (((int) goal.getX() == wayEnd.getX() && (int) goal.getY() == (int) wayEnd.getY() || (int) goal.getX() == way.getX() && (int) goal.getY() == (int) way.getY())){
                    if((int)angle==orient2)  player.changeState(Player.PlayerState.STAIRSUP);
                    else player.setPosition(start,angle);
                }
                else if ((int) goal.getX() != (int) start.getX() || (int) goal.getY() != (int) start.getY())
                    player.changeState(Player.PlayerState.JUMP);
            }
        }
    }
*/

}





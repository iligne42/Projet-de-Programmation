import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.util.Pair;

import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;


public abstract class GameVersion implements Serializable {
    protected Player player;
    protected Maze maze;
    protected Scores scores;
    protected int elapsed = 0;

    public GameVersion(int length, int width, String name, Scores score) throws FormatNotSupported {
        player = new Player(name);
        maze = new Maze(length, width);
        scores = score;
    }


    public GameVersion(Maze maze, Player player, Scores score) {
        this.maze = maze;
        this.player = player;
        scores = score;
    }

    public GameVersion(Maze maze, String name, Scores score) {
        this.maze = maze;
        player = new Player(name);
        scores = score;
    }

    public Maze maze() {
        return maze;
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
        startInFloor(maze.beginning());

    }

    public void startInFloor(Point b){
        Point2D begin = new Point2D.Double(b.getX(), b.getY());
        int orientation = 90;
        int x = (int) begin.getX();
        int y = (int) begin.getY();
        if (y == 0) orientation = 90;
        else if (x == 0) orientation = 0;
        else if (x == maze.getWidth() - 1) orientation = 180;
        else if (y == maze.getHeight() - 1) orientation = 270;
        begin = new Point2D.Double(begin.getX() + 0.5, begin.getY() + 0.5);
        player.setPosition(begin, orientation);

    }

    public boolean gameOver() {
        return maze.getCase(player.getPosition()) == Maze.END;
    }

    public boolean isInBounds(Point2D p) {
        return p.getX() >= 0 && p.getY() >= 0 && p.getX() < maze.getWidth() && p.getY() < maze.getHeight();
    }

    public void move(int direction) {
        Point2D p = (Point2D) player.getPosition().clone();
        switch (direction) {
            case 1:
                player.moveForward();
                break;
            case 2:
                player.moveBackward();
                break;
            case 3:
                player.moveLeft();
                break;

            case 4:
                player.moveRight();
                break;
        }
        this.handleMove(p);
    }


    public void handleMove(Point2D start) {
        Point2D goal = player.getPosition();
        int angle = player.orientation();
        float radius = player.radius();
        if (start.getX() != goal.getX() || start.getY() != goal.getY()) {
            if (!isInBounds(goal)) player.setPosition(start, angle);
            else {
                ArrayList<Pair<Integer, Object>> near = closestStuff(goal);
                Point2D wallB = checkCollision(goal, radius, closestWalls(near),1,1);
                Point2D doorB = checkCollision(goal, radius, closestDoors(near),1,1);
                if (wallB != null) slide(start, goal, wallB, radius, angle);
                else if (doorB != null) openDoor(doorB, start, goal, radius, angle);
                else {
                    for (Pair<Integer, Object> p : near) {
                        if (p.getKey() == Maze.MONSTRE) {
                            if (monsterInFront((Monstres) p.getValue(), start, goal, angle)) {
                                this.start();
                                break;
                            }
                        }
                    }
                    int x = (int) goal.getX();
                    int y = (int) goal.getY();
                    switch (maze.getCase(y, x)) {
                        case Maze.OBSTACLE:
                            Obstacles o = maze.getObstacle(x, y);
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
                        case Maze.KEY:
                            pickKey(x, y);
                        case Maze.TELEPORT:
                            teleport(x, y);
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
                if (isInBounds(p)){
                    int a=(int)p.getX();
                    int b=(int)p.getY();
                    if(maze.getCase(p) == Maze.WALL)  neighbours.add(new Pair(Maze.WALL,new Point2D.Double(a,b)));
                    else if(maze.getCase(p)==Maze.MONSTRE) neighbours.add(new Pair(Maze.MONSTRE,maze.getMonster(a,b)));
                    else if(maze.getCase(p)==Maze.DOOR) neighbours.add(new Pair(Maze.DOOR,new Point2D.Double(a,b)));
                }
            }
        return neighbours;
    }

    //Renvoie le coin supérieur gauche du mur ou de la porte avec lequel il y a eu la collision, null sinon
    public Point2D checkCollision(Point2D center, float radius,ArrayList<Point2D> closestWalls,float width,float height) {
        Point2D wallBeginning = null;
        for (int i = 0; i < closestWalls.size(); i++) {
            wallBeginning = closestWalls.get(i);
            System.out.println(wallBeginning);
            if(circleRectangleCollision(center,wallBeginning,radius,width,height)) {
                System.out.println("Colliding wall " + wallBeginning);
                return wallBeginning;
            }
        }
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

    public void slide(Point2D start,Point2D goal,Point2D wallB,float radius,int angle){
        Line2D wallLine = wallSegment(start, wallB, goal, radius,1,1);
        System.out.println(wallLine.getP1() + "    " + wallLine.getP2());
        Point2D collide = getCollisionCenter(wallLine, start, goal, radius);
        System.out.println("Center :" + collide);
           /* Vector3D mvt=new Vector3D(goal).subtract(new Vector3D(collide));
            Vector3D wallDir=new Vector3D(wallLine.getP2()).subtract(new Vector3D(wallLine.getP1()));
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
        }
    }

    public void retreat(Point2D start,Point2D goal,Point2D intersection,int ori,float radius){
        Vector3D radiusMvt=new Vector3D(goal).subtract(new Vector3D(start)).negColinear(radius);
        Vector3D interMvt=new Vector3D(intersection).subtract(new Vector3D(goal));
        Vector3D pDepth=radiusMvt.add(interMvt);
        player.setPosition(new Point2D.Double(goal.getX()+pDepth.x(),goal.getY()+pDepth.z()),ori);
        //player.setPosition(new Point2D.Double(2*intersection.getX()-goal.getX(),2*intersection.getY()-goal.getY()),ori);
    }

    public void openDoor(Point2D c,Point2D start,Point2D goal,float radius,int angle) {
        Door door = maze.getDoor((int)c.getX(),(int)c.getY());
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

    public boolean monsterInFront(Monstres monster, Point2D start, Point2D goal,double angle) {
        return monster.getDirec().get()==(angle+180)%360 && new Vector3D(goal).subtract(new Vector3D(start)).dotProduct(new Vector3D(monster.getPosition()).subtract(new Vector3D(start)))>0;
    }


    public void pickKey(int x, int y){
        Key key=maze.getKey(x,y);
        if(key!=null){
            player.pickUp(key);
            maze.free(y,x);
        }
    }

    public void pickBonus(int x, int y){
        Bonus bonus=maze.getBonus(x,y);
        if(bonus!=null){
            player.pickUp(bonus);
            maze.free(y,x);
        }
    }


    public void teleport(int x, int y){
        Teleporteur t=maze.getTeleport(x,y);
        player.setPosition(t.getEnd(),player.orientation());

    }

    /*------------------------------------------------------------------------------------------------------------------------*/




    /*---------------------------------------------Fonctions pour le glissement contre les murs------------------------------------------------*/
    //Renvoie le centre de la hitbox au moment exact de la collision avec le mur
    public Point2D getCollisionCenter(Line2D wall, Point2D start, Point2D goal, float radius) {
        Point2D intersection = segmentIntersection(wall, start, goal);
        System.out.println("Start : " + start);
        System.out.println("Goal : " + goal);
        System.out.println("Intersection :" + intersection);
        Vector3D interWall = new Vector3D(wall.getP1()).subtract(new Vector3D(intersection));
        System.out.println("PA " + interWall.x() + "  " + interWall.y() + "   " + interWall.z());
        Vector3D interDirection = new Vector3D(start).subtract(new Vector3D(intersection));
        System.out.println(start + "      " + intersection);
        System.out.println("PS " + interDirection.x() + "   " + interDirection.y() + "   " + interDirection.z());
        System.out.println(interWall.x() + "  " + interWall.y() + "   " + interWall.z());
        double sinInvAngle = (interWall.norm() * interDirection.norm()) / interWall.crossProduct(interDirection).norm();
        double length = radius * ((java.lang.Double.isNaN(sinInvAngle)) ? 1 : sinInvAngle);
        System.out.println("Center to wall :" + length);
        Vector3D collision = new Vector3D(start).subtract(new Vector3D(goal)).normalize().posColinear(length);
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
        return (segY1==segY2)?new Point2D.Double(goal.getX(),segY1):new Point2D.Double(segX1,Math.floor(Math.min(start.getY(),goal.getY())));
    }



    public Point2D circleIntersection(Point2D goal,Point2D center,float r1,float r2){
        return new Point2D.Double(((goal.getX()*r2)+(center.getX()*r1))/(r1+r2),((goal.getY()*r2)+(center.getY()*r1))/(r1+r2));
    }

    //Renvoie le segment de mur, de porte ou d'obstacle entré en collision
    public Line2D wallSegment(Point2D start, Point2D wallBeginning, Point2D goal, float radius,float width,float height) {
        Point2D wallX2 = new Point2D.Double(wallBeginning.getX() + width, wallBeginning.getY());
        Point2D wallY1 = new Point2D.Double(wallBeginning.getX(), wallBeginning.getY() + height);
        Point2D wallY2 = new Point2D.Double(wallBeginning.getX() + width, wallBeginning.getY() + height);
        double angle = player.orientation();
        if (start.getX() <= wallBeginning.getX()) {
            if (start.getY() <= wallY1.getY()) return new Line2D.Double(wallBeginning, wallY1);
            else if (start.getY() <= wallBeginning.getY()) {
                return circleSegmentCollision(wallBeginning, wallX2, goal, radius) ? new Line2D.Double(wallBeginning, wallX2) : new Line2D.Double(wallBeginning, wallY1);
            } else {
                return (circleSegmentCollision(wallY1, wallY2, goal, radius) ? new Line2D.Double(wallY1, wallY2) : new Line2D.Double(wallBeginning, wallY1));
            }
        } else {
            if (start.getY() <= wallBeginning.getY()) {
                if (start.getX() <= wallX2.getX()) {
                    return new Line2D.Double(wallBeginning, wallX2);
                } else {
                    return (circleSegmentCollision(wallBeginning, wallX2, goal, radius)) ? new Line2D.Double(wallBeginning, wallX2) : new Line2D.Double(wallX2, wallY2);
                }
            } else {
                if (start.getY() <= wallY2.getY()) {
                    return new Line2D.Double(wallX2, wallY2);
                } else {
                    if (start.getX() <= wallY2.getX()) {
                        return new Line2D.Double(wallY1, wallY2);
                    } else
                        return (circleSegmentCollision(wallY1, wallY2, goal, radius)) ? new Line2D.Double(wallY1, wallY2) : new Line2D.Double(wallX2, wallY2);
                }
            }
        }
    }








    /*-----------------------------Fonctions de test de collision--------------------------*/

    public boolean circleRectangleCollision(Point2D center,Point2D rBeginning,float radius,float width,float height){
        double deltaX = center.getX() - Math.max(rBeginning.getX(), Math.min(center.getX(), rBeginning.getX() + width));
        double deltaY = center.getY() - Math.max(rBeginning.getY(), Math.min(center.getY(), rBeginning.getY() + height));
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
        Vector3D lineD = new Vector3D(wall2).subtract(new Vector3D(wall1)).normalize();
        Vector3D center = new Vector3D(circle).subtract(new Vector3D(wall1));
        double project = center.dotProduct(lineD);
        Vector3D pDepth = center.subtract(lineD.multiply(project));
        if (project < 0) pDepth = center.subtract(new Vector3D(wall1));
        if (project > lineD.norm()) pDepth = center.subtract(new Vector3D(wall2));
        return (float) pDepth.norm() < radius;
    }

    /*-------------------------------------------------------------------------------*/









   //Unused so far-----------------------------------------------------------------------------------------------------------------------------------------
    public void pickObject() {
        Point2D point = player.getPosition();
        if (maze.getCase(point) == Maze.KEY) {
            Key key = maze.getKey(point);
            player.pickUp(key);
            maze.free((int) point.getY(), (int) point.getX());
        } else if (maze.getCase(point) == Maze.BONUS) {
            Bonus bonus = maze.getBonus(point);
            player.pickUp(bonus);
            maze.free((int) point.getY(), (int) point.getX());
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

}





import javax.sound.sampled.Line;
import java.awt.*;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.io.*;
import java.util.ArrayList;


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

    public boolean openDoor(Point2D point) {
        if (maze.getCase(point) != Maze.DOOR) return false;
        else {
            Door door = maze.getDoor(point);
            for (Key a : player.keys()) {
                if (door.getKey().equals(a)) return true;
            }
            return false;
        }
    }

    public boolean monsterInFront() {
        return false;
        //open classroom, si le monstre et le joueur sont face à face,et à moins d'une case de distance
    }

    public void teleport()



    public void handleMove(Point2D start) {

        //On verifie s'il y a collision avec un mur(on glisse), un obstacle(on perd des points de vie et on retourne en arriere), un objet(on le ramasse si on veut), un teleporteur(on propose de rentrer dedans, soit si on arrive sur la case ou jute devant l'entrée)
        //Apres correction de la collision
        //Peut etre une grosse fonction checkContact pour vérifier avec quoi il ya eu contact, qui peut renvoyer un couple point2D et le type
        //SI mur, on glisse, si entrée ou obstacle on fait un retreat(chercher comment faire) et on perd des points de vie si obstacle
        //Si portail, voir avec Iman
        //Si monstre(verifier le contact OU s'ils sont face à face à moins d'une case, on retourne à l'entrée
        //Si porte, si pas la clé on retreat, sinon on propose de l'ouvrir
        //Si clé on la ramasse
        //Donc apres correction de la collision, on fait l'action necessaire donc si un mur on glisse,
        //Object to object sliding
        Point2D goal = player.getPosition();
        int angle = player.orientation();
        double radius = player.radius();
        if (start.getX() != goal.getX() || start.getY() != goal.getY()){
            Point2D wallB = checkCollision(goal, radius);
           // System.out.println(wallB);
            if (wallB != null) slide(start,goal,wallB,radius,angle);
            else if (!isInBounds(goal)) player.setPosition(start, angle);
            else {
                switch(maze.getCase(goal)){
                    //case Maze.OBSTACLE: check Collsion soit circle circle soit rect circle
                    //case Maze.DOOR: if(openDoor()) il passe à la case suivante, sinon retreat
                    //case Maze.BONUS: pickObject();
                    //case Maze.KEY: pickObject();
                    //case Maze.TELEPORT: teleport();
                }
            }
        }
        // pickObject();
    }

    public void slide(Point2D start,Point2D goal,Point2D wallB,double radius,int angle){
        Line2D wallLine = wallSegment(start, wallB, goal, radius);
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
        if (checkCollision(player.getPosition(), radius) != null) {
            //player.setPosition(start, angle);
            player.setPosition(collide, angle);
        }
    }

    public void retreat(){

    }


    //Renvoie le coin supérieur gauche du mur avec lequel il y a eu la collision, null sinon
    public Point2D checkCollision(Point2D center, double radius) {
        Point2D wallBeginning = null;
        boolean collide = false;
        ArrayList<Point2D> closestWalls = closestWalls(center);
        for (int i = 0; i < closestWalls.size(); i++) {
            wallBeginning = closestWalls.get(i);
            System.out.println(wallBeginning);
            double deltaX = center.getX() - Math.max(wallBeginning.getX(), Math.min(center.getX(), wallBeginning.getX() + 1));
            ;
            double deltaY = center.getY() - Math.max(wallBeginning.getY(), Math.min(center.getY(), wallBeginning.getY() + 1));
            collide = (((float) (deltaX * deltaX + deltaY * deltaY)) < (radius * radius));
            if (collide) {
                System.out.println("Colliding wall " + wallBeginning);
                return wallBeginning;
            }
        }
        return null;
    }

    //Murs à proximité
    public ArrayList<Point2D> closestWalls(Point2D center) {
        ArrayList<Point2D> neighbours = new ArrayList<>();
        for (int i = -1; i < 2; i++)
            for (int j = -1; j < 2; j++) {
                Point2D p = new Point2D.Double(center.getX() + i, center.getY() + j);
                if (isInBounds(p) && maze.getCase(p) == Maze.WALL) {
                    System.out.println("point:::" + p);
                    neighbours.add(new Point2D.Double((int) p.getX(), (int) p.getY()));
                }
            }
        return neighbours;
    }


    //Renvoie le segment de mur entré en collision
    public Line2D wallSegment(Point2D start, Point2D wallBeginning, Point2D goal, double radius) {
        Point2D wallX2 = new Point2D.Double(wallBeginning.getX() + 1, wallBeginning.getY());
        Point2D wallY1 = new Point2D.Double(wallBeginning.getX(), wallBeginning.getY() + 1);
        Point2D wallY2 = new Point2D.Double(wallBeginning.getX() + 1, wallBeginning.getY() + 1);
        double angle = player.orientation();
        if (start.getX() <= wallBeginning.getX()) {
            if (start.getY() <= wallY1.getY()) return new Line2D.Double(wallBeginning, wallY1);
            else if (start.getY() <= wallBeginning.getY()) {
                return lineSegmentCollision(wallBeginning, wallX2, goal, radius) ? new Line2D.Double(wallBeginning, wallX2) : new Line2D.Double(wallBeginning, wallY1);
            } else {
                return (lineSegmentCollision(wallY1, wallY2, goal, radius) ? new Line2D.Double(wallY1, wallY2) : new Line2D.Double(wallBeginning, wallY1));
            }
        } else {
            if (start.getY() <= wallBeginning.getY()) {
                if (start.getX() <= wallX2.getX()) {
                    return new Line2D.Double(wallBeginning, wallX2);
                } else {
                    return (lineSegmentCollision(wallBeginning, wallX2, goal, radius)) ? new Line2D.Double(wallBeginning, wallX2) : new Line2D.Double(wallX2, wallY2);
                }
            } else {
                if (start.getY() <= wallY2.getY()) {
                    return new Line2D.Double(wallX2, wallY2);
                } else {
                    if (start.getX() <= wallY2.getX()) {
                        return new Line2D.Double(wallY1, wallY2);
                    } else
                        return (lineSegmentCollision(wallY1, wallY2, goal, radius)) ? new Line2D.Double(wallY1, wallY2) : new Line2D.Double(wallX2, wallY2);
                }
            }
        }
    }

    public boolean lineSegmentCollision(Point2D wall1, Point2D wall2, Point2D circle, double radius) {
        Vector3D lineD = new Vector3D(wall2).subtract(new Vector3D(wall1)).normalize();
        Vector3D center = new Vector3D(circle).subtract(new Vector3D(wall1));
        double project = center.dotProduct(lineD);
        Vector3D pDepth = center.subtract(lineD.multiply(project));
        if (project < 0) pDepth = center.subtract(new Vector3D(wall1));
        if (project > lineD.norm()) pDepth = center.subtract(new Vector3D(wall2));
        return (float) pDepth.norm() < radius;
    }


    //Renvoie le centre de la hitbox au moment exact de la collision avec le mur
    public Point2D getCollisionCenter(Line2D wall, Point2D start, Point2D goal, double radius) {
        Point2D intersection = wallIntersection(wall, start, goal);
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

    //Renvoie le point d'intersection du déplacement du joueur avec le mur
    public Point2D wallIntersection(Line2D wall, Point2D start, Point2D goal) {
        double wallX1 = wall.getX1();
        double wallX2 = wall.getX2();
        double wallY1 = wall.getY1();
        double wallY2 = wall.getY2();
        if (start.getX() != goal.getX()) {
            double a = (goal.getY() - start.getY()) / (goal.getX() - start.getX());
            double b = start.getY() - (a * start.getX());
            return (wallX1 == wallX2) ? new Point2D.Double(wallX1, a * wallX1 + b) : new Point2D.Double((wallY1 - b) / a, wallY1);
        }
        return (wallY1 == wallY2) ? new Point2D.Double(goal.getX(), wallY1) : new Point2D.Double(wallX1, Math.floor(Math.min(start.getY(), goal.getY())));

    }

    //La normale au segment de mur dirigée vers le centre de la hitbox
    public Vector3D wallNormal(Point2D collide, Line2D wall) {
        Vector3D direction = new Vector3D(wall.getP2()).subtract(new Vector3D(wall.getP1()));
        Vector3D center = new Vector3D(collide).subtract(new Vector3D(wall.getP1()));
        Vector3D wallNormal = (direction.crossProduct(center).crossProduct(direction)).normalize();
        return wallNormal;
    }



}





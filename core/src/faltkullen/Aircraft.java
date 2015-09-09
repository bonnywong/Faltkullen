package faltkullen;

import java.awt.*;
import java.util.*;

public class Aircraft extends Unit {

    int fuel = 1000;

    Vector2D basePosition = new Vector2D(100, 100);
    Vector2D craftDirection = new Vector2D(0,0);
    Vector2D craftPosition = new Vector2D();
    Vector2D currentTarget = new Vector2D();

    double angle = 0.0;
    double velocity = 0.6;

    //Used for changing direction.
    double dx, dy;
    double currentDistance = 0;

    //ArrayList<Vector2D> waypoints = new ArrayList<Vector2D>();
    Queue<Vector2D> waypointsQueue = new LinkedList<Vector2D>();
    Queue<Vector2D> waypointsMarker = new LinkedList<Vector2D>();

    /*Vector2D testDestination = new Vector2D(150, 270);
    Vector2D testDestination2 = new Vector2D(300, 50);
    Vector2D testDestination3 = new Vector2D(20, 50);
    Vector2D testDestination4 = new Vector2D(20, 300);*/

    Vector2D testDestination = new Vector2D(150, 350);
    Vector2D testDestination2 = new Vector2D(250, 320);
    Vector2D testDestination3 = new Vector2D(300, 200);
    Vector2D testDestination4 = new Vector2D(250, 100);
    Vector2D testDestination5 = new Vector2D(150, 50);

    public Aircraft() {
        craftPosition.x = basePosition.x;
        craftPosition.y = basePosition.y;

        waypointsQueue.add(testDestination);
        waypointsQueue.add(testDestination2);
        waypointsQueue.add(testDestination3);
        waypointsQueue.add(testDestination4);
        waypointsQueue.add(testDestination5);
        waypointsQueue.add(basePosition);

        //Used only to draw the waypoints.
        waypointsMarker.addAll(waypointsQueue);
        //Check first order.
        checkOrders();
    }


    public void resupply(String option) {

    }

    public void moveTo(Vector2D destination) {

    }

    public void testMove() {
        angle = basePosition.angle(testDestination);
        System.out.println(angle);
        System.out.println("Degrees: " + (180/Math.PI)*angle);
        targetDestination(testDestination);
        System.out.println("Distance:" + distanceCalculation(testDestination, craftPosition));
    }

    /**
     * Returns the aircraft back to base. Ignoring all previous orders.
     */
    public void returnToBase() {
        targetDestination(basePosition);
        waypointsQueue.removeAll(waypointsQueue);
    }

    /**
     * Calculates the distance between two vectors.
     * @param v1
     * @param v2
     * @return
     */
    public double distanceCalculation(Vector2D v1, Vector2D v2) {
        double dx = v1.x - v2.x;
        double dy = v1.y - v2.y;
        return Math.sqrt(Math.abs(dx*dx + dy*dy));
    }

    /**
     * Targets a given direction and sets the direction vector
     * towards that target.
     * @param target
     */
    public void targetDestination(Vector2D target) {
        dx = target.x - craftPosition.x;
        dy = target.y - craftPosition.y;
        craftDirection.x = dx;
        craftDirection.y = dy;
        craftDirection.normalize();
    }

    public void draw(Graphics g) {
        g.setColor(Color.GREEN);
        g.fillRect((int) basePosition.x, (int) basePosition.y, 10, 10);
        g.setColor(Color.red);
        for(Vector2D v : waypointsMarker) {
            g.fillRect((int)v.x, (int)v.y, 5,5);
        }
        g.setColor(Color.YELLOW);
        g.fillRect((int)craftPosition.x, (int)craftPosition.y, 5,5);
    }

    public void distanceToWaypoint(){
        currentDistance = distanceCalculation(craftPosition, currentTarget);
    }

    /**
     * Do something.
     */
    public void update() {
        distanceToWaypoint();
        if(currentDistance <= 2 || currentDistance >= currentDistance + 2) { //less than 2 px away or overshot with 2px
            checkOrders();
        } else {
            //Keep moving.
            craftPosition.x += craftDirection.x * velocity;
            craftPosition.y += craftDirection.y * velocity;
        }


    }
    /**
     * Checks the queue for available waypoints/orders.
     */
    public void checkOrders() {
        if(waypointsQueue.isEmpty()) {
            returnToBase();
        } else {
            currentTarget = waypointsQueue.poll();
            targetDestination(currentTarget);
        }
    }
}

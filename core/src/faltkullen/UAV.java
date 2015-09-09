package faltkullen;

import java.awt.Graphics;
import java.util.ArrayList;

/**
 * This class represents an UAV in the simulation. It is used to extend the viewing range 
 * of units on the map. 
 */
public class UAV extends Unit{

    ArrayList<Soldier> soldierList = new ArrayList<Soldier>();
    ArrayList<Soldier> detectedList = new ArrayList<Soldier>();

    /*Certain variables are unused*/
    int sensorRange;
    int cost;
    int startX, startY;

    double sensorSpeed;
    double x, y;
    double angle = 1;

    /**
     * Creates an UAV.
     *
     * @param startX the x coordinate of the starting position
     * @param startY the y coordinate of the starting position
     * @param sensorRange the range of the UAV
     * @param sensorSpeed the rotation speed of the UAV
     */
    public UAV(int startX, int startY,int sensorRange, double sensorSpeed) {
        this.sensorRange = sensorRange;
        this.sensorSpeed = sensorSpeed;
        this.startX = startX;
        this.startY = startY;
        x = startX;
        y = startY;
    }

    /**
     * Sets the UAV rotation speed in degrees/tick.
     *
     * @param sensorSpeed the UAV rotation speed in degrees
     */
    public void setPhaseAngle(double sensorSpeed) {
        this.sensorSpeed = sensorSpeed;
    }

    /**
     * Sets the list of soldiers to be used.
     *
     * @param soldierList an ArrayList of Soldier objects
     */
    public void receiveSoldiers(ArrayList<Soldier> soldierList) {
        this.soldierList = soldierList;
    }

    /**
     * Detects enemy soldiers. Is unused due to its slowness.
     */
    public void detectEnemy() {
   /*
  for(int i = 0; i < soldierList.size(); i++) {
   double distance = getDistance(soldierList.get(i).getPositionX(), soldierList.get(i).getPositionY()); 
   if (distance < sensorRange) {
    /*Om soldaten inte är upptäckt.*/
   /*
    if(!soldierList.get(i).getDetected()) {
     soldierList.get(i).setDetected(true);
     detectedList.add(soldierList.get(i));
     System.out.println("A soldier is in range!!!! Distance is: " + distance + ". Sensor range is: " + sensorRange);
     System.out.println("At position: x=" + soldierList.get(i).getPositionX() + ". y=" + soldierList.get(i).getPositionY()); 
    } else {
     /*Soldaten redan upptäckt.*/
   /*
    }
   } else {
    /* Soldaten icke längre upptäckt. Vi sätter då soldaten till
     * oupptäckt och tar bort soldaten från listan över upptäckta
     * soldater. */
   /*
    if(soldierList.get(i).getDetected()) {
     soldierList.get(i).setDetected(false);
     if(detectedList.size() == 1) {
      detectedList.remove(0);
     } else {
      detectedList.remove(i);
     }
     System.out.println("Soldier left the range of the sensor!. Last known distance is: " + distance + ". Sensor range is: " + sensorRange);
     System.out.println("At position: x=" + soldierList.get(i).getPositionX() + ". y=" + soldierList.get(i).getPositionY()); 
    }
   }
  }
   */
    }

    /**
     * Finds the distance between this UAV and a point
     *
     * @param x the x coordinate of a point
     * @param y the y coordinate of a point
     */
    public double getDistance(int x, int y) {
        double distance = Math.sqrt((getX() - x)*(getX() - x) + (getY() - y)*(getY() - y));
        return distance;
    }

    /**
     * Returns the list of soldiers in use.
     *
     * @return ArrayList<Soldier> a list of soldiers the simulation contains
     */

    public ArrayList<Soldier> getSoldiers() {
        return this.soldierList;
    }

    /**
     * Returns the x position of the UAV. Assumes that the (0,0) is in the middle of the UAV.
     *
     * @return double the x position of the UAV
     */
    public double getX() {
        return x+sensorRange; //Flyttar origo till mitten av UAVn
    }

    /**
     * Returns the y position of the UAV. Assumes that the (0,0) is in the middle of the UAV.
     *
     * @return double the y position of the UAV
     */
    public double getY() {
        return y+sensorRange; //Flyttar origo till mitten av UAVn
    }

    /**
     * Updates the movement of the UAV. It moves in a circular pattern which can be
     * changed to elipse shapes depending on the values.
     */
    public void update() {
 /*Beräknar och uppdaterar cirkulär rörelse*/
        detectEnemy();
        angle = angle + Math.toRadians(sensorSpeed); //Öka med x antal grader per beräkning.
        x = startX + (Math.sin(angle) * 200); // a + sin(angle) * r. a = startvärde för x, r = rotations radien
        y = startY + (Math.cos(angle) * 200); // b + cos(angle) * r. b = startvärde för y, r = rotation radien
  /*Notera att olika radier ger oval rörelse.*/
    }

    /**
     * Draws the UAV
     *
     * @param g A Java Graphics object
     */
    public void draw(Graphics g) {
        //myColor = new Color(245, 235, 140, 123);
        //g.setColor(myColor);
        g.drawOval((int)x, (int)y, sensorRange*2, sensorRange*2); //Anledningen till sensorRange*2 är pga av att de vill ha höjd och bredd, sensorRange är endast en radie.
    }
}

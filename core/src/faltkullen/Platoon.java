package faltkullen;

import java.util.ArrayList;

public class Platoon {

    //vilka v�rden ska vi ha?
    //------------------------------------------------------------------------------------------------
    public Position enemyPosition;   //the position the enemy is considered to be located
    public Position enemyDirection;   //the direction in which the enemy is considered to be located
    public Position platoonPosition;  //the central position of the platoon

    ArrayList<Squad> squads;    //the squads this platoon controls

    public double combatValue;    //the percentage of full efficiency this unit retains
    //------------------------------------------------------------------------------------------------
    public int orderType;
    public Position orderPosition;
    public Army inArmy;

    public Platoon() {
        squads = new ArrayList<Squad>();
    }

    /*
     * Corresponds to the platoon leader taking action. Can be done every 5th or 10th step, to reduce cpu-drainage
     */
    public void action() {
        if (squads != null) {
            for (Squad s : squads) {
                //hej
            }
        }
        //ta reda p� var fienden finns
        //fr�ga grupperna vad dom ser
        //bed�m situationen
        //var �r fienden
        //hur ser egna resurser ut
        //hur ser v�r egen order ut

        //agera utefter situationen
        //planera strategi
        //strukturera om grupper
        //beordra grupper
        //bed�m stridsv�rde
    }

    /*
     * returns a SquadOrder, a more general one and easier to use.
     */
    public SquadOrder createOrder(int type, Position orderPosition) {
        SquadOrder order = new SquadOrder(
                type,
                contactRisk(),
                contactTime(),
                orderPosition,
                enemyPosition(orderPosition),
                retreatDirection(),
                0,
                isDetected(),
                0
        );
        return order;
    }

    /*
     * returns a SquadOrder, a more complex call with combat angle and need for secrecy.
     */
    public SquadOrder createOrder(int type, Position orderPosition, double needForSecrecy, int combatAngle) {
        SquadOrder order = new SquadOrder(
                type,
                contactRisk(),
                contactTime(),
                orderPosition,
                enemyPosition(orderPosition),
                retreatDirection(),
                needForSecrecy,
                isDetected(),
                combatAngle
        );
        return order;
    }

    /*
     * Returns wether this platoon and its units have been discovered
     */
    private boolean isDetected() {
        // TODO Auto-generated method stub
        return true;
    }

    /*
     * Return a position to which the current group can retreat to if needed
     */
    private Position retreatDirection() {
        // TODO Auto-generated method stub
        return null;
    }

    /*
     * Returns the position of the closest enemy
     */
    private Position enemyPosition(Position orderPosition) {
        // TODO Auto-generated method stub
        return orderPosition;
    }


    /*
     * Calculates the time before contact is expected on this location
     */
    private int contactTime() {
        // TODO Auto-generated method stub
        return 0;
    }


    /*
     * Based on certain locations, calculate the risk of contact
     */
    private double contactRisk() {
        // TODO Auto-generated method stub
        return 0.75;
    }

    public void giveOrder(Squad squad, int orderType, Position orderPosition) {
        SquadOrder order = createOrder(orderType, orderPosition);
        squad.setOrder(order);

        //ta bort tidigare order
        //tilldela order
        //nollst�ll alla finished positions och switchar
    }

    /*
     * Adds a squad to this platoon.
     */
    public void addSquad(Squad squaden) {
        squads.add(squaden);
    }

    /*
     * Sets the overall platoon order
     */
    public void setPlatoonOrder(int type, int x, int y) {
        orderType = type;
        orderPosition = new Position(x, y);
        enemyPosition = new Position(-1, -1);
    }
}




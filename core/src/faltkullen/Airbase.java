package faltkullen;

import java.util.ArrayList;

/**
 * Basic representation of an Airbase. Holds fuel and ammunition.
 * Currently only a skeleton class.
 */
public class Airbase {

    int fuelStock = 10000;
    int ammunitionStock = 10000;
    ArrayList<AirSquadron> airSquadrons = new ArrayList<AirSquadron>();

    Vector2D basePosition = new Vector2D();

    public Airbase(Vector2D basePosition) {
        this.basePosition = basePosition;
    }

    /**
     * Recall the aircraft.
     */
    public void recallAircraft (Aircraft aircraft) {
        aircraft.returnToBase();
    }

    public void recallSquadron (AirSquadron airSquadron) {
        airSquadron.returnToBase();
    }

    public void refuel() {

    }
}

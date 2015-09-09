package faltkullen;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AirSquadron {

    ArrayList<Aircraft> aircrafts = new ArrayList<Aircraft>();
    Vector2D basePosition = new Vector2D(100, 100); //Temporary values


    public AirSquadron() {

    }

    public AirSquadron(Vector2D basePosition, ArrayList<Aircraft> aircrafts) {
        this.basePosition = basePosition;
        this.aircrafts = aircrafts;
    }

    /**
     * Returns the entire squadron back to base.
     */
    public void returnToBase() {
        for(Aircraft aircraft : aircrafts) {
            aircraft.returnToBase();
        }
    }

    /**
     * Returns a specified aircraft belonging to the squadron
     * back to base.
     * @param aircraft
     */
    public void returnAircraftToBase(Aircraft aircraft) {
        if(aircrafts.contains(aircraft)) {
            aircraft.returnToBase();
        }
    }
}

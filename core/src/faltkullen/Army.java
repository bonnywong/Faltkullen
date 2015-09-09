package faltkullen;

import java.util.ArrayList;

public class Army {
    public ArrayList<Unit> units = new ArrayList<Unit>();
    public ArrayList<Squad> command = new ArrayList<Squad>();
    public ArrayList<Platoon> platoons = new ArrayList<Platoon>();
    public ArrayList<Unit> detectedEnemies = new ArrayList<Unit>();
    public int team;

    private int nextID = 0;

    public Leader armyLeader;

    public Goal goal;
    public Position retreatTo;

    public boolean hasWon = false;

    public int getNextID(){
        return nextID++;
    }
}
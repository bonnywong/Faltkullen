package faltkullen;

public class Unit{
    public int health = 100; //current health of unit.
    public int team = 0;
    public int id = 0; //Debug variable
    public int morale;
    public int sensorRange;

    //public double x, y = 0; //global position of unit.
    public Position position;

    boolean detected = false; //Har enheten blivit upptäckt?
    public boolean alive = true;

    protected Armor armor;
    public MapSector containedIn;
    public TestOfMap map;

    public Position moveTo;
    public Group group;

    public double movespeed;

    public double fearRadius = 10.0;
    public boolean feared = false;

    public boolean retreated = false;

    public Unit(){

    }

    public Unit(Unit u){
        health = u.health;
        team = u.team;
        id = u.id;
        morale = u.morale;
        sensorRange = u.sensorRange;
        armor = u.armor;
    }

    //Called every second on the battlefield, for each Unit
    public void update(){

    }

    public void move(double dx, double dy) {
        position.x += dx;
        position.y += dy;
        System.out.println("This is never called is it?");
    }

    /**
     * Name : advanceTowards
     * Parameters : Position p
     * Returns : Nothing
     * Description:
     *  Moves this Unit across the battlefield towards P.
     *  This function will not cause the unit to walk past the point if its short enough.
     *  Division by 30 is due to 1 Second being 30 Frames.
     */
    public void advanceTowards(Position p){
        //System.out.println("Moving at " + movespeed + " speed");
        boolean debug = false;
        if(debug){
            System.out.println("Unit.advTow : Debug activated");
            System.out.println("Movespeed = " + movespeed);
        }
        Position trajectory = p.minus(getPosition());
        double distance = movespeed / 30;
        if(trajectory.length() < distance){
        }
        else {
            trajectory = trajectory.normalize().times(distance);
        }
        if(debug){
            System.out.println("Unit.advTow : Pos = " + getPosition().toString());
        }
        setPosition(getPosition().plus(trajectory));
        if(debug){
            System.out.println("Unit.advTow : Pos After = " + getPosition().toString());
        }
        MapSector currentSector = map.getSectorFromPoint(getPosition());
        if (currentSector != containedIn) {
            if (containedIn != null) {
                containedIn.removeFromSector(this);
                currentSector.addToSector(this);
                if(debug){
                    System.out.println("Unit.advTow : changed Sector");
                }
            }
            containedIn = currentSector;
        }
    }

    /*
    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }
    */

    public void setPosition(Position p){
        position = new Position(p);
    }

    public Position getPosition() {
        return new Position(position);
    }

    public double getX() {
        return position.x;
    }

    public double getY() {
        return position.y;
    }

    //Deprecrated
    public void hit(int dmg, Weapon w) {
        int finalDamageTaken = dmg - armor.getProtection(w);
        //System.out.println("armor prot = " + armor.getProtection(w));
        if (finalDamageTaken < 0) {
            finalDamageTaken = 0;
        }
        health -= finalDamageTaken;
        if (health <= 0) {
            if(this instanceof Vehicle){
                System.out.println("A VEHICLE HAS BEEN DESTROYED");
            }
            alive = false;
        }
    }

    public void hit(int dmg) {
        health -= dmg;
        if (health <= 0) {
            //System.out.println("Unit is dead (id " + id + ")");
            alive = false;
        }
    }

    /*Finner avståndet mellan två punkter.*/
    public double getDistance(int x, int y) {
        double distance = Math.sqrt((getX() - x) * (getX() - x) + (getY() - y) * (getY() - y));
        return distance;
    }

    public int getAccuracy() {
        return 0;
    }

    /*
     * Ändrar detected variabeln till värdet i argumentet beroende på om
     * soldaten är upptäckt eller ej.
     */
    public void setDetected(boolean b) {
        detected = b;
    }

    /*
     * Får detected värdet.
     */
    public boolean getDetected() {
        return detected;
    }


    public void setAlive(boolean b) {
        alive = b;
    }

    public Weapon getWeapon(){
        //Although a Unit does not necessarily have a Weapon, subclasses of Unit can have them, and should in that case override this
        return null;
    }

    public boolean fireAt(Unit target){
        return false;
    }

    public boolean movingFireAt(Unit target){
        return false;
    }

    public boolean isDead(String fromWhere) {
        //if (fromWhere.equals("Squad")) {
        //    System.out.println("This is called (2)");
        //}
        return health <= 0;
    }


    public Unit clone(){
        Unit clone = new Unit(this);
        return clone;
    }
}

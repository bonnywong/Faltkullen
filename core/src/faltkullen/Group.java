package faltkullen;

import com.badlogic.gdx.math.Vector3;
import com.mygdx.game.BaseUnitType;
import com.mygdx.game.PlacedGroup;
import com.mygdx.game.UnitType;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anton on 2015-02-19.
 */
public class Group {
    public int initialSize = 10;
    public ArrayList<Unit> members = new ArrayList<Unit>();
    public Position startPosition;
    public Leader groupLeader;
    public boolean inCombat = false;
    public TestOfMap map;
    public Unit type; //The type of unit this Group hosts
    public UnitType unitType;

    public PlacedGroup onMap;

    public String name;

    public int totalFeared = 0;

    public Group(int is){
        initialSize = is;
    }

    public Group(Group g){
        initialSize = g.initialSize;
        type = g.type;
        startPosition = g.startPosition;

        onMap = g.onMap;
        name = g.name;
    }

    public Group(ArmyComposition ac, String str){
        String[] split = str.split("/");
        name = split[0];
        unitType = ac.unitTypes.get(Integer.parseInt(split[1]));
        initialSize = Integer.parseInt(split[2]);
        if(split.length > 3){
            Vector3 v = new Vector3(Float.parseFloat(split[3]), Float.parseFloat(split[4]), 0);
            PlacedGroup placedGroup = new PlacedGroup(this, v, ac.sprite);
            onMap = placedGroup;
        }
    }

    public Army army;

    /* Method Name : simpleMove
     * Parameters : Position p that represents where we want people to move
     * Returns : Nothing
     * Description:
     * This method is used as a baseline for creating more advanced movement methods.
     * This method essentially orders all members to move towards position <p> at the same time.
     */
    public void simpleMove(Position p){
        for(Unit u : members){
            u.advanceTowards(p);
        }
    }

    public boolean advanceTowardsDebug = false;

    /**
     * Method Name : advanceTowards
     * Parameters : Position p that represents where we should move towards
     * Returns : Nothing
     * Description:
     * If this Group is in combat, it will attempt to fight
     * If this Group is not in combat, it will move towards p
     */
    public void advanceTowards(Position p){
        for(Unit u : members){
            u.update();
        }
        if(inCombat){
            if(advanceTowardsDebug){
                System.out.println("AdvTow We are in combat");
            }
            ArrayList<Unit> detectedEnemies = new ArrayList<Unit>();
            for (Unit u : members) {
                ArrayList<Unit> enemiesInRange = map.getEnemiesWithinRange(u, u.sensorRange);
                for (Unit enemy : enemiesInRange) {
                    if (!enemy.getDetected()) {
                        enemy.setDetected(true);
                        detectedEnemies.add(enemy);
                        army.detectedEnemies.add(enemy);
                    }
                }
            }
            if (detectedEnemies.size() > 0) {
                if(advanceTowardsDebug){
                    System.out.println("AdvTow We have detected new enemies");
                }
                attack(detectedEnemies);
            }
            else if(army.detectedEnemies.size() > 0){
                if(advanceTowardsDebug){
                    System.out.println("AdvTow There are old enemies");
                }
                attack(army.detectedEnemies);
            }
            else{
                if(advanceTowardsDebug){
                    System.out.println("AdvTow We are no longer in combat");
                }
                inCombat = false;
            }
        }
        else{
            if(advanceTowardsDebug){
                System.out.println("AdvTow We are moving towards");
                System.out.println(p.toString());
            }
            simpleMove(p);
            ArrayList<Unit> detectedEnemies = new ArrayList<Unit>();
            for (Unit u : members) {
                ArrayList<Unit> enemiesInRange = map.getEnemiesWithinRange(u, u.sensorRange);
                for (Unit enemy : enemiesInRange) {
                    if (!enemy.getDetected()) {
                        enemy.setDetected(true);
                        detectedEnemies.add(enemy);
                        army.detectedEnemies.add(enemy);
                    }
                }
            }
            if (detectedEnemies.size() > 0) {
                if(advanceTowardsDebug){
                    System.out.println("AdvTow We moved and are now attacking newly detected enemies");
                }
                attack(detectedEnemies);
            }
            else if(army.detectedEnemies.size() > 0){
                if(advanceTowardsDebug){
                    System.out.println("AdvTow We moved and are now attacking old enemies");
                }
                attack(army.detectedEnemies);
            }
            else{
                if(advanceTowardsDebug){
                    System.out.println("AdvTow We moved and are now not in combat");
                }
                inCombat = false;
            }
        }
    }

    /**
     * Method Name : attack
     * Parameters : An array of Units belonging to the enemy
     * Returns : Nothing
     * Description:
     * This method will order every member of this Group to fire against Units belonging to the enemy (in the parameter).
     * The group will attempt to spread out its fire over as many targets as possible.
     * First, it will assign targets as long as there is a target in range.
     * Secondly, if there is not a non-assigned target in range, it will assign fire to a assigned target.
     * Thirdly, if there is not a non-assigned target in range, a member will move towards the closest enemy.
     */
    public void attack(ArrayList<Unit> enemies){
        inCombat = true;
        ArrayList<Unit> toBeAssigned = new ArrayList<Unit>(enemies);
        ArrayList<Unit> assigned = new ArrayList<Unit>();
        ArrayList<Unit> assigned2 = new ArrayList<Unit>();
        boolean standby = groupLeader.goal instanceof Standby;
        for(Unit m : members){
            //We setup the variables needed in the future
            Weapon w = m.getWeapon();   //The weapon used by our Unit
            boolean foundTarget = false;    //

            double shortest = 0.0;  //The distance between this Unit and the closest Enemy
            Unit closest = null;    //The closests enemy Unit

            //We iterate through all relevant enemy Units to determine the closest Enemy
            for(int a=0;a<enemies.size();a++){
                Unit enemy = enemies.get(a);
                double distance = m.getPosition().distance(enemy.getPosition());
                if(closest == null){
                    shortest = distance;
                    closest = enemy;
                }
                else if(shortest > distance){
                    shortest = distance;
                    closest = enemy;
                }
            }
            moveOrder++;
            /*
            //Is this member too intimidated by the enemy's proximity? In that case, its feared and will start to run towards the starting position
            if((shortest < m.fearRadius || m.feared) && !groupLeader.isAggressive){
                m.feared = true;
                m.advanceTowards(startPosition);
                if(m.getPosition().distance(startPosition) < 5){
                    m.health = -1;
                }
                totalFeared++;
                continue;
            }
            */

            //We iterate through all relevant enemy Units
            for(int a=0;a<toBeAssigned.size();a++){
                Unit enemy = toBeAssigned.get(a);
                //If <enemy> is within range of our Unit, then...
                boolean debug = true;
                if(debug){
                    if(m == null){
                        System.out.println("m is null");
                    }
                    else if(m.getPosition() == null){
                        System.out.println("m.pos is null");
                    }
                    else if(enemy == null){
                        System.out.println("enemy is null");
                    }
                    else if(enemy.getPosition() == null){
                        System.out.println("enemy.pos is null");
                    }
                    else if(w == null){
                        System.out.println("w is null");
                        if(m instanceof Vehicle){
                            System.out.println("Cant find Vehicle Weapon");
                        }
                    }
                }
                if(m.getPosition().distance(enemy.getPosition()) <= w.getRange()){
                    //... check if to see if we should move and fire or stand and fire
                    //If we have an aggressive Leader, advance towards the enemy and fire
                        //The checking for the status of the enemy is to make sure we don't move towards a dead body which Position could be weird
                    if(groupLeader.isAggressive && !enemy.isDead("attack")){
                        m.advanceTowards(enemy.getPosition());
                        m.movingFireAt(enemy);
                    }
                    else {
                        //If we don't have an aggressive Leader, just stand and fire
                        boolean hit = m.fireAt(enemy);
                        if(standby){
                            //System.out.println("Standby fires");
                            if(hit){
                                //System.out.println("Standby hits");
                            }
                        }
                    }
                    //After we have fired, remove this enemy from the group of enemies to be Assigned as a target
                    toBeAssigned.remove(a);
                    //Then add the enemy to the group of enemies that have been Assigned as a target
                    assigned.add(enemy);
                    //Since we have found a target, set FoundTarget to true and leave this for-loop since its purpose is to find a target
                    foundTarget = true;
                    break;
                }
            }
            //If we have found a target at this point, continue to another friendly Unit
            if(foundTarget){
                continue;
            }
            //If we haven't found a target at this point, fire at an assigned target
            for(int a=0;a<assigned.size();a++){
                Unit enemy = assigned.get(a);
                if(m.getPosition().distance(enemy.getPosition()) <= w.getRange()){
                    if(groupLeader.isAggressive && !enemy.isDead("attack")){
                        m.advanceTowards(enemy.getPosition());
                        m.movingFireAt(enemy);
                    }
                    else {
                        boolean hit = m.fireAt(enemy);
                        if(standby){
                            //System.out.println("Standby fires");
                            if(hit){
                                //System.out.println("Standby hits");
                            }
                        }
                    }
                    assigned.remove(a);
                    assigned2.add(enemy);
                    foundTarget = true;
                    if(assigned.size() == 0){
                        assigned = assigned2;
                        assigned2 = new ArrayList<Unit>();
                    }
                    break;
                }
            }
            if(foundTarget){
                continue;
            }

            if(closest != null) {
                m.advanceTowards(closest.getPosition());
            }
            else{
                m.advanceTowards(groupLeader.goal.position);
            }
        }
    }

    public int moveOrder = 0;

    /**
     * Method Name : populate
     * Parameters:
     * TestOfMap map, the map that we should place this group inside
     * Position at, where the group is to be populated
     * Unit type, what type of unit to populate into this group
     * Returns : Nothing
     * Description:
     * When this method is called, this Group generates initialSize units of <type> into <map>.
     */
    public void populate(TestOfMap m){
        map = m;
        Random rand = new Random();
        for(int a=0;a<initialSize;a++){
            Unit u = type.clone();
            u.map = m;
            u.id = army.getNextID();
            army.units.add(u);
            u.team = army.team;

            //Set a new position for the new unit
            Position p = new Position(startPosition.x, startPosition.y);
            double randomDegree = rand.nextInt(360); //A random angle in degrees (0 and 360 is the same, and 360 is excluded)
            double randomDistance = rand.nextInt(10);
            double randomAngle = (Math.PI / 180) * randomDegree;
            p.x += Math.cos(randomAngle) * randomDistance;
            p.y += Math.sin(randomAngle) * randomDistance;
            u.setPosition(p);

            if(u instanceof Vehicle){
                //System.out.println("Populated a Vehicle");
            }

            MapSector sector = m.getSectorFromPoint(p);
            sector.addToSector(u);
            u.containedIn = sector;
            members.add(u);
            u.group = this;
        }
    }

    public void unpopulate(){
        for(int a=0;a<members.size();a++){
            Unit u = members.get(a);
            u.health = -1;
            u.retreated = true;
            System.out.println("Successful Retreat");
        }
        members.clear();
        groupLeader = null;
    }

    /*
     * Method Name : hasGroundUnitWithin
     * Parameters:
     * Position p : The position to search
     * double radius : A radius that combined with a Position becomes a circle
     * Returns: A boolean that represents whether or not this squad has a Soldier within <radius> range of Position
     *
     * Description:
     * Goes through all Units in this Group and returns true if at least one of them are within <radius> range of <p>
     * Otherwise false
     */
    public boolean hasGroundUnitWithin(Position p, double radius){
        for(Unit u : members){
            if(u.getPosition().distance(p) <= radius){
                return true;
            }
        }
        return false;
    }

    /*
     * Method Name : getReadiness
     * Parameters : None
     * Returns : The Groups combat readiness, measured as a double from 0.0 to 5.0, where 5.0 is perfect condition
     *
     * Description:
     * Goes through certain checks to determine this Groups combat readiness and returns that readiness in the form of a double.
     * Things checked:
     *      Current member size : Has the Group lost a lot of members? More lost members = less readiness
     */
    public double getReadiness(){
        double memberRatio = (double)(members.size() - totalFeared) / (double)initialSize;
        return 5.0 * memberRatio * memberRatio;
    }

    /*
     * Method Name : combatCheck
     * Parameters : None
     * Returns : Nothing
     *
     * Description:
     * This method checks if this Group can sense nearby enemies, and if they can, they attack.
     */
    public void combatCheck() {
        ArrayList<Unit> detectedEnemies = new ArrayList<Unit>();
        for (Unit u : members) {
            ArrayList<Unit> enemiesInRange = map.getEnemiesWithinRange(u, u.sensorRange);
            for (Unit enemy : enemiesInRange) {
                if (!enemy.getDetected()) {
                    enemy.setDetected(true);
                    detectedEnemies.add(enemy);
                    army.detectedEnemies.add(enemy);
                }
            }
        }
        if (detectedEnemies.size() > 0) {
            //System.out.println("Standby attacks");
            attack(detectedEnemies);
        } else if (army.detectedEnemies.size() > 0) {
            //System.out.println("Standby attacks");
            attack(army.detectedEnemies);
        } else {
            inCombat = false;
        }
    }

    public void setStartPosition(Vector3 v){
        startPosition = new Position(v.x, v.y);
    }

    public String getSaveString(){
        String add = "";
        if(onMap != null){
            add = "/" + onMap.position.x + "/" + onMap.position.y;
        }
        return name+"/"+unitType.id+"/"+initialSize+add;
    }

    public Position getWeightedPosition(){
        Position p = new Position(0, 0);
        int weight = 0;
        for(int a=0;a<members.size();a++){
            Unit m = members.get(a);
            if(!m.isDead("Group.getWeightedPosition")){
                p.x = p.x + m.position.x;
                p.y = p.y + m.position.y;
                weight++;
            }
        }

        p.x = p.x / weight;
        p.y = p.y / weight;
        return p;
    }

    public boolean isWiped(){
        return members.size()==0;
    }
}

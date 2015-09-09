package faltkullen;

import java.util.ArrayList;

public class Squad {
    private class soldierTargets {
        Soldier soldier;
        int state;
        public ArrayList<Position> soldierTargets = new ArrayList<Position>();

        public soldierTargets(Soldier s, int state, Position target) {
            soldier = s;
        }

        public Position firstTarget() {
            return soldierTargets.get(0);
        }

        public void addTarget(Position p) {
            soldierTargets.add(p);
        }

        public void targetReached() {
            soldierTargets.remove(0);
            soldierTargets.trimToSize();
        }

        public boolean isEmpty() {
            return soldierTargets.isEmpty();
        }
    }

    //--------------------------------------------------------------------------------------
    public ArrayList<soldierTargets> soldierPaths = new ArrayList<soldierTargets>(); //- primary individual soldier movement schemes
    public ArrayList<soldierTargets> soldierPaths2 = new ArrayList<soldierTargets>(); //- next-in-line soldier movement schemes

    //-------------------------------------------------------------------------------------
    //GLOBALA VARIABLER
    public int PRAKTISKT_SKJUTAVSTAND = 150;
    public double scale = 0.5;

    //--------------------------------------------------------------------------------------
    //FIELDS ADDED BY ANTON
    public Army inArmy;
    public boolean moveOrder = false;
    public Position startOfMoveOrder;
    public TestOfMap map;
    public boolean debugSplitTargets = false;
    //--------------------------------------------------------------------------------------

    public ArrayList<SquadCommand> orders;              //- list of orders which to carry out
    public ArrayList<Soldier> members;               //- which soldiers currently under unit command
    public static final double INVERTANGLE = -0.5;          //- the angle change limit when unit formation is inverted
    boolean fireAtWill;         //is the unit cleared to open fire, other in self defence?
    public boolean enemyContact;        //has the unit come in contact with any enemy?
    boolean enemyContactReaction;      //has the unit reacted to this new found enemy?
    boolean invertedFormation;       //is the formation inverted?
    boolean attackStrid;
    boolean dynamicMoveCycleComplete = false;   //is the move dynamic move cycle(ansatsvis etc.) complete?
    boolean defenceStarted = false;      //is the group moving to static defensive positions?
    boolean defenceFinished = false;     //is the group in static defensive positions?
    double combat_value;        //percentage of full capacity
    int ansatsIterator;         //iterator integer used to keep track on (ansatsvis)
    Position enemyMeanPosition;       //the average enemy position
    Position direction;         //which direction the unit currently faces

    int ammo;
    int losses;
    int kills;
    SquadOrder currentOrder;
    //--------------------------------------------------------------------------------------

    public Squad() {
        orders = new ArrayList<SquadCommand>();
        members = new ArrayList<Soldier>();
        direction = new Position(0, 0);
        fireAtWill = false;
        enemyContact = false;
        enemyContactReaction = false;
        invertedFormation = false;
        ammo = 10000000;
        losses = 0;
    }

    /*
     * This is the method that is called when this unit is about to do his move.
     */
    public ArrayList<Position> action(ArrayList<Position> currentPos, ArrayList<Unit> enemies) {
        if (members.size() == 0) {
            System.out.println("EMPTY SQUAD! WTF?");
            return currentPos;
        }

        helpLines(enemies);  //find targets, fire weapons, moves accordingly

        //System.out.println(enemyContact);

        return moveSquad(currentPos); //moves the squad, returns the new movements and terminates the method.
        //return currentPos;
    }


    /*
     * Takes current positions, moves them according to current command, returns new positions
     */
    public ArrayList<Position> moveSquad(ArrayList<Position> currentPos) {


        if (members.size() == 0) {
            return currentPos;
        }
        SquadCommand currentCommand = new SquadCommand(1, 0, 0);
        ArrayList<Position> newPositions = new ArrayList<Position>();
        //if(orders.get(0).formationCounter > 0){  orders.get(0).formationCounter--; }

        //if squad has any order
        if (orders.size() > 0) {
            currentCommand = orders.get(0);
            Position index = currentPos.get(getSquadPositionSoldierIndex(currentCommand.state));
            double totalDistance = currentCommand.target.distance(index);

            //if direction is changed more than INVERTANGLE, invert formation
            if (direction.unitDotProduct((currentCommand.target.minus(index))) < INVERTANGLE) {
                //unless formation is 6
                if (currentCommand.state != 6) {
                    invertedFormation = (!invertedFormation);
                }
            }

            //if squad is less than one step from target, remove order and move none.
            direction = currentCommand.target.minus(index);
            if (totalDistance < (getSpeed(currentCommand.state, 0)) && currentCommand.state <= 7) {
                if (orders.get(0).formationCounter <= 0) {
                    orders.remove(0);
                    return currentPos;
                }
            }
            //if squad state is 10 or 11 and formation has run its course, remove order
            if (currentCommand.state >= 10 && currentCommand.state <= 11) {
                if (orders.get(0).formationCounter == 0) {
                    orders.remove(0);
                    return currentPos;
                }
            }

            //if squad state is 12, this will be run only once.
            if (currentCommand.state == 12) {
                if (defenceStarted == false) {
                    defenceFinished = false;
                }
                if (defenceFinished == true) {//if everyone is in position hold positions.
                    return currentPos;
                }
            }
        }

        //if the first order steps are out, bring out the second ones
        if (soldierPaths.isEmpty()) {
            soldierPaths.addAll(soldierPaths2);
            soldierPaths2.clear();
        }

        //if both order steps are empty, flag that the dynamicMoveCycle is complete
        if (soldierPaths.isEmpty() && soldierPaths2.isEmpty()) {
            dynamicMoveCycleComplete = true;
        } else {
            dynamicMoveCycleComplete = false;
        }

        //if we have no moves in progress and is moving by Static, move by Static.
        if (currentCommand.state <= 7) {
            newPositions = moveGroupMembersStatic(currentCommand.state, currentCommand.target, currentPos);
            soldierPaths.clear();
        }
        //if we are moving by Dynamic, move by Dynamic.
        else if (currentCommand.state > 7) {
            newPositions = moveGroupMembersDynamic(currentCommand.state, currentCommand.target, currentPos);
        }

//  System.out.println("This squad is located at (" + currentPos.get(0).x + ", " + currentPos.get(0).y + ") and is walking towards  (" 
//  + orders.get(0).target.x + ", " + orders.get(0).target.y + ") ");


        //double fi = newPositions.get(0).minus(currentPos.get(0)).length();
        //double fu = currentPos.get(0).minus(orders.get(0).target).unitDotProduct(newPositions.get(0).minus(currentPos.get(0)));
        //System.out.println(fi);


        return newPositions;
    }


    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //--------------------------------||            ||----------------------------------------------------
    //--------------------------------|| HERE THE TOTAL CODE MADE BY ANTON BEGIN  ||----------------------------------------------------
    //--------------------------------||            ||----------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //---------------------------------------------------------------------------------------------------------------------------------------- 

    /*
     * Returns wether this squad has orders or not.
     */
    public boolean hasOrders() {
        return orders.size() > 0;
    }

    /*
     * Antons help lines
     */
    public void helpLines(ArrayList<Unit> enemies) {
        //Finns det fiender i nÃ¤rheten?
        boolean thePowerOfOptimization = true;
        ArrayList<Unit> detectedEnemies = new ArrayList<Unit>();
        if (!thePowerOfOptimization) {
            for (Soldier s : members) {
                detectedEnemies.addAll(s.detectEnemy(enemies));
            }
            if (detectedEnemies.size() > 0) {
                splitTargets(detectedEnemies);
            }
        } else if (thePowerOfOptimization) {
            for (Soldier s : members) {
                ArrayList<Unit> enemiesInRange = map.getEnemiesWithinRange(s, s.sensorRange);
                for (Unit enemy : enemiesInRange) {
                    if (!enemy.getDetected()) {
                        enemy.setDetected(true);
                        detectedEnemies.add(enemy);
                        inArmy.detectedEnemies.add(enemy);
                    }
                }
            }
            if (detectedEnemies.size() > 0) {
                splitTargets(detectedEnemies);
            }
        }

        if (enemyContact) {
            for (int a = 0; a < members.size(); a++) {
                Soldier s = members.get(a);

                //Is the soldier within range?
                if (s.getWeapon().getRange() <= s.distanceToEnemy(s.target)) {
                    //Yes we are within range, so fire at the enemy
                    s.fireAt(s.target);

                    //Is the target dead now?
                    if (s.target.isDead("Squad1")) {
                        //The target is dead, so attempt to find a new target
                        int shortestDistance = -1;
                        for (Soldier s2 : members) {
                            if (s.id == s2.id || s2.target.isDead("Squad1")) {
                                continue;
                            }
                            int distance = s.distanceToEnemy(s2.target);

                            if (distance < shortestDistance || shortestDistance == -1) {
                                shortestDistance = distance;
                                s.target = s2.target;
                            }
                        }
                        if (s.target.isDead("Squad")) {
                            //No alive target was found, no enemies spotted by this squad remains
                            enemyContact = false;
                        }
                    } else if (s.target.health <= 0) {
                        System.out.println(s.target.health <= 0);
                        System.out.println(s.target.health);
                        Soldier s2 = (Soldier) s.target;
                        System.out.println(s2.id);
                        System.out.println("Something is awry");
                    }
                } else {
                    //Move towards the enemy target
                    //moveSoldierByIndex(a, s.getPosition(), s.target.getPosition(), s.movespeed);
                }
            }

            //We get here if all targets in the squad are dead
            if (!enemyContact) {
                //Check if any other squads are firing
                ArrayList<Squad> squads = inArmy.command;
                double shortestDistance = -1;
                Squad closestSquad = this;
                Position pos = getGroupPosition();

                for (Squad s : squads) {
                    if (s == this) {
                        continue;
                    }
                    double distance = pos.distance(s.getGroupPosition());

                    //Does this squad have any alive targets?
                    if (s.hasAliveTargets()) {

                        if (distance < shortestDistance || shortestDistance == -1) {
                            shortestDistance = distance;
                            closestSquad = s;
                        }
                    }
                }
                if (closestSquad != this) {
                    ArrayList<Unit> closestSquadTargets = closestSquad.getTargets();
                    splitTargets(closestSquadTargets);
                }
            }
     /*
      if(getTargets().size()==0){
      //pick random enemies that are not detected
      splitTargets(enemies, true);
      if(getTargets().size()==0){
      enemyContact = false;
      System.out.println("This is never to be reached");
      System.out.println("enemies.size() = " + enemies.size());
      System.out.println("Team = " + inArmy.team);
      System.out.println("Size of squad = " + members.size());
      }
      }
      */
            //We get here if
            //Firstly, all this squads targets are dead
            //Secondly, all of the other squads targets are dead
            if (!enemyContact) {
                splitTargets(inArmy.detectedEnemies);
            }
     /*
      * This code was used as a "last resort" incase something bugged, we just made the squad attack based on the list of enemies
      * This code has been reimplemented to make the problem not halt as we are trying to work out bugs
      */
            if (!enemyContact) {
                splitTargets(map.enemies.get(inArmy.team).units);
            } else {
                enemyContact = true;
            }
        }
        if (enemyContact) {
            enemyContact(meanPosition(detectedEnemies), detectedEnemies.size()); //determine if in combat, alter movements
        }
    }

    //Returns true if this squad has any targets that are alive
    //Speed : O(N) where N is amount of members in the squad
    public boolean hasAliveTargets() {
        for (Soldier s : members) {
            if (s.target != null) {
                if (s.target.isDead("hasAliveTargets")) {
                } else {
                    return true;
                }
            }
        }
        return false;
    }


    public void splitTargets(ArrayList<Unit> targets, boolean debug) {
        debugSplitTargets = debug;
        splitTargets(targets);
        debugSplitTargets = false;
    }

    public void splitTargets(ArrayList<Unit> ts) {
        ArrayList<Unit> targets = new ArrayList<Unit>(ts);

        if (targets.size() == 0) {
            return;
        } else if (targets.size() == 1) {
            for (Soldier s : members) {

                s.target = targets.get(0);
            }
            enemyContact = true;
            return;
        }
        ArrayList<Soldier> soldiersWithoutTargets = new ArrayList<Soldier>(members);
        for (int a = 0; a < soldiersWithoutTargets.size(); a++) {
            Soldier s = soldiersWithoutTargets.get(a);

            if (s.target == null) {
                continue;
            } else if (s.target.isDead("Squad")) {
                continue;
            } else {
                soldiersWithoutTargets.remove(a);
                a--;
            }
        }
        if (debugSplitTargets) {
            System.out.println("soldiersWithoutTargets.size() = " + soldiersWithoutTargets.size());
        }
        while (soldiersWithoutTargets.size() > 0) {

            for (int b = 0; b < targets.size(); b++) {
                Unit enemy = targets.get(b);

                if (enemy.isDead("Squad")) {
                    targets.remove(b);
                    b--;
                    continue;
                }
                int shortestDistance = -1;
                int shortestIndex = 0;
                if (enemy == null) {
                    System.out.println("WTF?!");
                    System.out.println(targets.size());
                }
                for (int a = 0; a < soldiersWithoutTargets.size(); a++) {
                    int distance = soldiersWithoutTargets.get(a).distanceToEnemy(enemy);

                    if (distance < shortestDistance || shortestDistance == -1) {
                        shortestDistance = distance;
                        shortestIndex = a;
                    }
                }
                Soldier s = soldiersWithoutTargets.remove(shortestIndex);
                s.target = enemy;
                if (soldiersWithoutTargets.size() == 0) {
                    break;
                }
            }
            if (targets.size() == 0) {
                break;
            }
        }
        if (debugSplitTargets) {
            System.out.println("End of split targets");
        }
        if (soldiersWithoutTargets.size() == 0) {
            enemyContact = true;
        } else {
            enemyContact = false;
        }
    }

    public ArrayList<Unit> getTargets() {
        ArrayList<Unit> targets = new ArrayList<Unit>();
        for (Soldier s : members) {

            if (!targets.contains(s.target) && s.target != null) {
                if (s.target.alive) {

                    targets.add(s.target);
                }

            }
        }
        return targets;
    }
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //--------------------------------||            ||----------------------------------------------------
    //--------------------------------|| HERE ENDS THE TOTAL CODE MADE BY ANTON  ||----------------------------------------------------
    //--------------------------------||            ||----------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------
    //----------------------------------------------------------------------------------------------------------------------------------------

    /*
     * Calculate the mean position of the group.
     */
    public Position getGroupPosition() {
        Position totalPosition = new Position(0, 0);
        for (Soldier s : members) {
            if (s.alive == true) {
                Position p = s.getPosition();
                totalPosition.x += p.x;
                totalPosition.y += p.y;
            }
        }
        totalPosition.x /= members.size();
        totalPosition.y /= members.size();
        return totalPosition;
    }

    /*
     * Used to move individual group members, to form up formations, and direct them in the right direction.
     * Returns a list of new position for all soldiers in the group.
     */
    /*
     * Used to move individual group members, to form up formations, and direct them in the right direction.
     * Returns a list of new position for all soldiers in the group.
     */
    private ArrayList<Position> moveGroupMembersStatic(int state, Position Target, ArrayList<Position> positions) {


        double xTarget = Target.x;
        double yTarget = Target.y;
        int leftSide = members.size() / 2 + 1;
        int troopIndexStart = leftSide - members.size();
        int troopIndexEnd = members.size() + troopIndexStart;

        int middleNumber = members.size() / 2 - 1;
        if (middleNumber == -1 && members.size() == 1) {
            middleNumber = 0;

        }

        double groupPosXx = positions.get(middleNumber).x;
        double groupPosYy = positions.get(middleNumber).y;


        if (members.size() % 2 == 1) {
            groupPosXx = positions.get(members.size() / 2).x;
            groupPosYy = positions.get(members.size() / 2).y;
        }


        ArrayList<Position> troopLinePositions = new ArrayList<Position>();
        ArrayList<Position> troopUnitTarget = new ArrayList<Position>();
        ArrayList<Double> troopLineDistance = new ArrayList<Double>();
        double stragglerDistance = 0;


        //vi skapar koordinater fÃƒÂ¶r enhetsvektorkoordinaterna i fÃƒÂ¤rdriktningen
        double UnitVectorX = (xTarget - groupPosXx) / Math.sqrt(((yTarget - groupPosYy) * (yTarget - groupPosYy)) + ((xTarget - groupPosXx) * (xTarget - groupPosXx)));
        double UnitVectorY = (yTarget - groupPosYy) / Math.sqrt(((yTarget - groupPosYy) * (yTarget - groupPosYy)) + ((xTarget - groupPosXx) * (xTarget - groupPosXx)));
        //THESE ARE SUCCESSFULLY NORMALIZED.

        //normalvektor till enhetsvektorn
        //anvÃƒÂ¤nd normalvektorn fÃƒÂ¶r att gÃƒÂ¶ra nya koordinater att placera punkter pÃƒÂ¥
        double xNormalUnit = -UnitVectorY;
        double yNormalUnit = UnitVectorX;

        for (int a = troopIndexStart; a < troopIndexEnd; a++) {
            double currSoldierPosX = positions.get(a - troopIndexStart).x;
            double currSoldierPosY = positions.get(a - troopIndexStart).y;

            Position formationPoint = getStaticFormationPositions(state, a, members.size(), new Position(groupPosXx, groupPosYy), new Position(UnitVectorX, UnitVectorY), new Position(xNormalUnit, yNormalUnit));

            troopLinePositions.add(formationPoint);
            troopUnitTarget.add(new Position((formationPoint.x - currSoldierPosX), (formationPoint.y - currSoldierPosY)));

            double distance = Math.sqrt(((currSoldierPosX - formationPoint.x) * (currSoldierPosX - formationPoint.x)) + ((currSoldierPosY - formationPoint.y) * (currSoldierPosY - formationPoint.y)));
            double stragglerDirectionX = formationPoint.x - currSoldierPosX;
            double stragglerDirectionY = formationPoint.x - currSoldierPosY;
            double stragglerDirectionUnitX = stragglerDirectionX / Math.sqrt((stragglerDirectionX * stragglerDirectionX) + (stragglerDirectionY * stragglerDirectionY));
            double stragglerDirectionUnitY = stragglerDirectionY / Math.sqrt((stragglerDirectionX * stragglerDirectionX) + (stragglerDirectionY * stragglerDirectionY));

            //och nu, hur fÃƒÂ¤rdas soldaten i fÃƒÂ¶rhÃƒÂ¥llande till gruppen? dotprodukt
            double dotProduct = (stragglerDirectionUnitX * xNormalUnit) + (stragglerDirectionUnitY * yNormalUnit);
            troopLineDistance.add(distance);  //spara aktuell soldats avstÃƒÂ¥nd frÃƒÂ¥n avsedd punkt

            if (stragglerDistance < (dotProduct * distance)) {
                stragglerDistance = (dotProduct * distance);
            }
            //spara maxavstÃƒÂ¥ndet, detta anvÃƒÂ¤nds fÃƒÂ¶r att berÃƒÂ¤kna allas fart
        }

        for (int a = troopIndexStart; a < troopIndexEnd; a++) {
            Soldier s = members.get(a - troopIndexStart);
            double speed = ((getSpeed(state, 0) + 2 * Math.atan((troopLineDistance.get(a - troopIndexStart)) / 100)) * s.movespeed) / 5;

            if (stragglerDistance > 100) {
                speed -= 0.9;
            } else if (stragglerDistance > 50) {
                speed -= 0.5;
            } else if (stragglerDistance > 10) {
                speed -= 0.2;
            } else if (stragglerDistance > 5) {
                speed -= 0.1;
            }

            //nu har vi en individuell fart fÃƒÂ¶r varje soldat. rÃƒÂ¤kna ut vilken riktning och lÃƒÂ¤gg pÃƒÂ¥ tempot
            double targetX = troopUnitTarget.get(a - troopIndexStart).x;
            double targetY = troopUnitTarget.get(a - troopIndexStart).y;

            double targetUnitX = 0;
            double targetUnitY = 0;
            if (targetX != 0 && targetY != 0) {
                targetUnitX = targetX / Math.sqrt((targetX * targetX) + (targetY * targetY));
                targetUnitY = targetY / Math.sqrt((targetX * targetX) + (targetY * targetY));
            }
            if (Math.sqrt((targetX * targetX) + (targetY * targetY)) > Math.sqrt((targetUnitX * targetUnitX) + (targetUnitY * targetUnitY)) * speed * scale) {
                positions.get(a - troopIndexStart).x += (targetUnitX * speed * scale);
                positions.get(a - troopIndexStart).y += (targetUnitY * speed * scale);
            } else {
                positions.get(a - troopIndexStart).x += targetX;
                positions.get(a - troopIndexStart).y += targetY;
            }
        }
        //------------------------------- LÃ„GG TILL MÃ–JLIGHETER FÃ–R HINDERUPPTÃ„CKNING OCH PATHFINDING --------------------------------------------------

        return positions;
    }

    /*
     * Returns a list of positions soldiers should aim for, to keep formation intact.
     */
    public Position getStaticFormationPositions(int state, int index, int squadSize, Position groupPos, Position unitVector, Position unitVectorNormal) {
        Position troopLinePoint = groupPos;
        double stepWidthDistance = 10;
        double stepLengthDistance = -10;

        if (invertedFormation) {
            index = index * (-1);
        }

        switch (state) {
            case 1: //-----------------------IGELKOTTSFORMATION-----------------
                stepWidthDistance = 35 * scale;
                stepLengthDistance = 35 * scale;

                //how much space each soldier will have sideways to the next soldier
                if (index != 0) {
                    if (index < 0) {
                        index++;
                    }
                    troopLinePoint.x += Math.cos((3 * (double) index) / (squadSize - 1)) * (unitVectorNormal.x * (stepWidthDistance)) * Math.pow((-1), index);
                    troopLinePoint.y += Math.cos((3 * (double) index) / (squadSize - 1)) * (unitVectorNormal.y * (stepWidthDistance)) * Math.pow((-1), index);

                    //how much space each soldier will have in the travel direction to the next soldier
                    troopLinePoint.x += Math.sin((3 * (double) index) / (squadSize - 1)) * (unitVector.x * (stepLengthDistance)) * Math.pow((-1), index);
                    troopLinePoint.y += Math.sin((3 * (double) index) / (squadSize - 1)) * (unitVector.y * (stepLengthDistance)) * Math.pow((-1), index);
                }
                break;
            case 2: //-----------------------SKYTTEPLOG-------------------------
                stepWidthDistance = 10 * scale;
                stepLengthDistance = -10 * scale;

                //how much space each soldier will have sideways to the next soldier
                troopLinePoint.x += (index * unitVectorNormal.x * stepWidthDistance) + unitVector.x;
                troopLinePoint.y += (index * unitVectorNormal.y * stepWidthDistance) + unitVector.y;

                //how much space each soldier will have in the travel direction to the next soldier
                troopLinePoint.x += (Math.abs(index) * unitVector.x * stepLengthDistance);
                troopLinePoint.y += (Math.abs(index) * unitVector.y * stepLengthDistance);
                break;
            case 3: //-----------------------SKYTTELINJE-------------------------
                stepWidthDistance = 15 * scale;
                stepLengthDistance = 0 * scale;

                //how much space each soldier will have sideways to the next soldier
                troopLinePoint.x += (index * unitVectorNormal.x * stepWidthDistance) + unitVector.x;
                troopLinePoint.y += (index * unitVectorNormal.y * stepWidthDistance) + unitVector.y;

                //how much space each soldier will have in the travel direction to the next soldier
                troopLinePoint.x += (Math.abs(index) * unitVector.x * stepLengthDistance);
                troopLinePoint.y += (Math.abs(index) * unitVector.y * stepLengthDistance);
                break;
            case 4://-----------------------SKYTTEKOLONN---------------------------
                stepWidthDistance = 10 * scale;
                stepLengthDistance = -15 * scale;

                troopLinePoint.x += unitVector.x;
                troopLinePoint.y += unitVector.y;

                if (index != 0) {
                    //how much space each soldier will have sideways to the next soldier
                    if (index > 0) {
                        troopLinePoint.x += ((index / Math.abs(index)) * unitVectorNormal.x * stepWidthDistance) - (unitVector.x * stepLengthDistance / 2);
                        troopLinePoint.y += ((index / Math.abs(index)) * unitVectorNormal.y * stepWidthDistance) - (unitVector.y * stepLengthDistance / 2);
                    }
                    //how much space each soldier will have in the travel direction to the next soldier
                    troopLinePoint.x += (Math.abs(index) * unitVector.x * stepLengthDistance);
                    troopLinePoint.y += (Math.abs(index) * unitVector.y * stepLengthDistance);
                }
                break;
            case 5://-----------------------SKYTTELED-----------------------------
                stepWidthDistance = 0 * scale;
                stepLengthDistance = -20 * scale;


                troopLinePoint.x += unitVector.x;
                troopLinePoint.y += unitVector.y;

                if (index != 0) {
                    //how much space each soldier will have in the travel direction to the next soldier
                    if (index > 0) {
                        troopLinePoint.x += (unitVector.x * stepLengthDistance / 2);
                        troopLinePoint.y += (unitVector.y * stepLengthDistance / 2);
                    }
                    troopLinePoint.x += (Math.abs(index) * unitVector.x * stepLengthDistance);
                    troopLinePoint.y += (Math.abs(index) * unitVector.y * stepLengthDistance);
                }
                break;
            case 6://-----------------------SKYTTESVARM---------------------------
                stepWidthDistance = 40 * scale;
                stepLengthDistance = -40 * scale;

                troopLinePoint.x += unitVector.x;
                troopLinePoint.y += unitVector.y;

                if (index != 0) {
                    //how much space each soldier will have sideways to the next soldier
                    if (index > 0) {
                        troopLinePoint.x += ((index / Math.abs(index)) * unitVectorNormal.x * stepWidthDistance) - (unitVector.x * stepLengthDistance);
                        troopLinePoint.y += ((index / Math.abs(index)) * unitVectorNormal.y * stepWidthDistance) - (unitVector.y * stepLengthDistance);
                    }
                    //how much space each soldier will have in the travel direction to the next soldier
                    troopLinePoint.x += (Math.abs(index) * unitVector.x * stepLengthDistance);
                    troopLinePoint.y += (Math.abs(index) * unitVector.y * stepLengthDistance);
                }
                break;
            case 7://-----------------------LINJE---------------------------
                if (index != 0) {
                    stepWidthDistance = 15 * scale;
                    stepLengthDistance = 0 * scale;

                    //how much space each soldier will have sideways to the next soldier
                    troopLinePoint.x += (index * unitVectorNormal.x * stepWidthDistance) + unitVector.x;
                    troopLinePoint.y += (index * unitVectorNormal.y * stepWidthDistance) + unitVector.y;
                }
                break;
        }
        return new Position(troopLinePoint.x, troopLinePoint.y);
    }

    /*
     * Returns the soldier with the specified id.
     */
    public Soldier getMemberWithID(int id) {
        for (Soldier s : members) {
            if (s.id == id) {
                return s;
            }
        }
        return null;
    }

    /*
     * Dynamically moves soldiers.
     * Returns a list with new positions for soldiers.
     */
    public ArrayList<Position> moveGroupMembersDynamic(int state, Position groupTarget, ArrayList<Position> positions) {

        //if we are just done with one cycle, initiate new one.
        if (soldierPaths.isEmpty() && state != 12) {
            generatePaths(state, groupTarget, positions);
        }

        if (soldierPaths.isEmpty() && defenceStarted == true && state == 12) {
            defenceFinished = true;
        }

        if (soldierPaths.isEmpty() && defenceStarted == false && state == 12) {
            generatePaths(state, groupTarget, positions);
        }

        //Iterates through the soldierPaths list with standing move orders
        for (int a = 0; a < soldierPaths.size(); a++) {
            //for every move order, retrieve the soldier data to know who it really is
            int soldier_id = soldierPaths.get(a).soldier.id;
            int soldier_index = members.indexOf(getMemberWithID(soldier_id));


            //if current soldiers list isn't empty and it is the correct state, proceed, elsewise remove the move order
            if (!(soldierPaths.get(a).isEmpty() && (soldierPaths.get(a).state != state))) {

                //retrieve speed, current position, distance and direction
                double speed = getSpeed(state, 0);
                Position currPos = positions.get(soldier_index);
                Position direction = new Position((soldierPaths.get(a).firstTarget().x - currPos.x), (soldierPaths.get(a).firstTarget().y - currPos.y));
                Position directionUnit = direction.normalize();

                //if we are closer than one step, don't take a too large step.
                if (direction.length() > speed * scale) {
                    positions.get(soldier_index).x += (directionUnit.x * speed * scale);
                    positions.get(soldier_index).y += (directionUnit.y * speed * scale);
                } else {
                    positions.get(soldier_index).x += direction.x;
                    positions.get(soldier_index).y += direction.y;
                    soldierPaths.get(a).targetReached();
                }
            } else {
                soldierPaths.remove(a);
            }
        }
        return positions;
    }

    /*
     * Returns the speed based on state and straggler distance.
     * Higher straggler distance will overall slow down speed.
     */
    public double getSpeed(int state, double stragglerDistance) {
        double speed = 0;
        if (stragglerDistance > 100) {
            speed -= 0.9;
        } else if (stragglerDistance > 50) {
            speed -= 0.5;
        } else if (stragglerDistance > 10) {
            speed -= 0.2;
        } else if (stragglerDistance > 5) {
            speed -= 0.1;
        }

        if (state == 0) {
            return speed + 0.5;
        } else if (state >= 8 && state <= 9) {
            return speed + 4.0;
        } else if ((state >= 1) && (state <= 4)) {
            return speed + 1.0;
        } else if (state >= 10) {
            return speed + 3.0;
        }
        return 1;
    }

    /*
     * Return the index integer representing the "center"-soldier, or the group leader.
     */
    public int getSquadPositionSoldierIndex(int state) {
        if (state < 7) {
            if (members.size() % 2 == 1) {
                return (members.size() / 2);
            } else {
                return (members.size() / 2 - 1);
            }
        } else {
            return members.size() - 1;
        }
    }

    /*
     * Adds a soldier to the squad.
     */
    public void takeCommandOfSoldier(Soldier s) {
        members.add(s);
        s.inSquad = this;
    }

    /*
     * Removes a soldier due to death.
     */
    public void loseSoldier(int Deadid) {
        Soldier johnDoe = new Soldier(-1, -1, -1, -1);
        for (Soldier fallenOne : members) {
            if (fallenOne.id == Deadid) {
                johnDoe = fallenOne;
            }
        }
        members.remove(johnDoe);
        members.trimToSize();
        losses++;
    }

    /*
     * Moves a single soldier towards a target with a specified speed.
     */
    public Position moveSoldierByIndex(int index, Position currentPosition, Position target, int speed) {

        target = new Position((target.x - currentPosition.x), (target.y - currentPosition.y));
        Position targetUnit = new Position((target.x - currentPosition.x), (target.y - currentPosition.y)).normalize();

        if (Math.sqrt((target.x * target.x) + (target.y * target.y)) > (Math.sqrt((targetUnit.x * targetUnit.x) + (targetUnit.y * targetUnit.y)) * speed * scale)) {
            currentPosition.x += (targetUnit.x * speed * scale);
            currentPosition.y += (targetUnit.y * speed * scale);
        } else {
            currentPosition.x += target.x;
            currentPosition.y += target.y;

        }
        return currentPosition;
    }

    /*
     * Generates a path for one soldier, based on target, state, and current position of every other soldier.
     */
    public void generatePaths(int state, Position target, ArrayList<Position> positions) {
        //First we define some useful variables
        Position oldPos, targetUnit, targetUnitNormal, pathVertex;
        Soldier correctChoice;
        soldierTargets soldierPath;
        int invert = -1;
        if (invertedFormation) {
            invert = 1;
        } //if the formation is inverted we must account for that
        if (invertedFormation) {
            if (state == 8) {
                state = 8;
            } else if (state == 9) {
                state = 9;
            }
        } else {
            if (state == 8) {
                state = 9;
            } else if (state == 9) {
                state = 8;
            }
        }

        switch (state) {
            case 8: // -------------- BLIXTLAS HOGER ----------------------- //
                correctChoice = members.get(0);          //take soldier out on the edge of formation
                soldierPath = new soldierTargets(correctChoice, state, target); //create a new soldier path

                //fill soldierPath with path nodes, one behind each soldier in the line
                for (int a = 0; a < members.size(); a++) {
                    oldPos = positions.get(((a) % members.size()));
                    targetUnit = new Position(target.x - oldPos.x, target.y - oldPos.y).normalize();
                    targetUnitNormal = new Position((-1) * invert * targetUnit.y, invert * targetUnit.x);
                    pathVertex = new Position((targetUnitNormal.x) - (targetUnit.x * 5), (targetUnitNormal.y) - (targetUnit.y * 5));
                    soldierPath.addTarget(new Position(oldPos.x + pathVertex.x, oldPos.y + pathVertex.y));
                }
                //finally, add the target node where he is supposed to end up
                oldPos = positions.get(((members.size() - 1) % members.size()));
                targetUnit = new Position(target.x - oldPos.x, target.y - oldPos.y).normalize();
                targetUnitNormal = new Position(targetUnit.y, -targetUnit.x);
                pathVertex = new Position(targetUnitNormal.x * 10 * scale * (double) invert, targetUnitNormal.y * 10 * scale * (double) invert);
                soldierPath.addTarget(new Position(oldPos.x + pathVertex.x, oldPos.y + pathVertex.y));

                //add the newly created path to our path-list
                soldierPaths.add(soldierPath);

                //move soldier in the list of group members, so his list position correspond better to his formation position
                members.add(members.get(0));
                members.remove(0);
                positions.add(positions.get(0));
                positions.remove(0);
                break;
            case 9:  // -------------- BLIXTLAS VANSTER ----------------------- //
                correctChoice = members.get(members.size() - 1);
                soldierPath = new soldierTargets(correctChoice, state, target);

                //fill soldierPath with path nodes, one behind each soldier in the line
                for (int a = members.size(); a > 0; a--) {
                    oldPos = positions.get((((members.size() - 1) + a) % members.size()));
                    targetUnit = new Position(target.x - oldPos.x, target.y - oldPos.y).normalize();
                    targetUnitNormal = new Position((-1) * invert * targetUnit.y, invert * targetUnit.x);
                    pathVertex = new Position((targetUnitNormal.x) - (targetUnit.x * 5), (targetUnitNormal.y) - (targetUnit.y * 5));
                    soldierPath.addTarget(new Position(oldPos.x + pathVertex.x, oldPos.y + pathVertex.y));
                }
                //finally, add the target node where he is supposed to end up
                oldPos = positions.get(0);
                targetUnit = new Position(target.x - oldPos.x, target.y - oldPos.y).normalize();
                targetUnitNormal = new Position(targetUnit.y, -targetUnit.x);
                pathVertex = new Position(targetUnitNormal.x * -10 * scale * (double) invert, targetUnitNormal.y * -10 * scale * (double) invert);
                soldierPath.addTarget(new Position(oldPos.x + pathVertex.x, oldPos.y + pathVertex.y));

                //add the newly created path to our path-list
                soldierPaths.add(soldierPath);

                //move soldier in the list of group members, so his list position correspond better to his formation position
                members.add(0, members.get(members.size() - 1));
                members.remove(members.size() - 1);
                positions.add(0, positions.get(positions.size() - 1));
                positions.remove(positions.size() - 1);
                break;
            case 10:  // -------------- ANSATSVIS FRAMAT ----------------------- //
                //first off, calculate the direction vector for index soldier, and use it to keep the soldier directions aligned
                targetUnit = new Position(target.x - positions.get(members.size() / 2).x, target.y - positions.get(members.size() / 2).y).normalize();

                //create soldierPaths for every second soldier to move forward in index direction
                for (int a = ansatsIterator; a < members.size(); a++) {
                    Soldier currentSoldier = members.get(a);
                    soldierPath = new soldierTargets(currentSoldier, state, target);
                    soldierPath.addTarget(new Position(positions.get(a).x + (targetUnit.x * 15 * scale), positions.get(a).y + (targetUnit.y * 15 * scale)));
                    soldierPaths.add(soldierPath);
                    a++;
                }

                //create soldierPaths for the soldiers that didn't move in the previous loop
                ansatsIterator = (ansatsIterator + 1) % 2;
                targetUnit = new Position(target.x - positions.get(members.size() / 2).x, target.y - positions.get(members.size() / 2).y).normalize();
                for (int a = ansatsIterator; a < members.size(); a++) {
                    Soldier currentSoldier = members.get(a);
                    soldierPath = new soldierTargets(currentSoldier, state, target);
                    soldierPath.addTarget(new Position(positions.get(a).x + (targetUnit.x * 15 * scale), positions.get(a).y + (targetUnit.y * 15 * scale)));
                    soldierPaths2.add(soldierPath);
                    a++;
                }
                ansatsIterator = (ansatsIterator + 1) % 2;
                break;
            case 11:  // -------------- ANSATSVIS BAKAT ----------------------- //
                //first off, calculate the direction vector for index soldier, and use it to keep the soldier directions aligned
                targetUnit = new Position(target.x - positions.get(members.size() / 2).x, target.y - positions.get(members.size() / 2).y).normalize();

                //create soldierPaths for every second soldier to move forward in index direction
                for (int a = ((ansatsIterator + 1) % 2); a < members.size(); a++) {
                    Soldier currentSoldier = members.get(a);
                    soldierPath = new soldierTargets(currentSoldier, state, target);
                    soldierPath.addTarget(new Position(positions.get(a).x - (targetUnit.x * 15 * scale), positions.get(a).y - (targetUnit.y * 15 * scale)));
                    soldierPaths.add(soldierPath);
                    a++;
                }

                //create soldierPaths for the soldiers that didn't move in the previous loop
                ansatsIterator = (ansatsIterator + 1) % 2;
                for (int a = ((ansatsIterator + 1) % 2); a < members.size(); a++) {
                    Soldier currentSoldier = members.get(a);
                    soldierPath = new soldierTargets(currentSoldier, state, target);
                    soldierPath.addTarget(new Position(positions.get(a).x - (targetUnit.x * 15 * scale), positions.get(a).y - (targetUnit.y * 15 * scale)));
                    soldierPaths2.add(soldierPath);
                    a++;
                }
                ansatsIterator = (ansatsIterator + 1) % 2;
                break;
            case 12:  // -------------- DEFENSIVA POSITIONER ----------------------- //
                //first off, calculate the direction vector for index soldier, and use it to keep the soldier directions aligned
                double distanceBetweenSoldiers = 10 * scale;
                int troopIndexStart = (members.size() / 2) - members.size() + 1;
                int troopIndexEnd = members.size() + troopIndexStart;
                double angle;
                if (orders.get(0).angle == -1) {
                    angle = currentOrder.combat_angle;
                } else {
                    angle = orders.get(0).angle;
                }

                //System.out.println();

                if (angle <= 0) {
                    angle = 0.0001;
                }
                double soldierDistance = (distanceBetweenSoldiers * members.size()) / ((angle / 360) * Math.PI * 2);
                Position indexPos = new Position(members.get(-troopIndexStart).getX(), members.get(-troopIndexStart).getY());

                if (enemyContact == true) {
                    targetUnit = enemyMeanPosition.minus(indexPos).normalize();
                } else if (orders.get(0).enemyDirection.x == -1 && orders.get(0).enemyDirection.y == -1) {
                    targetUnit = currentOrder.enemyDirection.minus(indexPos).normalize();
                } else {
                    targetUnit = orders.get(0).enemyDirection.minus(indexPos).normalize();
                }

                targetUnitNormal = new Position(targetUnit.y, -targetUnit.x);
                //create soldierPaths for every second soldier to move forward in index direction
                for (int a = troopIndexStart; a < troopIndexEnd; a++) {
                    Position soldierPosition = new Position(0, 0);

                    double angleStep = (((angle / 360) * 2 * (Math.PI)) / members.size());
                    double minus = 1;
                    //if(a < 0 ){minus -= 2;}
                    if (invertedFormation) {
                        minus = -minus;
                    }

                    soldierPosition.subtract(targetUnitNormal.times((soldierDistance) * minus * Math.cos((angleStep * a) + (Math.PI / 2.0))));
                    soldierPosition.subtract(targetUnit.times(soldierDistance - (soldierDistance) * Math.sin((angleStep * a) + (Math.PI / 2.0))));
                    soldierPosition.add(indexPos);


                    //soldierPosition.add(targetUnitNormal.times(a));
                    //System.out.println(targetUnit.length());
                    //soldierPosition.add(targetUnitNormal.times(20));

                    Soldier currentSoldier = members.get(a - troopIndexStart);
                    soldierPath = new soldierTargets(currentSoldier, state, target);
                    soldierPath.addTarget(soldierPosition);
                    soldierPaths.add(soldierPath);
                }
                defenceStarted = true;
                break;
        }
    }

    /*
     * If an enemy has been encountered, take action and use combat formations to deal with the threat after conditions.
     */
    public void enemyContact(Position contactPosition, int enemyNumber) {
        if (enemyContact) {

            enemyMeanPosition = contactPosition;
            Position p = new Position(members.get(getSquadPositionSoldierIndex(12)).getPosition().x, members.get(getSquadPositionSoldierIndex(12)).getPosition().y);


            //If an enemy is encountered, the first action is to line up to return fire
            if (enemyContactReaction == false) {
                if (orders.get(0).state != 12) {
                    orders.add(0, new SquadCommand(12, p, contactPosition, 10));
                    //System.out.println("ENEMY CONTACT!");
                }
                enemyContactReaction = true;
            }
            //If we have finished lining up
            if (enemyContactReaction == true && defenceFinished == true) {

                double enemyRatio = percieveEnemy(enemyNumber);
                enemyRatio = 1.0;
                double enemyDistance = contactPosition.distance(getGroupPosition());
                int s = orders.get(0).state;

                if (retreat() == false) {
                    //If the enemy is out of range and we have permission to go after him, go after him
                    if (enemyDistance > PRAKTISKT_SKJUTAVSTAND && currentOrder.huntAtWill == true && enemyRatio <= 1.5) {
                        if (s <= 7 || s == 12) {
                            //System.out.println("Enemy out of range, move up!");
                            SquadCommand sc0 = new SquadCommand(10, contactPosition, contactPosition, (int) 10);
                            sc0.formationCounter = 10000;
                            orders.add(0, sc0);
                            //System.out.println("Enemy Distance: " + enemyDistance);
                        }
                    }
                    //If we are moving forward and we are within distance, then stop
                    else if (enemyDistance <= PRAKTISKT_SKJUTAVSTAND) {
                        while (orders.get(0).state == 10) {
                            orders.remove(0);
                            System.out.println("Enemy within range, stop moving!");
                            if (orders.get(0).state == 12) {
                                defenceStarted = false;
                                defenceFinished = false;
                                currentOrder.combat_angle = 180;
                            }
                        }
                    }
                } else {//retreat() == true
                    //s
                    if (enemyDistance <= PRAKTISKT_SKJUTAVSTAND) {
                        if (s <= 7 || s == 12) {
                            System.out.println("We are retreating!");

                            SquadCommand sc0 = new SquadCommand(11, contactPosition, contactPosition, (int) 10);
                            sc0.formationCounter = 10000;
                            orders.add(0, sc0);
                            //System.out.println("state = " + orders.get(0).state);
                        }
                    }
                    //If we are moving forward and we are within distance, then stop
                    else if (enemyDistance > PRAKTISKT_SKJUTAVSTAND * 2) {
                        while (orders.get(0).state == 11) {
                            //System.out.println("We have reached a safe distance");
                            orders.remove(0);
                            if (orders.get(0).state == 12) {
                                defenceStarted = false;
                                defenceFinished = false;
                                currentOrder.combat_angle = 10;
                            }
                        }
                    }
                }

                //om vi vill förbi
                //om vi har scout eller någon lägre "Ta-mark"
            }
        }
    }

    /*
     * Returns a boolean about if we want to retreat due to conditions
     */
    private boolean retreat() {
        if (currentOrder.ammo_levels >= ammo || currentOrder.maximum_losses < losses) {
            //TODO: beräkna kdr, fly om för dålig
            return true;
        }
        return false;
    }

    /*
      * Corresponds to the process of recieving an order from platoon leader, and the acknowledgement and planning
      * that comes hand in hand with this process.
      */
    public void setOrder(SquadOrder order) {
        currentOrder = order;
        defenceStarted = false;      //is the group moving to static defensive positions?
        defenceFinished = false;     //is the group in static defensive positions?
        orders = currentOrder.planOrderStrategySteps(false, members.size(), 10000);
    }

    /*
     * returns the enemy/friendly number ratio.
     */
    public double percieveEnemy(int enemyNumbers) {
        return enemyNumbers / members.size();
    }

    /*
     * Returns the mean position of a unit collection
     */
    public Position meanPosition(ArrayList<Unit> list) {
        Position meanPos = new Position(0, 0);
        for (Unit u : list) {
            meanPos.add(u.getPosition());
        }
        meanPos.x /= list.size();
        meanPos.y /= list.size();
        return meanPos;
    }

    /*
     * Method Name : hasSoldierWithin
     * Parameters:
     * Position p : The position to search
     * double radius : A radius that combined with a Position becomes a circle
     * Returns: A boolean that represents whether or not this squad has a Soldier within <radius> range of Position
     *
     * Description:
     * Goes through all Soldiers in this Squad and returns true if at least one of them are within <radius> range of <p>
     * Otherwise false
     */
    public boolean hasSoldierWithin(Position p, double radius){
        for(Soldier s : members){
            if(s.getPosition().distance(p) <= radius){
                return true;
            }
        }
        return false;
    }

}














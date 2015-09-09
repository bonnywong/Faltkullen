package faltkullen;

import java.util.ArrayList;

/**
 * New file, is supposed to be the orders coming from platoon leader, and is a generalization of SquadCommand.
 */
public class SquadOrder {
    //----------------------------------------------------------------
    //ATTACKERA FIENDE
    public static final int MEET = 100;
    public static final int STRIKE = 101;
    public static final int FIGHT = 102;
    public static final int PINDOWN = 103;
    public static final int INTERRUPT = 104;
    public static final int COMBAT_RECONNAISSANCE = 105;

    //F�RSVARA MARK
    public static final int DEFEND = 201;
    public static final int DELAY = 202;

    //TA MARK
    public static final int CLEAR = 301;
    public static final int TAKE = 302;
    public static final int ADVANCE = 303;
    public static final int SEARCH = 304;

    //LETA EFTER FIENDE
    public static final int RECONNAISSANCE = 401;
    public static final int OVERSEE = 402;
    public static final int MONITOR = 403;
    public static final int PATROL = 404;
    //----------------------------------------------------------------
    public boolean acknowledged;   //if the order has been acknowledged
    public int type;      //what kind of order it is, one of those above
    public int maximum_losses;    //how many losses is accepted before retreat
    public int ammo_levels;     //how much ammo is too less to be able to fight
    public int minimum_combat_value;  //how low combat value can sink before retreat

    public double undetected_speed;  //what speed is used if enemy is not aware of unit, and ambush/assault is possible
    public double detected_speed;  //what speed is used if enemy is aware of unit, and ambush/assault is impossible
    public double level_of_preparation; //how much preparation is to be made to fortify position
    public Position orderTarget;   //which position the order concerns
    public Position enemyDirection;   //which position/direction the enemy is expected from
    public Position retreatDirection;  //which direction is considered main fall-back-direction
    public double risk_for_contact;  //how likely enemy contact is
    public int combat_angle;   //how wide angle the line of fire is
    public double available_time;
    public double needForSecrecy;
    public boolean stealth;

    public boolean fireAtWill;   //if enemies are discovered, fire is allowed
    public boolean huntAtWill;   //if enemies are engaged and beaten, hunting them down is allowed by default

    //----------------------------------------------------------------

    /*
     * Creating an order, and determining what is usually decided by the platoon leader.
     */
    public SquadOrder(int ordertype, double contactRisk, int time, Position orderTarg, Position enemy, Position retreatDir, double needForSecrecy, boolean stealth, int combatAngle) {
        acknowledged = false;
        type = ordertype;
        risk_for_contact = contactRisk;
        orderTarget = orderTarg;
        available_time = time;
        retreatDirection = retreatDir;
        this.needForSecrecy = needForSecrecy;
        this.stealth = stealth;
        this.combat_angle = combatAngle;
        this.enemyDirection = enemy;
        this.huntAtWill = true;

        level_of_preparation = setPrepLevel(contactRisk, time);
        setUndetectedSpeed(contactRisk, available_time, needForSecrecy);
        setDetectedSpeed(contactRisk, available_time);
    }

    /*
     * Determines over all speed of unit based on time available and risk of contact with enemy.
     *
     */
    private void setDetectedSpeed(double contactRisk, double available_time) {
        if (available_time < 0.33) {
            available_time = 0.33;
        }
        if (available_time > 3) {
            available_time = 3;
        }
        detected_speed = ((1.0 / available_time) - (contactRisk / 2));
    }

    /*
     * Determines over all speed of unit based on time available, need for secrecy and risk of contact with enemy.
     */
    private void setUndetectedSpeed(double contactRisk, double available_time, double needForSecrecy) {
        available_time /= (1.01 - needForSecrecy);
        if (available_time < 0.33) {
            available_time = 0.33;
        }
        if (available_time > 3) {
            available_time = 3;
        }
        undetected_speed = ((1.0 / available_time) - (contactRisk / 2));
    }

    private double setPrepLevel(double contactRisk, int time) {
        //TODO: calculate preparation level
        return 1.0;
    }

    private int combatSituation() {
        // TODO Return some action or formation based on the fact that we're in the face of foes
        return 0;
    }

    /*
     * Returns a list of path-node positions to target, comparable to partial-targets.
     *
     * Returns a list of where to move, first node of where to move first, second node corresponds to next node, and so on.
     */
    private ArrayList<Position> generatePathNodes() {
        //Path to target
        ArrayList<Position> path = new ArrayList<Position>();


        // TODO: Based on the map, determine the smartest path to target based on certain variables(obstacles, slow terrain, visibility, etc)
        // TODO: At the moment this only returns one node, and generates a straight line to target.


        path.add(orderTarget);
        return path;
    }

    /*
     * Sets the boundaries for when the unit must retreat based on which order they have (acceptable losses, remaining ammo, combat value)
     *
     */
    private void determineLimitsForRetreat(int currentForce, int currentAmmo) {
        if (type > 100 && type < 200) {
            if ((type % 10) == 0) {//ALLA SKA MED!
                setRetreatValues(-1, 0, -1);
            } else if ((type % 10) == 1) {//SL�
                setRetreatValues(((3 / 5) * currentForce), ((1 / 5) * currentAmmo), 30);
            } else if ((type % 10) == 2) {//BEK�MPA
                setRetreatValues(((2 / 5) * currentForce), ((2 / 7) * currentAmmo), 40);
            } else if ((type % 10) == 3) {//PINDOWN
                setRetreatValues(((1 / 5) * currentForce), ((2 / 5) * currentAmmo), 50);
            } else if ((type % 10) == 4) {//ST�RA
                setRetreatValues(((1 / 5) * currentForce), ((2 / 5) * currentAmmo), 70);
            } else if ((type % 10) == 5) {//COMBAT_RECONNAISSANCE
                setRetreatValues(((1 / 10) * currentForce), ((2 / 5) * currentAmmo), 70);
            }
        } else if (type > 200 && type < 300) {
            if ((type % 10) == 0) {//ALLA SKA MED!
                setRetreatValues(-1, 0, -1);
            } else if ((type % 10) == 1) {//F�RSVARA
                setRetreatValues(((4 / 5) * currentForce), ((1 / 5) * currentAmmo), 20);
            } else if ((type % 10) == 2) {//F�RDR�J
                setRetreatValues(((1 / 5) * currentForce), ((1 / 5) * currentAmmo), 20);
            }
        } else if (type > 300 && type < 400) {
            if ((type % 10) == 1) {//CLEAR
                setRetreatValues(((2 / 3) * currentForce), ((2 / 5) * currentAmmo), 30);
            } else if ((type % 10) == 2) {//TAKE
                setRetreatValues(((1 / 2) * currentForce), ((2 / 5) * currentAmmo), 40);
            } else if ((type % 10) == 3) {//ADVANCE
                setRetreatValues(((1 / 4) * currentForce), ((2 / 5) * currentAmmo), 70);
            } else if ((type % 10) == 4) {//GENOMS�K
                setRetreatValues(((1 / 10) * currentForce), ((2 / 5) * currentAmmo), 85);
            }
        } else if (type > 400 && type < 500) {
            if ((type % 10) == 1) {//RECONNAISSANCE
                setRetreatValues(((1 / 10) * currentForce), ((2 / 5) * currentAmmo), 85);
            } else if ((type % 10) == 2) {//�VERVAKA
                setRetreatValues(((1 / 10) * currentForce), ((2 / 5) * currentAmmo), 85);
            } else if ((type % 10) == 3) {//MONITOR
                setRetreatValues(((1 / 10) * currentForce), ((2 / 5) * currentAmmo), 85);
            } else if ((type % 10) == 4) {//PATROL
                setRetreatValues(((1 / 10) * currentForce), ((2 / 5) * currentAmmo), 85);
            }
        }
    }

    /*
     * Set value to the 3 different retreat condition values (losses, ammo, combat value)
     */
    public void setRetreatValues(int losses, int ammo, int combatValue) {
        maximum_losses = losses;    //how many losses is accepted before retreat
        ammo_levels = ammo;      //how much ammo is too less to be able to fight
        minimum_combat_value = combatValue;  //how low combat value can sink before retreat
    }

    /*
     * Updates target position and returns an update path.
     *
     * If we are to kill an enemy, we must update order target position if he moves.
     */
    public ArrayList<Position> updateTargetPosition(Position p) {
        orderTarget = p;
        return generatePathNodes();
    }

    /*
     * Returns a list of Squad compatible commands.
     *
     * This method is run when the group is making their preparations around the ORDER, interpreting the order, breaking it up in
     * smaller components, analyzing, and making it ready for execution.
     * Called by the Squad, and uses Squad variables to determine states and tactics.
     *
     */
    public ArrayList<SquadCommand> planOrderStrategySteps(boolean contact, int currentForce, int currentAmmo) {
        acknowledged = true;    //Group acknowledge the order
        determineLimitsForRetreat(currentForce, currentAmmo);    //Determines when to abort, when it is no longer worth engaging the enemy

        ArrayList<Position> moveNodes = new ArrayList<Position>();
        moveNodes = generatePathNodes(); //Determines which overall path to order target

        //Iterates through the list of nodes, building up
        ArrayList<SquadCommand> orderSteps = new ArrayList<SquadCommand>();
        int state = setMoveFormation(contact); //Determines which formation between points

        for (Position p : moveNodes) {
            orderSteps.add(new SquadCommand(state, p, 0, detected_speed, undetected_speed));
        }
        orderSteps.add(new SquadCommand(12, orderTarget, 0, detected_speed, undetected_speed));
        return orderSteps;
    }

    /*
     * Returns an int of which formation to be used for movement based on risk for enemy contact and if contact has ocurred.
     *
     * If no enemies are expected, use a wider and more flexible formation, if enemies are near,
     * use a more straight, narrow and sharp tactical formation.
     */
    private int setMoveFormation(boolean contact) {
        if (contact == true) {
            return combatSituation();
        }

        if (risk_for_contact < 0.3) {
            return 5; //eller 3?
        } else if (risk_for_contact >= 0.3 && risk_for_contact < 0.65) {
            return 4; // eller 5?
        } else if (risk_for_contact >= 0.65) {
            return 2;
        }
        return 0;
    }

    /*
     *  Returns an int of which formation to be used for holding a position based on available time
     */
    private int setHoldFormation() {
        //TODO: vi beh�ver fler formationer f�r f�rsvar
        return 12;
    }

}




package faltkullen;

/**
 * Created by Anton on 2015-04-13.
 * This class is used to aid a Leader in making a decision regarding future actions and commands.
 * It has a simple <check> method that returns true if the condition is met, otherwise false.
 * An example would be CombatReadinessCondition, which returns true if the unit is ready for more combat.
 */
public abstract class DecisionCondition{
    /*
     * Method name : check
     * Parameters:
     *      Group group : If the condition needs to check a Group, it uses this Group
     * Description:
     * Returns whether or not this condition has been met, true if it has, otherwise false
     */
    public boolean check(Group group){
        return false;
    }
}

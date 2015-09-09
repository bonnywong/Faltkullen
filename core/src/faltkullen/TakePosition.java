package faltkullen;

/**
 * Created by Anton on 2015-02-19.
 */
public class TakePosition extends Goal{
    public double radius = 0.1; //A friendly, non-aerial unit must be within this range of the Position in order for it to be potentially taken.
    public TakePosition(Position p, double rad){
        super(p);
        radius = rad;
    }

    /*
     * Method Name : complete
     * Inherited from : Goal
     * Parameters : Leader leader
     * Returns : A boolean that represents whether or not the Goal has been completed
     * Description:
     * This method is meant to return whether or not a particular goal has been met.
     *
     * Class-specific Description:
     * This method returns true if the following are true.
     * 1. The Leader of this Goal is not in combat.
     * 2. There is at least one friendly unit commanded by the Leader within <radius> range of the position to be taken.
     */

    @Override
    public boolean complete(Leader leader){
        if(!leader.inCombat){
            if(leader.hasGroundUnitWithin(position, radius)){
                return true;
            }
        }
        return false;
    }

    /*
     * Method Name : action
     * Parameters:
     *      Group group : The group doing the action
     * Returns : Nothing
     * Description:
     * Some goals require different actions. This method executes those actions.
     * An example would be that TakePosition makes "group" move towards the point
     *
     * Class-specific Description:
     * This method makes the group in question move towards the position of the goal.
     */

    @Override
    public void action(Group group){
        group.advanceTowardsDebug = debug;
        group.advanceTowards(position);
        group.advanceTowardsDebug = false;
    }

    public String toString(){
        return "Take Position";
    }
}

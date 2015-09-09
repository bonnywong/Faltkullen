package faltkullen;

/**
 * Created by Anton on 2015-04-13.
 */
public class Standby extends Goal{
    public double radius = 0.1; //A friendly, non-aerial unit must be within this range of the Position in order for it to be potentially taken.
    public Standby(){
        super(new Position(0, 0));
    }

    /*
     * Method Name : complete
     * Inherited from : Goal
     * Parameters : None
     * Returns : A boolean that represents whether or not the Goal has been completed
     * Description:
     * This method is meant to return whether or not a particular goal has been met.
     *
     * Class-specific Description:
     * This method returns false at all times, since a Standby order has to be cancelled by a higher officer.
     */

    @Override
    public boolean complete(Leader leader){
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
     * This method makes the group see if they have enemies nearby, and in that case, they attack them.
     */

    @Override
    public void action(Group group){
        group.combatCheck();
    }

    @Override
    public String toString(){
        return "Standby";
    }
}

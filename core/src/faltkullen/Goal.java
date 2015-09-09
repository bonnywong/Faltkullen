package faltkullen;

/**
 * Created by Anton on 2015-02-19.
 */
public abstract class Goal {
    public Position position;
    public DecisionCondition condition;
    public Goal continueGoal; //The follow-up goal to be executed if the condition is met
    public Goal breakGoal; //The follow-up goal to be executed if the condition isnt met

    public boolean debug = false;

    public Goal(Position p){
        position = p;
    }

    /*
     * Method Name : complete
     * Parameters : None
     * Returns : A boolean that represents whether or not the Goal has been completed
     * Description:
     * This method is meant to return whether or not a particular goal has been met.
     */
    public boolean complete(Leader leader){
        return false;
    }

    /*
     * Method Name : setCondition
     * Parameters:
     *      DecisionCondition dc = The check that decides the followup goal
     *      Goal cont = The goal to be executed if dc is true
     *      Goal bre = The goal to be executed if dc is false
     * Description:
     * This method is meant to set the DecisionCondition used at the end of the goal to determine future actions, based on the state of the unit or the battlefield
     */
    public void setCondition(DecisionCondition dc, Goal cont, Goal bre){
        condition = dc;
        continueGoal = cont;
        breakGoal = bre;
    }

    /*
     * Method Name : action
     * Parameters:
     *      Group group : The group doing the action
     * Returns : Nothing
     * Description:
     * Some goals require different actions. This method executes those actions.
     * An example would be that TakePosition makes "group" move towards the point
     */
    public void action(Group group){

    }
}

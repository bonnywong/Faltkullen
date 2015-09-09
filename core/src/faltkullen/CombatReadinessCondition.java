package faltkullen;

/**
 * Created by Anton on 2015-04-13.
 */
public class CombatReadinessCondition extends DecisionCondition{
    @Override
    public boolean check(Group group){
        if(group.getReadiness() >= 3){
            return true;
        }
        else{
            return false;
        }
    }
}

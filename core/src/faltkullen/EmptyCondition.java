package faltkullen;

/**
 * Created by Anton on 2015-08-30.
 */
public class EmptyCondition extends DecisionCondition {
    //This class is simply to provide the utility to have chained orders
    //To do this, this Condition simply returns true every time
    @Override
    public boolean check(Group g){
        return true;
    }
}

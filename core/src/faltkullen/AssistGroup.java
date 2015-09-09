package faltkullen;

/**
 * Created by Anton on 2015-08-30.
 */
public class AssistGroup extends Goal {
    private Group group;

    public AssistGroup(Group g, Position p){
        super(p);
        group = g;
    }

    @Override
    public boolean complete(Leader leader){
        if(!group.inCombat || group.isWiped()){
            System.out.println("TRUE TRUE TURE");
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void action(Group group){
        group.advanceTowards(position);
    }

    public String toString(){
        return "Assist";
    }
}

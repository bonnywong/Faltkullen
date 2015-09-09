package faltkullen;

/**
 * Created by Anton on 2015-09-02.
 */
public class GoalCompleted extends LeaderCommunication {
    public Goal completed;

    public GoalCompleted(Goal g, String str){
        super(str);
        completed = g;
    }
}

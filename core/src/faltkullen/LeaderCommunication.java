package faltkullen;

import java.util.ArrayList;

/**
 * Created by Anton on 2015-04-13.
 * This class is intended to represent a particular message sent between one leader to another.
 * A general uses this to issue commands to lesser officers, and lesser officers use this to report back.
 */
public class LeaderCommunication {
    public int report; //Used when communicating Upward
        //1 means the Lower Leader is requesting Assistance
    public Goal goal; //The goal-command being sent
    public String to; //Who is supposed to recieve the communication?
    public String from; //Who is the communication from?
    public LeaderCommunication message;
    private boolean chainGoal = false;

    public Leader directTo;
    public Leader directFrom;

    public boolean special = false;

    private String fromCode;

    public LeaderCommunication(String fr){
        fromCode = fr;
    }

    public LeaderCommunication(Goal g, Leader last){
        Leader current = last;
        LeaderCommunication lc = new LeaderCommunication("LeaderCommunication Chain");
        lc.goal = g;
        lc.to = null;
        while(current.commandedBy != null){
            LeaderCommunication lc2 = new LeaderCommunication("Leader Communication Chain");
            lc2.message = lc;
            lc2.to = current.name;
            lc = lc2;
            current = current.commandedBy;
        }
        message = lc.message;
        to = lc.to;
        if(to == null){
            goal = g;
        }
    }

    private boolean chainGoalChanged = false;

    public void setChainGoal(boolean b){
        if(!b){
            System.out.println("OH WAIT THATS IMPOSSIBLE RIGHT?");
        }
        chainGoalChanged = true;
        chainGoal = b;
    }

    public boolean getChainGoalKnow(){
        System.out.println("Has chainGoal been changed?" + chainGoalChanged);
        System.out.println("Special?" + special);
        System.out.println("fromCode = " + fromCode);
        return chainGoal;
    }

    public boolean getChainGoal(){
        return chainGoal;
    }
}

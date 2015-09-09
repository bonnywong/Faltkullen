package faltkullen;

import java.util.ArrayList;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.PlacedGroup;
import faltkullen.Goal;

/**
 * Created by Anton on 2015-02-19.
 */
public class Leader implements Cloneable{
    public int communicationDelayUp = 5; //The delay between materializing a Decision and sending it upwards in the chain of command
    public int communicationDelayDown = 3; //The delay between materializing a Decision and sending it downward in the chain of command
        //This delay is for EACH lesser officer, so 10 officers with delay 1 means officer 1 will recieve it after 1 second, and officer 10 will recieve it after 10 seconds
    public int delayCounter = 0; //As there is delay, this variable represents how many seconds longer the delay must last
    public int decisionTime = 0; //The amount of seconds it takes for this Leader to make a Decision
    private double retreatAt = 3.0; //The Entropy value at which this Leader calls for a Retreat
    public boolean retreating = false;

    public Goal goal;   //The Goal of this Leader
    public boolean inCombat = false; //Is true if one of the Squads this Leader is commanding is in combat
    public ArrayList<Leader> commands; //A list of Leaders that this Leader has command over
    public Unit leaderInField; //Is this Leader on the field? In that case this is not null, and represents this Leader
    public Group inGroup; //If this Leader is on the field, this is the Group that its a part of
    public Leader replacement; //If the Leader is on the field and dies, this template Leader replaces the Leader inside the Leaders own squad.
    public Leader commandedBy; //If the Leader is commanded by another Leader, this is that leader
    private Leader verificationLeader; //This Leader sends verificationLeader to verify whether or not the Goal has been completed
        //If null, then itself should verify
    public boolean setInMotion = false; //Has this Leader commanded other Leaders to do action that should complete the Goal?
        //In other words, has the Leader ordered other Leaders already and is meerly waiting for them to respond?

    public ArrayList<LeaderCommunication> communications = new ArrayList<LeaderCommunication>();

    public boolean isAggressive = false;

    public String name = "Unnamed Leader";

    public Army army;

    public Leader(double ret, boolean aggro){
        retreatAt = ret;
        isAggressive = aggro;
        commands = new ArrayList<Leader>();
    }

    public Leader(Leader template, TestOfMap map, Army army, double ret, boolean aggro){
        retreatAt = ret;
        isAggressive = aggro;
        commands = new ArrayList<Leader>();
        ArrayList<Leader> templateCommands = template.getCommand();
        if(templateCommands.size() > 0) {
            for (Leader under : templateCommands) {
                if (under == null) {
                    System.out.println("under is null");
                }
                if (map == null) {
                    System.out.println("map is null");
                }
                if (army == null) {
                    System.out.println("army is null");
                }
                Leader newUnder = new Leader(under, map, army, ret, aggro);
                newUnder.commandedBy = this;
                commands.add(newUnder);
            }
        }
        if(template.inGroup != null){
            inGroup = new Group(template.inGroup);
            inGroup.army = army;
            inGroup.populate(map);
            inGroup.groupLeader = this;
            leaderInField = inGroup.members.get(0);
        }
    }

    //This construction creates a Leader from a String. See Save/Load for further insight
    public Leader(ArmyComposition ac, String str){
        commands = new ArrayList<Leader>();

        String[] split = str.split("/");
        name = split[0];
        communicationDelayUp = Integer.parseInt(split[1]);
        communicationDelayDown = Integer.parseInt(split[2]);
        retreatAt = Double.parseDouble(split[3]);
        if(split.length > 4){
            //The Leader was part of a Group
            String groupName = split[4];
            int type = Integer.parseInt(split[5]);
            int size = Integer.parseInt(split[6]);
            Group g = new Group(size);
            g.name = groupName;
            ac.groups.add(g);
            g.unitType = ac.unitTypes.get(type);
            //System.out.println("Loaded a Leader Led Group with unitType ID = " + g.unitType.id);
            g.groupLeader = this;
            inGroup = g;

            if(split.length > 7){
                //It was also placed
                Vector3 v = new Vector3(Float.parseFloat(split[7]), Float.parseFloat(split[8]), 0);
                PlacedGroup placedGroup = new PlacedGroup(g, v, ac.sprite);
                g.onMap = placedGroup;
            }
        }
    }

    public synchronized ArrayList<Leader> getCommand(){
        return new ArrayList<Leader>(commands);
    }

    /* Method Name:
     * Parameters:
     * Position p : The position to search
     * float radius : A radius that combined with a Position becomes a circle
     * Returns : A boolean that represents whether or not this Leader has a ground-based unit within <radius> range of <p>
     *
     * Description:
     * This method essentially goes through all ground-based Units under this Leader and returns true the moment it finds something that is within
     * <radius> range of <p>. This method is slow if called high up in the chain of command without any unit fitting the searched criteria.
     */
    public boolean hasGroundUnitWithin(Position p, double radius){
        for(int a = 0;a<commands.size();a++){
            if(commands.get(a).hasGroundUnitWithin(p, radius)){
                return true;
            }
        }
        if(leaderInField != null){
            return inGroup.hasGroundUnitWithin(p, radius);
        }
        return false;
    }

    public void giveOrder(){
        for(int a=0;a<commands.size();a++){
            commands.get(a).takeOrder(goal);
        }
    }

    public void takeOrder(Goal g){
        goal = g;
        setInMotion = false;
    }

    public void chainOrder(Goal g){
        System.out.println("Is this ever run?");
        if(inGroup != null){
            if(inGroup.inCombat){
                return;
            }
        }
        g.continueGoal = goal;
        g.condition = new EmptyCondition();
        goal = g;
        setInMotion = false;

        if(goal.continueGoal == null){
            System.out.println("THIS MAKES NO SENSE");
        }
    }

    public void reportSuccess(Leader l){

    }

    public void recieveCommunication(LeaderCommunication comm){
        if(retreating){
            return;
        }

        boolean debug = false;

        if(debug) {
            System.out.println("Communication recieved");
        }
        if(comm.goal != null && commandedBy.name == comm.from){
            if(debug) {
                System.out.println("Order accepted");
            }
            if(!comm.getChainGoal() && comm.goal instanceof AssistGroup){
                System.out.println("Lets count how many times this happens");
            }
            if(comm.getChainGoal()){
                chainOrder(comm.goal);
            }
            else {
                takeOrder(comm.goal);
            }
        }
        else if(comm.message != null && commandedBy.name == comm.from){
            if(debug) {
                System.out.println("Sending further down");
            }
            communications.add(comm.message);
        }
        else if(comm.report > 0){
            //Its a report from someone beneath
            if(comm instanceof AssistanceRequested){
                //They are requesting Assistance
                //Redirect those beneath you to assist them
                AssistanceRequested ar = (AssistanceRequested)comm;
                Group g = ar.requester;
                Position p = g.getWeightedPosition();
                ArrayList<LeaderCommunication> newCom = new ArrayList<LeaderCommunication>();
                for(int a=0;a<commands.size();a++){
                    if(commands.get(a) != comm.directFrom){
                        LeaderCommunication lc = new LeaderCommunication("Assist Group proper Creation");
                        lc.directTo = commands.get(a);
                        lc.setChainGoal(true);
                        lc.special = true;

                        lc.goal = new AssistGroup(g, p);
                        lc.goal.condition = new EmptyCondition();
                        newCom.add(lc);
                    }
                }
                newCom.addAll(communications);
                communications = newCom;

                /*
                System.out.println("LETS COUNT THEM " + name);
                for(int a=0;a<communications.size();a++){
                    LeaderCommunication com = communications.get(a);
                    if(com.goal instanceof AssistGroup){
                        if(com.getChainGoal()){
                            System.out.println("HERE IS ONE MUTHERFOCKER " + name + " " + a);
                        }
                    }
                }
                assistDebug = true;
                */
            }
            else if(comm instanceof GoalRequest){
                GoalRequest gr = (GoalRequest)comm;
                if(goal != null){
                    if(goal instanceof Standby){
                        requestGoal(gr.requester);
                    }
                    else{
                        LeaderCommunication lc = new LeaderCommunication("Granting Goal Request");
                        lc.directTo = gr.requester;
                        lc.goal = goal;
                        communications.add(lc);
                    }
                }
                else{
                    requestGoal(gr.requester);
                }
            }
        }
    }

    public int thoughtCounter = 0;
    public boolean extraDelay = false;

    private boolean requestedAssistance = false;

    private boolean assistDebug = false;

    public void think(){
        /*
        if(name.equals("Red Leader")) {
            System.out.println("Thinking");
            if (goal == null) {
                System.out.println("goal is null");
            } else {
                System.out.println("Current goal = " + goal.toString());
            }
        }
        */
        //Have we set things in motion?
        if(!setInMotion && commands.size() > 0 && goal != null){
            //No, so we should set a verificationLeader
            //For simplicity, we assert commands.get(0) as the verificationLeader
            verificationLeader = commands.get(0);
            //For each of those this Leader commands, create a LeaderCommunication object and insert it into communications
            for(int a=0;a<commands.size();a++){
                LeaderCommunication comm = new LeaderCommunication("Setting our plans in motion");
                comm.from = name;
                comm.to = commands.get(a).name;
                comm.goal = goal;
                if(goal instanceof AssistGroup){
                    comm.setChainGoal(true);
                    comm.goal.condition = new EmptyCondition();
                }
                communications.add(comm);
            }
            delayCounter = communicationDelayDown;
            if(extraDelay){
                delayCounter += commands.size();
            }
            //giveOrder();
            setInMotion = true;
        }
        if(communications.size () > 0){
            boolean debug = false;
            if(debug) {
                System.out.println(name + " is thinking");
            }
            if(assistDebug) {
                for (int a = 0; a < communications.size(); a++) {
                    LeaderCommunication com = communications.get(a);
                    if (com.goal instanceof AssistGroup) {
                        if (com.getChainGoal()) {
                            System.out.println("WE SHOULD SEE THIS, AS MANY " + name + " " + a);
                        }
                    }
                }
                assistDebug = false;
            }
            delayCounter--;
            while(delayCounter <= 0) {
                if(debug) {
                    System.out.println(name + " is making decisions");
                }
                LeaderCommunication comm = communications.remove(0);
                //Is the Communication supposed to go to himself?
                if(comm.to == null && comm.directTo == null){
                    if(debug) {
                        System.out.println(name + " sets his Goal");
                        System.out.println("goal set to " + comm.goal.toString());
                    }
                    goal = comm.goal;
                }
                else{
                    //No, its supposed to go to someone else
                    comm.from = name;
                    Leader commander = commandedBy;
                    if(commander == null){
                        commander = this;
                    }
                    if(comm.goal instanceof AssistGroup){
                        if(!comm.getChainGoal()){
                            //System.out.println("HOW?!!!!! " + name + " " + (8 - communications.size()));
                        }
                    }
                    //Is it supposed to go to this Leaders commander?
                    if(commander.name.equals(comm.to) && commander != this){
                        //Yes
                        commander.recieveCommunication(comm);
                        delayCounter = communicationDelayUp;
                    }
                    else {
                        if(comm.directTo != null){
                            comm.directTo.recieveCommunication(comm);
                            delayCounter = communicationDelayDown;
                        }
                        else {
                            for (int a = 0; a < commands.size(); a++) {
                                if (commands.get(a).name.equals(comm.to)) {
                                    if (debug) {
                                        System.out.println(name + " has sent it further down");
                                    }
                                    commands.get(a).recieveCommunication(comm);
                                    delayCounter = communicationDelayDown;
                                }
                            }
                        }
                    }
                }
                if(communications.size() == 0){
                    delayCounter = communicationDelayUp;
                }
                /*
                comm.to.recieveCommunication(comm);
                if(communications.size() > 0) {
                    if (communications.get(0).goal != null) {
                        delayCounter = communicationDelayDown;
                        if(extraDelay){
                            delayCounter += commands.size();
                        }
                    } else {
                        delayCounter = communicationDelayUp;
                    }
                }
                else{
                    delayCounter = 1;
                }
                */
            }
        }
        if(goal == null && inGroup != null){
            goal = new Standby();
            setInMotion = true;
        }
        thoughtCounter++;
        //System.out.println("We get here, for the " + thoughtCounter + " time");
        //If we don't have a goal, don't do anything
        if(inGroup != null && goal != null) {
            //System.out.println("Move soldier!");
            goal.action(inGroup);
            //inGroup.advanceTowards(goal.position);

            //Should we retreat?
            if (inGroup.getReadiness() <= retreatAt && !retreating) {
                //System.out.println("retreating with readiness " + inGroup.getReadiness() + ", our limit being " + retreatAt);
                //Yes, the Leader thinks they should retreat
                retreating = true;
                goal = new RetreatTo(inGroup.startPosition, 5.0);
                System.out.println("Retreat has Started");
            }

            //Are we not retreating?
            if(!retreating){
                //Are we in combat?
                if(inGroup.inCombat){
                    //We are, have we requested assistance?
                    if(requestedAssistance){
                        //We have, do nothing special
                    }
                    else if(commandedBy != null){
                        //We haven't, add a Assistance Request to our list of communications
                        AssistanceRequested assistance = new AssistanceRequested("Creating Assistance Requested");
                        assistance.to = commandedBy.name;
                        assistance.report = 1;
                        assistance.requester = inGroup;
                        ArrayList<LeaderCommunication> newCom = new ArrayList<LeaderCommunication>();
                        newCom.add(assistance);
                        newCom.addAll(communications);
                        communications = newCom;
                        requestedAssistance = true;
                    }
                    else{
                        requestedAssistance = true;
                    }
                }
                else{
                    //We are not in combat
                    //Reset requestedAssistance
                    requestedAssistance = false;
                }
            }

            //Have we completed our goal?
            if (goal.complete(this)) {
                //Was it to retreat to a point?
                if(goal instanceof RetreatTo){
                    //Yes, depopulate group
                    inGroup.unpopulate();
                    inGroup = null;
                }
                else {
                    boolean debug = false;
                    goalCompleted();
                    if (goal instanceof AssistGroup){
                        debug = true;
                    }
                    if(debug){
                        System.out.println("We have completed a non-RetreatTo goal");
                    }
                    //We have, is there a follow-up goal?
                    if (goal.condition != null) {
                        if(debug){
                            System.out.println("We might have a followup!");
                        }
                        //Yes, check if we meet the condition
                        if (goal.condition.check(inGroup)) {
                            if(debug){
                                System.out.println("We now have the followup as our goal");
                            }
                            //We meet the condition, so set our current goal to continueGoal
                            goal = goal.continueGoal;
                            goal.debug = true;
                        } else {
                            if(debug){
                                System.out.println("We went with another route");
                            }
                            //We do not meet the condition, so set our current goal to breakGoal
                            goal = goal.breakGoal;
                        }
                    }
                    else{
                        requestGoal(null);
                    }
                }
            }
        }
        for(Leader under : commands){
            under.think();
        }
    }

    public Leader clone(){
        try{
            Leader l = (Leader)super.clone();
            l.commands = new ArrayList<Leader>(commands);
            return l;
        }
        catch(CloneNotSupportedException e){
            return null;
        }
    }

    public synchronized Leader deepClone(TestOfMap m, ArrayList<Unit> baseUnits, Army army){
        try{
            Leader l = (Leader)super.clone();
            l.commands = new ArrayList<Leader>();
            l.communications = new ArrayList<LeaderCommunication>();
            if(inGroup!=null){
                l.inGroup = new Group(inGroup);
                l.inGroup.groupLeader = l;
                //System.out.println("Creating a new Group with typeID = " + inGroup.unitType.id);
                l.inGroup.type = baseUnits.get(inGroup.unitType.id);
                l.inGroup.setStartPosition(inGroup.onMap.position);
                l.inGroup.army = army;
                l.inGroup.populate(m);
            }
            for(int a=0;a<commands.size();a++){
                Leader l2 = commands.get(a).deepClone(m, baseUnits, army);
                l.commands.add(l2);
                l2.commandedBy = l;
            }
            this.army = army;
            return l;
        }
        catch(CloneNotSupportedException e){
            return null;
        }
    }

    public void setInitialOrders(ArrayList<LeaderCommunication> arr){
        communications.addAll(arr);
    }

    public Array<Leader> getLeaderList(){
        Array<Leader> ret = new Array<Leader>();
        ret.add(this);
        for(Leader l : commands){
            ret.addAll(l.getLeaderList());
        }
        return ret;
    }

    public void setRetreatAt(double d){
        retreatAt = d;
    }

    public double getRetreatAt(){
        return retreatAt;
    }

    public void addCommand(Leader l){
        if(l.commandedBy != null){
            l.commandedBy.removeCommand(l);
        }
        l.commandedBy = this;
        commands.add(l);
    }

    public void removeCommand(Leader l){
        for(int a=0;a<commands.size();a++){
            if(commands.get(a) == l){
                commands.remove(a);
                break;
            }
        }
    }

    public Vector3 getWeightedPoint(){
        if(inGroup != null){
            if(inGroup.onMap != null){
                return inGroup.onMap.position;
            }
            else{
                return new Vector3(0, 0, 0);
            }
        }
        else if(commands.size() > 0) {
            Vector3 point = new Vector3(0, 0, 0);
            for (int a = 0; a < commands.size(); a++) {
                Leader l = commands.get(a);
                point.add(l.getWeightedPoint());
            }
            point = new Vector3(point.x / commands.size(), point.y / commands.size(), 0);
            return point;
        }
        else{
            return new Vector3(0, 0, 0);
        }
    }

    public String toString(){
        return name;
    }

    public int getComUp(){
        return communicationDelayUp;
    }

    public int getComDown(){
        return communicationDelayDown;
    }

    public void getSaveStrings(StringBuilder builder, int p, String linebreak){
        builder.append(""+p+":"+name+"/"+getComUp()+"/"+getComDown()+"/"+retreatAt);
        if(inGroup != null){
            builder.append("/"+inGroup.getSaveString());
        }
        builder.append(linebreak);
        for(int a=0;a<commands.size();a++){
            commands.get(a).getSaveStrings(builder, p+1, linebreak);
        }
    }

    public void requestGoal(Leader previousRequester){
        if(commandedBy != null) {
            GoalRequest gr = new GoalRequest("No special goal");
            gr.requester = this;
            communications.add(gr);
            gr.report = 1;
            gr.directTo = commandedBy;
        }
        else{
            LeaderCommunication lc = new LeaderCommunication("Final Strike Goal");
            lc.directTo = previousRequester;
            lc.goal = goal;
            communications.add(lc);
        }
    }

    public void goalCompleted(){
        if(commandedBy != null) {
            GoalCompleted gc = new GoalCompleted(goal, "Goal Just Completed");
            gc.directTo = commandedBy;
            communications.add(gc);
        }else if(goal == army.goal){
            army.hasWon = true;
        }
    }
}

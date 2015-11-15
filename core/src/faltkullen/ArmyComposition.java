package faltkullen;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.Array;
import com.mygdx.game.StrategyComponent;
import com.mygdx.game.UnitType;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Anton on 2015-08-24.
 */
public class ArmyComposition {
    public Array<UnitType> unitTypes;
    public ArrayList<Group> groups;
    public Leader leader;
    public Array<Leader> leaderList;
    public Array<StrategyComponent> orders;

    public Sprite sprite;

    public ArmyComposition(Leader l, Main m){
        leader = l;
        unitTypes = new Array<UnitType>();
        createBaseSoldierUnitType(m);
        groups = new ArrayList<Group>();
        leaderList = leader.getLeaderList();
        orders = new Array<StrategyComponent>();
    }

    //The creation of an ArmyComposition object with the use of a BufferedReader.
    //See Save/Load for further insight
    public ArmyComposition(BufferedReader br, Main m, Sprite s, Skin sk){
        //Start with creating the arrays
        unitTypes = new Array<UnitType>();
        groups = new ArrayList<Group>();
        orders = new Array<StrategyComponent>();

        sprite = s;

        Array<Goal> orderTypes = new Array<Goal>();
        orderTypes.add(new TakePosition(new Position(0, 0), 10));
        orderTypes.add(new RetreatTo(new Position(0, 0), 10));

        String currentLine;
        String[] splitted;
        try {
            while ((currentLine = br.readLine()) != null) {
                splitted = currentLine.split(":");
                if(splitted[0].equals("T")){
                    //We found a new UnitType
                    String[] split2 = splitted[1].split("/");
                    String name = split2[0];
                    int type = Integer.parseInt(split2[1]);
                    //Load its Settings
                    Settings base = new Settings(m);
                    currentLine = br.readLine();
                    splitted = currentLine.split(":");
                    while(splitted[0].equals("NEW_Attribute")){
                        Attribute att = new Attribute(splitted[1],
                                Integer.parseInt(splitted[3]),
                                Integer.parseInt(splitted[2]),
                                Integer.parseInt(splitted[4]),
                                Integer.parseInt(splitted[5]),
                                base);
                        att.allowsChange = Boolean.parseBoolean(splitted[6]);
                        base.attributes.add(att);
                        if(att.allowsChange){
                            base.changeableAttributes.add(att);
                        }
                        splitted = br.readLine().split(":");
                    }
                    UnitType ut = new UnitType(name, type, base);
                    ut.id = unitTypes.size;
                    unitTypes.add(ut);
                }
                else if(splitted[0].equals("Leaders")){
                    //We found the Leadership Hierarchy
                    currentLine = br.readLine();
                    splitted = currentLine.split(":");
                    Leader l = new Leader(this, splitted[1]);
                    ArrayList<Leader> hierarchy = new ArrayList<Leader>();
                    hierarchy.add(l);
                    currentLine = br.readLine();
                    boolean debug = false;
                    while(!currentLine.equals("END_LEADERS")){
                        if(debug) {
                            System.out.println(currentLine);
                        }
                        splitted = currentLine.split(":");
                        Leader l2 = new Leader(this, splitted[1]);
                        int h = Integer.parseInt(splitted[0]);
                        if(h > hierarchy.size()){
                            Leader l2parent = hierarchy.get(hierarchy.size()-1);
                            if(debug) {
                                System.out.println("l2.parent = " + l2parent.name);
                                System.out.println("l2 = " + l2.name);
                            }
                            hierarchy.get(hierarchy.size()-1).addCommand(l2);
                            hierarchy.add(l2);
                        }
                        else if(h==hierarchy.size()){
                            hierarchy.get(h-2).addCommand(l2);
                            hierarchy.set(h-1, l2);
                        }
                        else if(h < hierarchy.size()){
                            while(hierarchy.size() >= h){
                                hierarchy.remove(hierarchy.size()-1);
                            }
                            hierarchy.get(h-2).addCommand(l2);
                            hierarchy.add(l2);
                        }
                        currentLine = br.readLine();
                    }
                    leaderList = l.getLeaderList();
                    leader = l;
                }
                else if(splitted[0].equals("G")){
                    //Its an unled Group
                    Group g = new Group(this, splitted[1]);
                    groups.add(g);
                }
                else if(splitted[0].equals("O")){
                    StrategyComponent sc = new StrategyComponent(splitted[1], sk, leaderList, orderTypes, null);
                    orders.add(sc);
                }
                else if(splitted[0].equals("END_ARMY")){
                    break;
                }
            }
        }
        catch(IOException e){
            System.out.println("Experienced a problem in reading a file while creating an ArmyComposition object");
        }
    }

    public UnitType createBaseSoldierUnitType(Main m){
        String name = "Soldier";

        Settings settings = new Settings(m);
        String[] names = new String[]{"Weapon Damage",
                "Weapon Accuracy",
                "Weapon Range",
                "Protection",
                "Sensor Range",
                "Sensor Interval",
                "Soldier Accuracy",
                "Soldier Morale",
                "Soldier Movespeed"};
        int[] min = new int[]{1, 90, 10, 5, 5, 1, 1, 70, 1};
        int[] base = new int[]{75, 95, 60, 10, 100, 1, 2, 100, 5};
        int[] max = new int[]{115, 100, 100, 15, 300, 20, 100, 100, 10};
        int[] evo = new int[]{100, 100, 80, 100, 225, -75, 125, 25, 75};

        for(int a=0;a<names.length;a++){
            Attribute attri = new Attribute(names[a], base[a], min[a], max[a], evo[a], settings);
            settings.attributes.add(attri);
        }
        settings.maxCost = settings.getTotalCost();

        UnitType ut = new UnitType(name, 0, settings);
        ut.id = unitTypes.size;
        unitTypes.add(ut);
        return ut;
    }

    public void refine(){
        for(int a=0;a<unitTypes.size;a++){
            unitTypes.get(a).id = a;
        }
    }

    public synchronized ArrayList<LeaderCommunication> getOrders(){
        ArrayList<LeaderCommunication> ret = new ArrayList<LeaderCommunication>();
        for(int a=0;a<orders.size;a++){
            LeaderCommunication lc = orders.get(a).getOrder();
            ret.add(lc);
        }
        return ret;
    }

    public String getSaveInfo(String linebreak){
        StringBuilder builder = new StringBuilder("NEWARMY" + linebreak);

        //Start with the UnitTypes
        for(int a=0;a<unitTypes.size;a++){
            UnitType ut = unitTypes.get(a);
            builder.append("T:"+ut.name+"/"+ut.type.type+"/"+ut.settings.getSaveString());
        }

        //Continue with the Leadership Hierarchy
        builder.append("Leaders"+linebreak);
        leader.getSaveStrings(builder, 1, linebreak);
        builder.append("END_LEADERS" + linebreak);

        //Continue with groups not led by a Leader yet
        for(int a=0;a<groups.size();a++){
            Group g = groups.get(a);
            boolean yes = false;
            if(g.groupLeader == null){
                yes = true;
            }
            else if(g.groupLeader.name.equals("unassigned")){
                System.out.println("This fires");
                yes = true;
            }
            if(yes){
                builder.append("G:"+g.getSaveString()+linebreak);
            }
        }

        //Finish off with strategy
        for(int a=0;a<orders.size;a++){
            builder.append("O:");
            builder.append(orders.get(a).getSaveString());
            builder.append(linebreak);
        }

        builder.append("END_ARMY"+linebreak);
        return builder.toString();
    }

    public void loadArmy(ArmyComposition ac){
        unitTypes = ac.unitTypes;
        groups = ac.groups;
        leader = ac.leader;
        leaderList = ac.leaderList;
        orders = ac.orders;

        sprite = ac.sprite;
    }
}

package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import faltkullen.ArmyComposition;
import faltkullen.Leader;

import java.util.ArrayList;

/**
 * Created by Anton on 2015-08-24.
 */
public class OrganogramGroup extends FocusGroup {
    private float width, height;
    private Skin skin;
    private OrganogramUnder under;

    private Tree tree;
    private ArrayList<Node> nodes;
    private ArrayList<Leader> leaders;

    private ScrollPane scrollPane;

    public OrganogramGroup(Skin sk, float w, float h, OrganogramUnder u) {
        super();
        skin = sk;
        width = w;
        height = h;
        under = u;

        Image panel = new Image(skin.newDrawable("white", Color.DARK_GRAY));
        panel.setSize(width, height);
        addActor(panel);

        tree = new Tree(skin);
        tree.setPosition(0, 0);
        tree.setSize(width, height);
        nodes = new ArrayList<Node>();
        leaders = new ArrayList<Leader>();

        scrollPane = new ScrollPane(tree, skin);
        scrollPane.setFadeScrollBars(false);
        scrollPane.setSize(width, height);
        addActor(scrollPane);
    }

    public void loadCommand(Leader l, ArmyComposition ac){
        tree.clearChildren();
        nodes.clear();
        leaders.clear();
        Node n = new Node(createButton(l.name));
        nodes.add(n);
        leaders.add(l);
        ArrayList<Leader> queue = new ArrayList<Leader>();
        int num = 0;
        queue.add(l);
        while(queue.size() > 0){
            Leader current = queue.remove(0);
            Node currentNode = nodes.get(num);
            ArrayList<Leader> commands = current.commands;
            for(int a=0;a<commands.size();a++){
                Leader sub = commands.get(a);
                createNode(currentNode, sub);
                queue.add(sub);
            }
            num++;
        }
        System.out.println("num = " + num);
        tree.add(n);
        under.leaderSelected(l, n);
        current = ac;
        under.setArmy(ac);
    }

    private Node draggingNode;
    private Leader draggingLeader;
    private Node overNode;
    private Leader overLeader;

    public TextButton createButton(String name){
        final boolean debug = true;
        TextButton t = new TextButton(name, skin);
        final int f = nodes.size();
        final ClickListener cl = new TreeClickListener(this, under, f);
        t.addListener(cl);
        return t;
    }

    public void createNode(Node n, Leader l){
        Node newNode = new Node(createButton(l.name));
        n.add(newNode);
        nodes.add(newNode);
        leaders.add(l);
    }

    public void removeNode(Node n, Leader l){
        //Move all subnodes to parent
        Array<Node> subnodes = n.getChildren();
        Node parent = n.getParent();
        n.remove();
        int nodesTotal = subnodes.size;
        for(int a=0;a<nodesTotal;a++){
            Node node = subnodes.get(a);
            parent.add(node);
        }

        //Move all subleaders to commander
        ArrayList<Leader> subleaders = l.getCommand();
        Leader commander = l.commandedBy;
        commander.commands.remove(l);
        for(Leader leader : subleaders){
            commander.commands.add(leader);
            leader.commandedBy = commander;
        }

        under.leaderSelected(commander, parent);
    }

    public Leader getLeader(int l){
        return leaders.get(l);
    }

    public Node getNode(int n){
        return nodes.get(n);
    }

    public void setOverNode(int n){
        overNode = nodes.get(n);
    }

    public void setOverLeader(int l){
        overLeader = leaders.get(l);
    }

    public Node getOverNode(){
        return overNode;
    }

    public Leader getOverLeader(){
        return overLeader;
    }

    public void nullifyOver(){
        overNode = null;
        overLeader = null;
    }
}

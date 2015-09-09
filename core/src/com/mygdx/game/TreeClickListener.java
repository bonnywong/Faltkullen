package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

/**
 * Created by Anton on 2015-08-30.
 */
public class TreeClickListener extends ClickListener {
    private final static boolean debug = false;

    private OrganogramGroup group;
    private OrganogramUnder under;
    private int number;

    private boolean isDragged = false;

    public TreeClickListener(OrganogramGroup g, OrganogramUnder u, int num){
        super();
        group = g;
        under = u;
        number = num;
    }

    @Override
    public void clicked(InputEvent event, float x, float y){
        super.clicked(event, x, y);
        if(debug) {
            System.out.println("Clicked");
        }
        under.leaderSelected(group.getLeader(number), group.getNode(number));
    }

    @Override
    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor){
        super.enter(event, x, y, pointer, fromActor);
        if(debug) {
            System.out.println("Enter " + number);
        }
        group.setOverNode(number);
        group.setOverLeader(number);
    }

    @Override
    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor){
        super.enter(event, x, y, pointer, toActor);
        if(debug) {
            System.out.println("Exit " + number);
        }
    }

    @Override
    public void touchUp(InputEvent event, float x, float y, int pointer, int button){
        super.touchUp(event, x, y, pointer, button);
        if(debug) {
            System.out.println("Touch Up " + number);
        }
        if(isDragged) {
            if (debug) {
                System.out.println("Something was dragged");
            }
            Node overNode = group.getOverNode();
            Node thisNode = group.getNode(number);
            if(overNode != thisNode){
                if(debug){
                    System.out.println("We dragged node number " + number + " to another one. Do shit");
                }
                thisNode.remove();
                overNode.add(thisNode);
                group.getOverLeader().addCommand(group.getLeader(number));
            }
            group.nullifyOver();
        }
    }

    @Override
    public void touchDragged(InputEvent event, float x, float y, int pointer){
        super.touchDragged(event, x, y, pointer);
        isDragged = true;
    }
}

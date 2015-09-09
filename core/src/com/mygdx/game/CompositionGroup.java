package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import faltkullen.ArmyComposition;
import faltkullen.*;

import java.util.ArrayList;

/**
 * Created by Anton on 2015-08-24.
 */
public class CompositionGroup extends FocusGroup {
    private Skin skin;
    private float width, height;
    private ArrayList<TextField> names;
    private ArrayList<SelectBox<UnitType>> types;
    private ArrayList<TextField> amounts;
    private ArrayList<SelectBox<Leader>> leaders;

    private float[] sizes;
    private float[] xPositions;

    private Leader unassigned;

    public CompositionGroup(Skin sk, float w, float h) {
        super();
        skin = sk;
        width = w;
        height = h;
        sizes = new float[]{100, 75, 140, 85};
        xPositions = new float[sizes.length];
        float currentX = 5;
        xPositions[0] = currentX;
        for(int a=1;a<sizes.length;a++){
            xPositions[a] = xPositions[a-1] + sizes[a-1] + 10;
        }

        Image panel = new Image(skin.newDrawable("white", Color.DARK_GRAY));
        panel.setSize(width, height);
        addActor(panel);

        TextButton tb = new TextButton("New Unit", skin, "button");
        tb.setPosition(5, height - 30);
        tb.setSize(75, 25);
        tb.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                createNewGroup();
            }
        });
        addActor(tb);

        String[] names = new String[]{"Group Name", "Type", "Amount of Units", "Leader"};
        for(int a=0;a<names.length;a++){
            Label l = new Label(names[a], skin);
            l.setPosition(xPositions[a], height - 65);
            l.setSize(sizes[a], 25);
            addActor(l);
        }

        this.names = new ArrayList<TextField>();
        types = new ArrayList<SelectBox<UnitType>>();
        amounts = new ArrayList<TextField>();
        leaders = new ArrayList<SelectBox<Leader>>();

        unassigned = new Leader(0.0, false);
        unassigned.name = "Unassigned";
    }

    public void loadComposition(ArmyComposition ac){
        for(int a=0;a<names.size();a++){
            names.get(a).remove();
            types.get(a).remove();
            amounts.get(a).remove();
            leaders.get(a).remove();
        }
        names.clear();
        types.clear();
        amounts.clear();
        leaders.clear();
        Array<Leader> leaderArray = new Array<Leader>();
        leaderArray.add(unassigned);
        current = ac;
        leaderArray.addAll(ac.leaderList);
        for(int a=0;a<ac.groups.size();a++){
            final Group g = ac.groups.get(a);
            createFields(g, leaderArray);
        }
    }

    public void createNewGroup(){
        Group g = new Group(10);
        g.name = "New Group";
        Array<Leader> leaderArray = new Array<Leader>();
        leaderArray.add(unassigned);
        leaderArray.addAll(current.leaderList);
        createFields(g, leaderArray);
        current.groups.add(g);
    }

    public void createFields(final Group g, Array<Leader> leaderArray){
        float h = height - 90 - 30*(names.size()*1);
        final TextField tf = new TextField(g.name, skin);
        tf.setPosition(xPositions[0], h);
        tf.setSize(sizes[0], 25);
        tf.setAlignment(1);
        tf.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor){
                g.name = tf.getText();
            }
        });
        addActor(tf);
        names.add(tf);

        final SelectBox<UnitType> sb = new SelectBox(skin);
        sb.setPosition(xPositions[1], h);
        sb.setSize(sizes[1], 25);
        sb.setItems(current.unitTypes);
        sb.setSelectedIndex(0);
        sb.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor) {
                UnitType selected = sb.getSelected();
                g.unitType = selected;
            }
        });
        addActor(sb);
        types.add(sb);

        final TextField tf2 = new TextField(""+g.initialSize, skin);
        tf2.setPosition(xPositions[2], h);
        tf2.setSize(sizes[2], 25);
        tf2.setAlignment(1);
        tf2.addListener(new ChangeListener() {
            public void changed(ChangeEvent event, Actor actor){
                try{
                    int s = Integer.parseInt(tf2.getText());
                    if(s < 1){
                        s = 1;
                    }
                    g.initialSize = s;
                }
                catch(NumberFormatException e){
                    g.initialSize = 1;
                }
            }
        });
        addActor(tf2);
        amounts.add(tf2);

        final SelectBox<Leader> sb2 = new SelectBox<Leader>(skin);
        sb2.setPosition(xPositions[3], h);
        sb2.setSize(sizes[3], 25);
        sb2.setItems(leaderArray);
        if(g.groupLeader != null){
            sb2.setSelected(g.groupLeader);
        }
        else {
            sb2.setSelected(unassigned);
        }
        sb2.addListener(new ChangeListener(){
            public void changed(ChangeEvent event, Actor actor){
                Leader selected = sb2.getSelected();
                if(selected == unassigned){
                    if(g.groupLeader != null) {
                        g.groupLeader.inGroup = null;
                    }
                    g.groupLeader = unassigned;
                }
                else {
                    for (int a = 0; a < current.groups.size(); a++) {
                        Group gr = current.groups.get(a);
                        if (gr.groupLeader == selected) {
                            gr.groupLeader = null;
                            leaders.get(a).setSelected(unassigned);
                        }
                    }
                    g.groupLeader = selected;
                    selected.inGroup = g;
                }
            }
        });
        addActor(sb2);
        leaders.add(sb2);

        g.unitType = sb.getSelected();
        g.name = tf.getText();
    }
}

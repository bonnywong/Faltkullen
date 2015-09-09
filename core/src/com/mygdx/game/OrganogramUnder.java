package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Tree.Node;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import faltkullen.ArmyComposition;
import faltkullen.Leader;

/**
 * Created by Anton on 2015-08-24.
 */
public class OrganogramUnder extends FocusGroup {
    private Label selection;
    private Skin skin;

    private Leader selectedLeader;
    private Node selectedNode;
    private TextField nameField;
    private TextField comDelayUp;
    private TextField comDelayDown;
    private TextField retreatAt;
    private TextButton aggro;

    private TextButton addButton;
    private TextButton removeButton;

    public OrganogramGroup organogram;

    public OrganogramUnder(Skin sk) {
        super();
        skin = sk;
        Label l = new Label("Currently Selected : Nothing", skin, "black");
        l.setPosition(0, 175);
        l.setSize(200, 25);
        addActor(l);
        selection = l;

        l = new Label("Name", skin, "black");
        l.setPosition(0, 150);
        l.setSize(100, 25);
        addActor(l);

        l = new Label("Delay Up", skin, "black");
        l.setPosition(105, 150);
        l.setSize(100, 25);
        addActor(l);

        l = new Label("Delay Down", skin, "black");
        l.setPosition(210, 150);
        l.setSize(100, 25);
        addActor(l);

        l = new Label("Retreat Threshold (Entropy)", skin, "black");
        l.setPosition(315, 150);
        l.setSize(175, 25);
        addActor(l);

        l = new Label("Aggressive", skin, "black");
        l.setPosition(495, 150);
        l.setSize(100, 25);
        addActor(l);

        TextField tf = new TextField("", skin);
        tf.setPosition(0, 125);
        tf.setSize(100, 25);
        tf.setAlignment(1);
        tf.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (selectedLeader != null) {
                    selectedLeader.name = nameField.getText();
                    TextButton tb = (TextButton) selectedNode.getActor();
                    if (tb != null) {
                        tb.setText(nameField.getText());
                    }
                    selection.setText("Currently Selected : " + nameField.getText());
                }
            }
        });
        addActor(tf);
        nameField = tf;

        tf = new TextField("5", skin);
        tf.setPosition(105, 125);
        tf.setSize(100, 25);
        tf.setAlignment(1);
        tf.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(selectedLeader != null){
                    try{
                        int up = Integer.parseInt(comDelayUp.getText());
                        selectedLeader.communicationDelayUp = up;
                    }
                    catch(NumberFormatException e){

                    }
                }
            }
        });
        addActor(tf);
        comDelayUp = tf;

        tf = new TextField("3", skin);
        tf.setPosition(210, 125);
        tf.setSize(100, 25);
        tf.setAlignment(1);
        tf.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(selectedLeader != null){
                    try{
                        int down = Integer.parseInt(comDelayDown.getText());
                        selectedLeader.communicationDelayDown = down;
                    }
                    catch(NumberFormatException e){

                    }
                }
            }
        });
        addActor(tf);
        comDelayDown = tf;

        tf = new TextField("3.0", skin);
        tf.setPosition(315, 125);
        tf.setSize(175, 25);
        tf.setAlignment(1);
        tf.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(selectedLeader != null){
                    try{
                        double ret = Double.parseDouble(retreatAt.getText());
                        selectedLeader.setRetreatAt(ret);
                    }
                    catch(NumberFormatException e){

                    }
                }
            }
        });
        addActor(tf);
        retreatAt = tf;

        TextButton tb = new TextButton("False", skin, "button");
        tb.setPosition(495, 125);
        tb.setSize(100, 25);
        tb.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(selectedLeader != null){
                    selectedLeader.isAggressive = !selectedLeader.isAggressive;
                    if(selectedLeader.isAggressive){
                        aggro.setText("True");
                    }
                    else{
                        aggro.setText("False");
                    }
                }
            }
        });
        addActor(tb);
        aggro = tb;

        addButton = new TextButton("Add Leader", skin, "button");
        addButton.setPosition(15, 50);
        addButton.setSize(85, 25);
        addButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(organogram != null && selectedNode != null){
                    Leader l = new Leader(0, false);
                    selectedLeader.commands.add(l);
                    l.commandedBy = selectedLeader;
                    organogram.createNode(selectedNode, l);
                    current.leaderList.add(l);
                }
            }
        });
        addActor(addButton);

        removeButton = new TextButton("Remove this Leader", skin, "button");
        removeButton.setPosition(105, 50);
        removeButton.setSize(145, 25);
        removeButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                System.out.println("Removal begun");
                if(organogram != null && selectedNode != null){
                    organogram.removeNode(selectedNode, selectedLeader);
                    current.leaderList.removeValue(selectedLeader, true);
                }
            }
        });
    }

    public void leaderSelected(Leader l, Node n) {
        selection.setText("Currently Selected : " + l.name);
        selectedLeader = l;
        selectedNode = n;
        nameField.setText(l.name);

        if(l.commandedBy == null){
            removeButton.remove();
        }
        else if(removeButton.getParent() != this){
            addActor(removeButton);
        }

        comDelayDown.setText(""+l.communicationDelayDown);
        comDelayUp.setText(""+l.communicationDelayUp);
        retreatAt.setText(""+l.getRetreatAt());
        if(l.isAggressive){
            aggro.setText("True");
        }
        else{
            aggro.setText("False");
        }
    }

    public void setArmy(ArmyComposition ac){
        current = ac;
    }
}

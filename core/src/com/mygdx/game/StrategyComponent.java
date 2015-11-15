package com.mygdx.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import faltkullen.*;

/**
 * Created by Anton on 2015-08-26.
 */
public class StrategyComponent extends WidgetGroup {
    public int number;
    private Leader leader;
    private Goal order;
    private SelectBox orderType;
    private SelectBox<Leader> leaderSelection;
    private TextField posX;
    private TextField posY;
    private TextButton place;
    private UIOrder onMap;
    private SelectBox follows;
    private StrategyComponent following;

    private Label numberLabel;

    private boolean placing = false;

    private Skin skin;

    private final static Texture takePositionTexture = new Texture("arrow.png");

    public StrategyComponent(String str, Skin sk, Array<Leader> leaders, Array<Goal> orderTypes, Array canFollow){
        super();
        setupComponent(sk, leaders, orderTypes, canFollow);

        //O.0/Number 4/1000/1000
        String[] split = str.split("/");
        orderType.setSelectedIndex(Integer.parseInt(split[0]));
        for(int a=0;a<leaders.size;a++){
            if(leaders.get(a).name.equals(split[1])){
                leaderSelection.setSelected(leaders.get(a));
            }
        }
        posX.setText(split[2]);
        posY.setText(split[3]);

        onMap.setPosition(Float.parseFloat(split[2]), Float.parseFloat(split[3]));
    }

    public StrategyComponent(Skin sk, Array<Leader> leaders, Array<Goal> orderTypes, Array canFollow){
        super();
        setupComponent(sk, leaders, orderTypes, canFollow);
    }

    private void setupComponent(Skin sk, Array<Leader> leaders, Array<Goal> orderTypes, Array canFollow){
        skin = sk;
        setSize(800, 25);

        numberLabel = new Label("0", skin, "black");
        numberLabel.setSize(20, 25);
        addActor(numberLabel);

        orderType = new SelectBox(skin);
        orderType.setSize(100, 25);
        orderType.setPosition(20, 0);
        orderType.setItems(orderTypes);
        orderType.setSelectedIndex(0);
        orderType.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //resetOrder();
            }
        });
        addActor(orderType);

        leaderSelection = new SelectBox<Leader>(skin);
        leaderSelection.setSize(100, 25);
        leaderSelection.setPosition(125, 0);
        leaderSelection.setItems(leaders);
        leaderSelection.setSelectedIndex(0);
        leaderSelection.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                //System.out.println("Selected a new Leader for an Order");
                onMap.setLeader(leaderSelection.getSelected());
            }
        });
        /*
        leaderSelection.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //resetOrder();
            }
        });
        */
        addActor(leaderSelection);

        posX = new TextField("1000", skin);
        posX.setSize(100, 25);
        posX.setPosition(230, 0);
        posX.setAlignment(1);
        addActor(posX);

        posY = new TextField("1000", skin);
        posY.setSize(100, 25);
        posY.setPosition(335, 0);
        posY.setAlignment(1);
        addActor(posY);

        ChangeListener listen = new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor){
                float x = 0, y = 0;
                try{
                    x = Float.parseFloat(posX.getText());
                }
                catch(NumberFormatException e){
                    x = 0;
                }
                try{
                    y = Float.parseFloat(posY.getText());
                }
                catch(NumberFormatException e){
                    y = 0;
                }
                onMap.setPosition(x, y);
            }
        };
        posX.addListener(listen);
        posY.addListener(listen);

        place = new TextButton("Place", skin, "button");
        place.setSize(50, 25);
        place.setPosition(455, 0);
        place.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(place.isChecked()){
                    place.setText("  X  ");
                    placing = true;
                }
                else{
                    place.setText("Place");
                    placing = false;
                }
            }
        });
        addActor(place);

        onMap = new UIOrder(new Sprite(takePositionTexture), new Vector3(1000, 1000, 0));
        onMap.setLeader(leaderSelection.getSelected());

        follows = new SelectBox(skin);
        follows.setSize(100, 25);
        follows.setPosition(515, 0);
        if(canFollow != null) {
            follows.setItems(canFollow);
            follows.setSelectedIndex(0);
        }
        follows.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if(follows.getSelected() instanceof StrategyComponent){
                    following = (StrategyComponent)follows.getSelected();
                }
                if(onMap == null){
                    System.out.println("changed : onMap is NULL");
                }
                else if(following == null){
                    System.out.println("changed : following is NULL");
                }
                onMap.setOriginOrder(following.getMapMarker());
            }
        });
        addActor(follows);

        /*
        orderType = new SelectBox(skin);
        orderType.setSize(100, 25);
        orderType.setPosition(10, 0);
        orderType.setItems(orderTypes);
        orderType.setSelectedIndex(0);
        orderType.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                //resetOrder();
            }
        });
        addActor(orderType);
        */
    }

    public void drawOrder(SpriteBatch batch){
        onMap.draw(batch);
    }

    public void update(Array canFollow){
        numberLabel.setText(""+number);
        onMap.setLeader(leaderSelection.getSelected());
        follows.setItems(canFollow);
        if(following != null){
            follows.setSelected(following);
        }
    }

    public void mapClick(float x, float y){
        if(placing){
            onMap.setPosition(x, y);
            posX.setText("" + x);
            posY.setText(""+y);
            resetPlacing();
        }
    }

    public void resetPlacing(){
        place.setText("Place");
        placing = false;
        place.setChecked(false);
    }

    public Position getPosition(){
        Vector3 v = onMap.getPosition();
        return new Position(v.x, v.y);
    }

    public UIOrder getMapMarker(){
        return onMap;
    }

    public synchronized LeaderCommunication getOrder(){
        int type = orderType.getSelectedIndex();
        if(type==0){
            //Its TakePosition
            TakePosition tp = new TakePosition(getPosition(), 5.0);
            LeaderCommunication lc = new LeaderCommunication(tp, leaderSelection.getSelected());
            return lc;
        }
        else return null;
    }

    public String getSaveString(){
        return ""+orderType.getSelectedIndex()+"/"+leaderSelection.getSelected().name+"/"+posX.getText()+"/"+posY.getText();
    }

    public String toString(){
        return "" + number + ". " + orderType.getSelected().toString();
        /*
        if(order != null) {
            return "" + number + ". " + order.toString();
        }
        else{
            return "" + number + ". Undefined";
        }
        */
    }
}

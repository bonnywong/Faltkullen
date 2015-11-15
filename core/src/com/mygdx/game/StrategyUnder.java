package com.mygdx.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.utils.Array;
import faltkullen.*;

import java.util.ArrayList;

/**
 * Created by Anton on 2015-08-26.
 */
public class StrategyUnder extends FocusGroup {
    private Label selection;
    private Skin skin;

    private Leader selectedLeader;
    private Tree.Node selectedNode;
    private TextField nameField;
    private TextButton addButton;
    private TextButton removeButton;

    private Array<Goal> orderTypes;
    private ArrayList<StrategyComponent> orders;

    private float listSize;
    private WidgetGroup list;
    private ScrollPane scrollPane;
    private WidgetGroup scrollHolder;

    private Array canFollow;

    public StrategyUnder(Skin sk) {
        super();
        skin = sk;

        orders = new ArrayList<StrategyComponent>();
        orderTypes = new Array<Goal>();
        orderTypes.add(new TakePosition(new Position(0, 0), 10));
        orderTypes.add(new RetreatTo(new Position(0, 0), 10));

        canFollow = new Array();
        canFollow.add("Nothing");

        addButton = new TextButton("Create New Order", skin, "button");
        addButton.setPosition(15, 160);
        addButton.setSize(125, 25);
        addButton.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                createNewOrder();
            }
        });
        addActor(addButton);

        Label l = new Label("Order", skin, "black");
        l.setPosition(35, 130);
        addActor(l);

        l = new Label("Leader", skin, "black");
        l.setPosition(140, 130);
        addActor(l);

        l = new Label("X Coordinate", skin, "black");
        l.setPosition(245, 130);
        addActor(l);

        l = new Label("Y Coordinate", skin, "black");
        l.setPosition(350, 130);
        addActor(l);

        l = new Label("Place on Map", skin, "black");
        l.setPosition(450, 130);
        addActor(l);

        l = new Label("Follows Order", skin, "black");
        l.setPosition(550, 130);
        addActor(l);

        list = new WidgetGroup(){
            @Override
            public float getPrefHeight(){
                return listSize;
            }
        };
        scrollPane = new ScrollPane(list, skin);
        scrollPane.setSize(650, 130);
        scrollPane.setFadeScrollBars(false);
        scrollHolder = new WidgetGroup();
        scrollHolder.setPosition(0, 0);
        scrollHolder.addActor(scrollPane);
        addActor(scrollHolder);
    }

    public void loadArmy(ArmyComposition ac){
        current = ac;
        for(int a=0;a<orders.size();a++){
            StrategyComponent sc = orders.get(a);
            sc.resetPlacing();
            sc.remove();
        }
        orders.clear();
        canFollow.clear();
        canFollow.add("Nothing");
        listSize = 100;
        list.setHeight(100);
        System.out.println("LOADING ARMY INTO STRATEGY");
        System.out.println("ac.orders.size = " + ac.orders.size);
        for(int a=0;a<ac.orders.size;a++){
            StrategyComponent sc = ac.orders.get(a);
            canFollow.add(sc);
        }
        for(int a=0;a<ac.orders.size;a++){
            StrategyComponent sc = ac.orders.get(a);
            sc.setPosition(15, 100 + 30 * (a));
            orders.add(sc);
            list.addActor(sc);
            sc.number = a+1;
            sc.update(canFollow);
        }
        listSize = 100 + ac.orders.size * 30;
        list.setHeight(listSize);
    }

    public void createNewOrder(){
        //Just create a new TakePosition order and modify the UI to allow the user to modify it
        StrategyComponent sc = new StrategyComponent(skin, current.leaderList, orderTypes, canFollow);
        sc.setPosition(15, 100 + 30 * (orders.size()));
        orders.add(sc);
        list.addActor(sc);
        current.orders.add(sc);
        sc.number = orders.size();
        listSize+=30;
        list.setHeight(listSize);
        scrollPane.setScrollY(0);
        canFollow.add(sc);
        for(int a=0;a<orders.size();a++){
            orders.get(a).update(canFollow);
        }
    }

    public void drawOrders(SpriteBatch batch){
        for(int a=0;a<orders.size();a++){
            orders.get(a).drawOrder(batch);
        }
    }

    @Override
    public void mapClick(float x, float y){
        for(StrategyComponent sc : orders){
            sc.mapClick(x, y);
        }
    }
}

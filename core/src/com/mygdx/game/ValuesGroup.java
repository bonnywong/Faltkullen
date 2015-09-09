package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextField;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import faltkullen.ArmyComposition;
import faltkullen.Attribute;
import faltkullen.Main;
import faltkullen.Settings;

/**
 * Created by Anton on 2015-08-23.
 */
public class ValuesGroup extends FocusGroup {
    private Label[] labels;
    private TextField[][] textFields;
    private CheckBox[] boxes;

    private String[] attributeNames;
    private String[] minimumValues;
    private String[] baseValues;
    private String[] maximumValues;
    private String[] evolutionCosts;
    private boolean[] checked;

    private SelectBox<UnitType> unitTypeSelection;
    private TextField unitTypeName;
    private SelectBox<BaseUnitType> baseTypeSelection;
    private BaseUnitType[] baseTypes;
    private TextButton addButton;

    private Skin skin;
    private float width, height;

    private UnitType selected;

    public ValuesGroup(Skin sk, float w, float h){
        super();
        skin = sk;
        width = w;
        height = h;

        Image panel = new Image(skin.newDrawable("white", Color.DARK_GRAY));
        panel.setSize(width, height);
        addActor(panel);

        String[] labelStrings = new String[]{"Minumum Value", "Base Value", "Maximum Value", "Evolutionary Cost"};
        for(int a=0;a<labelStrings.length;a++){
            Label l = new Label(labelStrings[a], skin);
            l.setPosition(25 + 125 * (a + 1), height - 75);
            addActor(l);
        }

        UnitType empty = new UnitType("", 0, null);

        SelectBox<UnitType> sb = new SelectBox<UnitType>(skin);
        sb.setPosition(15, height - 25);
        sb.setSize(200, 20);
        sb.setItems(empty);
        sb.addListener(new ChangeListener(){
            public void changed(ChangeEvent event, Actor actor){
                loadUnitType(unitTypeSelection.getSelected());
            }
        });
        addActor(sb);
        unitTypeSelection = sb;

        TextField tf = new TextField("Empty", skin);
        tf.setPosition(15, height - 50);
        tf.setSize(200, 20);
        tf.setAlignment(1);
        tf.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeListener.ChangeEvent event, Actor actor) {
                if(selected != null){
                    selected.name = unitTypeName.getText();
                }
            }
        });
        addActor(tf);
        unitTypeName = tf;

        baseTypes = new BaseUnitType[]{new BaseUnitType(0),new BaseUnitType(1),new BaseUnitType(2),new BaseUnitType(3)};
        SelectBox<BaseUnitType> sb2 = new SelectBox<BaseUnitType>(skin);
        sb2.setPosition(220, height - 50);
        sb2.setSize(100, 20);
        sb2.setItems(baseTypes);
        sb2.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(!isLoadingUnit) {
                    transformUnitType(baseTypeSelection.getSelected());
                }
            }
        });
        addActor(sb2);
        baseTypeSelection = sb2;

        addButton = new TextButton("Add new Unit Type", skin, "button");
        addButton.setPosition(250, height - 25);
        addButton.setSize(150, 20);
        addButton.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                createNewUnitType();
            }
        });
        addActor(addButton);
    }

    public void loadArmy(ArmyComposition ac){
        current = ac;
        unitTypeSelection.setItems(ac.unitTypes);
        UnitType first = current.unitTypes.get(0);
        loadUnitType(first);
    }

    public void generateComponents(){
        if(labels != null){
            if(labels.length > 0){
                for(int a=0;a<labels.length;a++){
                    labels[a].remove();
                    for(int b=0;b<4;b++){
                        textFields[a][b].remove();
                    }
                    boxes[a].remove();
                }
            }
        }
        labels = new Label[attributeNames.length];
        textFields = new TextField[labels.length][4];
        boxes = new CheckBox[attributeNames.length];
        for(int a=0;a<labels.length;a++) {
            float h = height - 100 - 27 * a;

            Label l = new Label(attributeNames[a], skin);
            l.setPosition(5, h);
            addActor(l);
            labels[a] = l;

            CheckBox c = new CheckBox("", skin);
            c.setPosition(130, h);
            addActor(c);
            c.setChecked(checked[a]);
            boxes[a] = c;

            String[] values = new String[]{minimumValues[a], baseValues[a], maximumValues[a], evolutionCosts[a]};
            for (int b = 0; b < 4; b++) {

                TextField t = new TextField(""+values[b], skin);
                t.setPosition(25 + 125 * (b + 1), h);
                t.setSize(100, 25);
                t.setAlignment(1);
                addActor(t);
                textFields[a][b] = t;
            }
        }
        System.out.println("Textfields.length = " + textFields.length);
    }

    public Settings getSettings(Main m, String from){
        System.out.println("From : " + from);
        System.out.println("Getting settings");
        Settings s = new Settings(m);
        for(int a=0;a<textFields.length;a++){
            int[] values = new int[4];
            System.out.println(minimumValues[a]);
            /*
            String one = minimumValues[a];
            String two = baseValues[a];
            String three = maximumValues[a];
            String four = evolutionCosts[a];
            */
            String[] inCaseOfError = new String[]{minimumValues[a], baseValues[a], maximumValues[a], evolutionCosts[a]};
            for(int b=0;b<4;b++){
                try{
                    values[b] = Integer.parseInt(textFields[a][b].getText());
                }
                catch(NumberFormatException e){
                    values[b] = Integer.parseInt(inCaseOfError[b]);
                    textFields[a][b].setText(""+inCaseOfError[b]);
                }
            }
            //Name, base, min, max, cost, Settings
            Attribute att = new Attribute(attributeNames[a],
                    values[1],
                    values[0],
                    values[2],
                    values[3],
                    s);
            s.attributes.add(att);
            if(boxes[a].isChecked()){
                System.out.println("THIS IS ACTUALLY RUN, AT SOME POINT");
                att.allowsChange = true;
                s.changeableAttributes.add(att);
            }
        }
        s.maxCost = s.getTotalCost();
        return s;
    }

    public void loadSoldierValues(){
        attributeNames = new String[]{"Weapon Damage",
                "Weapon Accuracy",
                "Weapon Range",
                "Protection",
                "Sensor Range",
                "Sensor Interval",
                "Soldier Accuracy",
                "Soldier Morale",
                "Soldier Movespeed"};
        minimumValues = new String[]{"1", "90", "10", "5", "5", "1", "1", "70", "1"};
        baseValues = new String[]{"75", "95", "60", "10", "100", "10", "25", "100", "5"};
        maximumValues = new String[]{"115", "100", "100", "15", "300", "20", "100", "100", "10"};
        evolutionCosts = new String[]{"100", "100", "80", "100", "225", "-75", "125", "25", "75"};
        checked = new boolean[]{false, false, false, false, false, false, false, false, false};
    }

    public void loadTankValues(){
        attributeNames = new String[]{"Weapon Damage", "Weapon Range", "Attack Interval (s)", "Splash Radius", "Protection", "Velocity", "Fuel per Kilometer", "Fuel Capacity", "Sensor Range", "Sensor Interval"};
        minimumValues = new String[]{"150", "100", "1", "2", "50", "25", "1", "1000", "50", "1"};
        baseValues = new String[]{"250", "200", "5", "5", "150", "40", "4", "1700", "100", "5"};
        maximumValues = new String[]{"350", "300", "10", "10", "250", "50", "10", "2500", "200", "10"};
        evolutionCosts = new String[]{"100", "100", "-300", "100", "225", "75", "-75", "75", "225", "-75"};
        checked = new boolean[]{false, false, false, false, false, false, false, false, false, false};
    }

    public void loadValues(Settings s){
        int length = s.attributes.size();
        attributeNames = new String[length];
        baseValues = new String[length];
        minimumValues = new String[length];
        maximumValues = new String[length];
        evolutionCosts = new String[length];
        checked = new boolean[length];
        for(int a=0;a<length;a++){
            Attribute att = s.attributes.get(a);
            attributeNames[a] = att.type;
            baseValues[a] = ""+att.base;
            minimumValues[a] = ""+att.minimum;
            maximumValues[a] = ""+att.maximum;
            evolutionCosts[a] = ""+att.cost;
            checked[a] = att.allowsChange;
        }
    }

    private boolean isLoadingUnit = false;

    public void loadUnitType(UnitType ut){
        isLoadingUnit = true;
        boolean debug = true;
        if(selected != null){
            selected.settings = getSettings(selected.settings.main, "loadUnitType, loading " + ut.name);
        }
        selected = ut;
        if(debug) {
            System.out.println("Selecting Unit Type");
        }
        unitTypeSelection.setSelected(ut);
        if(debug) {
            System.out.println("Loading Values");
        }
        loadValues(ut.settings);
        if(debug) {
            System.out.println("Setting Unit Type Name");
        }
        unitTypeName.setText(ut.name);
        if(debug) {
            System.out.println("Generating Components");
        }
        if(debug) {
            System.out.println("Selecting Base Type");
        }
        baseTypeSelection.setSelectedIndex(ut.type.type);
        generateComponents();
        isLoadingUnit = false;
    }

    public void createNewUnitType(){
        selected.settings = getSettings(selected.settings.main, "createNewUnitType : used to save current settings");
        UnitType last = current.createBaseSoldierUnitType(selected.settings.main);
        last.name = "New Unit Type";
        unitTypeSelection.setItems(current.unitTypes);
        loadUnitType(last);
        generateComponents();
    }

    public void transformUnitType(BaseUnitType but){
        if(isLoadingUnit){
            return;
        }
        if(but.type == 0){
            loadSoldierValues();
        }
        else if(but.type == 1){
            loadTankValues();
        }
        generateComponents();
        selected.type.type = but.type;
        selected.settings = getSettings(selected.settings.main, "transformUnitType : We are selecting changing a UnitTypes base Type");
    }

    public void save(Main main){
        selected.settings = getSettings(main, "save : We are changing focusGroup or starting the Simulation");
    }
}

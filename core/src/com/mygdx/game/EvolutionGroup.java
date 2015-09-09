package com.mygdx.game;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.ui.TextField.TextFieldListener;

/**
 * Created by Anton on 2015-08-24.
 */
public class EvolutionGroup extends FocusGroup {
    private Skin skin;
    private float width, height;
    private String[] baseValues;
    private int[] minimumValues;
    private TextField[] textFields;
    private CheckBox[] boxes;
    private Label runTypeLabel;
    private SelectBox runType;
    private Label stepLabel;
    private TextField stepField;
    private int totalInfo;

    public EvolutionGroup(Skin sk, float w, float h) {
        super();
        skin = sk;
        width = w;
        height = h;

        Image panel = new Image(skin.newDrawable("white", Color.DARK_GRAY));
        panel.setSize(width, height);
        addActor(panel);
        baseValues = new String[]{"10", "5", "100", "100"};
        minimumValues = new int[]{1, 1, 1, 1};
        String[] labelStrings = new String[]{"Certainty:", "Radicality:", "Population Size:", "Amount of Generations:", "Display Map during Simulations:", "No Generation Limit:"};
        totalInfo = labelStrings.length;

        textFields = new TextField[baseValues.length];
        boxes = new CheckBox[labelStrings.length - baseValues.length];
        for (int a = 0; a < labelStrings.length; a++) {
            Label l = new Label(labelStrings[a], skin);
            l.setPosition(5, height - 25*(a+1));
            addActor(l);

            if(a < textFields.length) {
                TextField t = new TextField(""+baseValues[a], skin);
                t.setPosition(230, height - 25 * (a + 1));
                t.setAlignment(1);
                addActor(t);
                textFields[a] = t;
            }
            else{
                final CheckBox t = new CheckBox("", skin);
                t.setPosition(230, height - 25 * (a + 1));
                addActor(t);
                boxes[a-textFields.length] = t;
            }
        }
        boxes[0].setChecked(true);

        runTypeLabel = new Label("Run Type (Display)", skin);
        runTypeLabel.setPosition(5, height - 25 * (labelStrings.length + 1));
        addActor(runTypeLabel);

        runType = new SelectBox<String>(skin);
        runType.setPosition(230, height - 25 * (labelStrings.length + 1));
        runType.setItems("Continous", "Stepwise");
        runType.setSize(100, 25);
        runType.addListener(new ChangeListener(){
            @Override
            public void changed(ChangeEvent event, Actor actor){
                if(runType.getSelectedIndex() == 1){
                    addActor(stepLabel);
                    addActor(stepField);
                }
                else{
                    stepField.remove();
                    stepLabel.remove();
                }
            }
        });
        addActor(runType);

        stepLabel = new Label("Step Length (Seconds)", skin);
        stepLabel.setPosition(5, height - 25 * (labelStrings.length + 2));

        stepField = new TextField("5", skin);
        stepField.setPosition(230, height - 25 * (labelStrings.length + 2));
        stepField.setAlignment(1);
    }

    public int[] getValues(){
        int[] ret = new int[totalInfo];
        for(int a=0;a<textFields.length;a++){
            try{
                ret[a] = Integer.parseInt(textFields[a].getText());
            }
            catch(Exception e){
                ret[a] = minimumValues[a];
                textFields[a].setText(""+minimumValues[a]);
            }
        }
        for(int a=0;a<boxes.length;a++){
            if(boxes[a].isChecked()){
                ret[a+textFields.length] = 1;
            }
            else{
                ret[a+textFields.length] = 0;
            }
        }
        return ret;
    }

    public boolean isStepwise(){
        return runType.getSelectedIndex() == 1;
    }

    public int getStepLength(){
        try{
            return Integer.parseInt(stepField.getText());
        }
        catch(NumberFormatException e){
            return 5;
        }
    }
}

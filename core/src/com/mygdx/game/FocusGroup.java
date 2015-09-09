package com.mygdx.game;

import com.badlogic.gdx.scenes.scene2d.ui.WidgetGroup;
import faltkullen.ArmyComposition;
import faltkullen.Main;

/**
 * Created by Anton on 2015-08-29.
 */
public class FocusGroup extends WidgetGroup {
    protected ArmyComposition current;

    public FocusGroup(){
        super();
    }

    public void mapClick(float x, float y){

    }

    //Used to make sure every change made in the Focus Group is saved when we either change Focus Group or when we start the Simulations
    public void save(Main m){

    }
}

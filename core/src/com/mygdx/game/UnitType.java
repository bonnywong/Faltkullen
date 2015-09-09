package com.mygdx.game;

import faltkullen.Group;
import faltkullen.Settings;
import faltkullen.Unit;

import java.util.ArrayList;

/**
 * Created by Anton on 2015-08-26.
 */
public class UnitType {
    public Settings settings;
    public String name;
    public BaseUnitType type;
    public Unit baseUnit;
    public int id = 0;

    public UnitType(String n, int b, Settings s){
        name = n;
        type = new BaseUnitType(b);
        settings = s;
    }

    public String toString(){
        return name;
    }
}

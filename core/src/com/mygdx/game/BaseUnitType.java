package com.mygdx.game;

import faltkullen.Settings;
import faltkullen.Soldier;
import faltkullen.Vehicle;
import faltkullen.Unit;

/**
 * Created by Anton on 2015-08-26.
 */
public class BaseUnitType {
    public int type;
    public BaseUnitType(int t){
        type = t;
    }

    public synchronized Unit getBaseUnit(Settings s){
        if(type==0){
            return new Soldier(s);
        }
        else if(type==1){
            return new Vehicle(s);
        }
        return null;
    }

    public String toString(){
        if(type==0){
            return "Troop";
        }
        else if(type==1){
            return "Tank";
        }
        else if(type==2){
            return "Aircraft";
        }
        else if(type==3){
            return "Drone";
        }
        else return "Error";
    }
}

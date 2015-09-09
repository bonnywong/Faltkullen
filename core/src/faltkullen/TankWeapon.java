package faltkullen;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Anton on 2015-09-02.
 */
public class TankWeapon extends Weapon {
    public int splashRadius = 5;

    private int cooldown = 0;

    public TankWeapon(Settings s){
        damage = s.getAttribute("Weapon Damage").current;
        range = s.getAttribute("Weapon Range").current;
        splashRadius = s.getAttribute("Splash Radius").current;
        explosive = true;
    }

    public TankWeapon(TankWeapon t){
        damage = t.damage;
        range = t.range;
        splashRadius = t.splashRadius;
        explosive = t.explosive;
    }

    @Override
    public boolean fireAt(Unit shooter, Unit target){
        //System.out.println("TankWeapon.fire");
        ArrayList<Unit> list = shooter.map.getUnitsWithinRange(target.getPosition(), splashRadius);
        //System.out.println("Target sector = " + target.containedIn.x + "/" + target.containedIn.y);
        for(int a=0;a<list.size();a++){
            Unit u = list.get(a);
            //System.out.println("Damage");
            u.hit(damage, this);
        }
        return true;
    }

    @Override
    public boolean movingFireAt(Unit shooter, Unit target){
        //System.out.println("TankWeapon.movingFireAt");
        Position p = target.getPosition();
        double angle = new Random().nextDouble() * 2.0;
        double dist = new Random().nextDouble() * 5.0;
        p.x = p.x + Math.cos(angle) * dist;
        p.y = p.y + Math.sin(angle) * dist;
        ArrayList<Unit> list = shooter.map.getUnitsWithinRange(p, splashRadius);
        for(int a=0;a<list.size();a++){
            Unit u = list.get(a);
            //System.out.println("Damage");
            u.hit(damage, this);
        }
        return true;
    }
}

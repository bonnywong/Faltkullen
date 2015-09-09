package faltkullen;

import java.util.Random;

public class Weapon {
    protected int damage;
    protected int accuracy;
    private int weight;
    protected int range;
    protected double cost;
    public boolean shootsBullets = false;
    public boolean explosive = false;

    public Weapon() {
    }

    public Weapon(int dmg, int acc) {
        damage = dmg;
        accuracy = acc;
    }

    public Weapon(Settings s) {
        damage = s.getAttribute("Weapon Damage").current;
        accuracy = s.getAttribute("Weapon Accuracy").current;
        range = s.getAttribute("Weapon Range").current;
        shootsBullets = true;
    }

    //Alternativ konstruktor.
    public Weapon(int dmg, int acc, int weight, double cost) {
        damage = dmg;
        accuracy = acc;
        this.weight = weight;
        this.cost = cost;
    }

    public Weapon(Weapon w){
        damage = w.getDamage();
        accuracy = w.getAccuracy();
        weight = w.getWeight();
        range = w.getRange();
        cost = w.getCost();
        shootsBullets = w.shootsBullets;
        explosive = w.explosive;
    }

    public boolean fireAt(Unit shooter, Unit target) {
        //System.out.println("A weapon has been fired");
        //H�mta ett slumpat nummer mellan 0 och 99
        //System.out.println("Weapon.fireAt");
        int randomNumber = new Random().nextInt(100);
        int shooterAccuracy = shooter.getAccuracy();
        //�r detta nummer l�gre �n accuracy?
        if (randomNumber < (accuracy * shooterAccuracy) / 100) {
            //Om ja, d� tar m�let skada
            //System.out.println("Seriously?");
            target.hit(damage, this);
            //System.out.println("Nothing in between?");
            return true;
        } else {
            //Om nej, d� har vi missat
            //System.out.println("Miss");
            return false;
        }
    }

    public boolean movingFireAt(Unit shooter, Unit target){
        //Hämta ett slumpat nummer mellan 0 och 99
        int randomNumber = new Random().nextInt(100);
        int shooterAccuracy = shooter.getAccuracy() / 4;
        //System.out.println("Weapon.movingFireAt");
        //Är detta nummer lägre än accuracy?
        if (randomNumber < (accuracy * shooterAccuracy) / 100) {
            //Om ja, då tar målet skada
            System.out.println("Hit");
            target.hit(damage, this);
            return true;
        } else {
            //Om nej, då har vi missat
            //System.out.println("Miss");
            return false;
        }
    }

    public int getDamage(){
        return damage;
    }

    public int getAccuracy(){
        return accuracy;
    }

    private int getWeight() {
        return weight;
    }

    public double getCost() {
        return cost;
    }

    public int getRange() {
        return range;
    }
}
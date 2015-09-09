package faltkullen;

public class Armor {
    public static final int TROOP = 0;
    public static final int TANK = 1;
    public static final int VEHICLE = 2;
    public static final int FUTURISTIC = 999; //Anv�nds endast som exempel

    private int type;
    private int protection;

    public Armor(int t) {
        type = t;
        switch(t) {
            case TROOP:
                protection = 10;
                break;
            case TANK:
                protection = 80;
                break;
            case VEHICLE:
                protection = 50;
                break;
            case FUTURISTIC:
                protection = 90;
                break;
        }
    }

    public Armor(int t, int p) {
        type = t;
        protection = p;
    }

    public Armor(Armor a){
        type = a.getType();
        protection = a.getProtection();
    }

    public int getType(){
        return type;
    }

    public int getProtection(){
        return protection;
    }

    public int getProtection(Weapon w) {
        if ((type == VEHICLE || type == TANK) && w.shootsBullets) {
            return 10000;
        } else return protection;
    }
}
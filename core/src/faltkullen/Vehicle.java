package faltkullen;

import java.util.ArrayList;

/**
 * A vehicle.
 */
public class Vehicle extends Unit {
    public int vehicleAccuracyBase;

    public Unit target;
    public Squad inSquad;
    public Weapon weapon;

    protected int attackInterval = 5;
    protected int cooldown = 0;

    public Vehicle(int dmg, int acc, int mor, int vehAccBase, int sensorRange) {
        weapon = new Weapon(dmg, acc);
        armor = new Armor(Armor.VEHICLE);
        morale = mor;
        this.vehicleAccuracyBase = vehAccBase;
    }

    public Vehicle(Settings s) {
        //attributes.get(0) Ã¤r DamageAttribute
        //attributes.get(1) Ã¤r AccuracyAttribute
        //attributes.get(2) Ã¤r ProtectionAttribute
        weapon = new TankWeapon(s);
        armor = new Armor(Armor.VEHICLE, s.getAttribute("Protection").current);
        sensorRange = s.getAttribute("Sensor Range").current;
        movespeed = s.getAttribute("Velocity").current;
        attackInterval = s.getAttribute("Attack Interval (s)").current;
    }

    public Vehicle(Vehicle sold){
        super(sold);
        weapon = new TankWeapon((TankWeapon)sold.weapon);
        armor = new Armor(sold.armor);
        sensorRange = sold.sensorRange;
        movespeed = sold.movespeed;
        attackInterval = sold.attackInterval;
    }

    public int getAccuracy() {
        return (morale * vehicleAccuracyBase) / 100;
    }

    public int effAcc(int vAccB, int mor) {
        int vEffAcc = vAccB - mor;
        if (vEffAcc < 10) {
            vEffAcc = 10;
        }
        return vAccB;
    }

    @Override
    public void update(){
        super.update();
        if(cooldown > 0){
            cooldown--;
        }
    }

    @Override
    public boolean fireAt(Unit target) {
        if(cooldown <= 0) {
            //System.out.println("VEHICLE ATTACKS");
            cooldown = attackInterval * 30;
            return weapon.fireAt(this, target);
        }
        else{
            return false;
        }
    }

    /*Finner avstånde mellan denna soldat och en fiendesoldat*/
    public int distanceToEnemy(Unit enemy) {
        double distance = getDistance((int) enemy.getX(), (int) enemy.getY());
        return (int) distance; //Kastar om  double till int.
    }

    /*Skannar av sin omgivning.*/
    public ArrayList<Unit> detectEnemy(ArrayList<Unit> enemyList) {
        ArrayList<Unit> detectedEnemies = new ArrayList<Unit>();
        for (int i = 0; i < enemyList.size(); i++) {
            if (distanceToEnemy(enemyList.get(i)) < sensorRange) {

                /*Enemy in range. Add to detected list? Add to shooting list? */
                /*Om soldaten inte har blivit upptäckt.*/
                if (!enemyList.get(i).getDetected()) {

                    enemyList.get(i).setDetected(true);
                    detectedEnemies.add(enemyList.get(i)); //Lägg till soldaten i listan detectedSoldiers
                    inSquad.inArmy.detectedEnemies.add(enemyList.get(i));

                } else {
                    /*Soldaten redan upptäckt sedan tidigare. Vi kan strunta i soldaten. */
                }

            } else {
                /*Enemy not in range*/
                enemyList.get(i).setDetected(false);
            }
        }
        return detectedEnemies;
    }

    @Override
    public Weapon getWeapon(){
        return weapon;
    }

    @Override
    public Vehicle clone(){
        Vehicle v = new Vehicle(this);
        return v;
    }
}

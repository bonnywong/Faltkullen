package faltkullen;

import java.util.ArrayList;

public class Soldier extends Unit{

    //int x, y; //Soldatens nuvarande position.
    public int sensorInterval;
    public int sensorTicker;
    private int soldierAccuracyBase;
    private int soldierAccuracyEffective;

    private boolean isMounted;

    public Unit target;
    public Squad inSquad;
    private Weapon weapon;

    /*Konstruktor som innehåller sensorRange. Har inte tagit bort de tidigare konstruktorerna.*/
    public Soldier(int dmg, int acc, int mor, int sAccB, int sensorRange) {
        health = 100;
        weapon = new Weapon(dmg, acc);
        armor = new Armor(Armor.TROOP);
        morale = mor;
        this.sensorRange = sensorRange;
        soldierAccuracyBase = sAccB;
        soldierAccuracyEffective = effAcc(soldierAccuracyBase, morale);
    }

    public Soldier(int dmg, int acc, int mor, int sAccB) {
        weapon = new Weapon(dmg, acc);
        armor = new Armor(Armor.TROOP);
        morale = mor;
        soldierAccuracyBase = sAccB;
        soldierAccuracyEffective = effAcc(soldierAccuracyBase, morale);
    }

    public Soldier(Settings s) {
        //attributes.get(0) Ã¤r DamageAttribute
        //attributes.get(1) Ã¤r AccuracyAttribute
        //attributes.get(2) Ã¤r ProtectionAttribute
        weapon = new Weapon(s);
        //armor = new Armor(s.getAttribute("Protection").current, true);
        armor = new Armor(Armor.TROOP, s.getAttribute("Protection").current);
        morale = s.getAttribute("Soldier Morale").current;
        sensorRange = s.getAttribute("Sensor Range").current;
        //System.out.println("SENSOR RANGE IS " + sensorRange);
        movespeed = s.getAttribute("Soldier Movespeed").current;
        soldierAccuracyBase = s.getAttribute("Soldier Accuracy").current;
    }

    public Soldier(Soldier sold){
        super(sold);
        weapon = new Weapon(sold.weapon);
        armor = new Armor(sold.armor);
        morale = sold.morale;
        sensorRange = sold.sensorRange;
        movespeed = sold.movespeed;
        soldierAccuracyBase = sold.soldierAccuracyBase;
    }

    @Override
    public void update(){
        super.update();
        if(cooldown > 0){
            cooldown--;
        }
    }

    //är bara en placeholder av moralens inflytande på siktet. hej.
    public int effAcc(int sAccB, int mor) {
        int sEffAcc = sAccB - mor;
        if (sEffAcc < 10) {
            sEffAcc = 10;
        }
        return sEffAcc;
    }

    @Override
    public int getAccuracy() {
        return (morale * soldierAccuracyBase) / 100;
    }

    public int cooldown = 0;

    @Override
    public boolean fireAt(Unit target) {
        if(cooldown <= 0) {
            cooldown = 3;
            return weapon.fireAt(this, target);
        }
        return false;
    }

    @Override
    public boolean movingFireAt(Unit target){
        if(cooldown <= 0){
            cooldown = 3;
            return weapon.movingFireAt(this, target);
        }
        return false;
    }

    //This is apparently not used
    @Override
    public void hit(int dmg, Weapon w) {
        int finalDamageTaken = dmg - armor.getProtection(w);
        if (finalDamageTaken < 0) {
            finalDamageTaken = 0;
        }
        health -= finalDamageTaken;
        if (health <= 0) {
            //System.out.println("Soldier with ID " + id + " died!");
            //System.out.println("Soldier belonged to Group " + group.name);
            alive = false;
        }
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

    /*Finner avstånde mellan denna soldat och en fiendesoldat*/
    public int distanceToEnemy(Unit enemy) {
        double distance = getDistance((int) enemy.getX(), (int) enemy.getY());
        return (int) distance; //Kastar om  double till int.
    }

    @Override
    public Weapon getWeapon() {
        return weapon;
    }

    @Override
    public Soldier clone() {
        Soldier clone = new Soldier(this);
        return clone;
    }
}


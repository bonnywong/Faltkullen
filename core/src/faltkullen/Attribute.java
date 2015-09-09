package faltkullen;

public class Attribute implements Cloneable {
    protected Settings settings;
    public boolean allowsChange = false;
    public int base, current, minimum, maximum;
    public int cost;
    public String type;

    public Attribute(Settings s) {
        settings = s;
    }

    public Attribute(String t, int b, int min, int max, int co, Settings s) {
        base = current = b;
        minimum = min;
        maximum = max;
        type = t;
        cost = co;
        settings = s;
    }

    //Returnerar true of en attribut får ändras med c, annars false
    public boolean change(int c) {
        //settings.main.optimality += 2;
        if (current + c > maximum || current + c < minimum) {
            return false;
        }
        //settings.main.optimality += 1;
        current += c;
        return true;
    }

    public void reset() {
        current = base;
    }

    //Returnerar en string som förklarar vilken sorts Attribute det är
    public String toString() {
        return type;
    }

    public void print() {
        System.out.println(type + " : " + current);
        //settings.main.gui.consoleText.append("\n"+type + " : " + current);
    }

    //Klonar detta Attribute-objekt
    public Attribute clone() {
        try {
            Attribute retur = (Attribute) super.clone();
            return retur;
        } catch (CloneNotSupportedException cnse) {
        }
        return null;
    }

    public String getSaveString(){
        String linebreak = System.getProperty("line.separator");
        return "NEW_Attribute:" + type + ":" + minimum + ":" + base + ":" + maximum + ":" + cost + ":" + allowsChange + linebreak;
    }
}

package faltkullen;

import java.util.Random;

/*
 * 
 */
public class ProbabilityTest {
    static int hits;
    static int misses;

    static int ammunition = 300; //"ammunition"

    static Random rng = new Random();

    public ProbabilityTest() {
    }

    public static void main(String args[]) {
        double currentProbability;
        double chanceToHit = 0.8; // 80% chans att tr�ff
        probability(chanceToHit);

        if (chanceToHit <= 0.5) { //Byt ordning p� hits och misses beroende p� sannolikheten, annars f�r man v�rden st�rre �n ett.
            currentProbability = (double) hits / misses;
        } else {
            currentProbability = (double) 1 - (double) misses / hits; //komplementet av miss �r tr�ff, d�rf�r �r 1 - chans f�r miss = chans f�r tr�ff.
        }
        System.out.println("Current chance to hit set to: " + chanceToHit * 100 + " %");
        System.out.println("Hit: " + hits + " times." +
                " Missed: " + misses + " times.");
        System.out.println("Actual probability: " + currentProbability * 100 + " %");
    }

    public static void probability(double d) {
        for (int i = 0; i < ammunition; i++) {
            if (rng.nextDouble() <= d) { //genererar en double mellan 0 och 1. Variabel d fungerar som en sorts gr�ns. I detta fall �r det v�r sannolikhet att kulan tr�ffar.
                hits++;
            } else {
                misses++;
            }
        }
    }
}

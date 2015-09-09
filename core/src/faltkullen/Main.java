package faltkullen;

import com.badlogic.gdx.utils.Array;
import com.mygdx.game.MyGdxGame;
import com.mygdx.game.UnitType;

import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;

public class Main{
  public ArrayList<Soldier> playerSoldiers = new ArrayList<Soldier>();
  public ArrayList<Soldier> computerSoldiers = new ArrayList<Soldier>();

  //Generella inställningar, inte relaterade till ett visst värde på en Unit
  private int certainty = 10; //Antalet simuleringar per evolution
 //Mer kritisk i Biologisk adjustering eftersom varje grupp kör <certainty> simuleringar
  private int radicality = 5; //Hur radikala ändringar för programmet göra mellan varje evolution?
    //H�gra siffror inneb�r h�gre precision men kan innebära längre körstid
  private int maximumIncrementWidth = 3; //Hur m�nga attribut kan vi �ka samtidigt?
    //Kan anv�ndas om vi t.ex vill �ka b�de Damage och Accuracy samtidigt
    //�kar s�kerheten men �kar �ven k�rstid
 //Anv�nds endast f�r Up / Down adjustering
  private int populationSize = 25; //Hur m�nga olika inst�llningar per generation ska unders�kas?
 //�kar s�kerheten men �kar �ven k�rstid
 //Anv�nds endast i Biologisk adjustering
  private int amountOfGenerations = 100; //Hur m�nga generationer av soldater ska vi g� igenom innan vi avslutar?
 //�kar s�kerheten men �kar �ven k�rstid
 //Anv�nds endast i Biologisk adjustering

  private int optimality = 0; //Hur optimal �r nuvarande l�sning? Varje utr�kning �kar optimality med 1
  //L�g optimality inneb�r bra optimering (mindre utr�kningar)
  private int threads = 1; //Hur m�nga tr�dar k�rs samtidigt?
  private int activeThreads = threads; //Hur m�nga �r aktiva just nu?
  private ArrayList<SimulationThread> currentThreads;

    private Settings baseSettings = new Settings(this);
    private Settings evolutionSettings;
    private Array<UnitType> evolutionSet;

  private TestOfMap map;
  public SwingGUI gui;
  public boolean displayMap = true;
  public boolean breakEvolution = false;
  public boolean infiniteGenerations = false;
  public int generations = 0;
  public int battles = 0;

  public ArrayList<Settings> population;
  public ArrayList<Settings> reported;

    public ArrayList<ArrayList<Settings>> population2;
    public ArrayList<ArrayList<Settings>> reported2;

  public Settings bestFitnessSettings;
  public int bestFitnessValue;

    public boolean evolutionIsPaused = false;
    public boolean evolutionShouldBreak = false;
    public boolean startNew = true;
    public boolean randomizationIsDone = true;

    public MyGdxGame gui2;

    public ArmyComposition redArmy, blueArmy;

    public double hertz = 30.0;
    public boolean stepwise = false;
    public int stepLength = 5;
  
    public Main(boolean old){
        System.out.println("Construction");
        if(old) {
            gui = new SwingGUI(this);
        }
        currentThreads = new ArrayList<SimulationThread>();
    }

    public Main(MyGdxGame g){
        currentThreads = new ArrayList<SimulationThread>();
        gui2 = g;
    }

  public void startEvolutionThread(Settings s){
      System.out.println("Starting Evolution");
      startNew = false;
      evolutionSettings = s;
      generations = 0;
      battles = 0;

      updateGUI();
      Thread loop = new Thread() {
          public void run() {
              startEvolution();
          }
      };
      loop.start();
      System.out.println("Startup complete");
  }

    public void startEvolutionThread(ArmyComposition red, ArmyComposition blue){
        startNew = false;
        evolutionSet = red.unitTypes;
        generations = 0;
        battles = 0;

        redArmy = red;
        blueArmy = blue;

        System.out.println("Map is stepwise = " + stepwise + " (starting Evolution)");

        updateGUI();
        Thread loop = new Thread() {
            public void run() {
                startEvolution();
            }
        };
        loop.start();
        System.out.println("Startup complete");
    }

  public void startEvolution(){
    while(breakEvolution){
      System.out.println("Waiting");
    }
    System.out.println("New evolution has started");
    long start = System.currentTimeMillis();
 
    //Biologisk adjustering
    int localRadicality = radicality;
    Settings s;
    trackMovement = new int[400][400];

      System.out.println("Map is stepwise = " + stepwise + " (Evolution has started)");
    
    /* INITIALIZING POPULATION
     * Starts by creating <population> clones of the starting settings
     * and randomizing some of their values
     */

      population2 = new ArrayList<ArrayList<Settings>>();
      reported2 = new ArrayList<ArrayList<Settings>>();

      //Go through every set of settings in the population
      for(int a=0;a<populationSize-1;a++){
          ArrayList<Settings> geneSet = new ArrayList<Settings>();
          for(int b=0;b<evolutionSet.size;b++){
              //We clone the settings of a particular UnitType
              s = evolutionSet.get(b).settings.clone();

              //Then we randomize the values inside
              boolean success = s.randomize(radicality);
              //This returns true there were no problems in randomizing the values
              //If there were any problems in the randomization, add a clone of the original settings to the population
              if(!success){
                  s = evolutionSet.get(b).settings.clone();
              }
              //This works both as a failsafe and as a method of randomly including a number of unchanged clones to create stability

              //Add the new clone to the gene set
              geneSet.add(s);
          }
          //Add the gene set to the population
          population2.add(geneSet);
      }

      //Lastly, since we have only created <populationSize-1> gene sets, we create one with additional radicality
      ArrayList<Settings> geneSet = new ArrayList<Settings>();
      for(int b=0;b<evolutionSet.size;b++){
          //If you want insight into this code, look at the above double for-loop
          s = evolutionSet.get(b).settings.clone();
          boolean success = s.randomize(radicality * 5);
          if(!success){
              s = evolutionSet.get(b).settings.clone();
          }
          geneSet.add(s);
      }
      population2.add(geneSet);

      //SettingsIO.saveSettings(population, "TestSettings");
      //We will have another save process setup later
    
    /* INITIALIZING SIMULATION PROCESS
     * We start by setting values for the grand scheme
     * We then initialize the threads
     * We lastly end this thread (as the rest of the evolution will be handled by the threads through function calls)
     */

      System.out.println("Map is stepwise = " + stepwise + " (before Threads)");
    
    activeThreads = threads;
    for(int a=0;a<threads;a++){
      SimulationThread thread = new SimulationThread(this, new TestOfMap(2));
      if(a==0){
          System.out.println("First thread specials");
          if(gui!=null) {
              gui.setMap(thread.getMap());
          }
          else if(gui2!=null){
              System.out.println("Setting map");
              gui2.setMap(thread.getMap());
          }
      }
        if(stepwise){
            System.out.println("Main is stepwise (Main starting SimThreads)");
        }
        else{
            System.out.println("Main isnt stepwise (Main starting SimThreads)");
        }
      thread.start();
      currentThreads.add(thread);
    }
  }
  
  public int getCertainty(){
    return certainty;
  }
  
  /* This method is used by SimulationThreads to retrieve a ArrayList of Settings from <population> and the preset Settings for non-evolutionary armies to setup a battle to be simulated.
   * If an ArrayList is returned, the Settings-object from <population> will be remove from <population>
   * If null is returned, the SimulationThread stops
   * Parameters : None
   * Returns : A ArrayList of Settings that the SimulationThread will use
   */
  public synchronized ArrayList<Settings> getMission(){
    if(population2.size()>0){
        return population2.remove(0);
    }
    else{
      activeThreads--;
      if(activeThreads==0){
        //There are no more active threads doing simulations, so we need to walk another step in the evolution
        //If there is no next step in the evolution (we ran out of generations) we print the final results
        generations++;
          updateGUI();
        if(generations == amountOfGenerations && !infiniteGenerations){
          printResults();
        }
        else{
          nextStep();
        }
      }
      return null;
    }
  }
  
  /* This method is used by SimulationThreads to report how succesfull a certain Setting were
   * Parameters:
   * Settings values : A Settings-object with the values used in the simulation
   * int result      : How succesfull the simulation was
   * Returns : Nothing
   */
  public synchronized void reportMission(ArrayList<Settings> values, int result){
      values.get(0).currentFitness = result;
      reported2.add(values);
      updateGUI();
      //gui.setSimulationsDone(battles);
  }
  
  /* This method causes the evolution to do jump to the next generation.
   * This means that the program will take out the best Settings and create a new population with those Settings and randomize the new population with new values
   * After this is done we generate new SimulationThreads and start simulation battles for the new generation
   * Parameters : None
   * Returns : Nothing
   */

    public void nextStep(){
        //We go through all the reported results and pick a winner

        //While we do this, we calculate the average fitness of the Settings
        randomizationIsDone = false;
        Settings winner = reported2.get(0).get(0);
        ArrayList<Settings> winnerSet = reported2.get(0);
    int highest = winner.currentFitness;
    String history = winner.history;
    int totalFitness = highest;
    compareToBest(winner, highest);
    for(int a=1;a<reported2.size();a++){
      //If the result from Settings <a> is better than the current best then replace it
      int contender = reported2.get(a).get(0).currentFitness;
      totalFitness += contender;
      if(highest<contender){
        //We have a new highest fitness
        compareToBest(reported2.get(a).get(0), contender);
        highest = contender;
        winner = reported2.get(a).get(0);
          winnerSet = reported2.get(a);
        history = winner.history;
      }
    }
    /*
    System.out.println("history = " + history);
    System.out.println("history length = " + history.length());
    */
    int averageFitness = totalFitness / reported2.size();
    //If any of the Settings fitness is lower than or equal to the average they are culled
    for(int a=0;a<reported2.size();a++){
      int result = reported2.get(a).get(0).currentFitness;
      if(result <= averageFitness){
        reported2.remove(a);
        a--;
      }
    }
    if(reported2.size() == 0){
      reported2.add(winnerSet);
    }
    
    /* We repopulate <population> with clones from those not culled
     * Repopulation is done evenly, as in we loop through the current population (<reported>) and clone until <population> is full (size == <populationSize>)
     */
    population2.clear();
    population2.addAll(reported2);
    int getFromReported = 0;
    while(population2.size()<populationSize){
      population2.add(reported2.get(getFromReported));
      getFromReported++;
      if(getFromReported == reported2.size()){
        getFromReported = 0;
      }
    }
        reported2.clear();
    
    /* <population> is now refilled with the not-culled members of reported and clones of those members
     * We now do a new evolutionary randomization
     */
    //We loop from a through population.size()-1 because the last member of population gets extra randomization
    for(int a=0;a<population2.size()-1;a++){
        ArrayList<Settings> oldGenes = population2.get(a);
        ArrayList<Settings> newGenes = new ArrayList<Settings>();
        for(int b=0;b<oldGenes.size();b++){
            Settings s = oldGenes.get(b).clone();
            boolean success = s.randomize(radicality);
            if(!success){
                newGenes.add(oldGenes.get(b).clone());
            }
            newGenes.add(s);
        }
        newGenes.get(a).currentFitness = 0;
        population2.set(a, newGenes);
    }
    
    /* Before we finish up we print the best result of the generation
     * This is partly for debug purposes but also for the user to know that improvements are being made
     */

        if(gui != null) {
            gui.consoleText.append("\nGeneration " + generations + " complete!");
            gui.consoleText.append("\nBest Fitness = " + highest);
            gui.consoleText.append("\nBest settings:");
        }
    winner.print();
    
    /* Lastly, we set up new Threads to simulate these battles.
     * We don't designate missions in this call because the Threads call getMission themselves when they start up
     */
    
        currentThreads.clear();

        randomizationIsDone = true;


        while(evolutionIsPaused){
        }
        if(evolutionShouldBreak){
            evolutionShouldBreak = false;
        }
        else{
            System.out.println("Population size = " + population.size());
            activeThreads = threads;
            for(int a=0;a<threads;a++){
                SimulationThread thread = new SimulationThread(this, new TestOfMap(2));
                if(a==0){
                    if(gui!=null) {
                        gui.setMap(thread.getMap());
                    }
                }
                thread.start();
                currentThreads.add(thread);
            }
        }
    }

  
  /* Compares the inputed Settings fitness in the latest tests
   * If the inputed Settings fitness is higher than the current best, replace the old with the new
   * Parameters:
   * Settings set : The inputed Settings that might be the best
   * int fitness : The fitness of the Settings
   * Returns : Nothing
   */
  public void compareToBest(Settings set, int fitness){
    if(bestFitnessSettings==null){
      bestFitnessSettings = set;
      bestFitnessValue = fitness;
    }
    else if(fitness > bestFitnessValue){
      bestFitnessSettings = set;
      bestFitnessValue = fitness;
    }
  }
  
  /* Prints the final results of the evolution
   * Results are printed in the GUI-window
   */
  public void printResults(){
    if(bestFitnessSettings!=null){
        if(gui!=null) {
            gui.consoleText.append("\nThe best results were achieved with the following settings:");
            bestFitnessSettings.print();
            gui.consoleText.append("\nWith these settings " + bestFitnessValue + " soldiers survives on average.");
        }
    }
    if(breakEvolution){
      breakEvolution = false;
    }
  }
    
  
  /* Denna metod plockar ut <num> olika attribut ur <from> med rekursion
   * Om (num == 1) n�r vi plockar en attribut ska vi k�ra improveResults med to-parametern som d� �ven inkluderar attributen som plockades i denna instans av pickAttributes
   * Parameter : int num                   = Antalet attribut vi beh�ver plocka ut 
   * Parameter : ArrayList<Attribute> all  = En lista �ver de attribut vi f�r �ndra p� i improveResults
   * Parameter : ArrayList<Attribute> from = En lista �ver de attribut vi f�r v�lja mellan
   * Parameter : ArrayList<Attribute> to   = En lista �ver de attribut vi redan plockat ut
   */
  public void pickAttributes(int num, ArrayList<Attribute> all, ArrayList<Attribute> from, ArrayList<Attribute> to, Settings s){
    //G�r f�rst en kopia f�r att vi inte ska anv�nda samma from fr�n f�rra iterationen av denna rekursiva metod
    ArrayList<Attribute> newAttributes = new ArrayList<Attribute>();
    newAttributes.addAll(from);
    while(newAttributes.size()>=num){
      to.add(newAttributes.remove(0));
      if(num==1){
        improveResults(all, to, compareTo, s);
        //Återst�ll s och compareTo
        compareTo = baseResult;
        s.reset();
      }
      else{
        pickAttributes(num-1, all, newAttributes, to, s);
      }
      to.remove(to.size()-1);
    }
  }

  public int baseResult = 0;
  public int bestResult = 0;
  public Settings bestSettings;
  public int compareTo = 0;
  
    /* Denna funktion f�rs�ker f�rb�ttra resultaten mellan simulationer genom att �ka <attri> och s�nka andra attributer
   * Parameter : ArrayList<Attribute> attributes = En lista �ver attribut vi f�r �ndra (i detta fall s�nka)
   * Parameter : Attribute<Attribute> increase   = En lista �ver attribut vi ska �ka
   * Parameter : int compareTo                   = Resultatet vi f�rs�ker �verkomma
   * Parameter : Settings s                      = Nuvarande inst�llningarna f�r attributerna
   */
  public void improveResults(ArrayList<Attribute> attributes, ArrayList<Attribute> increase, int compareTo, Settings s){
    System.out.println("improveResults being run by increasing the following attributes:");
    for(Attribute i : increase){
      System.out.println(i.toString());
    }
    boolean resultsAreImproving = true;
    ArrayList<Attribute> chooseFrom = new ArrayList<Attribute>();
    chooseFrom.addAll(attributes);
    chooseFrom.removeAll(increase);
    while(resultsAreImproving){
      resultsAreImproving = false;
      for(int a=1;a<=radicality;a++){
        //G� igenom alla attribut i <increase> och f�rs�k �ka dem med radicality
        boolean canChange = false;
        int totalChange = 0;
        boolean[] changed = new boolean[increase.size()];
        for(int b=0;b<increase.size();b++){
          if(increase.get(b).change(a)){
            canChange = true;
            totalChange += a;
            changed[b] = true;
          }
        }
        if(canChange){
          //Dela ut <totalChange> p� alla m�jliga s�tt och k�r simulateCombat f�r varje s�tt
          resultsAreImproving = distributeDecreasingValues(totalChange, chooseFrom, s);
          if(resultsAreImproving){
            //Eftersom vi kunde �ka med a utan problem och f� ett b�ttre resultat s� kan vi dra en break i slutet eftersom vi inte beh�ver g�ra mer radikala f�r�ndringar
            //
            break;
          }
          //Annars s� m�ste vi g�ra mer radikala �ndringar, s� vi m�ste ta tillbaks de senaste �ndringarna
          for(int b=0;b<increase.size();b++){
            if(changed[b]){
              increase.get(b).change(-a);
            }
          }
        }
        else{
          //Eftersom vi inte kan �ka attributen mer s� hoppar vi ut ur "radicality"-loopen
          break;
        }
      }
    }
  }
  
  //Denna metod delar upp <change> minskningar i v�rden mellan attributerna i chooseFrom
  //Om det inte g�r att dela upp mer s� k�rs en simulering
  //Om denna simulering �r b�ttre �n compareTo s� returneras true rekursivt
  //Annars s� g�rs fler uppdelningar (och d�rmer fler simulationer) tills inga andra val finns kvar
  //Om ingen uppdelning av <change> gav b�ttre resultat �n compareTo i simuleringen s� returneras false
  public boolean distributeDecreasingValues(int change, ArrayList<Attribute> chooseFrom, Settings s){
    Attribute current = chooseFrom.get(0);
    ArrayList<Attribute> newChooseFrom = new ArrayList<Attribute>();
    newChooseFrom.addAll(chooseFrom);
    newChooseFrom.remove(0);
    for(int a=change;a>=0;a--){
      //Kan vi �ndra p� current med -a?
      if(current.change(-a)){
        //Ja det kan vi.
        //Om a == change s� g�r det inte att distributera mer, och i s�danna fall k�r en simulation
        if(a == change){
          int result = simulateCombat(s);
          if(result > compareTo){
            compareTo = result;
            if(result > bestResult){
              bestResult = result;
              bestSettings = s.clone();
            }
            return true;
          }
          //Eftersom vi ska "f�rminska minskningen" av <current> s� m�ste vi �ka den med de vi s�nkte den med innan, dvs <a>
          current.change(a);
        }
        else{
          //Annars s� ska vi f�rs�ka att forts�tta med att distributera change mellan resten av chooseFrom
          //Vi kan endast g�ra detta om newChooseFrom.size() �r st�rre �n 0 och om change > 0
          //- Change m�ste vara st�rre �n 0 annars kommer vi g�ra <certainty> simuleringar f�r varje attribut vi inte provat att �ndra 0 med
          //- Exempel : Vi har tidigare i rekursen s�nkt Damage med 4 och Protection med 1, men vi har Sensor Range och Weapon Range kvar
          //-- D� skulle denna metod g�ra <certainty> simuleringar d� den kommit till sensorRange.change(0) och d� weaponRange.change(0) h�nt
          if(newChooseFrom.size()>0 && change - a > 0){
            if(distributeDecreasingValues(change - a, newChooseFrom, s)){
              return true;
            }
            //Efter alla dessa distributioner som sker i denna kallelse s� vill vi ta tillbaks de senaste �ndringarna f�r att distributera "fr�n scratch" i denna instans av denna metod
            current.change(a);
          }
          else{
            //Vi har inga andra v�rden att �ndra p�, s� vi borde avbryta denna rekursiva instans
            //Innan vi g�r det borde vi ta tillbaks de senaste �ndringarna
            current.change(a);
            break;
          }
        }
      }
    }
    return false;
  }
  
  /* Denna funktion f�rs�ker f�rb�ttra resultaten mellan simulationer genom att �ka <attri> och s�nka andra attributer
   * Parameter : ArrayList<Attribute> attributes = En lista �ver attribut vi f�r �ndra (i detta fall s�nka)
   * Parameter : Attribute attri                 = Attributen vi �kar om det g�r b�ttre
   * Parameter : int compareTo                   = Resultatet vi f�rs�ker �verkomma
   * Parameter : Settings s                      = Nuvarande inst�llningarna f�r attributerna
   */
  public void improveResults(ArrayList<Attribute> attributes, Attribute attri, int compareTo, Settings s){
    boolean resultsAreImproving = true;
    while(resultsAreImproving){
      resultsAreImproving = false; //Vi antar att resultaten inte kommer g� b�ttre. Om det nu r�kar vara s� att resultaten blir b�ttre s� s�tter vi denna variabel till true senare
      //Kan vi �ka attributen med 1?
      if(attri.change(1)){
        //Ja, det kan vi, s� l�t oss f�rs�ka s�nka en annan attribut med 1 f�r att kompensera
        //G� igenom alla andra attribut
        for(int b=0;b<attributes.size();b++){
          if(attributes.get(b)==attri)continue;
          Attribute lower = attributes.get(b);
          //Kan vi s�nka denna attribut?
          if(lower.change(-1)){
            //Ja, g�r en simulering
            int result = simulateCombat(s);
            //B�ttre resultat �n innan? D� g�r vi en till �kning av attri och en ny s�nkning
            if(result > compareTo){
              compareTo = result;
              resultsAreImproving = true;
              //B�ttre �n b�sta resultatet? Ändra d� b�sta resultatet
              if(result > bestResult){
                bestResult = result;
                bestSettings = s.clone();
              }
              break;
            }
            //S�mre resultat? �ka lower med 1 och s�nk en annan attribut
            else if(lower.change(1)){
            }
            else{
              System.out.println("oops");
            }
          }
        }
      }
      //F�r vi inte �ka attributen mer? L�t oss d� unders�ka en annan attribut
      else{
        resultsAreImproving = false;
      }
    }
  }
    
  public void oldTest(){
    /* Simpla tester g�rs
     * Test 1 : Damage g�r fr�n 75 till 1 medans Accuracy g�r fr�n 25 till 99
     * - Detta test borde leda till att b�sta inst�llningarna �r n�gonstans runt 60 Damage och 40 Accuracy
     * Test 2 : Damage g�r fr�n 75 till 99 medans Accuracy g�r fr�n 25 till 1
     * - Detta test borde leda till att b�sta inst�llningarna f�rblir of�r�ndrade
     */
    //bestResult �r h�gsta genomsnittliga antalet soldater som �verlevde p� spelarens sida
    int bestResult = 0;
    int bestDamage = 0; //Damage-v�rdet p� b�sta simuleringen
    int bestAccuracy = 0; //Accuracy-v�rdet p� b�sta simuleringen
    for(int a=0;a<74;a++){
      //Varje inst�llning simuleras 100 g�nger f�r s�kerhets skull och genomsnittsresultatet anv�nds i j�mf�relsen senare
      int totalResult = 0;
      for(int b=0;b<100;b++){
        resetArmies(75-a, 25+a);
        totalResult += simulateCombat();
      }
      int averageResult = totalResult / 100;
      if(averageResult > bestResult){
        bestDamage = 75-a;
        bestAccuracy = 25+a;
        bestResult = averageResult;
      }
    }
    for(int a=0;a<24;a++){
      //Varje inst�llning simuleras 100 g�nger f�r s�kerhets skull och genomsnittsresultatet anv�nds i j�mf�relsen senare
      int totalResult = 0;
      for(int b=0;b<100;b++){
        resetArmies(75+a, 25-a);
        totalResult += simulateCombat();
      }
      int averageResult = totalResult / 100;
      if(averageResult > bestResult){
        bestDamage = 75+a;
        bestAccuracy = 25-a;
        bestResult = averageResult;
      }
    }
    System.out.println("Best result was as follows:");
    System.out.println("  Average amount of survivors: " + bestResult);
    System.out.println("  Damage of soldiers: " + bestDamage);
    System.out.println("  Accuracy of soldiers: " + bestAccuracy);
  }
  
  //Denna metod rensar b�da arméerna och �terskapar dem
  //Parametrarna p�verkar "spelarens" soldaters utrustning
  public void resetArmies(int dmg, int acc){
    playerSoldiers.clear();
    computerSoldiers.clear();
    /*
    for(int a=0;a<100;a++){
      playerSoldiers.add(new Soldier(dmg, acc));
      computerSoldiers.add(new Soldier(75, 25));
    */
  }
  
  /* Denna metod rensar b�da arméerna och �terskapar dem
   * Parameter : Settings s - Vilka inst�llningar som ska anv�ndas p� spelarens soldater
   */
  public void resetArmies(Settings s){
    playerSoldiers.clear();
    computerSoldiers.clear();
    optimality += 2;
    for(int a=0;a<100;a++){
      playerSoldiers.add(new Soldier(s));
      computerSoldiers.add(new Soldier(baseSettings));
    }
  }
  
  /* Simulerar en strid och returnerar hur m�nga av spelarens soldater som �verlevde
   * Parameter : Settings s - Vilka inst�llningar som ska anv�ndas p� spelarens soldater
   * Returv�rde : Fitness (kostnaden av f�rluster eller antalet soldater som �verlevde)
   */
  /*
  public int simulateCombat(Settings s){
    //G�r ett antal simuleringar beroende p� <certainty> med de best�mda inst�llningarna och returnera det genomsnittliga resultatet
    int sumOfResults = 0;
    optimality++;
    for(int a=0;a<certainty;a++){
      //Återst�ll arméerna
      resetArmies(s);
      //Simulera en strid
      int result = simulateCombat();
      sumOfResults += result;
      optimality++;
    }
    //Dividera sumOfResults med certainty f�r att f� fram genomsnittliga resultatet
    optimality++;
    return sumOfResults / certainty;
  }
  */
  
  public int[][] trackMovement;
  
  public int simulateCombat(Settings s){
      String str = "derp";
      ArrayList<Settings> settings = new ArrayList<Settings>();
      settings.add(s);
      settings.add(baseSettings);
      int totalFitness = 0;
      int number = 0;
      //gui.resetMap();
      for(int a=0;a<certainty;a++) {
          gui.resetMap();
          map.hertz = hertz;
          if (!displayMap) {
              map.enabled = false;
          } else {
              map.enabled = true;
          }
          totalFitness += map.runSimulationLoop(settings, true);
          battles++;
          updateGUI();
          //gui.setSimulationsDone(battles);
          number++;
          //System.out.println("Frames : " + map.totalFrames);
          for (Position p : map.track) {
              int x = (int) p.x;
              int y = (int) p.y;
              if (x < 0) {
                  x = 0;
              }
              if (y < 0) {
                  y = 0;
              }
              trackMovement[x][y] += 15;
          }
          map.track.clear();
          if (map.debug) {
              breakEvolution = true;
          }
          if (breakEvolution) {
              break;
          }
      }
    /*
   map.runSimulationLoop(settings); //Skapar en ny simulation
   while(map.notDone()){
    //Simulationen �r inte klar, s� forts�tt
   }
   System.out.println("One simulation done");
   return map.getSurvivors();
   */
    if(map.debug){
      System.out.println("sending");
    }
    System.out.println("Done " + doneTest);
    doneTest++;
    return totalFitness / certainty;
  }
  
  public int doneTest = 0;
  
  public void forceBreakEvolution(){
    breakEvolution = true;
  }
  
  //Simulerar en strid (v�ldigt simpelt f�r tillf�llet) och returnerar hur m�nga av spelarens soldater som �verlevde
  public int simulateCombat(){
    //Striden h�ller p� s� l�nge b�da sidorna har soldater kvar
    while(playerSoldiers.size()>0 && computerSoldiers.size()>0){
      //G� igenom alla spelarens soldater och "simulera" att de skjuter
      for(int a=0;a<playerSoldiers.size();a++){
        Soldier soldier = playerSoldiers.get(a);
        //Slumpv�lj en fiende soldat
        Soldier target = pickRandom(computerSoldiers);
        soldier.fireAt(target);
      }
      //G�r samma f�r datorns soldater
      for(int a=0;a<computerSoldiers.size();a++){
        Soldier soldier = computerSoldiers.get(a);
        //Slumpv�lj en fiende soldat
        Soldier target = pickRandom(playerSoldiers);
        soldier.fireAt(target);
      }
      //Ta bort alla soldater som dog i b�da arméerna
      for(int a=0;a<playerSoldiers.size();a++){
        if(playerSoldiers.get(a).isDead("Main")){
          playerSoldiers.remove(a);
          //cost.increase(100); // one soldier is dead, let's build the cost. The number doesn't mean anything
          a--;
        }
      }
      for(int a=0;a<computerSoldiers.size();a++){
        if(computerSoldiers.get(a).isDead("Main")){
          computerSoldiers.remove(a);
          a--;
        }
      }
    }
    return playerSoldiers.size();
  }
  
  //Denna metod v�ljer slumpm�ssigt en soldat fr�n en ArrayList av soldater
  public Soldier pickRandom(ArrayList<Soldier> soldiers){
    Random rand = new Random();
    int randomPosition = rand.nextInt(soldiers.size());
    return soldiers.get(randomPosition);
  }
  
  public void setEvolutionValues(int[] values, boolean sw, int sl){
    certainty = values[0];
    radicality = values[1];
    populationSize = values[2];
    amountOfGenerations = values[3];
    if(values[4]==1){
      displayMap = true;
        hertz = 30.0;
    }
    else{
      displayMap = false;
        hertz = 10000.0;
    }
    if(values[4]==1){
      infiniteGenerations = true;
    }
    else{
      infiniteGenerations = false;
    }
      if(sw && displayMap){
          hertz = 10000.0;
          stepLength = sl;
          stepwise = true;
      }
      else{
          stepwise = false;
      }
      System.out.println("Main is stepwise = " + stepwise);
  }
  
  public void pauseThreads(){
    evolutionIsPaused = true;
    for(SimulationThread st : currentThreads){
      st.getMap().pause();
    }
  }
  
  public void unpauseThreads(){
    evolutionIsPaused = false;
    for(SimulationThread st : currentThreads){
      st.getMap().unpause();
    }
  }
  
  public void resetThreads(){
    evolutionShouldBreak = true;
    evolutionIsPaused = false;
    startNew = true;
    for(SimulationThread st : currentThreads){
        //st.stop();
        st.end();
    }
    currentThreads.clear();
  }

    /*
     * Method Name : getSettings
     * Parameters : None
     * Returns: An ArrayList containin all Settings-objects currently a part of the evolutionary process
     * Additional Info:
     * This method pauses the evolutionary process before it retrieves all the Settings-objects.
     * This is done both to make the Save function take priority and to prevent potentially weird behaviour.
     * If Main is currently randomizing values, it will wait until the randomization has been completed.
     */
    public ArrayList<Settings> getSettings(){
        pauseThreads();
        while(!randomizationIsDone){

        }
        ArrayList<Settings> retur = new ArrayList<Settings>();
        if(population != null) {
            retur.addAll(population);
            retur.addAll(reported);
        }
        return retur;
    }

    /*
     * Method Name : loadSettings
     * Parameters : ArrayList<Settings> s - The Settings-objects to be loaded into Main
     * Returns : Nothing
     * Description:
     * This method loads an array of Settings into this Main object and cancels the current evolution test.
     * After this is done, the Main object is set to Unpause when the Start button is pressed in the GUI.
     */
    public void loadSettings(ArrayList<Settings> pop, ArrayList<Settings> rep){
        resetThreads();
        while(!randomizationIsDone){

        }
        population = pop;
        reported = rep;
        startNew = false;
    }

    public void updateGUI() {
        if (gui != null) {
            gui.setSimulationsDone(battles);
            gui.setGenerationsCompleted(generations);
        } else if (gui2 != null) {
            gui2.updateBattlesSimulated(battles);
            gui2.updateGenerationsCompleted(generations);
        }
    }
  
  
  public void setMap(TestOfMap m){
    map = m;
    m.main = this;
  }
    public void setThreads(int t){
        threads = t;
    }
  
  public void setGUI(SwingGUI g){
    gui = g;
  }
  
  public SwingGUI getGUI(){
    return gui;
  }
    
  
  public static void main(String args[]){
    new Main(true);
  }



    public void oldStartEvolution(){
        while(breakEvolution){
            System.out.println("Waiting");
        }
        System.out.println("New evolution has started");
        long start = System.currentTimeMillis();

        //Biologisk adjustering
        baseSettings = evolutionSettings.clone();
        int localRadicality = radicality;
        Settings s;
        trackMovement = new int[400][400];

    /* INITIALIZING POPULATION
     * Starts by creating <population> clones of the starting settings
     * and randomizing some of their values
     */

        //Best�m en lite-slumpad population av bas soldaten
        population = new ArrayList<Settings>();
        reported = new ArrayList<Settings>();
        //We loop <populationSize-1> times because the last setting is intended to be a bit more radical than most
        for(int a=0;a<populationSize-1;a++){
            //Cloning the starting value
            s = evolutionSettings.clone();
            //Randomizes the values inside <s>
            boolean worked = s.randomize(radicality);
            //If <worked> is true then the randomization was succesfull, otherwise we reset <s> to the clone
            //This both works as a failsafe and as a method of randomly including a number of unchanged clones to make sure changing the values is actually valuable
            if(!worked){
                s = evolutionSettings.clone();
            }
            //s.print();
            //Optimality was once used to measure how optimal the system was
            optimality++;
            //Adds the new clone to the population
            population.add(s);
        }
        //En radikal mutation l�ggs till
        //Cloning the starting value
        s = evolutionSettings.clone();
        //Randomizes the values inside <s> with 5 times the radicality of the other clones
        boolean worked = s.randomize(radicality * 5);
        //We reset <s> if the randomization was not succesfull
        if(!worked)s = evolutionSettings.clone();
        //We add <s> to the population
        population.add(s);

        SettingsIO.saveSettings(population, "TestSettings");

    /* INITIALIZING SIMULATION PROCESS
     * We start by setting values for the grand scheme
     * We then initialize the threads
     * We lastly end this thread (as the rest of the evolution will be handled by the threads through function calls)
     */

        int bestFitnessValue = 0;
        Settings bestFitnessSettings = null;

        int currentGeneration = 0;
        optimality += 2;
        int bestSettingRepeat = 0;

        activeThreads = threads;
        for(int a=0;a<threads;a++){
            SimulationThread thread = new SimulationThread(this, new TestOfMap(2));
            if(a==0){
                System.out.println("First thread specials");
                if(gui!=null) {
                    gui.setMap(thread.getMap());
                }
                else if(gui2!=null){
                    System.out.println("Setting map");
                    gui2.setMap(thread.getMap());
                }
            }
            if(stepwise){
                System.out.println("Main is stepwise (Main starting SimThreads)");
            }
            else{
                System.out.println("Main isnt stepwise (Main starting SimThreads)");
            }
            thread.start();
            currentThreads.add(thread);
        }

    /*
    for(int a=0;a<attributes.size();a++){
      //Återst�ll inst�llningarna
      s.reset();
      Attribute attri = attributes.get(a);
      int compareToResult = baseResult;
      improveResults(attributes, attri, compareToResult, s);
      }
      */
    /*
     System.out.println("The best results were achieved with the following settings:");
     bestFitnessSettings.print();
     System.out.println("With these settings " + bestFitnessValue + " soldiers survived on average");
     System.out.println("Optimality = " + optimality);
     */

    /*

    if(bestFitnessSettings!=null){
      gui.consoleText.append("\nThe best results were achieved with the following settings:");
      bestFitnessSettings.print();
      gui.consoleText.append("\nWith these settings " + bestFitnessValue + " soldiers survives on average.");
    }
    if(breakEvolution){
      breakEvolution = false;
    }

    JFrame info = new JFrame("Window for Movement Pattern");
    info.setSize(400, 400);
    info.setLayout(null);
    info.add(new InfoPanel(trackMovement));
    info.setVisible(true);
    info.repaint();
    long end = System.currentTimeMillis();
    System.out.println("One evolution took " + (end - start) + " milliseconds");

    */
    }
    public synchronized ArrayList<Settings> oldGetMission(){
        if(population.size()>0){
            //System.out.println("Fetching a new mission");
            //System.out.println("current size = " + population.size());
            ArrayList<Settings> retur = new ArrayList<Settings>();
            retur.add(population.remove(0));
            retur.add(baseSettings);
            //System.out.println("size after = " + population.size());
            return retur;
        }
        else{
            activeThreads--;
            if(activeThreads==0){
                //There are no more active threads doing simulations, so we need to walk another step in the evolution
                //If there is no next step in the evolution (we ran out of generations) we print the final results
                generations++;
                updateGUI();
                //gui.setGenerationsCompleted(generations);
                if(generations == amountOfGenerations && !infiniteGenerations){
                    printResults();
                }
                else{
                    nextStep();
                }
            }
            return null;
        }
    }

    public void oldNextStep(){
        //We go through all the reported results and pick a winner
        //While we do this, we calculate the average fitness of the Settings
        randomizationIsDone = false;
        Settings winner = reported.get(0);
        int highest = winner.currentFitness;
        String history = winner.history;
        int totalFitness = highest;
        compareToBest(winner, highest);
        for(int a=1;a<reported.size();a++){
            //If the result from Settings <a> is better than the current best then replace it
            int contender = reported.get(a).currentFitness;
            totalFitness += contender;
            if(highest<contender){
                //We have a new highest fitness
                compareToBest(reported.get(a), contender);
                highest = contender;
                winner = reported.get(a);
                history = winner.history;
            }
        }
    /*
    System.out.println("history = " + history);
    System.out.println("history length = " + history.length());
    */
        int averageFitness = totalFitness / reported.size();
        //If any of the Settings fitness is lower than or equal to the average they are culled
        for(int a=0;a<reported.size();a++){
            int result = reported.get(a).currentFitness;
            if(result <= averageFitness){
                reported.remove(a);
                a--;
            }
        }
        if(reported.size() == 0){
            reported.add(winner);
        }

    /* We repopulate <population> with clones from those not culled
     * Repopulation is done evenly, as in we loop through the current population (<reported>) and clone until <population> is full (size == <populationSize>)
     */
        population.clear();
        population.addAll(reported);
        int getFromReported = 0;
        while(population.size()<populationSize){
            population.add(reported.get(getFromReported).clone());
            getFromReported++;
            if(getFromReported == reported.size()){
                getFromReported = 0;
            }
        }

    /* <population> is now refilled with the not-culled members of reported and clones of those members
     * We now do a new evolutionary randomization
     */
        //We loop from a through population.size()-1 because the last member of population gets extra randomization
        for(int a=0;a<population.size()-1;a++){
            Settings s = population.get(a).clone();
            boolean worked = s.randomize(radicality);
            //If the randomization worked, we replace the old Settings with the new Settings
            if(worked){
                population.set(a, s);
            }
            population.get(a).currentFitness = 0;
        }

    /* Before we finish up we print the best result of the generation
     * This is partly for debug purposes but also for the user to know that improvements are being made
     */

        if(gui != null) {
            gui.consoleText.append("\nGeneration " + generations + " complete!");
            gui.consoleText.append("\nBest Fitness = " + highest);
            gui.consoleText.append("\nBest settings:");
        }
        winner.print();

    /* Lastly, we set up new Threads to simulate these battles.
     * We don't designate missions in this call because the Threads call getMission themselves when they start up
     */

        currentThreads.clear();

        randomizationIsDone = true;


        while(evolutionIsPaused){
        }
        if(evolutionShouldBreak){
            evolutionShouldBreak = false;
        }
        else{
            System.out.println("Population size = " + population.size());
            activeThreads = threads;
            for(int a=0;a<threads;a++){
                SimulationThread thread = new SimulationThread(this, new TestOfMap(2));
                if(a==0){
                    if(gui!=null) {
                        gui.setMap(thread.getMap());
                    }
                }
                thread.start();
                currentThreads.add(thread);
            }
        }
    }
}

package faltkullen;

import java.util.ArrayList;

public class SimulationThread extends Thread{
    private Main main;
    private TestOfMap battlefield;
    private boolean end;
  public SimulationThread(Main m, TestOfMap ma){
    super();
    main = m;
    battlefield = ma;
    if(!main.displayMap){
      ma.enabled = false;
      ma.hertz = 100000.0;
    }
    else{
      ma.enabled = true;
      ma.hertz = main.hertz;
        if(main.stepwise){
            System.out.println("Main is stepwise (SimThread)");
            ma.setStepwise(main.stepLength);
        }
        else{
            System.out.println("Main isnt stepwise (SimThread)");
            ma.disableStepwise();
        }
    }
      ma.main = m;
  }

    public void run(){
        while(true){
            ArrayList<Settings> mission = main.getMission();
            if(mission==null){
                break;
            }
            else{
                int result = 0;
                for(int a=0;a<main.getCertainty();a++){
                    battlefield.reset();
                    battlefield.loadArmies(mission, main);
                    int survivors = battlefield.startBattle();
                    if(end){
                        battlefield.reset();
                        break;
                    }
                    result += survivors;
                    main.battles++;
                    main.updateGUI();
                }
                result /= main.getCertainty();
                main.reportMission(mission, result);
            }
        }
    }

    /*
     * Method Name : end
     * Parameters : None
     * Returns : Nothing
     * Description:
     * This method calls methods on the map that cause it to end safely as soon as possible.
     * This methid also sets up variables so this thread ends after the battlefield.startBattle() returns without changing anything.
     */
    public void end(){
        end = true;
        battlefield.done = true;
    }
  
  public TestOfMap getMap(){
    return battlefield;
  }

    public void oldRun(){
        while(true){
            ArrayList<Settings> mission = main.getMission();
            if(mission==null){
                break;
            }
            else{
                int result = 0;
                int battlesImproved = 0;
                for(int a=0;a<main.getCertainty();a++){
                    //System.out.println("New battle started");
                    battlefield.reset();
                    battlefield.loadSettings(mission);
                    int survivors = battlefield.startBattle();
                    if(end){
                        battlefield.reset();
                        break;
                    }
                    result += survivors;
                    main.battles++;
                    battlesImproved++;
          /*
          System.out.println("Largest Find 1 = " + battlefield.largestFind);
          System.out.println("Largest Find 2 = " + battlefield.largestFind2);
          System.out.println("Slowest Loop = " + battlefield.slowestLoop);
          System.out.println("Loops = " + battlefield.loops);
          */
                    main.updateGUI();
                }
                if(battlesImproved != 10){
                    System.out.println("Only played = " + battlesImproved + " battles!");
                    System.out.println("Certainty is " + main.getCertainty());
                }
                result /= main.getCertainty();
                //main.reportMission(mission.get(0), result);
            }
        }
    }
}
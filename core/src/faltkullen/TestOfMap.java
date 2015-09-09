package faltkullen;

import com.mygdx.game.UnitType;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.BorderFactory;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;

public class TestOfMap extends JPanel {

    public TestOfMap test;

    /*Storlek på fönstret i x- och y-led*/
      int x = 400;
      int y = 400;
      int state = 1;
      int mouseX = 0;
      int mouseY = 0;
      boolean mousePressed = false;

    /*Startposition fï¿½r test objektet. Existerar endast temporï¿½rt fï¿½r demonstrationen*/
      double angle = 1;
      double angleRad;

    /*För användning vid pausning och liknande i simulations loopen.*/
    private  boolean running = true;
    private boolean paused = false;
    private boolean enableGrid = false;
    private int fps = 120;
    private int frameCount = 0;

    public float interpolation;
    public boolean debugging = false;
    public boolean enabled = true;
    public boolean debug = false;

    public Map map;
    public Main main;

      MouseListener m = new MouseListener() {
        @Override
        public void mouseClicked(MouseEvent e) {
            // TODO Auto-generated method stub
            mouseX = e.getX() - 5;
            mouseY = e.getY() - 25;
            System.out.println("Mouse pressed at (" + mouseX + ", " + mouseY + ")");
            mousePressed = true;
        }

        @Override
        public void mouseEntered(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseExited(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mousePressed(MouseEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void mouseReleased(MouseEvent e) {
            // TODO Auto-generated method stub

        }
    };

      KeyListener k = new KeyListener() {
        @Override
        public void keyPressed(KeyEvent e) {
            // TODO Auto-generated method stub
        }

        @Override
        public void keyReleased(KeyEvent e) {
            // TODO Auto-generated method stub

        }

        @Override
        public void keyTyped(KeyEvent e) {
            // TODO Auto-generated method stub
            switch (e.getKeyChar()) {
                case '1':
                    state = 1;
                    break;
                case '2':
                    state = 2;
                    break;
                case '3':
                    state = 3;
                    break;
                case '4':
                    state = 4;
                    break;
                case '5':
                    state = 5;
                    break;
                case '6':
                    state = 6;
                    break;
            }
        }
    };


    //-------------------------------------------------------------------------------------
      Soldier[] draftRegister = new Soldier[10000];
      int draftSize; //antalet registrerade soldater

      Squad[] ncoCommand = new Squad[100];
      int squadleaderCount; //antalet registrerade soldater
    //-------------------------------------------------------------------------------------

    public int forces;


    //File file;
    Image image;
    /*En ImageObserver som behövs för att rita bilden. */
    public   ImageObserver imgo = new ImageObserver() {

        @Override
        public boolean imageUpdate(Image arg0, int arg1, int arg2, int arg3,
                                   int arg4, int arg5) {
            // TODO Auto-generated method stub
            return false;
        }
    };

    public TestOfMap(int f) {
        setBorder(BorderFactory.createLineBorder(Color.black));
        setSize(x, y);
        setPreferredSize(new Dimension(380, 390));
        forces = f;
        map = new Map(2000, 2000, f);
        /*
        MapFileFormat mff = new MapFileFormat();
        mff.importFromFile();
        //image = mff.bakgrundsbild.getImage();
        */
        test = this;
    }

    public   void main(String[] args) throws InterruptedException {
      /*
      JFrame f = new JFrame("Test Map");


      f.addKeyListener(k);
      f.addMouseListener(m);
      test = new TestOfMap();
      f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      f.add(test);
      f.setSize(x, y);
      f.setResizable(false); //Lï¿½ser fï¿½nstret sï¿½ att storleken inte gï¿½r att ï¿½ndra. Mest fï¿½r att gï¿½ra saker enklare fï¿½r tillfï¿½llet.
      f.setVisible(true);
      */
        //runSimulationLoop(null); //Startar loopen.
    }

    /*Skapar en ny tråd för att köra simulationsloopen
     * Mer info och kommentarer om sjäva loopen finns: http://www.java-gaming.org/index.php?topic=24220.0
     */
    public void runSimulationLoop(ArrayList<Settings> s) {
        settings = s;
        done = false;
        paused = false;
        if (loop == null) {
            loop = new Thread() {
                public void run() {
                    try {
                        System.out.println("Starting loop");
                        simulationLoop();
                    } catch (InterruptedException e) {
                        System.out.println("InterruptedException caught!");
                    }
                }
            };
        }
        loop.start();
        try {
            loop.join();
        } catch (InterruptedException e) {
            System.out.println("InterruptedException caught when trying to wait for death of thread");
        }
        System.out.println("Thread destroyed");
    }

    public int runSimulationLoop(ArrayList<Settings> s, boolean alternative) {
        settings = s;
        done = false;
        try {
            simulationLoop();
        } catch (InterruptedException e) {
            System.out.println("Getting here means major malfunction");
        }
        if (debug) {
            System.out.println("Returning");
        }
        return getSurvivors();
    }

    public Thread loop;

    public   ArrayList<Settings> settings = new ArrayList<Settings>();
    public   ArrayList<Army> armies = new ArrayList<Army>();
    public   ArrayList<Army> enemies = new ArrayList<Army>();

    public   double hertz = 30.0;

    public   boolean done = false;

    public ArrayList<Position> track;

    public boolean statusRequested = false;

    private int currentStepsLeft = 150;

    public void simulationLoop() throws InterruptedException {
        final double SIMULATION_HERTZ = hertz; //Hur många gånger simulationer ska beräknas per sekund.
        final double TIME_BETWEEN_UPDATES = 1000000000 / SIMULATION_HERTZ; //Antalet tillï¿½tna nanosekunder per simulation.
        final int MAX_UPDATES_BEFORE_RENDER = 5; //Högsta tillåtna gånger vi tillï¿½ter berï¿½kningar att ske innan vi ritar/uppdaterar kartan/bilden.
        double lastUpdateTime = System.nanoTime();
        double lastRenderTime = System.nanoTime();

        //-------------------------------------------------------------------------
        //-------------------------------------------------------------------------
  
        /*FPS tal som vi vill nå*/
        final double TARGET_FPS = 120;
        final double TARGET_TIME_BETWEEN_RENDERS = 1000000000 / TARGET_FPS;

        int lastSecondTime = (int) (lastUpdateTime / 1000000000);

        totalFrames = 0;

        int counter = 0;

        if(stepwise){
            currentStepsLeft = stepLength * 30;
        }

        while (!done) { //M�jlighet att l�gga till Boolean fï¿½r att pausa simulationen.
            double now = System.nanoTime();
            int updateCount = 0;

            if (debug) {
                System.out.println("Big");
            }

            /*
            if (statusRequested) {
                statusRequested = false;
                for (int a = 0; a < forces; a++) {
                    System.out.println("*****\n*****\nStatus of Army " + a);
                    Army army = armies.get(a);
                    for (int b = 0; b < army.units.size(); b++) {
                        Unit u = army.units.get(b);
                        if (u instanceof Soldier) {
                            Soldier s = (Soldier) u;
                            if (s.target != null) {
                                if (s.target.isDead("Status")) {
                                    System.out.println("Soldier " + s.id + " has a dead target");
                                } else {
                                    System.out.println("Soldier " + s.id + " has an alive target");
                                }
                            } else {
                                System.out.println("Soldier " + s.id + " does not have a target");
                            }
                            if (s.inSquad == null) {
                                System.out.println("The soldier is not in a squad!!!!");
                            } else {
                                System.out.println("The soldier is in a squad with " + s.inSquad.members.size() + " members");
                                if (s.inSquad.enemyContact) {
                                    System.out.println("The soldiers squad is in contact with the enemy!");
                                } else {
                                    System.out.println("The soldiers squad is NOT in contact with the enemy");
                                }
                            }
                        }
                    }
                }
            }
            */

            if (!paused) { //Möjlighet att lägga till Boolean fï¿½r att pausa simulationen.

                if(stepwise){
                    currentStepsLeft--;
                    if(currentStepsLeft <= 0){
                        paused = true;
                        main.gui2.stepsCompleted();
                    }
                }
                else{
                    System.out.println("NOT STEPWISE");
                }

                /*Uppdatera sï¿½ lï¿½nge skillnaden mellan tiden nu och senaste uppdateringen ï¿½r inom
                 *de definierade ramarna*/
                while (now - lastUpdateTime > TIME_BETWEEN_UPDATES && updateCount < MAX_UPDATES_BEFORE_RENDER && !done) {
                    totalFrames++;
                    if (debug) {
                        System.out.println("Medium");
                    }
                    updateSimulation();
                    lastUpdateTime += TIME_BETWEEN_UPDATES;
                    updateCount++;

                    //----------------------------------------------------------------------------------
                    //--------------------------- FLYTTA SOLDATERNA ------------------------------------
                    //----------------------------------------------------------------------------------
                    moveAllGroups();
                    counter++;

                    if (mousePressed) {
                        for (int a = 0; a < squadleaderCount; a++) {
                            if (ncoCommand[a].orders.size() > 0) {
                                ncoCommand[a].orders.remove(0);
                                ncoCommand[a].orders.trimToSize();
                            }
                            //ncoCommand[a].orders.add(new SquadOrder(state, mouseX, mouseY));
                        }
                        mousePressed = false;
                    }

                    //Remove dead units
                    for (int a = 0; a < armies.size(); a++) {
                        for (int b = 0; b < armies.get(a).detectedEnemies.size();b++){
                            Unit u = armies.get(a).detectedEnemies.get(b);
                            if(u.isDead("TestOfMapDetectRemove")){
                                armies.get(a).detectedEnemies.remove(b);
                                b--;
                            }
                        }
                        for (int b = 0; b < armies.get(a).units.size(); b++) {
                            Unit u = armies.get(a).units.get(b);
                            if (u instanceof Soldier) {
                                Soldier s = (Soldier) u;
                                if (a == 0 && s.id == 45) {
                                    //Get position
                                    Position p = s.getPosition();
                                    if (track.size() > 0) {
                                        Position comp = track.get(track.size() - 1);
                                        if ((int) comp.x == (int) p.x && (int) comp.y == (int) p.y) {
                                            //Do nothing
                                        } else {
                                            track.add(p);
                                        }
                                    } else {
                                        track.add(p);
                                    }
                                }
                            }
                            if(u.retreated){
                                System.out.println("Unit " + u.id + "retreated");
                            }
                            if (u.isDead("TestOfMap")) {
                                //System.out.println("Removing Unit " + u.id);
                                armies.get(a).units.remove(b);
                                u.group.members.remove(u);
                                if(u.feared){
                                    u.group.totalFeared--;
                                }
                                u.group = null;
                                u.containedIn.removeFromSector(u);
                                //u.containedIn.contains[a].remove(u);
                                u.containedIn = null;
                                b--;
                                if (u instanceof Soldier) {
                                    Soldier soldier = (Soldier) u;
             /*
              * Whatever you do
              * DO NOT ENABLE THIS CODE
             soldier.inSquad.members.remove(soldier);
             if(soldier.inSquad.members.size()==0){
               armies.get(a).command.remove(soldier.inSquad);
             }
             */
                                }
                            }
                        }
                    }


                }

                //If for some reason an update takes forever, we don't want to do an insane number of catchups.
                //If you were doing some sort of game that needed to keep EXACT time, you would get rid of this.
                if (now - lastUpdateTime > TIME_BETWEEN_UPDATES) {
                    lastUpdateTime = now - TIME_BETWEEN_UPDATES;
                }
    
              /*Detta berï¿½knar vï¿½r interpolation, behï¿½vs troligen inte alls. Men jag behï¿½ller den hï¿½r ifall vi anvï¿½nder den senare*/
                float interpolation = Math.min(1.0f, (float) ((now - lastUpdateTime) / TIME_BETWEEN_UPDATES));
                drawGame(interpolation); //Ritar fï¿½remï¿½len pï¿½ kartan.
                lastRenderTime = now;

              /*Uppdatera frameraten.*/
                int thisSecond = (int) (lastUpdateTime / 1000000000);
                if (thisSecond > lastSecondTime) {
                    //System.out.println("NEW SECOND " + thisSecond + " " + frameCount);
                    fps = frameCount;
                    frameCount = 0;
                    lastSecondTime = thisSecond;
                    //System.out.println("Red army count : " + armies.get(0).units.size());
                    //System.out.println("Blue army count : " + armies.get(1).units.size());
                }

                /*Optimering.
                 * Existerar fï¿½r att minska CPU anvï¿½ndning. */
                while (now - lastRenderTime < TARGET_TIME_BETWEEN_RENDERS && now - lastUpdateTime < TIME_BETWEEN_UPDATES && !done) {
                    if (debug) {
                        System.out.println("Small");
                    }
                    //Thread.yield();
                    try {
                        Thread.sleep(1);
                    } catch (Exception e) {
                    }
                    now = System.nanoTime();
                }

                int armiesStanding = 0;
                for (int a = 0; a < armies.size(); a++) {
                    if (armies.get(a).units.size() > 0) {
                        armiesStanding++;
                    }
                }
                if (armiesStanding <= 1 && !done) {
                    survivors = armies.get(0).units.size();
                    done = true;

                    //Clean all MapSectors, armies, enemeis et
                    for (int a = 0; a < forces; a++) {
                        while (armies.get(a).units.size() > 0) {
                            Unit u = armies.get(a).units.get(0);
                            if (u.containedIn != null) {
                                //ArrayList<Unit> cont = u.containedIn.contains[a];
                                ArrayList<Unit> cont = u.containedIn.getUnitsFromTeam(a);
                                while (cont.size() > 0) {
                                    cont.remove(0).containedIn = null;
                                }
                            }
                            armies.get(a).units.remove(0);
                        }
                        while (enemies.get(a).units.size() > 0) {
                            enemies.get(a).units.remove(0);
                        }
                    }
                }
            }
        }
        if (debug) {
            System.out.println("We are free!");
        }
    }

    public int survivors = 0;

    public int totalFrames = 0;

    public boolean notDone() {
        return !done;
    }

    public void reset() {
        //debug = true;
        done = true;
        //Clean all MapSectors, armies, enemeis etc
        if (armies.size() != 0) {
            for (int a = 0; a < forces; a++) {
                while (armies.get(a).units.size() > 0) {
                    Unit u = armies.get(a).units.get(0);
                    if (u.containedIn != null) {
                        //ArrayList<Unit> cont = u.containedIn.contains[a];
                        ArrayList<Unit> cont = u.containedIn.getUnitsFromTeam(a);
                        while (cont.size() > 0) {
                            cont.remove(0).containedIn = null;
                        }
                    }
                    armies.get(a).units.remove(0);
                }
                while (enemies.get(a).units.size() > 0) {
                    enemies.get(a).units.remove(0);
                }
            }
        }
        map.clear();
        //System.out.println("Cleaning done");
    }

    public void pause() {
        paused = true;
    }

    public void unpause() {
        currentStepsLeft = stepLength * 30;
        paused = false;
    }

    public boolean isPaused() {
        return paused;
    }

    public int getSurvivors() {
        return survivors;
    }

    /*Draw everything*/
    private  void drawGame(float interpolation) {
        setInterpolation(interpolation);
        test.repaint();
    }

    /*Sets the interpolation value*/
    public void setInterpolation(float interp) {
        interpolation = interp;
    }

    /*Uppdaterar simulationen.*/
    private void updateSimulation() {
    }

    public int mapID = 0;

    Aircraft testCraft = new Aircraft();

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        //System.out.println("Printing on map with id " + mapID);
        //System.out.println("Current frameCount = " + frameCount);

        if (!enabled) {
            frameCount++;
            return;
        }
        drawBackground(g);
        if(enableGrid) {
            drawGrid(400, 400, 8, g);
        }

        for (int a = 0; a < armies.size(); a++) {
            for (int b = 0; b < armies.get(a).units.size(); b++) {
                Unit u = armies.get(a).units.get(b);
                if (u.alive) {
                    if (a == 0) {
                        g.setColor(Color.blue);
                    } else if (a == 1) {
                        g.setColor(Color.red);
                    } else if (a == 2) {
                        g.setColor(Color.green);
                    }
                    Position p = u.getPosition();
                    g.drawRect((int) p.x, (int) p.y, 1, 1);
                }
            }
        }
        testCraft.draw(g);
        testCraft.update();
        g.setColor(Color.black);



       /* for(int a = 0; a < draftSize; a++){
            if(!draftRegister[a].isDead()){
                g.drawRect((int)draftRegister[a].x, (int)draftRegister[a].y, 10, 10);
            }
            else{
                g.drawRect((int)(draftRegister[a].x - 2), ((int)draftRegister[a].y - 2), 5, 5);
                g.drawRect((int)(draftRegister[a].x - 1), ((int)draftRegister[a].y - 1), 3, 3);
                g.drawRect((int)draftRegister[a].x, (int)draftRegister[a].y, 1, 1);
                g.drawString("RIP", (int)(draftRegister[a].x - 8), (int)(draftRegister[a].y - 5));
            }
        }*/


        frameCount++;

    }

    public ArrayList<Army> fetchArmies(){
        return armies;
    }

    public   void moveGroup(int squad) {
   /*
  ArrayList<Position> positions = new ArrayList<Position>(); 
  
  for(Soldier s : ncoCommand[squad].members){
   positions.add(new Position(s.x, s.y));
  }    
  //ncoCommand[squad].action(positions, enemies);
 
  int soldierRowIndex = 0;
  for(Integer i : ncoCommand[squad].members){
   draftRegister[i].x = positions.get(soldierRowIndex).x;
   draftRegister[i].y = positions.get(soldierRowIndex).y;
   soldierRowIndex++;
  }
  */
    }

    /*
     * Method used to move every soldier currently in a group the designated distance.
     */
    public void moveAllGroups() {
        for (int a = 0; a < armies.size(); a++) {
            Army army = armies.get(a);

            army.armyLeader.think();
            /*
              OLD HENRIK CODE
            for (Squad s : army.command) {
                ArrayList<Position> positions = new ArrayList<Position>();
                for (Soldier soldier : s.members) {
                    positions.add(new Position(soldier.x, soldier.y));
                }
                if (s.members.size() == 0) {
                    continue;
                }
                positions = s.action(positions, enemies.get(a).units);
                if (debugging && !s.hasOrders()) {
                    System.out.println("One group does not have orders");
                }
                int soldierRowIndex = 0;
                for (Soldier soldier : s.members) {
                    soldier.x = positions.get(soldierRowIndex).x;
                    soldier.y = positions.get(soldierRowIndex).y;
                    soldierRowIndex++;
                    MapSector currentSector = getSectorFromPoint(soldier.getPosition());
                    if (currentSector != soldier.containedIn) {
                        if (soldier.containedIn != null) {
                            soldier.containedIn.removeFromSector(soldier);
                            currentSector.addToSector(soldier);
                        }
                        soldier.containedIn = currentSector;
                    }
                }
                if(s.moveOrder){
                  s.moveOrder = false;
                  Position newGroupPosition = s.getGroupPosition();
                  if(newGroupPosition.equals(s.startOfMoveOrder)){
                    //They are barely moving
                    System.out.println("Is something wrong?");
                  }
                }
            }
                */
        }
          /*
          for(int a = 0; a < squadleaderCount; a++){
            moveGroup(a);
           }
           */
    }

    /*
     * Temporary method to tell the squad they have lost a soldier. Input is the unique soldier ID.
     */
    public   void killSoldier(int unique_id) {
        for (int a = 0; a < squadleaderCount; a++) {
            ncoCommand[a].loseSoldier((Integer) unique_id);
            draftRegister[unique_id].hit(1000000);
        }
    }

    /*
     * Ritar griden.
     *
     * int x: storlek på fönster i x led
     * int y: storlek på fönster i y led
     * int grids: antalet grids vi vill ha.
     * Graphics g:
     */
    public void drawGrid(int x, int y, int grids, Graphics g) {
        int x_step = (int) Math.floor(x / grids);
        int y_step = (int) Math.floor(y / grids);

        /* Vertikala och horisontella linjer för att bilda rutnätet */
        for (int i = 0; i < grids; i++) {
            g.drawLine(x_step * i, 0, x_step * i, 800);
            g.drawLine(0, y_step * i, 800, y_step * i);
        }
    }

    /*Draws the background image.*/
    public void drawBackground(Graphics g) {
        //g.drawImage(image, 0, 0, Color.white, imgo);
    }

    public MapSector getSectorFromPoint(Position p) {
        //p.boundaries();
        int x = (int) (p.x) / 50;
        int y = (int) (p.y) / 50;

        MapSector sector = null;
        try{
            sector = map.sectors[x][y];
        }
        catch(ArrayIndexOutOfBoundsException e){
            sector = map.sectors[0][0];
        }

        return sector;
    }

    public int[] getSectorPositionFromPoint(Position p) {
        int[] retur = new int[2];
        retur[0] = (int) p.x / 50;
        retur[1] = (int) p.y / 50;
        return retur;
    }

    public ArrayList<Unit> getEnemiesWithinRange(Unit u, int range) {
        int b = ((range - 1) / 50) + 1;
        ArrayList<Unit> retur = new ArrayList<Unit>();
        Position p = u.getPosition();
        int x = (int) p.x / 50;
        int y = (int) p.y / 50;
        int potentialEnemies = 0;
        int searched = 0;
        for (int f = 0; f < forces; f++) {
            if (f == u.team) {
                continue;
            }
            for (int a = 0; a <= b * 2; a++) {
                int xNum = x - b + a;
                if (xNum < 0 || xNum >= map.xLimit) {
                    continue;
                }
                for (int c = 0; c <= b * 2; c++) {
                    int yNum = y - b + c;
                    if (yNum < 0 || yNum >= map.yLimit) {
                        continue;
                    } else {
                        searched++;
                        //ArrayList<Unit> enemiesInSector = map.sectors[xNum][yNum].contains[f];
                        ArrayList<Unit> enemiesInSector = new ArrayList<Unit>(map.sectors[xNum][yNum].getUnitsFromTeam(f));
                        for (Unit enemy : enemiesInSector) {
                            potentialEnemies++;
                            try {
                                if (u.getPosition().distance(enemy.getPosition()) <= range) {
                                    retur.add(enemy);
                                }
                            } catch (NullPointerException e) {
                            }
                        }
                    }
                }
            }
        }

        if(potentialEnemies > 0 && debug) {
            System.out.println("potentialEnemies = " + potentialEnemies);
            System.out.println("b = " + b);
            System.out.println("range = " + range);
        }

        return retur;
    }

    public ArrayList<Unit> getUnitsWithinRange(Position p, int range) {
        int b = ((range - 1) / 50) + 1;
        ArrayList<Unit> retur = new ArrayList<Unit>();
        int x = (int) p.x / 50;
        int y = (int) p.y / 50;
        for (int f = 0; f < forces; f++) {
            for (int a = 0; a <= b * 2; a++) {
                int xNum = x - b + a;
                if (xNum < 0 || xNum >= map.xLimit) {
                    continue;
                }
                for (int c = 0; c <= b * 2; c++) {
                    int yNum = y - b + c;
                    if (yNum < 0 || yNum >= map.yLimit) {
                        continue;
                    } else {
                        //ArrayList<Unit> unitsInSector = map.sectors[xNum][yNum].contains[f];
                        ArrayList<Unit> unitsInSector = map.sectors[xNum][yNum].getUnitsFromTeam(f);
                        for (Unit unit : unitsInSector) {
                            if (p.distance(unit.getPosition()) <= range) {
                                retur.add(unit);
                            }
                        }
                    }
                }
            }
        }
        return retur;
    }

    public ArrayList<Unit> getUnitsWithinRangeDebug(Position p, int range){
        int b = ((range - 1) / 50) + 1;
        ArrayList<Unit> retur = new ArrayList<Unit>();
        int x = (int) p.x / 50;
        int y = (int) p.y / 50;
        for (int f = 0; f < forces; f++) {
            for (int a = 0; a <= b * 2; a++) {
                int xNum = x - b + a;
                if (xNum < 0 || xNum >= map.xLimit) {
                    continue;
                }
                for (int c = 0; c <= b * 2; c++) {
                    int yNum = y - b + c;
                    if (yNum < 0 || yNum >= map.yLimit) {
                        continue;
                    } else {
                        //ArrayList<Unit> unitsInSector = map.sectors[xNum][yNum].contains[f];
                        ArrayList<Unit> unitsInSector = map.sectors[xNum][yNum].getUnitsFromTeam(f);
                        int added = 0;
                        for (Unit unit : unitsInSector) {
                            if (p.distance(unit.getPosition()) <= range) {
                                retur.add(unit);
                                added++;
                            }
                        }
                        System.out.println("Searched in sector " + xNum + "/" + yNum);
                        System.out.println("Attempted to add " + unitsInSector.size() + " units");
                        System.out.println("Added " + added + " in total");
                    }
                }
            }
        }
        return retur;
    }

    public void getStatus() {
        statusRequested = true;
    }
    
    public void loadSettings(ArrayList<Settings> s){
      settings = s;
    }

    public void loadArmies(ArrayList<Settings> geneSet, Main armySource){
        armies.clear();
        enemies.clear();
        map.clear();
        if (track == null) {
            track = new ArrayList<Position>();
        } else {
            track.clear();
        }

        ArmyComposition[] comps = new ArmyComposition[]{armySource.redArmy, armySource.blueArmy};

        //Create armies, duh
        for(int a=0;a<comps.length;a++){
            ArmyComposition ac = comps[a];

            Army army = new Army();
            army.team = a;

            //Create Base Units
            ArrayList<Unit> baseUnits = new ArrayList<Unit>();
            for(int b=0;b<ac.unitTypes.size;b++){
                UnitType ut = ac.unitTypes.get(b);
                Settings set = ut.settings;
                if(a==0){
                    //Its the army thats evolving, get settings from Parameters instead
                    set = geneSet.get(b);
                }
                Unit u = ut.type.getBaseUnit(set);
                //System.out.println("Unit Type ID = " + ut.id);
                baseUnits.add(u);
            }

            //Deep Clone the Leadership Hierarchy
            Leader l = ac.leader.deepClone(this, baseUnits, army);
            army.armyLeader = l;
            l.setInitialOrders(ac.getOrders());
            armies.add(army);

            //Create a new army that will work as this armys enemies and add it to enemies
            army = new Army();
            enemies.add(army);

            army.goal = new TakePosition (new Position(1950 - a * 1900, 1950 - a*1900), 5);
        }

        //Att ha enemies-arrayer är en dålig lösning på hitta-fiender-problemet, men det får duga för nu
        for (int a = 0; a < comps.length; a++) {
            Army listOfEnemies = enemies.get(a);
            for (int b = 0; b < comps.length; b++) {
                if (a == b) continue;
                listOfEnemies.units.addAll(armies.get(b).units);
            }
        }
    }
 
    public int startBattle(){
      done = false;
      paused = false;
      survivors = 0;
      try{
        simulationLoop();
      }
      catch(InterruptedException e){
        System.out.println("WE GOT INTERRUPTED");
      }
        System.out.println("Steps Left = " + currentStepsLeft);
      return getSurvivors();
    }

    private boolean stepwise = false;
    private int stepLength = 5;

    public void setStepwise(int steps){
        stepwise = true;
        stepLength = steps;
    }

    public void disableStepwise(){
        stepwise = false;
    }
}

package faltkullen;

import java.util.ArrayList;
import java.util.Random;

//import javax.xml.stream.events.Attribute;

public class Settings implements Cloneable{
    public ArrayList<Attribute> attributes = new ArrayList<Attribute>();
    public ArrayList<Attribute> changeableAttributes = new ArrayList<Attribute>();
    public int maxCost = 13000;
    public Main main; //Används för optimalitetsuträkning
    public int currentFitness = 0;

    public ArrayList<Group> groups = new ArrayList<Group>();
    public ArrayList<Leader> leaders = new ArrayList<Leader>();
    public Leader armyLeader;

    public Settings(Main main){
        this.main = main;
           /*
          ArrayList<Attribute> atts = new ArrayList<Attribute>();
          atts.add(new Attribute("Damage", 75, 1, 99, 100, this));
          atts.add(new Attribute("Accuracy", 25, 1, 99, 125, this));
          atts.add(new Attribute("Protection", 10, 5, 15, 225, this));
          atts.add(new Attribute("SensorRange", 5, 1, 25, 75, this));
          atts.add(new Attribute("Walkingspeed", 3, 1, 7, 75, this));
          for(int a=0;a<atts.size();a++){
           attributes.add(atts.get(a));
           changeableAttributes.add(atts.get(a));
           main.optimality += 7; //5 f�r varje attribut + 2 f�r adds
          }
          maxCost = getTotalCost();
          main.optimality += 1;
          */
    }

    public Settings(Settings s){
        main = s.main;
        for(int a=0;a<s.attributes.size();a++){
            Attribute clone = s.attributes.get(a).clone();
            attributes.add(clone);
            if(s.changeableAttributes.contains(s.attributes.get(a))){
                changeableAttributes.add(clone);
           //main.optimality++;
            }
          //main.optimality++;
        }
    //  for(int a=0;a<s.changeableAttributes.size();a++){
    //   changeableAttributes.add(s.changeableAttributes.get(a).clone());
    //   main.optimality++;
    //  }
        maxCost = s.maxCost;
        currentFitness = 0;
    }

      public void print(){
        for(int a=0;a<attributes.size();a++){
          attributes.get(a).print();
        }
        main.gui.consoleText.append("\nMax Cost : " + maxCost);
        main.gui.consoleText.append("\nTotal Cost : " + getTotalCost());
      }


      //Returnerar attributen av typen <type> från detta Settings-objekt
      //Om inget attribut hittades returneras null
      public Attribute getAttribute(String type){
        for(Attribute a : attributes){
          if(a.type.equals(type)){
            return a;
          }
        }
        return null;
      }

      //Returnerar de variabler som f�r �ndras
      public ArrayList<Attribute> getChangeableAttributes(){
     /*
        ArrayList<Attribute> retur = new ArrayList<Attribute>();
        for(int a=0;a<attributes.size();a++){
         if(attributes.get(a).allowsChange()){
           retur.add(attributes.get(a));
      }
     }
     return retur;
     */
       return changeableAttributes;
      }


      public void reset(){
        for(int a=0;a<attributes.size();a++){
         attributes.get(a).reset();
        }
      }

      public String history = "";

      //Denna metod slumpar till värdena lite i denna Settings-objekt genom att öka och sänka slumpmässiga attribut <num> gånger.
      //[SENARE] Om totalkostnaden inte är nära maxkostnaden (90-100%) så ska antingen slumpmässiga värden sänkas tills det ligger under 100%
      //  eller �kas tills det ligger mellan 90 och 100%
      //num == radicality
      //Returnerar true om det gick, annars false
      public boolean randomize(int num){
        StringBuilder buffer = new StringBuilder();
        int totalIncrease = 0;
        Random rand = new Random();
          /*
          System.out.println("Printing Attributes");
          for(int a=0;a<attributes.size();a++){
              attributes.get(a).print();
          }
          */

          if(changeableAttributes.size()-2 < 1){
              return false;
          }

        int aperture = rand.nextInt(changeableAttributes.size()-2) + 1; //om arrayen har en storlek p� 5 s� vill vi slumpa mellan 1 och 4
        if(aperture < 0){
          aperture = 1;
        }
        //rand.nextInt(size()-2) �r d� mellan 0 och 3
        //+1 g�r d� att minimum �r 1 och maximum �r 4
        ArrayList<Attribute> localChangeable = new ArrayList<Attribute>(changeableAttributes);
        ArrayList<Attribute> creepUp = new ArrayList<Attribute>(localChangeable);
        ArrayList<Attribute> reduceOnly = new ArrayList<Attribute>();
        buffer.append("Start randomization with aperture " + aperture + "\nRadicality = " + num+"\nTotal cost = " + getTotalCost() + "\n");
        while(localChangeable.size()>aperture){
          int removeAtRandom = rand.nextInt(localChangeable.size());
          Attribute attribute = localChangeable.remove(removeAtRandom);
          reduceOnly.add(attribute);
          buffer.append("Moving " + attribute.type + " to reduceOnly\n");
        }
        //�ka v�rden slumpm�ssigt inom localChangeable (som �r en samling <aperture> slumpade attributes som f�r �kas)
        for(int a=0;a<num;a++){
          //F� fram ett slumpat nummer som kan anv�ndas f�r att h�mta en attribut i <attributes>
          int attributeToChange = rand.nextInt(localChangeable.size());
          Attribute attribute = localChangeable.get(attributeToChange);
          int change = 1;
          if(attribute.cost < 0){
            change = -1;
          }
          //F�rs�k att �ka denna attribut
          if(attribute.change(change)){
            //Det gick att �ka, s� g�r ingenting
            buffer.append("Changed " + attribute.type + ". Total cost now at " + getTotalCost() + "\n");
          }
          else{
            //Ta bort attributen fr�n localChangeable
            localChangeable.remove(attributeToChange);
            buffer.append("Couldn't change " + attribute.type + ". Removing it from localChangeable.\n");
            if(localChangeable.size()==0){
              buffer.append("Broke away from improvement early. Total cost still " + getTotalCost() + "\n");
              break;
            }
          }
        }

    //  if(totalIncrease==0){
    //   System.out.println("*\n*\n*");
    //   System.out.println("TOTAL INCREASE WAS 0");
    //   System.out.println("*\n*\n*");
    //  }
    //
    //  boolean debug = false;
    //  if(getAttribute("Damage").current < -1){
    //   debug = true;
    //   System.out.println("*\n*\n*");
    //   System.out.println("Debug starts at");
    //   print();
    //  }

        //S�nk v�rden som inte �kades slumpm�ssigt tills vi ligger p� budget
        int totalCost = getTotalCost();
        boolean forceBreak = false;
        while(overBudget(totalCost) && !forceBreak){
          //F� fram ett slumpat nummer som kan anv�ndas f�r att h�mta en attribut i reduceOnly
          int attributeToChange = rand.nextInt(reduceOnly.size());
          int change = -1;
          if(reduceOnly.get(attributeToChange).cost < 0){
            change = 1;
          }
          //F�rs�k att s�nka den
          if(reduceOnly.get(attributeToChange).change(change)){
            //Det gick, s� uppdatera totalCost
            totalCost += (reduceOnly.get(attributeToChange).cost * change);
            buffer.append("Changed (negative) " + reduceOnly.get(attributeToChange).type + ". Total cost now at " + getTotalCost() + "\n");
          }
          else{
            //Det gick inte, s� ta bort den fr�n reduceOnly och kolla om det finns n�got mer att g�ra
            buffer.append("Couldn't change (negative) " + reduceOnly.get(attributeToChange).type + ". Removing it from reduceOnly.\n");
            reduceOnly.remove(attributeToChange);
            if(reduceOnly.size()==0){
              buffer.append("Broke away from reduction early. Total cost still " + getTotalCost() + "\n");
              forceBreak = true;
            }
          }
        }

        if(forceBreak){
          buffer.append("Forcebreaking : End of history. TotalCost at " + getTotalCost());
          history = history + buffer.toString();
          return false;
        }
        else{

          if(overBudget()){
            System.out.println("Super bugg");
          }
          //Allt gick bra och nu �r vi under budget igen, men kan vi krypa upp till maxCost?
          buffer.append("We start to creep upward.\n");
          while(creepUp.size()>0){
            //Plocka fram en slumpm�ssig attribut ur creepUp
            int attributeNumber = rand.nextInt(creepUp.size());
            Attribute attribute = creepUp.get(attributeNumber);
            int change = 1;
            if(attribute.cost < 0){
              change = -1;
            }
            //G�r det att �ka denna utan att g� �ver budget?
            if(!overBudget(totalCost + (attribute.cost * change))){
              //Ja, men g�r den att �ka?
              if(attribute.change(change)){
                //Ja, s� g�r uppdatera totalCost
                buffer.append("Creeping by improving " + attribute.type + ". Total cost at " + getTotalCost() + "\n");
                totalCost += (attribute.cost * change);
              }
              else{
                //Det gick inte, s� ta bort den fr�n creepUp
                buffer.append("Couldn't creep by improving " + attribute.type + ". Removing it from creepUp\n");
                creepUp.remove(attributeNumber);
              }
            }
            else{
              //Det gick inte, s� ta bort den fr�n creepUp
               buffer.append(attribute.type + " is too expensive. Removing it from creepUp\n");
              creepUp.remove(attributeNumber);
            }
          }
        }
        if(overBudget()){
          System.out.println("Ultrabugg");
        }
        buffer.append("End of history. TotalCost at " + getTotalCost());
        history = history + buffer.toString();
        //System.out.println("History = " + history);
        return true;

        /* Gammal kod
         *
         //S�nk v�rden slumpm�ssigt
         //Eftersom vissa attribut kanske inte kan s�nkas mer och eftersom vi m�ste s�nka lika m�nga g�nger som vi �kar s� kanske vi hamnar i en o�ndlig loop
         //F�r att f�rhindra detta tar vi bort de attributer som inte inte gick att s�nka
         //N�r det inte finns n�gra attributer kvar s� �r forceBreak = true
         boolean forceBreak = false;
         localChangeable = reduceOnly;
         main.optimality += localChangeable.size();
         while(totalIncrease>0 && !forceBreak){
         //F� fram ett slumpat nummer som kan anv�ndas f�r att h�mta en attribut i <attributes>
         int attributeToChange = rand.nextInt(localChangeable.size());
         //F�rs�k att s�nka denna attribut
         if(localChangeable.get(attributeToChange).change(-1)){
         //Det gick att s�nka den, s� s�nk totalIncrease med 1
         totalIncrease--;
         }
         else{
         localChangeable.remove(attributeToChange);
         if(localChangeable.size()==0){
         forceBreak = true;
         }
         }
         main.optimality += 2;
         }

         if(forceBreak){
         if(debug){
         System.out.println("Force Broke (1) at");
         print();
         }
         return false;
         }
         //Nu har vi �kat och s�nkt lika m�nga g�nger, men �r vi �ver budjet?
         int totalCost = getTotalCost();
         localChangeable = new ArrayList<Attribute>(changeableAttributes);
         main.optimality += localChangeable.size() + 1;
         if(overBudget(totalCost)){
         //S�nk v�rden tills vi ligger under budget
         while(!forceBreak && overBudget(totalCost)){
         int attributeToChange = rand.nextInt(localChangeable.size());
         if(!localChangeable.get(attributeToChange).change(-1)){
         //Det gick inte att s�nka, s� ta bort den attributen fr�n localChangeable
         localChangeable.remove(attributeToChange);
         if(localChangeable.size()==0){
         if(debug){
         System.out.println("Force Broke (2) at");
         print();
         }
         forceBreak = true;
         }
         }
         else{
         //Det gick att s�nka, uppdatera totalCost
         totalCost -= changeableAttributes.get(attributeToChange).cost;
         }
         main.optimality += 2;
         }
         }
         //�r vi v�ldigt under budget? (�r total cost mindre �n 95% av max cost?)
         else if(totalCost * 100 < maxCost * 95){
         main.optimality += 1;
         //�ka v�rden s� att total cost inte g�r �ver budget men att vi samtidigt kommer s� n�ra maxCost som m�jligt
         while(localChangeable.size()>0){
         int attributePosition = rand.nextInt(localChangeable.size());
         Attribute attributeToChange = localChangeable.get(attributePosition);
         main.optimality += 3;
         if(!overBudget(attributeToChange.cost + totalCost)){
         //Att �ka attributeToChange g�r inte att vi g�r �ver budget, s� pr�va att �ka
         if(attributeToChange.change(1)){
         //Det gick att �ka, uppdatera totalCost
         totalCost += attributeToChange.cost;
         main.optimality += 1;
         //Eftersom allt gick bra, hoppa till n�sta steg i loopen
         continue;
         }
         }
         //N�gonting gick snett (eftersom continue inte kallades). Ta bort attributen fr�n localChangeable och kolla om det finns n�gra kvar
         localChangeable.remove(attributePosition);
         main.optimality += 1;
         }
         }

         return !forceBreak;
         */
      }

      //Returnerar true om totala kostnaden f�r utrustningen �verstiger max budget
      public boolean overBudget(){
        if(getTotalCost()>maxCost){
          return true;
        }
        else{
          return false;
        }
      }

      //Extra metod f�r overBudget f�r optimisering i evolutionsprocessen
      public boolean overBudget(int totalCost){
        if(totalCost>maxCost){
          return true;
        }
        else{
          return false;
        }
      }

      //Returnerar totala kostnaden f�r utrustningen
      public int getTotalCost(){
       int total = 0;
       for(int a=0;a<attributes.size();a++){
        Attribute att = attributes.get(a);
        total+= att.cost * att.current;
       }
       return total;
      }

    public Settings clone() {
        return new Settings(this);
    }

    /*
     * Method Name : getSaveString
     * Parameters : None
     * Returns : A String that represents this Settings-object as a base Settings object inside a .txt file
     */
    public String getSaveString(){
        //Get the linebreak character
        String linebreak = System.getProperty("line.separator");
        //Add BASE_Settings to what we want to return
        StringBuilder build = new StringBuilder("BASE_Settings" + linebreak);
        //Go through all Attributes inside this Settings-object
        for(int a=0;a<attributes.size();a++){
            //For each Attribute inside this Settings-object, add its getSaveString to what we want to return
            build.append(attributes.get(a).getSaveString());
        }
        //Add a "end of Settings"-type message to the end
        build.append("BASE_End" + linebreak);
        //Save the entire string inside str
        String str = build.toString();
        //System.out.println("Settings saved as " + str);
        //Return str
        return str;
    }

    public String getCurrentSaveString(){
        String linebreak = System.getProperty("line.separator");
        StringBuilder build = new StringBuilder("NEW_Settings" + linebreak);
        for(int a=0;a<attributes.size();a++){
            build.append("" + attributes.get(a).current + linebreak);
        }
        build.append("FITNESS:" + currentFitness + linebreak);
        return build.toString();
    }
}

package faltkullen;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
public class SettingsIO {

    public static ArrayList<Settings> loadSettings(String fileName, Main main) {
        File f = new File(fileName + ".txt");
        return loadSettings(f, main);
    }
    public static ArrayList<Settings> loadSettings(File f, Main main){
        ArrayList<Settings> retur = new ArrayList<Settings>();
        Settings base = new Settings(main);
        retur.add(base);
        try {
            FileReader reader = new FileReader(f);
            BufferedReader br = new BufferedReader(reader);
            String currentLine = "";
            String[] splitted;
            while ((currentLine = br.readLine()) != null) {
                splitted = currentLine.split(":");
                if(splitted[0].equals("TOPIC")){
                    if(splitted[1].equals("Settings")){
                        continue;
                    }
                    else{
                        System.out.println("Filetype is wrong");
                        throw new IOException();
                    }
                }
                else if(splitted[0].equals("BASE_Settings")){
                    currentLine = br.readLine();
                    splitted = currentLine.split(":");
                    while(splitted[0].equals("NEW_Attribute")){
                        Attribute att = new Attribute(splitted[1],
                                Integer.parseInt(splitted[3]),
                                Integer.parseInt(splitted[2]),
                                Integer.parseInt(splitted[4]),
                                Integer.parseInt(splitted[5]),
                                base);
                        att.allowsChange = Boolean.parseBoolean(splitted[6]);
                        base.attributes.add(att);
                        if(att.allowsChange){
                            base.changeableAttributes.add(att);
                        }
                        splitted = br.readLine().split(":");
                    }
                }
                else if(splitted[0].equals("NEW_Settings")){
                    Settings newSettings = base.clone();
                    currentLine = br.readLine();
                    splitted = currentLine.split(":");
                    int a=0;
                    while(!splitted[0].equals("FITNESS")){
                        newSettings.attributes.get(a).current = Integer.parseInt(splitted[0]);
                        a++;
                        splitted = br.readLine().split(":");
                    }
                    newSettings.currentFitness = Integer.parseInt(splitted[1]);
                    retur.add(newSettings);
                }
                else if(splitted[0].equals("ENDTOPIC")){
                    break;
                }
            }
            return retur;
        }
        catch (FileNotFoundException fnfe){
            System.out.println("File could not be found");
        }
        catch (IOException e) {
            System.out.println("Something went wrong (File found)");
        }
        return null;
    }
    public static void saveSettings(ArrayList<Settings> s, String fileName) {
        File f = new File(fileName + ".txt");
        saveSettings(s, f);
    }
    public static void saveSettings(ArrayList<Settings> s, File f){
        //We get the linebreak for whatever system we are currently on
        String linebreak = System.getProperty("line.separator");
        try{
            //outString starts out as being what we want at the top of the text file. Just an indicator of what type of file it is maybe? Dunno, its better than nothing
            String outString = "TOPIC:Settings" + linebreak;
            StringBuilder builder = new StringBuilder(outString);

            //We want to write the base settings for this run
            builder.append(s.get(0).getSaveString());
            for(int a=0;a<s.size();a++){
                String saveString = s.get(a).getCurrentSaveString();
                builder.append(saveString);
            }
            builder.append("ENDTOPIC");
            FileWriter outWriter = new FileWriter(f);
            BufferedWriter bufferedWriter = new BufferedWriter(outWriter);
            bufferedWriter.write(builder.toString());
            bufferedWriter.close();
        }
        catch(IOException e){
            System.out.println("Failed to Save to file.");
        }
    }
}

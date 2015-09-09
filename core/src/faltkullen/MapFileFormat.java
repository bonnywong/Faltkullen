package faltkullen;

import java.awt.Image;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import javax.swing.ImageIcon;

public class MapFileFormat implements Serializable {

    public ImageIcon bakgrundsbild;
    public Position rutstorlek;
    public Square[][] kartmatris;

    public MapFileFormat() {
    }

    public MapFileFormat(Image img, Position p, Square[][] matrix) {
        bakgrundsbild = new ImageIcon(img);
        rutstorlek = p;
        kartmatris = matrix;

        //  try {
        //   img = ImageIO.read(new File("strawberry.jpg"));
        //  } catch (IOException e) {
        //   // TODO Auto-generated catch block
        //   e.printStackTrace();
        //  }
    }

    public void exportToFile() {
        try {

            FileOutputStream fout = new FileOutputStream("hej.ser");
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(this);
            oos.close();
            fout.close();
            System.out.println("Exported to file.");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void importFromFile() {
        try {

            MapFileFormat f = new MapFileFormat();
            FileInputStream fin = new FileInputStream("hej.ser");
            ObjectInputStream ois = new ObjectInputStream(fin);
            f = (MapFileFormat) ois.readObject();
            ois.close();

            this.bakgrundsbild = f.getBakgrund();
            this.rutstorlek = f.getRutstorlek();
            this.kartmatris = f.getKartMatris();
            //      this.name = f.name;

        } catch (Exception ex) {
            ex.printStackTrace();
            System.err.println("couldn't import that specific file! TRY AGAIN, MY LORD!");
        }
    }

    public ImageIcon getBakgrund() {
        return bakgrundsbild;
    }

    public Position getRutstorlek() {
        return rutstorlek;
    }

    public Square[][] getKartMatris() {
        return kartmatris;
    }
}

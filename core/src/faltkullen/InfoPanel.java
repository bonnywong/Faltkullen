package faltkullen;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Graphics;

public class InfoPanel extends JPanel {
    int[][] info;

    public InfoPanel(int[][] trackMovement) {
        super();
        setBounds(0, 0, 400, 400);
        info = trackMovement;
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int a = 0; a < 400; a++) {
            for (int b = 0; b < 400; b++) {
                g.setColor(getColor(info[a][b]));
                g.drawRect(a, b, 1, 1);
            }
        }
    }

    public Color getColor(int a) {
        if (a != 0) {
            //System.out.println("a is " + a);
        }
        if (a < 256) {
            return new Color(0, 0, a);
        } else if (a >= 256 && a < 512) {
            return new Color(0, a - 256, 512 - a);
        } else if (a < 768) {
            return new Color(a - 512, 768 - a, 0);
        } else {
            return new Color(256, 0, 0);
        }
    }
}
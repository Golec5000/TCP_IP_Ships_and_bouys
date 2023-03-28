package pwr.ryszkowski.daniel.gui.panels;

import pwr.ryszkowski.daniel.clients.hq.HQ;
import pwr.ryszkowski.daniel.gui.labels.MyLabel;

import javax.swing.*;
import java.awt.*;

public class HQPanelLegend extends JPanel {

    private final HQ hq;

    public HQPanelLegend (HQ hq){

        this.hq = hq;
        this.setSize(250,850);
        this.setBackground(Color.GRAY);
        this.setLocation(800,0);
        this.setLayout(null);

        setLabels();
    }
    /*
    tabela kolorów
    green - > 0
    pink -> 1-2
    orange - > 3
    red -> 4
    black -> >=5
     */

    public void setLabels(){

        MyLabel legend = new MyLabel("Legend:",50);
        legend.setHorizontalAlignment(SwingConstants.CENTER);

        MyLabel green = new MyLabel("dark green - > 0", 100);
        green.setHorizontalAlignment(SwingConstants.CENTER);

        MyLabel pink = new MyLabel("pink -> 1-2", 140);
        pink.setHorizontalAlignment(SwingConstants.CENTER);

        MyLabel orange = new MyLabel("orange - > 3",180);
        orange.setHorizontalAlignment(SwingConstants.CENTER);

        MyLabel red = new MyLabel("red -> 4", 220);
        red.setHorizontalAlignment(SwingConstants.CENTER);

        MyLabel black = new MyLabel("black -> >=5", 260);
        black.setHorizontalAlignment(SwingConstants.CENTER);

        this.add(legend);

        this.add(green);
        this.add(pink);
        this.add(orange);
        this.add(red);
        this.add(black);

    }


}

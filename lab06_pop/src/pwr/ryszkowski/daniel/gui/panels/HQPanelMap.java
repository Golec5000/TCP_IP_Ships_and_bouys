package pwr.ryszkowski.daniel.gui.panels;

import pwr.ryszkowski.daniel.clients.hq.HQ;
import pwr.ryszkowski.daniel.helperClasses.elemetsOfSea.Buoy;

import javax.swing.*;
import java.awt.*;

public class HQPanelMap extends JPanel {

    private HQ hq;
    private final int yComp = 20;
    private final int xComp = 20;

    public HQPanelMap(HQ hq){
        this.hq = hq;
        this.setSize(800,850);
        this.setLocation(0,0);
        this.setBackground(Color.CYAN);
        this.setLayout(null);
    }

    public void render(){
        while (true){
            try {
                Thread.sleep(1000/60);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            repaint();
        }
    }

    public void paintMap(Graphics2D g2){
        for(int i = 0; i < 800; i += yComp)
            for(int j = 0; j < 800; j += xComp)
                g2.drawRect(j,i,xComp,yComp);
    }

    /*
    tabela kolorów
    green - > 0
    pink -> 1-2
    orange - > 3
    red -> 4
    black -> >=5
     */
    public void paintBuoys(Graphics2D g2){
        for(Buoy b : hq.getBuoysX64().getBuoys()){

            switch (b.getDepth()){
                case 0 -> g2.setColor(new Color(55, 68, 19));
                case 1,2 -> g2.setColor(Color.PINK);
                case 3 -> g2.setColor(Color.ORANGE);
                case 4 -> g2.setColor(Color.RED);
                default -> g2.setColor(Color.BLACK);
            }
            g2.fillRect(b.getxCord(),b.getyCord(),xComp,yComp);

        }
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        paintMap(g2);
        paintBuoys(g2);

    }

}

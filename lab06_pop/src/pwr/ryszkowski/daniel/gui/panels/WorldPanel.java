package pwr.ryszkowski.daniel.gui.panels;


import pwr.ryszkowski.daniel.clients.world.World;
import pwr.ryszkowski.daniel.helperClasses.elemetsOfSea.Buoy;
import pwr.ryszkowski.daniel.helperClasses.sea.BuoysX64;
import pwr.ryszkowski.daniel.helperClasses.elemetsOfSea.HelperShip;

import javax.swing.*;
import java.awt.*;

public class WorldPanel extends JPanel {

    private final World world;
    private final int xComp = 20, yComp = 20;
    private final BuoysX64 x64 = new BuoysX64();

    public WorldPanel(World world){

        this.world = world;
        this.setSize(830,830);
        this.setBackground(Color.CYAN);
        this.setLocation(0,0);
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


    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;

        drawMap(g2);
        drawBuoys(g2);
        drawShips(g2);

    }

    public void drawMap(Graphics2D g2){

        //Rysowanie siatki
        for(int i = 0; i < 800; i += yComp)
            for(int j = 0; j < 800; j += xComp)
                g2.drawRect(j,i,xComp,yComp);

    }

    public void drawBuoys(Graphics2D g2){

        for(Buoy b : x64.getBuoys()) g2.fillRect(b.getxCord(),b.getyCord(),xComp,yComp);

    }

    public void drawShips(Graphics2D g2){
        for(int i = 0; i < 800; i += 20)
            for(int j = 0; j < 800; j += 20)
                for(HelperShip ship : world.getShips())
                    if(ship.getxCord() == j && ship.getyCord() == i) {
                        g2.setColor(Color.red);
                        g2.fillRect(ship.getxCord(), ship.getyCord(), xComp, yComp);
                    }


    }

}

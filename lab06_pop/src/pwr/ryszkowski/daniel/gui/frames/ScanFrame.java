package pwr.ryszkowski.daniel.gui.frames;

import pwr.ryszkowski.daniel.gui.labels.MyLabel;

import javax.swing.*;
import java.util.Arrays;

public class ScanFrame extends JFrame {

    private String commands;
    public ScanFrame(String commands){

        this.commands = commands;

        this.setSize(400,400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Lab06 - ship");
        this.setLayout(null);
        this.setVisible(true);

        addLabels();

    }

    public void addLabels(){

        String []tempShips = commands.split("@");
        //       0           1             2
        //"@" + id + "%" + xCord + "%" + yCord
        int y = 0;

        for(int i = 0; i < tempShips.length; i++){

            String [] temp = tempShips[i].split("%");

            try {

                String scan = "id: " + temp[0] + " -> x:" + temp[1] + " y: " + temp[2];
                this.add(new MyLabel(scan, y));
                y += 30;

            }catch (ArrayIndexOutOfBoundsException ignored){}

        }

    }


}

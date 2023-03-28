package pwr.ryszkowski.daniel.gui.buttons;

import javax.swing.*;

public class MyButton extends JButton {

    public MyButton(String dir, int x, int y){
        this.setText(dir);
        this.setSize(100,50);
        this.setLocation(x,y);
    }

}

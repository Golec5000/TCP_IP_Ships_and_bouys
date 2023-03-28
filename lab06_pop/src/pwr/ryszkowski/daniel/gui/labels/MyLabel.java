package pwr.ryszkowski.daniel.gui.labels;

import javax.swing.*;

public class MyLabel extends JLabel {

    public MyLabel(String text, int y){

        setText(text);
        setLocation(0,y);
        setSize(200,20);

    }

}

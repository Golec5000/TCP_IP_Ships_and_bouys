package org.application.ship_fx.helper;

import javafx.scene.paint.Color;

public class Cord {

    private final int cellSize = 20;
    private int xCord;
    private int yCord;

    private Color color;

    public Cord(int xCord, int yCord) {
        super();
        setxCord(xCord);
        setyCord(yCord);
        setColor(Color.BLUE);

    }

    public int getxCord() {
        return xCord;
    }

    public void setxCord(int xCord) {
        this.xCord = xCord;
    }

    public int getyCord() {
        return yCord;
    }

    public void setyCord(int yCord) {
        this.yCord = yCord;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}

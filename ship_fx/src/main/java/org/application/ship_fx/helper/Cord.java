package org.application.ship_fx.helper;

import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Cord extends Rectangle {

    private final int cellSize = 20;
    private int xCord;
    private int yCord;

    public Cord(int xCord, int yCord) {
        super();
        setxCord(xCord);
        setyCord(yCord);
        setLayoutX(xCord * cellSize);
        setLayoutY(yCord * cellSize);
        setWidth(cellSize);
        setHeight(cellSize);
        setFill(Color.BLUE);
        setStroke(Color.BLACK);
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
}

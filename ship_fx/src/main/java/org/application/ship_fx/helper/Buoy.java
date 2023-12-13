package org.application.ship_fx.helper;

import javafx.scene.paint.Color;

public class Buoy {
    private int x;
    private int y;
    private int depth;
    private Color color;

    public Buoy(int x, int y) {
        setX(x);
        setY(y);
        setDepth(0);
        setColor(Color.color(55/255.0, 68/255.0, 19/255.0));
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public String toString(int id) {
        return "@" +  id + "%" + depth;
    }

    public String logString(int id) {
        return "Buoy " + id + " x: " + x + " y: " + y + " depth: " + depth;
    }
}

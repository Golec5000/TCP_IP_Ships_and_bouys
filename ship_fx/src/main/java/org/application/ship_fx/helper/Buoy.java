package org.application.ship_fx.helper;

public class Buoy {
    private int x;
    private int y;
    private int depth;

    public Buoy(int x, int y) {
        setX(x);
        setY(y);
        setDepth(0);
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

    public String toString(int id) {
        return "@" +  id + "%" + depth;
    }
}

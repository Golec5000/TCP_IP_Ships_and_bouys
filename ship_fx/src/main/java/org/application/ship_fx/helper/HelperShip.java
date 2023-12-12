package org.application.ship_fx.helper;

public class HelperShip {

    private int xCord;
    private int yCord;

    public HelperShip(int x, int y) {
        this.xCord = x;
        this.yCord = y;
    }
    public String cordForBuoys(){
        return "@" + (xCord/20) + "%" + (yCord/20);
    }
    public String scanning(int id){
        return "@" + id + "%" + xCord + "%" + yCord;
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

package pwr.ryszkowski.daniel.helperClasses.sea;

public class Cord {

    private int x,y;
    private boolean occupied;

    public Cord(int x, int y){
        this.x = x;
        this.y = y;
        this.occupied = false;
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

    public boolean isOccupied() {
        return occupied;
    }

    public void setOccupied(boolean occupied) {
        this.occupied = occupied;
    }
}

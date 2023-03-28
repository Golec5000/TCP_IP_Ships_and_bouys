package pwr.ryszkowski.daniel.helperClasses.elemetsOfSea;

public class HelperShip {

    private int xCord, yCord,id;

    public HelperShip(int x, int y, int id){

        this.xCord = x;
        this.yCord = y;
        this.id = id;

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

    public int getId() {
        return id;
    }

    @Override
    public String toString(){
        return id + "= x: " + xCord + " y: " + yCord;
    }
    public String cordForBuoys(){
        return "@" + (xCord/20) + "%" + (yCord/20);
    }
    public String scanning(){
        return "@" + id + "%" + xCord + "%" + yCord;
    }


}

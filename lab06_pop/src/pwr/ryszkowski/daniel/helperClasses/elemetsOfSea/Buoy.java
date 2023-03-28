package pwr.ryszkowski.daniel.helperClasses.elemetsOfSea;

public class Buoy {

    private int depth;
    private final int xCord;
    private final int yCord;
    private final int id;

    public Buoy(int x, int y, int id){

        this.depth = 0;
        this.xCord = x;
        this.yCord = y;
        this.id = id;

    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getxCord() {
        return xCord;
    }

    public int getyCord() {
        return yCord;
    }

    @Override
    public String toString(){
        return "@" +  id + "%" + depth;
    }
}

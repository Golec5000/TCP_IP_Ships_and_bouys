package pwr.ryszkowski.daniel.helperClasses.sea;

import pwr.ryszkowski.daniel.helperClasses.sea.Cord;

import java.util.ArrayList;

public class MapOfSea {

    private final ArrayList<Cord> cords;

    public MapOfSea(){

        cords = new ArrayList<>();
        creatCords();
    }

    public void creatCords(){

        for(int y = 0; y < 800; y +=20)
            for(int x = 0; x < 800; x +=20)
                cords.add(new Cord(x,y));

    }

    public ArrayList<Cord> getCords() {
        return cords;
    }
}

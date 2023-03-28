package pwr.ryszkowski.daniel.helperClasses.sea;

import pwr.ryszkowski.daniel.helperClasses.elemetsOfSea.Buoy;

import java.util.ArrayList;

public class BuoysX64 {

    private final ArrayList<Buoy> buoys;

    public BuoysX64(){

        buoys = new ArrayList<>();
        creatBuoys();
    }
    public void creatBuoys(){
        int id = 0;
        for(int i=40;i<800;i+=100){
            for(int j=40;j<800;j+=100){
                id ++;
                buoys.add(new Buoy(j,i,id));
            }
        }
    }

    public synchronized ArrayList<Buoy> getBuoys() {
        return buoys;
    }

}

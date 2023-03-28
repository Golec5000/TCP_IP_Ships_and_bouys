package pwr.ryszkowski.daniel.clients.world;

import pwr.ryszkowski.daniel.clients.control.Msg;
import pwr.ryszkowski.daniel.gui.panels.WorldPanel;
import pwr.ryszkowski.daniel.helperClasses.elemetsOfSea.HelperShip;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class World extends Msg {

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {

        SwingUtilities.invokeAndWait(() ->{

            World world = new World();
            world.actions();

        });
    }

    private final ArrayList<HelperShip> ships = new ArrayList<>();
    private int sendingPort;
    private WorldPanel wp;

    public World(){

        try{
            port = 1000;
            sendingPort = 6666;
            ss = new ServerSocket(port);

            sendMsg("creatWorld", String.valueOf(port),sendingPort);

            String []temp = getMsg().split("[%,#]");
            //"creat" + "#" + port
            if(temp[0].equals("creatWorld")) System.out.println("new World");

        }catch (IOException e){
            e.printStackTrace();
        }
        creatFrame();
        mapRender();

    }

    public void actions(){

        new Thread(() ->{
            while (true){

                actionsOnMap();

            }
        }).start();

    }

    public void actionsOnMap(){

        String [] command = getMsg().split("[#,%]");

        //                0                1                    2                 3
        //newCommand = "creat" + "#" + cord.getX() + "," + cord.getY() + "%" + newPort;
        switch (command[0]) {
            case "creat" -> {
                int x = 0;
                int y = 0;
                int id = 0;

                try {

                    id = Integer.parseInt(command[3]);
                    x = Integer.parseInt(command[1]);
                    y = Integer.parseInt(command[2]);

                } catch (NumberFormatException ignored) {

                }

                HelperShip ghost = new HelperShip(x, y, id);

                if (!ships.isEmpty()) ships.add(ghost);

                boolean test = false;

                for (HelperShip ship : ships)
                    if (ship.getId() == ghost.getId()) {
                        test = true;
                        break;
                    }

                if (!test) ships.add(ghost);

                depthOfBuoys();

            }
            case "move" ->{
                //   0              1          2         3
                //"move" + "# " + port + "," + x + "," + y ( 2 i 3 -> współrzędne przesunięcia)

                int id = 0;
                int x = 0;
                int y = 0;

                try{

                    id = Integer.parseInt(command[1]);
                    x = Integer.parseInt(command[2]);
                    y = Integer.parseInt(command[3]);

                }catch (NumberFormatException ignored){

                }

                int nextX = 0;
                int nextY = 0;

                for(HelperShip ship : ships){
                    if(ship.getId() == id){

                        nextX = ship.getxCord() + x;
                        nextY = ship.getyCord() + y;

                        if(nextX < 0) nextX = 780;
                        if(nextX >= 800) nextX = 0;

                        if(nextY < 0) nextY = 780;
                        if(nextY >= 800) nextY = 0;

                        ship.setxCord(nextX);
                        ship.setyCord(nextY);

                        String newCommand = ship.getId() + "," + ship.getxCord() + "," + ship.getyCord();
                        sendMsg("doneMove", newCommand, sendingPort);

                        break;

                    }
                }

                ArrayList<Integer> ids = new ArrayList<>();

                for(HelperShip s1 : ships){
                    for(HelperShip s2 : ships){
                        if(s1.getId() != s2.getId()){
                            if(s1.getxCord() == s2.getxCord() && s1.getyCord() == s2.getyCord() && !checkID(s2.getId(),ids) && !checkID(s1.getId(), ids)){

                                ids.add(s2.getId());
                                ids.add(s1.getId());

                            }

                        }
                    }
                }

                if(ids.size() > 0) System.out.println(ids);

                for(Integer integer : ids){
                    for (int i = ships.size() - 1; i <= 0 ; i--){
                        if(ships.get(i).getId() != integer) continue;
                        ships.remove(ships.get(i));

                    }
                }

                if(ids.size() > 0) {
                    StringBuilder sb = new StringBuilder();

                    for (Integer i : ids)
                        sb.append(i).append("%");

                    sendMsg("collider", sb.toString(), sendingPort);
                }
                depthOfBuoys();

            }

            case "scan" ->{

                int id = 0;

                try{
                    id = Integer.parseInt(command[1]);
                }catch (NumberFormatException ignored){}

                StringBuilder sb = new StringBuilder();

                for(HelperShip ship : ships) sb.append(ship.scanning());

                String temp = sb.toString();

                sendMsg("doneScan",id + "$" + temp,sendingPort);


            }

        }



    }

    public boolean checkID(int id,ArrayList<Integer> ids){

        for(Integer i : ids) if(i == id) return true;
        return false;
    }

    public void depthOfBuoys(){

        StringBuilder sb = new StringBuilder();
        for(HelperShip ship : ships) sb.append(ship.cordForBuoys());
        sendMsg("buoys",sb.toString(),sendingPort);

    }

    public void creatFrame(){
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(830,850);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Lab06 - world map");
        this.setLayout(null);
        this.setVisible(true);

        wp = new WorldPanel(this);

        this.add(wp);

    }
    public void mapRender(){
        new Thread(wp::render).start();
    }

    public ArrayList<HelperShip> getShips() {
        return ships;
    }
}

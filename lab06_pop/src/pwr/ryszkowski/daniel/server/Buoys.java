package pwr.ryszkowski.daniel.server;

import pwr.ryszkowski.daniel.clients.control.Msg;
import pwr.ryszkowski.daniel.helperClasses.elemetsOfSea.Buoy;
import pwr.ryszkowski.daniel.helperClasses.elemetsOfSea.Waves;
import pwr.ryszkowski.daniel.helperClasses.sea.BuoysX64;
import pwr.ryszkowski.daniel.helperClasses.sea.Cord;
import pwr.ryszkowski.daniel.helperClasses.sea.MapOfSea;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import static java.lang.Thread.sleep;

/**
 * @author Daniel Ryszkowski
 */

public class Buoys extends Msg {

    public static void main(String[] args) {
        Buoys buoys = new Buoys();
        buoys.startOperations();
    }

    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private int uniqPortForWorld;
    private int uniqPortForHQ;
    private final MapOfSea map = new MapOfSea();
    private int[][] levelOfSea;
    private final Waves waves = new Waves();
    private final BuoysX64 buoysX64 = new BuoysX64();


    public Buoys(){

        try {
            ss = new ServerSocket(6666);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void startOperations(){

        //nasłuch
        new Thread(() ->{

            while (true) commands.add(getMsg());

        }).start();

        //działanie
        new Thread(() ->{

            while (true){

                try {

                    String msgType;
                    int portToSend = 0;
                    String newCommand;

                    String command = commands.take();
                    String []temp = command.split("[#,$]");

                    msgType = temp[0];
                    try {
                        portToSend = Integer.parseInt(temp[1]);
                    }catch (NumberFormatException ignored){}

                    switch (msgType){

                        case "creatShip" ->{

                            Cord cord = map.getCords().get(new Random().nextInt(map.getCords().size()));
                            newCommand = cord.getX() + "," + cord.getY() + "%" + portToSend;
                            //odpowiedź do statku
                            sendMsg("creat",newCommand,portToSend);
                            //odpowiedź do świata
                            sendMsg("creat",newCommand,uniqPortForWorld);

                        }
                        case "creatWorld" ->{

                            uniqPortForWorld = portToSend;
                            sendMsg("creatWorld",String.valueOf(uniqPortForWorld),portToSend);

                        }
                        case "creatHQ" ->{

                            uniqPortForHQ = portToSend;
                            sendMsg("creatHQ",String.valueOf(uniqPortForHQ),portToSend);

                        }
                        case "ShipMove" ->{

                            //   0                  1          2         3
                            //"ShipMove" + "# " + port + "," + x + "," + y
                            newCommand = temp[1] + "," + temp[2] + "," + temp[3];
                            sendMsg("move",newCommand,uniqPortForWorld);

                        }
                        case "doneMove" ->{

                            //     0                 1                      2                       3
                            //"doneMove" + "#" +ship.getId() + "," + ship.getxCord() + "," + ship.getyCord();
                            try{

                                portToSend = Integer.parseInt(temp[1]);

                            }catch (NumberFormatException ignored){

                            }
                            newCommand = temp[1] + "," + temp[2] + "," + temp[3];
                            sendMsg("doneMove",newCommand,portToSend);

                        }
                        case "scan" ->{

                            //   0          1
                            //"scan" + # + port
                            sendMsg("scan",temp[1],uniqPortForWorld);

                        }
                        case "doneScan" ->{

                            //    0               1              2
                            //"doneScan" + "#" + port + "$" + scanresult
                            newCommand = temp[1] + "$" + temp[2];
                            sendMsg("doneScan",newCommand, portToSend);
                        }
                        case "buoys" -> buoysAction(temp[1]);
                        case "collider" -> collider(temp[1]);
                        default -> {}

                    }


                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }



            }

        }).start();

        //wysyłąniedo centrali wartości boji
        new Thread(() ->{

            while (true){

                if(uniqPortForHQ > 0){

                    StringBuilder sb = new StringBuilder();

                    for(Buoy buoy : buoysX64.getBuoys()) sb.append(buoy.toString());

                    sendMsg("buoysLvl",sb.toString(),uniqPortForHQ);

                }

                try {
                    sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }).start();

    }

    public void collider(String command){
        String[] temp = command.split("%");

        for (String s : temp) {

            try {

                sendMsg("dead", s, Integer.parseInt(s));

            } catch (NumberFormatException ignored) {}
        }

    }
    public void buoysAction(String command){

        levelOfSea = new int[40][40];


        String []tempLvl1 = command.split("@");
        ArrayList<ArrayList<Integer>> lvl = new ArrayList<>();

        //           0                1
        //"@" + (xCord/20) + "%" + (yCord/20)

        for (String s : tempLvl1) {

            String[] tempLvl2 = s.split("%");
            ArrayList<Integer> temp = new ArrayList<>();

            try {

                temp.add(Integer.parseInt(tempLvl2[0]));
                temp.add(Integer.parseInt(tempLvl2[1]));

            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {}

            lvl.add(temp);

        }

        //usunięcie pierszej pustej tablicy indeksów
        lvl.remove(0);

        for(ArrayList<Integer> temp : lvl){

            int x = temp.get(0);
            int y = temp.get(1);

            for(int i = -2; i < 3; i++){

                x += i;

                for(int j = -2; j < 3; j++){

                    y += j;

                    if(isInBorder(x,y)) levelOfSea[y][x] += waves.getPowerOfWaves()[i+2][j+2];

                    y = temp.get(1);
                }

                x = temp.get(0);
            }

        }

//        for(int i = 0; i < 40; i++) {
//            for (int j = 0; j < 40; j++)
//                System.out.print(levelOfSea[i][j] + " ");
//            System.out.println();
//        }

        //przypisanie wartości zanużeżenia każdej boi
        for(Buoy buoy : buoysX64.getBuoys()) buoy.setDepth(levelOfSea[buoy.getyCord()/20][buoy.getxCord()/20]);

//        for(Buoy buoy : buoysX64.getBuoys()) System.out.println(buoy.toString());


    }

    public boolean isInBorder(int col, int row){
        //sprawdzenie czy jest coś pod taki indeksami
        try{
            //jest
            int test = levelOfSea[row][col];
            return true;

        }catch (ArrayIndexOutOfBoundsException e){
            //nie ma
            return false;
        }

    }

}

package org.application.ship_fx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.application.ship_fx.helper.Buoy;
import org.application.ship_fx.helper.Cord;
import org.application.ship_fx.helper.MapOfSea;
import org.application.ship_fx.helper.Waves;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.*;


public class ServerController extends MessageCenter implements Initializable {

    @FXML
    private TextArea serverLogs;

    @FXML
    private AnchorPane mainScene;

    @FXML
    private Button disconnectButton;

    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private int uniqPortForSea;
    private int uniqPortForHQ;
    private boolean isRunning = true;
    private MapOfSea map;
    private final Waves waves = new Waves();
    private final ConcurrentMap<Integer, Buoy> buoys = new ConcurrentHashMap<>();
    private int[][] levelOfSea;
    private ExecutorService executorService;
    private final int gridSize = 40;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        serverLogs.setEditable(false);
        map = new MapOfSea();
        executorService = Executors.newFixedThreadPool(3);
        createBuoys();

        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        disconnectButton.setOnAction(e -> stop());

        startOperations();

    }

    private void startOperations() {
        executorService.submit(() -> {

            //nasłuchiwanie na portach
            while (isRunning) {
                String command = receiveMessage();
                if (command != null) {
                    addLog(command);
                    commands.add(command);
                } else {
                    System.out.println("Received null message");
                }
            }

        });

        executorService.submit(() -> {

            while (isRunning) {

                try {

                    String msgType;
                    int portToSend = 0;
                    String newCommand;

                    String command = commands.take();
                    String[] temp = command.split("[#,$]");

                    msgType = temp[0];
                    try {
                        portToSend = Integer.parseInt(temp[1]);
                    } catch (NumberFormatException ignored) {
                    }

                    switch (msgType) {
                        case "creatShip" -> {

                            Cord cord = map.getCells().get(new Random().nextInt(map.getCells().size()));
                            newCommand = cord.getxCord() + "," + cord.getyCord() + "$" + portToSend;
//                          //odpowiedź do statku
                            addLog(newCommand);
                            sendMessage("creat", newCommand, portToSend);
                            //odpowiedź do świata
                            sendMessage("creat", newCommand, uniqPortForSea);

                        }

                        case "creatWorld" -> {
                            uniqPortForSea = portToSend;
                            addLog("World created");
                        }

                        case "creatHQ" -> {
                            uniqPortForHQ = portToSend;
                            addLog("HQ created");
                        }

                        case "ShipMove" -> {

                            //   0                  1          2         3
                            //"ShipMove" + "# " + port + "," + x + "," + y
                            newCommand = temp[1] + "," + temp[2] + "," + temp[3];
                            addLog(newCommand);
                            sendMessage("move", newCommand, uniqPortForSea);

                        }
                        case "doneMove" -> {

                            //     0                 1                      2                       3
                            //"doneMove" + "#" +ship.getId() + "," + ship.getxCord() + "," + ship.getyCord();
                            try {

                                portToSend = Integer.parseInt(temp[1]);

                            } catch (NumberFormatException ignored) {
                            }

                            newCommand = temp[1] + "," + temp[2] + "," + temp[3];
                            addLog(newCommand);
                            sendMessage("doneMove", newCommand, portToSend);

                        }
                        case "scan" ->{

                            //   0          1
                            //"scan" + # + port
                            addLog("scan#" + port);
                            sendMessage("scan",temp[1],uniqPortForSea);

                        }

                        case "doneScan" ->{

                            //    0               1              2
                            //"doneScan" + "#" + port + "$" + scanresult
                            newCommand = temp[1] + "$" + temp[2];
                            addLog(newCommand);
                            sendMessage("doneScan",newCommand, portToSend);
                        }

                        case "collision" -> collider(temp[1]);
                        case "buoys" -> createBuoysAction(temp[1]);

                        default -> addLog("unknown command");

                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        });
        executorService.submit(() -> {

            while (isRunning) {

                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                StringBuilder sb = new StringBuilder();

                buoys.forEach((id, buoy) -> {
                    sb.append(buoy.toString(id));
                });
                addLog(sb.toString());
                sendMessage("buoysLvl",sb.toString(),uniqPortForHQ);

            }

        });
    }

    private void addLog(String log) {
        Platform.runLater(() -> serverLogs.appendText(log + "\n"));
    }

    private void collider(String command){
        String[] temp = command.split("%");

        for (String s : temp) {
            System.out.println(s + " -> dead");
            try {
                sendMessage("dead", s, Integer.parseInt(s));

            } catch (NumberFormatException ignored) {}
        }

    }

    private void createBuoys() {

        int id = 0;

        for (int x = 2; x < gridSize; x += 5) {
            for (int y = 2; y < gridSize; y += 5) {
                buoys.put(id++, new Buoy(x, y));
            }
        }
    }

    private void createBuoysAction(String command) {
        levelOfSea = new int[gridSize][gridSize];

        ArrayList<ArrayList<Integer>> lvl = getArrayLists(command);

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
        //przypisanie wartości zanużeżenia każdej boi
        buoys.forEach((id, buoy) -> buoy.setDepth(levelOfSea[buoy.getY()][buoy.getX()]));

    }

    private ArrayList<ArrayList<Integer>> getArrayLists(String command) {
        String[] tempLvl1 = command.split("@");
        ArrayList<ArrayList<Integer>> lvl = new ArrayList<>();

        //           0                1
        //"@" + (xCord/20) + "%" + (yCord/20)

        for (String s : tempLvl1) {

            String[] tempLvl2 = s.split("%");
            ArrayList<Integer> temp = new ArrayList<>();

            try {

                temp.add(Integer.parseInt(tempLvl2[0]));
                temp.add(Integer.parseInt(tempLvl2[1]));

            } catch (ArrayIndexOutOfBoundsException | NumberFormatException ignored) {
            }

            lvl.add(temp);

        }
        //usunięcie pierszej pustej tablicy indeksów
        lvl.removeFirst();
        return lvl;
    }

    private boolean isInBorder(int col, int row) {
        //sprawdzenie czy jest coś pod taki indeksami
        try {
            //jest
            int test = levelOfSea[row][col];
            return true;

        } catch (ArrayIndexOutOfBoundsException e) {
            //nie ma
            return false;
        }

    }

    public void stop() {
        isRunning = false;
        Stage stage = (Stage) mainScene.getScene().getWindow();
        executorService.shutdownNow();
        stage.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

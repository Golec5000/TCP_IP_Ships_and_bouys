package org.application.ship_fx.controller;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.application.ship_fx.helper.Cord;
import org.application.ship_fx.helper.HelperShip;
import org.application.ship_fx.helper.MapOfSea;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;

public class SeaController extends MessageCenter implements Initializable {


    @FXML
    private AnchorPane seaMap;

    private final int gridSize = 40;
    private final int cellSize = 20;
    private MapOfSea mapOfSea;
    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private final ConcurrentMap<Integer, HelperShip> ships = new ConcurrentHashMap<>();
    private final int sendingPort = 6666;
    private boolean isRunning = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        seaMap.setPrefSize(gridSize * cellSize, gridSize * cellSize);
        mapOfSea = new MapOfSea();

        for (Cord cord : mapOfSea.getCells()) seaMap.getChildren().add(cord);

        try{
            port = 1000;
            serverSocket = new ServerSocket(port);

            sendMessage("creatWorld", String.valueOf(port),sendingPort);

            startOperations();

        }catch (IOException e){
            e.printStackTrace();
        }


    }

    private void startOperations(){
        new Thread(() ->{

            while (isRunning){

                commands.add(receiveMessage());

            }

        }).start();

        new Thread(() ->{

            while (isRunning){

                try {
                    String [] command = commands.take().split("[#,%]");

                    switch (command[0]){

                        case "creatWorld" -> System.out.println("new World");

                        case "creat" ->{

                            int x = 0;
                            int y = 0;
                            int id = 0;

                            try {

                                id = Integer.parseInt(command[3]);
                                x = Integer.parseInt(command[1]);
                                y = Integer.parseInt(command[2]);

                            } catch (NumberFormatException ignored) {}

                            HelperShip ghost = new HelperShip(x, y);

                            if(!ships.containsKey(id)) ships.put(id, ghost);


                        }

                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }).start();
    }

    public void stop() {
        isRunning = false;
        Stage stage = (Stage) seaMap.getScene().getWindow();
        stage.close();
    }
}
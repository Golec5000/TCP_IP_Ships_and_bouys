package org.application.ship_fx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.application.ship_fx.helper.Cord;
import org.application.ship_fx.helper.MapOfSea;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        serverLogs.setEditable(false);
        map = new MapOfSea();

        try {
            serverSocket = new ServerSocket(6666);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        disconnectButton.setOnAction(e -> stop());

        startOperations();

    }

    private void startOperations() {
        new Thread(() -> {

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

        }).start();

        new Thread(() -> {

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
                            newCommand = cord.getxCord() + "," + cord.getxCord() + "%" + portToSend;
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

                        default -> addLog("unknown command");

                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }
        }).start();
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

    public void stop() {
        isRunning = false;
        Stage stage = (Stage) mainScene.getScene().getWindow();
        stage.close();
    }
}

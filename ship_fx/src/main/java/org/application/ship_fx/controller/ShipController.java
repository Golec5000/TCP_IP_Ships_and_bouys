package org.application.ship_fx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


public class ShipController extends MessageCenter implements Initializable {

    @FXML
    private TextArea scanTextArea;

    @FXML
    private Button scanButton;

    @FXML
    private Button nButton;

    @FXML
    private Button eButton;

    @FXML
    private Button sButton;

    @FXML
    private Button wButton;

    @FXML
    private Button neButton;

    @FXML
    private Button nwButton;

    @FXML
    private Button seButton;

    @FXML
    private Button swButton;

    @FXML
    private Button connectButton;

    @FXML
    private Label connectStatus;

    @FXML
    private Label positionLabel;

    @FXML
    private Button disconnectButton;

    @FXML
    private AnchorPane mainScene;

    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();

    private final int sendingPort = 6666;
    private int xCord;
    private int yCord;
    private boolean isRunning = true;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        scanTextArea.setEditable(false);
        connectStatus.setText("No connection");
        positionLabel.setText("");
        disconnectButton.setDisable(true);


        nButton.setOnAction(e -> moving(0, 1));
        eButton.setOnAction(e -> moving(1, 0));
        sButton.setOnAction(e -> moving(0, -1));
        wButton.setOnAction(e -> moving(-1, 0));
        neButton.setOnAction(e -> moving(1, 1));
        nwButton.setOnAction(e -> moving(-1, 1));
        seButton.setOnAction(e -> moving(1, -1));
        swButton.setOnAction(e -> moving(-1, -1));

        scanButton.setOnAction(e -> sendMessage("scan", String.valueOf(port), sendingPort));

        disconnectButton.setOnAction(e -> stop());

        connectButton.setOnAction(e -> {
            boolean isPortAlreadyOccupied;

            do {

                this.port = new Random().nextInt(1000) + 2000;

                isPortAlreadyOccupied = isLocalPortInUse(port);

                if (!isPortAlreadyOccupied) {
                    try {
                        serverSocket = new ServerSocket(port);
                    } catch (IOException exception) {
                        throw new RuntimeException(exception);
                    }
                }

            } while (isPortAlreadyOccupied);

            connectButton.setDisable(true);
            disconnectButton.setDisable(false);

            sendMessage("creatShip", String.valueOf(port), sendingPort);

            startOperations();
        });


    }

    private void startOperations() {

        //nasłuchiwanie na portach
        new Thread(() -> {

            while (isRunning) {
                commands.add(receiveMessage());
            }

        }).start();

        new Thread(() -> {

            while (isRunning) {

                try {
                    String command = commands.take();
                    String[] temp = command.split("[#,$%]");

                    System.out.println(Arrays.toString(temp));

                    switch (temp[0]) {

                        case "doneScan" -> scanTextArea.appendText(temp[2] + "\n");

                        case "creat" -> {
                            //                0                1                    2                 3
                            //newCommand = "creat" + "#" + cord.getX() + "," + cord.getY() + "%" + newPort;
                            try {

                                this.xCord = Integer.parseInt(temp[1]);
                                this.yCord = Integer.parseInt(temp[2]);
                                Platform.runLater(() -> {
                                    positionLabel.setText("Position: " + xCord + " " + yCord);
                                    connectStatus.setText("Connected");
                                });

                            } catch (NumberFormatException ignored) {
                            }

                        }

                        case "doneMove" -> {
                            //     0                 1                      2                       3
                            //"doneMove" + "#" +ship.getId() + "," + ship.getxCord() + "," + ship.getyCord();
                            try {

                                this.yCord = Integer.parseInt(temp[3]);
                                this.xCord = Integer.parseInt(temp[2]);
                                Platform.runLater(() -> positionLabel.setText("Position: " + xCord + " " + yCord));

                            } catch (NumberFormatException ignored) {
                            }
                        }

                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        }).start();

    }

    public void moving(int x, int y) {
        String msgToSend = port + "," + x + "," + y;
        sendMessage("ShipMove", msgToSend, sendingPort);
    }

    private boolean isLocalPortInUse(int port) {
        try {
            // ServerSocket próbuje utworzyć lokalne połączenie
            new ServerSocket(port).close();
            // lokalny port nie jest zajęty więc można taki utworzyć
            return false;
        } catch (IOException e) {
            // lokalny port jest zajęty więc nie można utworzyć
            return true;
        }
    }
    public void stop() {
        isRunning = false;
        Stage stage = (Stage) mainScene.getScene().getWindow();
        stage.close();
    }

}

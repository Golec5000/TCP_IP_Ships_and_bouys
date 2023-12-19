package org.application.ship_fx.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import org.application.ship_fx.enums.Direction;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.Arrays;
import java.util.Random;
import java.util.ResourceBundle;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
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

    private ExecutorService executorService;

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

        setButtons(true);

        executorService = Executors.newFixedThreadPool(2);

        nButton.setOnAction(e -> moving(Direction.NORTH));
        eButton.setOnAction(e -> moving(Direction.EAST));
        sButton.setOnAction(e -> moving(Direction.SOUTH));
        wButton.setOnAction(e -> moving(Direction.WEST));
        neButton.setOnAction(e -> moving(Direction.NORTHEAST));
        nwButton.setOnAction(e -> moving(Direction.NORTHWEST));
        seButton.setOnAction(e -> moving(Direction.SOUTHEAST));
        swButton.setOnAction(e -> moving(Direction.SOUTHWEST));

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

            setButtons(false);

            sendMessage("creatShip", String.valueOf(port), sendingPort);

            startOperations();
        });


    }

    private void startOperations() {

        //nasłuchiwanie na portach
        executorService.submit(() -> {

            while (isRunning) {
                if (serverSocket.isClosed()) continue;
                String command = receiveMessage();
                if (command != null) {
                    commands.add(command);
                }
            }

        });

        executorService.submit(() -> {

            while (isRunning) {

                try {
                    String command = commands.take();
                    String[] temp = command.split("[#,$%@]");

                    System.out.println(Arrays.toString(temp));

                    switch (temp[0]) {

                        case "doneScan" -> {

                            scanTextArea.clear();

                            for (int i = 3; i < temp.length; i += 3)
                                scanTextArea.appendText(temp[i] + " ->  x: " + temp[i + 1] + " y: " + temp[i + 2] + "\n");

                        }
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

                                setButtons(false);

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

                        case "dead" -> {

                            Platform.runLater(() -> {
                                disconnectButton.setDisable(true);
                                connectButton.setDisable(false);
                                connectStatus.setText("No connection");
                            });

                            setButtons(true);

                            try {
                                serverSocket.close();
                            } catch (IOException e) {
                                throw new RuntimeException(e);
                            }
                        }

                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        });

    }

    private void moving(Direction direction) {
        String msgToSend = port + "," + direction.getX() + "," + direction.getY();
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
        executorService.shutdownNow();
        Stage stage = (Stage) mainScene.getScene().getWindow();
        stage.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void setButtons(boolean value) {

        Platform.runLater(() -> {
            nButton.setDisable(value);
            eButton.setDisable(value);
            sButton.setDisable(value);
            wButton.setDisable(value);
            neButton.setDisable(value);
            nwButton.setDisable(value);
            seButton.setDisable(value);
            swButton.setDisable(value);
            scanButton.setDisable(value);
        });
    }

}

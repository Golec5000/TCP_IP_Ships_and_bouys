package org.application.ship_fx.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.application.ship_fx.helper.HelperShip;
import org.application.ship_fx.helper.MapOfSea;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.LinkedBlockingQueue;


public class SeaController extends MessageCenter implements Initializable {


    @FXML
    private AnchorPane seaMap;

    @FXML
    private Canvas seaCanvas;

    private final int gridSize = 40;
    private final int cellSize = 20;
    private MapOfSea mapOfSea;
    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private final ConcurrentMap<Integer, HelperShip> ships = new ConcurrentHashMap<>();
    private final int sendingPort = 6666;
    private boolean isRunning = true;

    private GraphicsContext gc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        seaMap.setPrefSize(gridSize * cellSize, gridSize * cellSize);

        seaCanvas.setHeight(gridSize * cellSize);
        seaCanvas.setWidth(gridSize * cellSize);

        seaCanvas.setLayoutX(0);
        seaCanvas.setLayoutY(0);

        mapOfSea = new MapOfSea();

        gc = seaCanvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };

        try {
            port = 1000;
            serverSocket = new ServerSocket(port);

            sendMessage("creatWorld", String.valueOf(port), sendingPort);

            startOperations();
            timer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void draw() {

        gc.clearRect(0, 0, seaCanvas.getWidth(), seaCanvas.getHeight());

        mapOfSea.getCells().forEach(cord -> {
            gc.setFill(cord.getColor());
            gc.fillRect(cord.getxCord() * cellSize, cord.getyCord() * cellSize, cellSize, cellSize);

            gc.setStroke(Color.BLACK);
            gc.strokeRect(cord.getxCord() * cellSize, cord.getyCord() * cellSize, cellSize, cellSize);
        });

        ships.forEach((id, ship) -> {
            gc.setFill(Color.RED);
            gc.fillRect(ship.getxCord() * cellSize, ship.getyCord() * cellSize, cellSize, cellSize);

        });

    }

    private void startOperations() {
        new Thread(() -> {

            while (isRunning) {

                String command = receiveMessage();
                if (command != null) {
                    commands.add(command);
                } else {
                    System.out.println("Received null message");
                }

            }

        }).start();

        new Thread(() -> {

            while (isRunning) {

                try {
                    String[] command = commands.take().split("[#,%]");

                    switch (command[0]) {

                        case "creatWorld" -> System.out.println("new World");

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

                            if (!ships.containsKey(id)) ships.put(id, new HelperShip(x, y));

                            System.out.println(ships.size() + " ships");

                        }

                        case "move" -> {
                            //   0              1          2         3
                            //"move" + "# " + port + "," + x + "," + y ( 2 i 3 -> współrzędne przesunięcia)

                            int id = 0;
                            int x = 0;
                            int y = 0;

                            try {

                                id = Integer.parseInt(command[1]);
                                x = Integer.parseInt(command[2]);
                                y = Integer.parseInt(command[3]);

                            } catch (NumberFormatException ignored) {
                            }

                            int nextX = ships.get(id).getxCord() + x;
                            int nextY = ships.get(id).getyCord() + y;

                            if (nextX < 0) nextX = 39;
                            if (nextX > 39) nextX = 0;

                            if (nextY < 0) nextY = 39;
                            if (nextY > 39) nextY = 0;

                            ships.get(id).setxCord(nextX);
                            ships.get(id).setyCord(nextY);

                            String newCommand = id + "," + ships.get(id).getxCord() + "," + ships.get(id).getyCord();
                            sendMessage("doneMove", newCommand, sendingPort);

                            Set<Integer> keys = new HashSet<>();

                            for (Integer ID : ships.keySet()) {

                                if (ID != id) {
                                    if (ships.get(ID).getxCord() == ships.get(id).getxCord() && ships.get(ID).getyCord() == ships.get(id).getyCord()) {
                                        sendMessage("collision", ID + "," + ships.get(ID).getxCord() + "," + ships.get(ID).getyCord(), sendingPort);
                                        keys.add(ID);
                                        keys.add(id);
                                    }

                                }

                            }

                            if (keys.contains(id)) sendMessage("collision", id + "," + ships.get(id).getxCord() + "," + ships.get(id).getyCord(), sendingPort);

                            for(Integer key : keys) ships.remove(key);

                            System.out.println(ships.size() + " ships");


                        }

                        case "scan" -> {
                            int id = 0;

                            try {
                                id = Integer.parseInt(command[1]);
                            } catch (NumberFormatException e) {
                                System.out.println("Failed to parse ship ID from command: " + command[1]);
                                e.printStackTrace();
                            }

                            StringBuilder sb = new StringBuilder();

                            ships.forEach((key, value) -> sb.append(value.scanning(key)));

                            String temp = sb.toString();

                            sendMessage("doneScan", id + "$" + temp, sendingPort);
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
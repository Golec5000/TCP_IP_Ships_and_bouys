package org.application.ship_fx.controller;

import javafx.animation.AnimationTimer;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.application.ship_fx.helper.Buoy;
import org.application.ship_fx.helper.HelperShip;
import org.application.ship_fx.helper.MapOfSea;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.HashSet;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.*;


public class SeaController extends MessageCenter implements Initializable {

    @FXML
    private AnchorPane seaMap;

    @FXML
    private Canvas seaCanvas;

    private final int gridSize = 40;
    private final int cellSize = 20;
    private final MapOfSea mapOfSea = new MapOfSea();
    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private final ConcurrentMap<Integer, HelperShip> ships = new ConcurrentHashMap<>();
    private final ConcurrentMap<Integer, Buoy> buoys = new ConcurrentHashMap<>();
    private final int sendingPort = 6666;
    private boolean isRunning = true;
    private ExecutorService executorService;
    private GraphicsContext gc;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        seaMap.setPrefSize(gridSize * cellSize, gridSize * cellSize);

        seaCanvas.setHeight(gridSize * cellSize);
        seaCanvas.setWidth(gridSize * cellSize);

        seaCanvas.setLayoutX(0);
        seaCanvas.setLayoutY(0);

        createBuoys();

        gc = seaCanvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };

        executorService = Executors.newFixedThreadPool(2);

        try {
            port = 1000;
            serverSocket = new ServerSocket(port);

            sendMessage("creatWorld", String.valueOf(port), sendingPort);

            startOperations();
            timer.start();

        } catch (IOException e) {
            System.out.println(e.getMessage());
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

        buoys.forEach((id, buoy) -> {
            gc.setFill(Color.BLACK);
            gc.fillRect(buoy.getX() * cellSize, buoy.getY() * cellSize, cellSize, cellSize);
        });

        ships.forEach((id, ship) -> {
            gc.setFill(Color.RED);
            gc.fillRect(ship.getxCord() * cellSize, ship.getyCord() * cellSize, cellSize, cellSize);

        });

    }

    private void startOperations() {
        executorService.submit(() -> {

            while (isRunning) {

                String command = receiveMessage();
                if (command != null) {
                    System.out.println(command);
                    commands.add(command);
                }

            }

        });

        executorService.submit(() -> {

            while (isRunning) {

                try {
                    String[] command = commands.take().split("[#,$]");

                    switch (command[0]) {

                        case "creatWorld" -> System.out.println("new World");

                        case "creat" -> {

                            int x = 0;
                            int y = 0;
                            int id = 0;

                            try {

                                x = Integer.parseInt(command[1]);
                                y = Integer.parseInt(command[2]);
                                id = Integer.parseInt(command[3]);

                            } catch (NumberFormatException ignored) {
                            }

                            if (!ships.containsKey(id)) ships.put(id, new HelperShip(x, y));

                            System.out.println(ships.size() + " ships");

                            depthOfBuoys();

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

                            if (!ships.containsKey(id)) break;

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

                            if (keys.contains(id))
                                sendMessage("collision", id + "," + ships.get(id).getxCord() + "," + ships.get(id).getyCord(), sendingPort);

                            for (Integer key : keys) ships.remove(key);

                            System.out.println(ships.size() + " ships");


                            depthOfBuoys();


                        }

                        case "scan" -> {
                            int id = 0;

                            try {
                                id = Integer.parseInt(command[1]);
                            } catch (NumberFormatException e) {
                                System.out.println(e.getMessage());
                            }

                            StringBuilder sb = new StringBuilder();

                            ships.forEach((key, value) -> sb.append(value.scanning(key)));

                            String temp = sb.toString();

                            sendMessage("doneScan", id + "$" + temp, sendingPort);
                        }

                        default -> System.out.println("Unknown command");


                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            }

        });


    }

    public void stop() {
        isRunning = false;
        executorService.shutdownNow();
        Stage stage = (Stage) seaMap.getScene().getWindow();
        stage.close();
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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

    private void depthOfBuoys() {

        StringBuilder sb = new StringBuilder();
        ships.forEach((key, value) -> sb.append(value.cordForBuoys()));
        sendMessage("buoys", sb.toString(), sendingPort);

    }

}
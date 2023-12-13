package org.application.ship_fx.controller;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import org.application.ship_fx.helper.Buoy;
import org.application.ship_fx.helper.MapOfSea;
import org.application.ship_fx.massage.MessageCenter;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.URL;
import java.util.*;
import java.util.concurrent.*;

public class HQController extends MessageCenter implements Initializable {

    @FXML
    private Canvas seaCanvas;

    @FXML
    private TextArea stausTextArea;

    @FXML
    private SplitPane mainScene;

    private final int gridSize = 40;
    private final int cellSize = 20;
    private final int sendingPort = 6666;
    private ExecutorService executorService;
    private boolean isRunning = true;
    private final BlockingQueue<String> commands = new LinkedBlockingQueue<>();
    private final ConcurrentMap<Integer, Buoy> buoys = new ConcurrentHashMap<>();
    private GraphicsContext gc;

    private final MapOfSea mapOfSea = new MapOfSea();


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        port = 1001;

        stausTextArea.setEditable(false);

        try {
            this.serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendMessage("creatHQ",String.valueOf(port), sendingPort);

        createBuoys();

        gc = seaCanvas.getGraphicsContext2D();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                draw();
            }
        };

        executorService = Executors.newFixedThreadPool(2);

        startOperation();
        timer.start();

    }

    private void draw() {

        gc.clearRect(0, 0, seaCanvas.getWidth(), seaCanvas.getHeight());

        mapOfSea.getCells().forEach(cord -> {
            gc.setStroke(Color.BLACK);
            gc.strokeRect(cord.getxCord() * cellSize, cord.getyCord() * cellSize, cellSize, cellSize);
        });

        buoys.forEach((id, buoy) -> {
            gc.setFill(buoy.getColor());
            gc.fillRect(buoy.getX() * cellSize, buoy.getY() * cellSize, cellSize, cellSize);
        });

    }

    private void startOperation(){

        executorService.submit(() ->{
            while (isRunning){
                String command = receiveMessage();
                if (command != null) {
                    commands.add(command);
                } else {
                    System.out.println("Received null message");
                }
            }
        });

        executorService.submit(() ->{

            while (isRunning) {
                try {
                    String[] command = commands.take().split("#");

                    if (command[0].equals("buoysLvl")) {
                        List<String> list = new ArrayList<>(Arrays.asList(command[1].split("@")));
                        list.removeFirst();

                        list.forEach(buoy -> {
                            String[] temp = buoy.split("%");
                            buoys.get(Integer.parseInt(temp[0])).setDepth(Integer.parseInt(temp[1]));
                        });

                        stausTextArea.clear();

                        buoys.forEach((id, buoy) -> {

                            addLog(buoy.logString(id));

                            switch (buoy.getDepth()) {
                                case 0 -> buoy.setColor(Color.color(55/255.0, 68/255.0, 19/255.0));
                                case 1, 2 -> buoy.setColor(Color.PINK);
                                case 3 -> buoy.setColor(Color.ORANGE);
                                case 4 -> buoy.setColor(Color.RED);
                                default -> buoy.setColor(Color.BLACK);
                            }

                        });

                    } else {
                        System.out.println("Unknown command");
                    }

                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }

        });

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

    private void createBuoys() {

        int id = 0;

        for (int x = 2; x < gridSize; x += 5) {
            for (int y = 2; y < gridSize; y += 5) {

                buoys.put(id++, new Buoy(x, y));

            }
        }
    }
    private void addLog(String log) {
        Platform.runLater(() -> stausTextArea.appendText(log + "\n"));
    }

}


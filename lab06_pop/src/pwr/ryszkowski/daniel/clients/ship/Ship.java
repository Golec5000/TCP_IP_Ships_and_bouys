package pwr.ryszkowski.daniel.clients.ship;

import pwr.ryszkowski.daniel.clients.control.Msg;
import pwr.ryszkowski.daniel.gui.buttons.MyButton;
import pwr.ryszkowski.daniel.gui.frames.ScanFrame;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.Arrays;
import java.util.Random;

public class Ship extends Msg {

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {

        SwingUtilities.invokeAndWait(() ->{
            Ship ship = new Ship();
            ship.actionsOfShip();

        });

    }

    private final int sendingPort;
    private int xCord, yCord;
    private JButton connect;
    private MyButton move, scan;
    private JLabel stats;

    public Ship(){
        this.sendingPort = 6666;
        drawFrame();
    }

    public void actionsOfShip(){

        move.addActionListener(e -> move());
        scan.addActionListener(e -> scan());
        connect.addActionListener(e -> statShip());

    }

    public void move(){

        int[] moving = new int[]{-40,40,0,20,40};
        int x = moving[ new Random().nextInt(moving.length)];
        int y = moving[ new Random().nextInt(moving.length)];

        String msgToSend = port + "," + x + "," + y;

        sendMsg("ShipMove",msgToSend,sendingPort);

        String []temp = getMsg().split("[#,]");

        if(temp[0].contains("doneMove")){
            //     0                 1                      2                       3
            //"doneMove" + "#" +ship.getId() + "," + ship.getxCord() + "," + ship.getyCord();
            try{

                this.yCord = Integer.parseInt(temp[3]);
                this.xCord = Integer.parseInt(temp[2]);
                this.stats.setText("Cords: " + xCord + " " + yCord);

            }catch (NumberFormatException ignored){

            }

        }
        if(temp[0].equals("dead")){
            move.setEnabled(false);
            scan.setEnabled(false);
            stats.setText("Ship destroy");
            try {
                ss.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            JOptionPane.showMessageDialog(null,"Game over","Destroy",JOptionPane.WARNING_MESSAGE);
        }

    }

    public void scan(){
        sendMsg("scan",String.valueOf(port),sendingPort);

        String []temp = getMsg().split("[#,$]");
        System.out.println(Arrays.toString(temp));

        if(temp[0].equals("doneScan")){
            ScanFrame sf = new ScanFrame(temp[2]);
        }
    }

    public void drawFrame(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(250,300);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Lab06 - ship");
        this.setLayout(null);
        this.setVisible(true);

        stats = new JLabel("Cords: " + xCord + " " + yCord);
        stats.setLocation(50,20);
        stats.setSize(200,30);

        move = new MyButton("move",10,70);
        scan = new MyButton("scan",110,70);

        move.setEnabled(false);
        scan.setEnabled(false);

        connect = new JButton("Connect");
        connect.setSize(100,50);
        connect.setLocation(50,160);

        this.add(stats);
        this.add(move);
        this.add(scan);
        this.add(connect);
    }

    public void statShip(){

        boolean isPortAlreadyOccupied;

        do{

            this.port = new Random().nextInt(1000)+2000;

            isPortAlreadyOccupied = isLocalPortInUse(port);

            if(!isPortAlreadyOccupied){
                try {
                    ss = new ServerSocket(port);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }

        }while(isPortAlreadyOccupied);

        sendMsg("creatShip", String.valueOf(port),sendingPort);
        //                0                1                    2                 3
        //newCommand = "creat" + "#" + cord.getX() + "," + cord.getY() + "%" + newPort;

        String[] temp = getMsg().split("[,#%]");
        System.out.println(Arrays.toString(temp));
        if (temp[0].equals("creat")) {

            try {

                this.xCord = Integer.parseInt(temp[1]);
                this.yCord = Integer.parseInt(temp[2]);
                this.stats.setText("Cords: " + xCord + " " + yCord);

            } catch (NumberFormatException ignored) {}

        }

        connect.setEnabled(false);
        move.setEnabled(true);
        scan.setEnabled(true);

    }

    private boolean isLocalPortInUse(int port) {
        try {
            // ServerSocket próbuje utworzyć lokalne połączenie
            new ServerSocket(port).close();
            // lokalny port nie jest zajęty więc można taki utworzyć
            return false;
        } catch(IOException e) {
            // lokalny port jest zajęty więc nie można utworzyć
            return true;
        }
    }
}

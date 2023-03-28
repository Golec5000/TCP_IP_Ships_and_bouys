package pwr.ryszkowski.daniel.clients.hq;

import pwr.ryszkowski.daniel.clients.control.Msg;
import pwr.ryszkowski.daniel.gui.panels.HQPanelLegend;
import pwr.ryszkowski.daniel.gui.panels.HQPanelMap;
import pwr.ryszkowski.daniel.helperClasses.sea.BuoysX64;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.Arrays;

public class HQ extends Msg {

    public static void main(String[] args) throws InterruptedException, InvocationTargetException {
        SwingUtilities.invokeAndWait(() ->{

            HQ hq = new HQ();
            hq.reporting();

        });
    }

    private HQPanelMap hqPanelMap;
    private final BuoysX64 buoysX64 = new BuoysX64();
    public HQ(){

        int sendingPort = 6666;
        this.port = 1001;

        try {
            this.ss = new ServerSocket(port);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        sendMsg("creatHQ",String.valueOf(port), sendingPort);

        String []temp = getMsg().split("[%,#]");

        if(temp[0].equals("creatHQ")) System.out.println(" new HQ");

        creatFrame();
        mapRender();
    }

    public void reporting(){

        new Thread(() -> {

            while (true) hqAction();

        }).start();

    }

    public void hqAction(){

        String []tempV1 = getMsg().split("#");
        System.out.println(Arrays.toString(tempV1));
        if(tempV1[0].equals("buoysLvl")){

            String[] tempV2 = tempV1[1].split("@");

            ArrayList<String> tempV3 = new ArrayList<>(Arrays.asList(tempV2));

            tempV3.remove(0);

            for(int i = 0; i < tempV3.size(); i++){

                try {

                    String []temp = tempV3.get(i).split("%");
                    buoysX64.getBuoys().get(i).setDepth(Integer.parseInt(temp[1]));

                }catch (NumberFormatException ignored){}

            }
        }

    }

    public void creatFrame(){

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setSize(1000,850);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Lab06 - Headquarter");
        this.setLayout(null);
        this.setVisible(true);

        this.hqPanelMap = new HQPanelMap(this);
        HQPanelLegend hqPanelLegend = new HQPanelLegend(this);

        this.add(hqPanelMap);
        this.add(hqPanelLegend);
    }

    public void mapRender(){
        new Thread(hqPanelMap::render).start();
    }

    public BuoysX64 getBuoysX64() {
        return buoysX64;
    }
}

package pwr.ryszkowski.daniel.clients.control;

import javax.swing.*;
import java.io.*;
import java.net.*;

public abstract class Msg extends JFrame {
    protected int port;
    protected ServerSocket ss;

    public void sendMsg(String flagType, String msgToSend, int port){

        Socket s;

        try{

            s = new Socket("localhost", port);
            PrintWriter output = new PrintWriter( s.getOutputStream(),true);

//            System.out.println(flagType + "#" + msgToSend);
            output.println(flagType + "#" + msgToSend);

            output.close();
            s.close();

        } catch (ConnectException | BindException e ){
            System.out.println(e.getMessage());
        } catch (IOException e){
            e.printStackTrace();
        }

    }

    public String getMsg(){

        String newMsg = null;
        try{

            Socket s = ss.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

            newMsg = input.readLine();

            input.close();
            s.close();
        }
        catch (ConnectException | BindException e){
            System.out.println(e.getMessage());
        }
        catch (IOException e){
            e.printStackTrace();
        }

        return newMsg;
    }

}

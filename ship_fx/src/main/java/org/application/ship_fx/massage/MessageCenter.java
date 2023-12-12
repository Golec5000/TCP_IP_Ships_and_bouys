package org.application.ship_fx.massage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.*;

public abstract class MessageCenter {

    protected int port;
    protected ServerSocket serverSocket;

    public void sendMessage(String flagType, String message, int port) {

        try {
            Socket socket = new Socket("localhost", port);

            PrintWriter output = new PrintWriter(socket.getOutputStream(), true);

            System.out.println(flagType + "#" + message);
            output.println(flagType + "#" + message);

            output.close();
            socket.close();

        } catch (ConnectException | BindException e) {
            System.out.println("Server is not running");
            throw new RuntimeException(e);
        } catch (IOException e) {
            System.out.println("Error in sending message");
            System.out.println(e.getMessage());
        }

    }

    public String receiveMessage() {
        String newMsg = null;
        try {

            Socket s = serverSocket.accept();
            BufferedReader input = new BufferedReader(new InputStreamReader(s.getInputStream()));

            newMsg = input.readLine();

            input.close();
            s.close();

        } catch (ConnectException | BindException e) {
            System.out.println("Server is not running");
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println("Error in sending message");
            System.out.println(e.getMessage());
        }

        return newMsg;
    }

}

package models;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

/**
 * Created by Isaac on 1/27/18.
 */
public class ServerListenerThread extends Thread {

    private String name;
    private InputStreamReader input;

    public ServerListenerThread(String name, InputStreamReader input) {
        this.name = name;
        this.input = input;
        System.out.println("Creating Thread \"" + name + "\"");
    }

    public void run() {
        try {
            BufferedReader inputReader = new BufferedReader(input);
            String fromServer = inputReader.readLine();
            while (true) {
//                if (fromServer != null) {
                    System.out.println("[" + name + "]: MESSAGE FROM SERVER: " + fromServer);
//                }
                fromServer = inputReader.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}

package models;

import java.io.*;

/**
 * Created by Isaac on 1/27/18.
 */
public class ServerListenerThread extends Thread {

    private String name;
    private BufferedReader inputStream;

    public ServerListenerThread(String name, InputStream input) {
        this.name = name;
        this.inputStream = new BufferedReader(new InputStreamReader(input));
        System.out.println("Creating Thread \"" + name + "\"");
    }


    public void run() {
        String fromServer;
        try {
            fromServer = inputStream.readLine();
            while (true) {
                if (!fromServer.isEmpty()) {
                    System.out.println("[" + name + "]: MESSAGE FROM SERVER: " + fromServer);
                }
                fromServer = inputStream.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

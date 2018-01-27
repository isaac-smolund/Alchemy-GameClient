package utils;

import com.jme3.app.SimpleApplication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

/**
 * Created by Isaac on 1/27/18.
 */
public class ServerUtils {

    private static final int MAX_RETRIES = 10;
    private static final int RETRY_DELAY = 2000;

    private static Socket gameSocket;
    private static DataOutputStream output;
    private static DataInputStream input;
    private static int retryCounter;

    private static SimpleApplication app;

    public static void setApp(SimpleApplication newApp) {
        app = newApp;
    }

    public static void disconnectFromHost() {
        try {
            output.writeBytes("Client disconnected.\n");
            output.writeBytes("quit\n");
            gameSocket.close();
            input.close();
            output.close();
        } catch (IOException | NullPointerException e) {
            System.err.print("Failed to properly close connection.");
        }
    }

    public static void connectToHost(String hostname, int port) {
        gameSocket = null;
        input = null;
        output = null;
        try {
            gameSocket = new Socket(hostname, port);
            input = new DataInputStream(gameSocket.getInputStream());
            output = new DataOutputStream(gameSocket.getOutputStream());
        } catch (UnknownHostException e) {
            System.err.println("Can't find host: " + hostname);
            if (retryCounter++ <= MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Retrying...");
                connectToHost(hostname, port);
            } else {
                System.err.println("Maximum connection retries exceeded. Exiting...");
                exitGame();
            }
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for connection to: " + hostname);
            if (retryCounter++ <= MAX_RETRIES) {
                try {
                    Thread.sleep(RETRY_DELAY);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                System.out.println("Retrying...");
                connectToHost(hostname, port);
            } else {
                System.err.println("Maximum connection retries exceeded. Exiting...");
                exitGame();
            }
        }

        if (gameSocket != null && output != null && input != null) {
            try {
                output.writeBytes("Connection to client successful!\n");
                System.out.println("Woo!");
//                String responseLine;
//                BufferedReader reader = new BufferedReader(new InputStreamReader(input));
//                while ((responseLine = reader.readLine()) != null) {
//                    if (responseLine.contains("quit")) {
//                        break;
//                    }
//                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        LogUtils.setOutputStream(output);
    }

    public static void exitGame() {
        ServerUtils.disconnectFromHost();
        if (app != null) {
            app.stop();
        }
        System.exit(0);
    }
}

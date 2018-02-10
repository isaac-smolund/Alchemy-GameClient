package utils;

import com.jme3.app.SimpleApplication;
import models.ServerListenerThread;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
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
    private static InputStreamReader input;
    private static int retryCounter;

    private static SimpleApplication app;

    public static void setApp(SimpleApplication newApp) {
        app = newApp;
    }

    public static void disconnectFromHost() {
        try {
            output.writeBytes("Client disconnected.\n");
            output.writeBytes("quit\n");
            closeAll();
        } catch (IOException | NullPointerException e) {
            System.err.print("Failed to properly close connection.");
        }
    }

    private static void closeAll() throws IOException {
        if (gameSocket != null) {
            gameSocket.close();
        }
        if (input != null) {
            input.close();
        }
        if (output != null) {
            output.close();
        }
    }

    public static void connectToHost(String hostname, int port) {
        try {
            closeAll();
            gameSocket = new Socket(hostname, port);
            input = new InputStreamReader(gameSocket.getInputStream());
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
                LogUtils.setOutputStream(output);
                init();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private static void init() throws IOException {
        ServerListenerThread serverThread = new ServerListenerThread("thread_1", gameSocket.getInputStream());
        serverThread.start();
    }

    public static void exitGame() {
        ServerUtils.disconnectFromHost();
        if (app != null) {
            app.stop();
        }
        System.exit(0);
    }
}

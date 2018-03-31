package models.threads;

import com.jme3.app.SimpleApplication;
import utils.GraphicsUtils;

/**
 * Created by Isaac on 2/11/18.
 */
public class MainThread extends Thread{
    private String name;
    private SimpleApplication app;
    private static MainThread instance;
    public MainThread(String name, SimpleApplication app) {
        this.name = name;
        this.app = app;
        System.out.println("Creating thread \"" + name + "\"");
    }
    public void run() {
        app.start();
    }

    public static MainThread getInstance() {
        return instance;
    }

    public static void setInstance(MainThread newInstance) {
        instance = newInstance;
    }

    public void render() {
        GraphicsUtils.renderBoard();
    }

}

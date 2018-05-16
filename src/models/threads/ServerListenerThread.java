package models.threads;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gameState.Game;
import gameState.RenderQueue;
import models.Player;
import models.board.BoardEntity;
import models.board.BoardState;
import utils.LogUtils;

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
                if (fromServer != null && !fromServer.isEmpty()) {
                    System.out.println("[" + name + "]: MESSAGE FROM SERVER: " + fromServer);
                    if (fromServer.charAt(0) == '{') {
                        Gson gson = new GsonBuilder()
                                .registerTypeAdapter(BoardEntity.class, new LogUtils.BoardEntityAdapter())
                                .excludeFieldsWithoutExposeAnnotation()
                                .create();
                        BoardState boardState = gson.fromJson(fromServer, BoardState.class);
                        // Preserve localPlayer flag:
                        for (Player player : Game.players) {
                            if (player.isLocalPlayer) {
                                for (Player newPlayer : boardState.getPlayers()) {
                                    if (newPlayer.getTurnOrder() == player.getTurnOrder()) {
                                        newPlayer.isLocalPlayer = true;
                                    }
                                }
                            }
                        }
                        BoardState.setIntstance(boardState);
                        Game.setPlayers(boardState.getPlayers().get(0), boardState.getPlayers().get(1));

                        RenderQueue.getInstance().queueUpdate(RenderQueue.UpdateType.UPDATE_ALL);
                    }
                    else if (fromServer.contains("end")) {
                        System.out.println("Thread = " + Thread.currentThread());
                        Game.endTurn(Game.getCurrentPlayer());
                    }
                }
                fromServer = inputStream.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

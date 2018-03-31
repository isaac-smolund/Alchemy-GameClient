package models.threads;

import com.google.gson.Gson;
import gameState.Game;
import models.Player;
import models.board.BoardState;
import utils.GraphicsUtils;

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

    private BoardState decode(String stateString) {
        BoardState state = new BoardState();

        for (Player player : Game.players) {
            int nameIndex = stateString.indexOf(player.playerName());
            int startIndex = stateString.indexOf("health=", nameIndex) + 7;
            int endIndex = stateString.indexOf(",", startIndex);
            int playerHealth = Integer.parseInt(stateString.substring(startIndex, endIndex));

            player.getEntity().setCurrentHealth(playerHealth);
        }

        MainThread.getInstance().render();
        return state;
    }

    public void run() {
        String fromServer;
        try {
            fromServer = inputStream.readLine();
            while (true) {
                if (!fromServer.isEmpty()) {
                    System.out.println("[" + name + "]: MESSAGE FROM SERVER: " + fromServer);
                    if (fromServer.charAt(0) == '{') {
                        BoardState boardState = new Gson().fromJson(fromServer, BoardState.class);
                        // Preserve localPlayer flag:
                        for (Player player : Game.players) {
                            if (player.isLocalPlayer) {
                                for (Player newPlayer : boardState.players) {
                                    if (newPlayer.getTurnOrder() == player.getTurnOrder()) {
                                        newPlayer.isLocalPlayer = true;
                                    }
                                }
                            }
                        }
                        BoardState.setIntstance(boardState);
                        Game.setPlayers(boardState.players.get(0), boardState.players.get(1));
                        GraphicsUtils.renderBoard();
                    }
                         else if (fromServer.contains("end")) {
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

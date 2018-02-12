package models;

import gameState.Game;
import libraries.Cards;
import models.cards.Card;
import models.energyUtils.EnergyState;
import models.exceptions.CardNotFoundException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;
import utils.GraphicsUtils;
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
                if (!fromServer.isEmpty()) {
                    System.out.println("[" + name + "]: MESSAGE FROM SERVER: " + fromServer);
                    if (Game.getStatus() != null && Game.getStatus().equals(Game.STATUS.ENEMY_TURN)) {
                        if (fromServer.contains("play")) {
                            String cardName = fromServer.substring(fromServer.indexOf("play") + 5);
                            Card toPlay;
                            if (!cardName.isEmpty()) {
                                toPlay = Cards.getCardFromName(cardName);
                            } else {
                                toPlay = Cards.getCardFromName("Poison Gas");
                            }
                            Game.getCurrentPlayer().playCard(toPlay);
//                            Game.getPlayer().getEntity().dealDamage(5);
                        } else if (fromServer.contains("end")) {
                            Game.endTurn(Game.getCurrentPlayer());
                        } else if (fromServer.contains("yellow")) {
                            Game.getCurrentPlayer().getStoredEnergy().addEnergy(EnergyState.ENERGY_TYPE.YELLOW, 1);
                            GraphicsUtils.renderBoard();
                        }
                    }
                }
                fromServer = inputStream.readLine();
            }
        } catch (IOException | CardNotFoundException | PositionOccupiedException | IllegalMoveException e) {
            e.printStackTrace();
        }
    }

}

package utils;

import gameState.Game;
import models.Player;
import models.board.BoardPosition;
import models.board.BoardState;
import models.cards.Card;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by Isaac on 6/10/17.
 */
public class LogUtils {

    private static DataOutputStream output;
    public static void setOutputStream(DataOutputStream newOutput) {
        output = newOutput;
    }

    private static ArrayList<String> logs = new ArrayList<String>();
    private static int DEFAULT_LOG_LENGTH = 5;
    private static int LOG_LIMIT = 100;
    private static int PRINT_DELAY_MS = 100;

    // Text colors:
    public static final String ANSI_BLACK = "\u001B[30m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_BLUE = "\u001B[34m";
    public static final String ANSI_RESET = "\u001B[0m";

    public static String colorText(String str, String color) {
        return color + str + ANSI_RESET;
    }
    public static String colorRed(String str) {
        return colorText(str, ANSI_RED);
    }
    public static String colorBlue(String str) {
        return colorText(str, ANSI_BLUE);
    }
    public static String colorGreen(String str) {
        return colorText(str, ANSI_GREEN);
    }
    public static String colorYellow(String str) {
        return colorText(str, ANSI_YELLOW);
    }

    public static enum LOG_TYPE {
        PRIVATE,
        PUBLIC
    }

    public static void log(LOG_TYPE type, String logEntry) {
        try {
            output.writeBytes(logEntry + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(logEntry + "\n");
        if (type.equals(LOG_TYPE.PUBLIC)) {
            logs.add(logEntry);
            if (logs.size() > LOG_LIMIT) {
                logs.remove(0);
            }
            try {
                Thread.sleep(PRINT_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void logHistory() {
        logHistory(DEFAULT_LOG_LENGTH);
    }

    public static void logHistory(int numberOfEntries) {
        numberOfEntries = Math.min(numberOfEntries, logs.size());
        for (int i = logs.size() - numberOfEntries; i < logs.size(); i++) {
            System.out.println("[" + (i + 1) + "]: " + logs.get(i));
        }
    }

    public static void logWarning(String warning) {
        log(LOG_TYPE.PRIVATE, colorRed("INVALID ACTION: ") + warning);
        GraphicsUtils.setHudText(warning);
    }

    public static void logCardPlayed(Player player, Card card) {
        log(LOG_TYPE.PUBLIC, player.playerName() + " played " + card.getName() + ".");
    }

    public static void logActionCancelled() {
        log(LOG_TYPE.PRIVATE, "Cancelled.");
        logs.remove(logs.size() - 1);
    }

    public static void printStoredEnergy(Player player) {
        log(LOG_TYPE.PRIVATE, player.getStoredEnergy().toString());
    }

    public static void printBoardState() {
        log(LOG_TYPE.PRIVATE, BoardState.getInstance().toString());
    }

    public static void printHand(Player player) {
        log(LOG_TYPE.PRIVATE, player.getHand().toString());
    }

    public static void printCardDescription(Card card) {
        log(LOG_TYPE.PRIVATE, card.toString());
    }

    public static void printBoardPositionDetails(BoardPosition position) {
        log(LOG_TYPE.PRIVATE, position.toString());
    }

    public static void printPlayerStatus(Player player) {
        log(LOG_TYPE.PRIVATE, player.getEntity().toString());
    }

    public static void printPlayers() {
        for (Player player : Game.players) {
            log(LOG_TYPE.PRIVATE, player.toString());
        }
    }

}
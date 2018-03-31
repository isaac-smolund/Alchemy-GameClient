package gameState;

import com.google.gson.*;
import com.jme3.app.SimpleApplication;
import models.Player;
import models.board.BoardEntity;
import models.board.BoardPosition;
import models.board.BoardState;
import models.board.Hero;
import utils.GraphicsUtils;
import utils.LogUtils;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Isaac on 5/30/17.
 */
public class Game {

    private SimpleApplication app;

    public static ArrayList<Player> players = new ArrayList<>();
    private static Player player;

    private static int turn;
    private static Player winningPlayer = null;
    private static final Player TIE_GAME = new Player(-1, null, false);

    public static final int STARTING_HEALTH = 30;

    public static BoardEntity target;
    private static Hero.CardSelector selector;

    private static STATUS status;

    public enum STATUS {
        ENERGY_PHASE,
        MAIN_PHASE,
        ABILITY_PHASE,
        SELECTING_CARD_TARGET,
        SELECTING_ABILITY_TARGET,
        IMBUING,
        ENEMY_TURN
    };

    public enum OBJECT_TYPE {
        CARD,
        TEXT,
        BOARD,
        POSITION,
        ENERGY,
        BUTTON,
        HIGHLIGHT_NODE,
        IMAGE,
        NONE
    }

    public static STATUS getStatus() {
        return status;
    }

    public static Hero.CardSelector getSelector() {
        return selector;
    }

    public static OBJECT_TYPE stringToObjectType(String str) {
        for (OBJECT_TYPE type : OBJECT_TYPE.values()) {
            if (type.toString().equals(str)) {
                return type;
            }
        }
        return OBJECT_TYPE.NONE;
    }

    public static void setStatusMain() {
        setStatus(STATUS.MAIN_PHASE);
        GraphicsUtils.setHudText("");
//        GraphicsUtils.setSelectables(GraphicsUtils.getHandNode());
        GraphicsUtils.setSelectablesHandAndEnergy();
    }

    public static void setStatusEnergyPhase() {
        setStatus(STATUS.ENERGY_PHASE);
        GraphicsUtils.setHudText("SELECT AN ENERGY TYPE TO GAIN");
        GraphicsUtils.setSelectables(GraphicsUtils.getEnergyNode());
    }

    public static void setStatusAbilityTargeting(Hero.CardSelector newSelector, String helpText) {
        selector = newSelector;
        setStatus(STATUS.SELECTING_ABILITY_TARGET);
        GraphicsUtils.setHudText(helpText);
        GraphicsUtils.setSelectables(GraphicsUtils.getBoardNode());
    }

    public static void setStatusAbilityTargeting(Hero.CardSelector newSelector) {
        setStatusAbilityTargeting(newSelector, "SELECT TARGET FOR ABILITY");
    }

    public static void setStatusTargeting() {
        setStatus(STATUS.SELECTING_CARD_TARGET);
        GraphicsUtils.setHudText("SELECT TARGET");
        GraphicsUtils.setSelectables(GraphicsUtils.getSlotNode());
    }

    public static void setStatusImbuing() {
        setStatus(STATUS.IMBUING);
        GraphicsUtils.setHudText("SELECT CARD TO IMBUE WITH POWER");
        GraphicsUtils.setSelectables(GraphicsUtils.getSlotNode());
    }

    public static void setStatus(STATUS status) {
        GraphicsUtils.setHudText(status.toString());
        Game.status = status;
    }

    public Game(SimpleApplication app, Player ... players) {
        this(players);
        this.app = app;
    }

    public Game(Player ... players) {
        Collections.addAll(Game.players, players);
        player = players[0];
        turn = 0;
    }

    public static void setPlayers(Player ... players) {
        Game.players = new ArrayList<>();
        Collections.addAll(Game.players, players);
    }

    public static Player getPlayer() {
        return player;
    }

    public static Player getCurrentPlayer() {
        System.out.println("Current turn: " + turn);
        for (Player player : players) {
            System.out.println("Checking player with turn #: " + player.getTurnOrder() + " and name: " + player.playerName());
            if (player.getTurnOrder() == turn) {
                return player;
            }
        }
        LogUtils.logWarning("Player not found!");
        return players.get(0);
    }

    public static Player getNextPlayer() {
        for (Player player : players) {
            System.out.println("Searching players for " + (turn + 1) + " ... " + player.getTurnOrder());
            if (player.getTurnOrder() == turn + 1) {
                return player;
            }
        }
        return players.get(0);
    }

    public static Player getPlayerByName(String name) {
        for (Player player : players) {
            if (player.playerName().equalsIgnoreCase(name)) {
                return player;
            }
        }
        LogUtils.logWarning("Player not found!");
        return null;
    }

    private static void incrementTurn() {
        turn = turn >= (players.size() - 1) ? 0 : turn + 1;
    }

    public void start() {
//        InputUtils.initInput();

        for (Player player : players) {
//            System.out.print("Enter name for " + player.playerName() + " (or ENTER to skip): ");
//            String name = InputUtils.getInput();
//            if (!name.isEmpty()) {
//                player.setName(name);
//            }
            player.shuffleDeck();
            player.draw(4);
            System.out.println();
        }

        GraphicsUtils.renderCards(player);


//        while (!gameIsOver()) {
        takeTurn(player);


//        }
//        if (winningPlayer != null && winningPlayer.equals(TIE_GAME)) {
//            LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, "It's a tie!");
//        } else if (winningPlayer != null) {
//            LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, winningPlayer.playerName() + " wins!");
//        }
    }

    private static void takeTurn(Player currentPlayer) {

        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, "*** " + currentPlayer.playerName() + "'s turn ***");

        turn = currentPlayer.getTurnOrder();
        System.out.println("Current turn # is " + turn);

        setStatusEnergyPhase();

        currentPlayer.draw();

        currentPlayer.getStoredEnergy().refill();

//        GsonBuilder builder = new GsonBuilder();
//        Gson gson = builder.excludeFieldsWithoutExposeAnnotation().create();
//        String json = gson.toJson(BoardState.getInstance());
//
//        System.out.println("Json result:\n" + json);

    }

    public static void endTurn(Player currentPlayer) {
        if (currentPlayer.equals(Game.getPlayer())) {
            Game.setStatus(STATUS.ABILITY_PHASE);
            executeEndTurnAbilityForPosition(currentPlayer.getBoard()[0]);
        } else {
            Game.takeTurn(Game.getPlayer());
        }
//        for (BoardPosition pos : currentPlayer.getBoard()) {
//            if (!pos.isEmpty()) {
//                if (pos.getEntity() instanceof Hero) {
//                    Hero entity = (Hero)pos.getEntity();
//                    entity.onTurnEnd();
//
//                    entity.getStoredEnergy().reset();
//                }
//            }
//        }
//        takeTurn(currentPlayer);
//        GraphicsUtils.renderBoard();
//        GraphicsUtils.renderCards(getPlayer());
        //   incrementTurn();
    }

    public static void executeEndTurnAbilityForPosition(BoardPosition pos) {
        if (pos.getEntity() instanceof Hero) {
            Hero entity = (Hero)pos.getEntity();
            entity.onTurnEnd();

            entity.getStoredEnergy().reset();
        }
        if (Game.getStatus().equals(STATUS.SELECTING_ABILITY_TARGET)) {
            return;
        }

        BoardPosition newPos;

        try {
            newPos = BoardState.getInstance().allPositions().get(pos.getPosition() + 1);
        } catch (IndexOutOfBoundsException e) {
            newPos = null;
        }

        if (newPos != null) {
            executeEndTurnAbilityForPosition(newPos);
        } else {
            takeTurn(getNextPlayer());
            Game.setStatus(STATUS.ENEMY_TURN);
            GraphicsUtils.renderBoard();
            GraphicsUtils.renderCards(getPlayer());
        }
    }

    public static void killPlayer(Player player) {
        players.remove(player);

        if (players.size() <= 0) {
            winningPlayer = TIE_GAME;
        } else if (players.size() <= 1) {
            winningPlayer = players.get(0);
        }
    }

    private static boolean gameIsOver() {
        return winningPlayer != null;
    }

}

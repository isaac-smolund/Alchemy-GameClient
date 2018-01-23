package utils;

import gameState.Game;
import libraries.Cards;
import models.Player;
import models.board.BoardEntity;
import models.board.BoardState;
import models.board.Hero;
import models.cards.Card;
import models.cards.EquipmentCard;
import models.cards.RitualCard;
import models.energyUtils.EnergyState;
import models.exceptions.ActionCancelledException;
import models.exceptions.CardNotFoundException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;

import java.util.*;

/**
 * Created by Isaac on 6/4/17.
 */
public class InputUtils {

    private static Scanner scanner;

    public enum INPUT {

        BOARD(text("board", "view the current state of the game board."), "board", "ls"),
        DESCRIBE(text("describe",  "[cn]", "print the text of a card with name " + paramColor("cn") + "."), "describe", "description"),
        HAND(text("hand", "list the cards in your hand."), "hand"),
        HELP(text("help", "display this help menu."), "help", "h"),
        IMBUE(text("imbue", "[pos][a][t]", "provide the card at position " + paramColor("pos") + " with " + paramColor("a") + " units of energy " + paramColor("t") + "."), "imbue"),
        INSPECT(text("inspect", "[pos]", "view details of card at position " + paramColor("pos") + "."), "inspect", "view"),
        LOG(text("log", "[n]*optional*" + "display previous " + paramColor("n") + " actions."), "log"),
        NAME(text("name", "check your name."), "name", "whoami"),
        PASS(text("pass", "end your turn."), "pass", "end"),
        PLAY(text("play", "[cn]", "play a card with name" + paramColor("cn") + " from your hand."), "play"),
        PLAYERS(text("players", "view the list of players."), "players"),
        QUIT(text("quit", "exit game."), "quit"),
        STATUS(text("status", "[n]", "display the current status of player with name " + paramColor("n") + "."), "health", "status"),
        STOCKPILE(text("stockpile", "view your energy stockpile."), "stockpile", "energy", "mana"),

        YES("", "yes", "y"),
        NO("", "no", "n"),

        CANCEL("", "cancel", "exit"),

        RED("", "red", "r"),
        BLUE("", "blue", "b"),
        GREEN("", "green", "g"),
        YELLOW("", "yellow", "y"),

        NOT_FOUND("");

        private String helpText;
        private String[] commands;

        INPUT(String helpText, String ... commands) {
            this.helpText = helpText;
            this.commands = commands;
        }

        private static String paramColor(String param) {
            return LogUtils.colorBlue(param);
        }

        private static String text(String command, String description) {
            return text(command, "", description);
        }
        private static String text(String command, String params, String description) {
            return "\"" + LogUtils.colorGreen(command) + "\" " + (!params.isEmpty() ? paramColor(params) : "") + " - " + description;
        }


        public String helpText() {
            return helpText;
        }

        public String[] acceptedInputs() {
            return commands;
        }

        public boolean isValidForString(String command) {
            return Arrays.asList(commands).contains(command.toLowerCase());
        }
    }

    private static void showHelpMenu() {
        System.out.println();
        for (INPUT input : INPUT.values()) {
            if (!input.helpText.isEmpty()) {
                System.out.println(input.helpText);
            }
        }
        System.out.println();
    }

    public static void initInput() {
        scanner = new Scanner(System.in);
    }

    public static String getInput() {
        return scanner.nextLine();
    }

    public static INPUT getInputAsEnum() {
        String userInput = scanner.nextLine();
        return parseInput(userInput, INPUT.values());
    }

    private static String getNthWord(String str, int n) {
        int wordStart = 0, wordEnd = 0;
        for (int i = 0; i < n; i++) {
            wordStart = wordEnd;
            wordEnd = str.indexOf(" ", wordStart + 1);
        }
        if (wordEnd < 0) {
            wordEnd = str.length();
        }
        return wordEnd > 0 ? str.substring(wordStart, wordEnd).trim() : str.trim();
    }

    private static String getTextAfterFirstWord(String str) {
        int spaceIndex = str.indexOf(" ");
        return spaceIndex > 0 ? str.substring(spaceIndex, str.length()).trim() : "";
    }

    private static INPUT parseInput(String userInput, INPUT ... acceptableInputs) {
        INPUT[] inputsToCheck = acceptableInputs.length < 1 ? INPUT.values() : acceptableInputs;
        for (INPUT input : inputsToCheck) {
            if (input.isValidForString(userInput)) {
                return input;
            }
        }
        return INPUT.NOT_FOUND;
    }

    private static INPUT[] basicInputs() {
        return new INPUT[] {
            INPUT.HELP,
            INPUT.LOG,
            INPUT.HAND,
            INPUT.DESCRIBE,
            INPUT.BOARD
        };
    }

    private static INPUT handleInputGeneric(String userInput, String modifier) {
        return handleInputGeneric(parseInput(userInput), modifier);
    }

    private static INPUT handleInputGeneric(INPUT command, String modifier) {
        Player currentPlayer = Game.getCurrentPlayer();

        try {
            switch (command) {
                case HELP:
                    showHelpMenu();
                    break;
                case STOCKPILE:
                    LogUtils.printStoredEnergy(currentPlayer);
                    break;
                case LOG:
                    if (modifier.isEmpty()) {
                        LogUtils.logHistory();
                    } else {
                        LogUtils.logHistory(Integer.parseInt(modifier));
                    }
                    break;
                case HAND:
                    LogUtils.printHand(currentPlayer);
                    break;
                case DESCRIBE:
                    LogUtils.printCardDescription(Cards.getCardFromName(modifier));
                    break;
                case BOARD:
                    LogUtils.printBoardState();
                    break;
                case INSPECT:
                    LogUtils.printBoardPositionDetails(BoardState.getInstance().allPositions().get(Integer.parseInt(modifier)));
                    break;
                case STATUS:
                    Player playerToInspect = Game.getPlayerByName(modifier);
                    if (playerToInspect != null) {
                        LogUtils.printPlayerStatus(playerToInspect);
                    }
                    break;
                case PLAYERS:
                    LogUtils.printPlayers();
                    break;
                case NAME:
                    LogUtils.log(LogUtils.LOG_TYPE.PRIVATE, "You are " + currentPlayer.playerName());
                    break;
                case QUIT:
                    LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, "Game was ended by " + currentPlayer.playerName());
                    System.exit(0);
                    break;
                default:
                    return command;
            }
        } catch (NumberFormatException e) {
            LogUtils.logWarning("Expected a number!");
        } catch (CardNotFoundException ignored) {
        }

        return command;
    }

    public static INPUT handleInputEnergyPhase() {
        Player currentPlayer = Game.getCurrentPlayer();

        String userInput = "";
        INPUT input = INPUT.NOT_FOUND;
        while (EnergyState.getEnumValueFromString(userInput) == null) {
            System.out.println("Select energy type to gain (current energy is " + currentPlayer.getStoredEnergy() + "):\n");
            userInput = getInput();
            input = handleInputGeneric(getNthWord(userInput, 1), getTextAfterFirstWord(userInput));
            if (input.equals(INPUT.QUIT)) {
                return input;
            }
        }

        currentPlayer.getStoredEnergy().addEnergy(EnergyState.getEnumValueFromString(input.commands[0]), 1);
        return input;
    }

    public static INPUT handleInputMainPhase(String input) {
        String commandPrefix = getNthWord(input, 1);
        String commandSuffix = getTextAfterFirstWord(input);

        Player currentPlayer = Game.getCurrentPlayer();

        INPUT command = parseInput(commandPrefix);

        try {
            switch (command) {
                case PLAY:
                    Card cardToPlay = currentPlayer.getHand().findCard(commandSuffix);
                    currentPlayer.playCard(cardToPlay);
                    break;
                case IMBUE:
                    BoardEntity entityToImbue = BoardState.getInstance().getBoardEntityFromString(getNthWord(input, 2));
                    if (entityToImbue == null) {
                        LogUtils.logWarning("Spot is empty!");
                        break;
                    } else if (!(entityToImbue instanceof Hero)) {
                        LogUtils.logWarning("Not a card!");
                        break;
                    }
                    int energyAmount = Integer.parseInt(getNthWord(input, 3));
                    EnergyState.ENERGY_TYPE energyType = EnergyState.getEnumValueFromString(getNthWord(input, 4));
                    if (!imbueEnergy(currentPlayer, (Hero)entityToImbue, energyType, energyAmount)) {
                        LogUtils.logWarning("Not enough energy!");
                    }
                    break;
                default:
                    command = handleInputGeneric(commandPrefix, commandSuffix);
                    break;
            }
        } catch (CardNotFoundException | PositionOccupiedException | IllegalMoveException ignored) {
        } catch (IndexOutOfBoundsException e) {
            LogUtils.logWarning("Incorrect data.");
        } catch (NumberFormatException e) {
            LogUtils.logWarning("Expected a number.");
        }

        return command;
    }

    public static boolean imbueEnergy(Player player, Hero entity, EnergyState.ENERGY_TYPE energyType, int energyAmount) {
        if (player.getStoredEnergy().getCurrentEnergy().getEnergy(energyType) < energyAmount) {
            return false;
        }
        player.getStoredEnergy().useEnergy(energyType, energyAmount);
        entity.getStoredEnergy().addEnergy(energyType, energyAmount);
        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, entity.getCard().getName() + " gained " + energyAmount + " " + energyType.displayName() + " energy.");
        return true;
    }

    private static String getFreeSpacesString(List<Integer> freeSpaces) {
        StringBuilder toReturn = new StringBuilder();
        for (Integer i : freeSpaces) {
            toReturn.append(i.toString()).append(" ");
        }
        return toReturn.toString();
    }

    public static int handleInputForCardPosition() throws ActionCancelledException {
        List<Integer> freeSpaces = BoardState.getInstance().getFreeSpaces(Game.getCurrentPlayer());
        if (freeSpaces.size() < 1) {
            System.out.println("No room to play card!\n");
            return -1;
        }
        System.out.println("Which position?\nOpen spaces: " + getFreeSpacesString(freeSpaces));
        int toReturn = -1;
        String input;
        while (!freeSpaces.contains(toReturn)) {
            input = getInput();
            try {
                toReturn = Integer.parseInt(input);
            } catch(NumberFormatException e) {
                if (parseInput(input).equals(INPUT.CANCEL)) {
                    throw new ActionCancelledException("", null);
                }
                LogUtils.logWarning("Expected a number, or 'cancel' to exit.");
            }
        }
        return toReturn;
    }

    private static BoardEntity promptForTarget() throws ActionCancelledException {
        LogUtils.printBoardState();
        String userInput = getInput();
        INPUT response = handleInputGeneric(getNthWord(userInput, 1), getTextAfterFirstWord(userInput));

        BoardEntity toReturn = null;

        while (response != INPUT.NOT_FOUND) {
            if (response == INPUT.CANCEL) {
                throw new ActionCancelledException("", null);
            }
            userInput = getInput();
            response = handleInputGeneric(getNthWord(userInput, 1), getTextAfterFirstWord(userInput));
        }

        try {
            toReturn = BoardState.getInstance().getBoardEntityFromString(userInput);
        } catch (CardNotFoundException e) {
            promptForTarget();
        }
        return toReturn;
    }

    public static BoardEntity promptForTarget(Hero hero) {
        System.out.println(hero.getName() + " requires a target for its ability. Choose a board position:");
        BoardEntity toReturn;
        try {
            toReturn = promptForTarget();
        } catch (ActionCancelledException e) {
            LogUtils.logWarning("You must choose a target!");
            toReturn = promptForTarget(hero);
        }
        return toReturn;
//        Game.setStatusTargeting();

    }

    public static BoardEntity promptForTarget(EquipmentCard equipmentCard) throws ActionCancelledException {
        System.out.println("Choose a hero to equip " + equipmentCard.getName() + " to:");
        return promptForTarget();
    }

    public static BoardEntity promptForTarget(RitualCard card) throws ActionCancelledException {
        System.out.println(card.getName() + " requires a target. Choose a board position:");
        return promptForTarget();

    }

}

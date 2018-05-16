package models.board;

import com.google.gson.annotations.Expose;
import gameState.Game;
import models.Player;
import models.cards.HeroCard;
import models.exceptions.CardNotFoundException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;

import java.util.*;

/**
 * Created by Isaac on 6/3/17.
 */
public class BoardState {
    public static final int NUMBER_OF_BOARD_SPACES = 4;
    public static final int PLAYER_POSITION = 2;

    /**
     * List of players in game. First entry
     * should always be local player.
     */
    @Expose
    private List<Player> players;

    private static BoardState state = null;

    public BoardState() {
        this.players = new ArrayList<>();
        this.players.addAll(Game.players);
    }

    public static BoardState getInstance() {
        if (state == null) {
            state = new BoardState();
        }
        return state;
    }

    public static void setIntstance(BoardState newState) {
        state = newState;
    }

    /**
     * Get list of players.
     *
     * @return The list of players
     */
    public List<Player> getPlayers() {
        return players;
    }

    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (Player player : Game.players) {
            toReturn.append("\n\n*** ").append(player.playerName()).append("'s Board: ***\n");
            for (BoardPosition position : player.getBoard()) {
                toReturn.append(position.getPosition());
                toReturn.append(position.isEmpty() ? "[] " : "[" + position.getEntity().getName() + "] ");
            }
        }
        return toReturn + "\n";
    }

    public BoardEntity getEntityForPosition(int position) {
        BoardPosition pos = null;
        try {
            pos = getSlotForPosition(position);
        } catch (CardNotFoundException e) {
            e.printStackTrace();
        }
        return pos == null ? null : pos.getEntity();
    }

    private BoardPosition getSlotForPosition(int position) throws CardNotFoundException {
        for (BoardPosition pos : allPositions()) {
            if (pos.getPosition() == position) {
                return pos;
            }
        }
        throw new CardNotFoundException("Not a valid position!", null);
    }

    public Hero placeCard(Player player, int position, HeroCard card) throws IllegalMoveException, PositionOccupiedException {
        BoardPosition slot;
        try {
            slot = getSlotForPosition(position);
        } catch(CardNotFoundException e) {
            throw new IllegalMoveException("Position does not exist!", null);
        }
        if (!slot.isEmpty()) {
            throw new PositionOccupiedException("There is a card in this slot.", null);
        }
        if (!slot.getPlayer().equals(player)) {
            throw new IllegalMoveException("Position does not belong to you!", null);
        }
        Hero entity = card.generateEntity(player, slot);
        slot.setEntity(entity);

        return entity;
    }

    public List<Integer> getFreeSpaces(Player player) {
        ArrayList<Integer> freeSpaces = new ArrayList<>();
        for (BoardPosition current : player.getBoard()) {
            if (current.isEmpty()) {
                freeSpaces.add(current.getPosition());
            }
        }
        return freeSpaces;
    }

    public BoardEntity getBoardEntityFromString(String position) throws NumberFormatException, CardNotFoundException {
        return getEntityForPosition(Integer.parseInt(position));
    }

    public List<BoardPosition> allPositions() {
        ArrayList<BoardPosition> toReturn = new ArrayList<>();
        for (Player player : Game.players) {
            Collections.addAll(toReturn, player.getBoard());
        }
        return toReturn;
    }

    public List<BoardEntity> allEntities() {
        ArrayList<BoardEntity> toReturn = new ArrayList<>();
        for (Player player : Game.players) {
            for (BoardPosition pos : player.getBoard()) {
                if (!pos.isEmpty()) {
                    toReturn.add(pos.getEntity());
                }
            }
        }
        return toReturn;
    }

    public BoardPosition getNextPosition(BoardPosition pos) {
        return allPositions().get(pos.getPosition() + 1);
    }

    public Map<String, Object> encode() {
        Map<String, Object> boardMap = new HashMap<>();
        boardMap.put("entityType", BoardState.class);

        ArrayList<Map<String, Object>> playerMap = new ArrayList<>();
        for (Player player : Game.players) {
            playerMap.add(player.encode());
        }

        boardMap.put("players", playerMap);

        return boardMap;
    }

}

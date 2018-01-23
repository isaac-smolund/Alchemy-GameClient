package models.board;

import com.jme3.asset.AssetLoadException;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.texture.Texture;
import gameState.Game;
import models.Player;
import models.cards.HeroCard;
import models.exceptions.CardNotFoundException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;
import utils.GraphicsUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by Isaac on 6/3/17.
 */
public class BoardState {
    public static final int NUMBER_OF_BOARD_SPACES = 4;
    public static final int PLAYER_POSITION = 2;

    private static BoardState state = null;

    public static BoardState getInstance() {
        if (state == null) {
            state = new BoardState();
        }
        return state;
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
        if (!Arrays.asList(player.getBoard()).contains(slot)) {
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

}
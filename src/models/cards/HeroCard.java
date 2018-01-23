package models.cards;

import models.Player;
import models.board.BoardEntity;
import models.board.BoardPosition;
import models.board.BoardState;
import models.board.Hero;
import models.energyUtils.Cost;
import models.exceptions.ActionCancelledException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;
import utils.InputUtils;
import libraries.Cards.TAG;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Isaac on 6/3/17.
 */
public abstract class HeroCard extends Card {

    private int maxHealth;

    private ArrayList<TAG> tags;

    public int getHealth() {
        return maxHealth;
    }

    public boolean hasTag(TAG target) {
        return tags.contains(target);
    }

    public void addTags(TAG ... tags) {
        this.tags.addAll(Arrays.asList(tags));
    }

    private String tagString() {
        if (tags.isEmpty()) {
            return "";
        }
        StringBuilder toReturn = new StringBuilder("TAGS: " + tags.get(0).displayName());
        for (int i = 1; i < tags.size(); i++) {
            toReturn.append(", ").append(tags.get(i).displayName());
        }
        return toReturn.toString();
    }

    public HeroCard(String name, String text, String description, Cost cost, int maxHealth) {
        this(name, text, description, cost, null, maxHealth);
    }

    public HeroCard(String name, String text, String description, Cost cost, String image, int maxHealth) {
        super(name, text, description, cost, image);

        this.maxHealth = maxHealth;
        this.tags = new ArrayList<>();
    }


    public abstract Hero generateEntity(Player player, BoardPosition pos);

//    @Override
//    public BoardEntity play(Player player) throws ActionCancelledException, IllegalMoveException, PositionOccupiedException {
//        int position;
//        position = InputUtils.handleInputForCardPosition();
//        return BoardState.getInstance().placeCard(player, position, this);
//    }



//    @Override
//    public BoardEntity play(Player player, int position) throws ActionCancelledException {
//        try {
//            return BoardState.getInstance().placeCard(player, position, this);
//        } catch (IllegalMoveException | PositionOccupiedException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

    @Override
    public String toString() {
        return super.toString() + tagString();
    }
}

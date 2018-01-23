package models.cards;

import models.Player;
import models.board.BoardEntity;
import models.energyUtils.Cost;
import models.exceptions.ActionCancelledException;
import models.exceptions.CardNotFoundException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;

/**
 * Created by Isaac on 7/2/17.
 */
public abstract class RitualCard extends Card {

    public RitualCard(String name, String text, String description, Cost cost) {
        super(name, text, description, cost);
    }

    public abstract void onPlay(int target) throws ActionCancelledException;

//    @Override
//    public BoardEntity play(Player player) throws ActionCancelledException, IllegalMoveException, PositionOccupiedException, CardNotFoundException {
//        onPlay();
//        return null;
//    }

}

package models;

import com.sun.istack.internal.NotNull;
import gameState.Game;
import models.board.*;
import models.cards.*;
import models.energyUtils.Stockpile;
import models.exceptions.*;
import utils.EventService;
import utils.GraphicsUtils;
import utils.InputUtils;
import utils.LogUtils;

/**
 * Created by Isaac on 5/30/17.
 */
public class Player {

    public int playOrder;

    private String name;

    private Hand hand;
    private Deck deck;

    private Stockpile storedEnergy;

    private PlayerEntity entity;
    private BoardPosition[] boardSpaces;

    public Player(int turnOrder, Deck deck) {
        this(turnOrder, null, deck);
    }

    public Player(int turnOrder, String name, Deck deck) {
        this.playOrder = turnOrder;
        this.hand = new Hand();
        this.name = name;
        this.deck = deck;

        this.storedEnergy = new Stockpile(this, null);
        this.entity = new PlayerEntity(this);
        boardSpaces = initBoardPositions();
    }

    public Player(int turnOrder, String name, Deck deck, Stockpile storedEnergy) {
        this(turnOrder, name, deck);
        this.storedEnergy = storedEnergy;
    }

    // For testing purposes:
    public void fillStockpile() {
        this.storedEnergy = Stockpile.generateFullStockpile(this);
    }

    private BoardPosition[] initBoardPositions() {
        int numSpacesIncludingPlayer = BoardState.NUMBER_OF_BOARD_SPACES + 1;
        BoardPosition[] toReturn = new BoardPosition[numSpacesIncludingPlayer];
        for (int i = 0; i < numSpacesIncludingPlayer; i++) {
            toReturn[i] = new BoardPosition(i + (playOrder * (numSpacesIncludingPlayer)), this);
        }
        toReturn[BoardState.PLAYER_POSITION].setEntity(this.entity);
        return toReturn;
    }

    public String playerNameNoColor() {
        return (name != null ? name : "Player " + (playOrder + 1));
    }

    public String playerName() {
        return LogUtils.colorRed(playerNameNoColor());
    }

    public void draw() {
        try {
            Card drawnCard = deck.draw();
//            LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, playerName() + " drew a card.");

            hand.addCard(drawnCard);
            GraphicsUtils.renderCards(this);

            EventService.getInstance().queueAndExecuteSingleEvent(EventService.EventType.DRAW, playerName() + " drew a card.", getEntity());
        } catch (DeckOutOfCardsException ignored) {
        }
    }

    public void draw(int numberOfTimes) {
        for (int i = 0; i < numberOfTimes; i++) {
            draw();
        }
    }

    public Equipment playCard(EquipmentCard playedCard, Hero equipTo) throws ActionCancelledException {
//        BoardEntity equipTo = null;
//        while (equipTo == null) {
//            equipTo = InputUtils.promptForTarget(playedCard);
//            if (!(equipTo instanceof Hero) || equipTo.getPlayer() != this) {
//                LogUtils.logWarning("Not a valid target for equipment.");
//                equipTo = null;
//            }
//        }
        Equipment toEquip = playedCard.generateEntity(this, equipTo);
        equipTo.equip(toEquip);
        return toEquip;
    }

    public void playCard(Card playedCard, int position) throws PositionOccupiedException, IllegalMoveException {
        if (playedCard.getCost().isSatisfiedByOffering(getStoredEnergy().getCurrentEnergy())) {

            LogUtils.logCardPlayed(this, playedCard);
            BoardEntity entity = null;
            try {
//            entity = BoardState.getInstance().placeCard(this, position, playedCard);
//            entity = playedCard.play(this);

                if (playedCard instanceof HeroCard) {
                    entity = BoardState.getInstance().placeCard(this, position, (HeroCard) playedCard);
                } else if (playedCard instanceof EquipmentCard) {
                    BoardEntity equipTo = BoardState.getInstance().getEntityForPosition(position);
                    if (equipTo instanceof Hero) {
                        entity = ((EquipmentCard) playedCard).generateEntity(this, (Hero)equipTo);
                        ((Hero)equipTo).equip((Equipment)entity);
                    }
                } else if (playedCard instanceof RitualCard) {
                    ((RitualCard) playedCard).onPlay(position);
                }

                getStoredEnergy().useEnergy(playedCard.getCost());

                // Remove card from hand:
                getHand().playCard(playedCard);

                if (entity != null) {
                    entity.onPlay();
                }

            } catch (CardNotFoundException | ActionCancelledException e) {
                e.printStackTrace();
            }
        }
    }

    public void playCard(Card playedCard) throws CardNotFoundException, PositionOccupiedException, IllegalMoveException {
        if (playedCard.getCost().isSatisfiedByOffering(getStoredEnergy().getCurrentEnergy())) {

            BoardEntity entity = null;

//            try {
                LogUtils.logCardPlayed(this, playedCard);

//                entity = playedCard.play(this);

                getStoredEnergy().useEnergy(playedCard.getCost());

                getHand().playCard(playedCard);

                if (entity != null) {
                    entity.onPlay();
                }
//            } catch (ActionCancelledException e) {
//                LogUtils.logActionCancelled();
//            }
        }
        else {
            LogUtils.log(LogUtils.LOG_TYPE.PRIVATE, "Not enough energy!");
        }
    }

    public void setName(String name) {
        this.name = name;
    }

    public void shuffleDeck() {
        deck.shuffle();
        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, playerName() + " has shuffled their deck.");
    }

    public int getTurnOrder() {
        return playOrder;
    }

    public Hand getHand() {
        return hand;
    }

    @NotNull
    public Stockpile getStoredEnergy() {
        if (storedEnergy == null) {
            storedEnergy = new Stockpile(this);
        }
        return storedEnergy;
    }

    public BoardPosition[] getBoard() {
        return boardSpaces;
    }

    public PlayerEntity getEntity() {
        return entity;
    }
}

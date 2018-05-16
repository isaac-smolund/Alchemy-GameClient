package models;

import com.google.gson.annotations.Expose;
import com.sun.istack.internal.NotNull;
import gameState.RenderQueue;
import models.board.*;
import models.cards.*;
import models.energyUtils.Stockpile;
import models.exceptions.*;
import utils.EventService;
import utils.LogUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isaac on 5/30/17.
 */
public class Player {

    @Expose
    private int playOrder;
    public boolean isLocalPlayer;

    @Expose
    private String name;

    private Hand hand;
    private Deck deck;

    @Expose
    private Stockpile storedEnergy;

    private PlayerEntity entity;
    @Expose
    private BoardPosition[] boardSpaces;

    public Player(int turnOrder, Deck deck, boolean isLocalPlayer) {
        this(turnOrder, null, deck, isLocalPlayer);
    }

    public Player(int turnOrder, String name, Deck deck, boolean isLocalPlayer) {
        this.playOrder = turnOrder;
        this.hand = new Hand();
        this.name = name;
        this.deck = deck;
        this.isLocalPlayer = isLocalPlayer;

        this.storedEnergy = new Stockpile(this, null);
        this.entity = new PlayerEntity(this);
        boardSpaces = initBoardPositions();
    }

    public Player(int turnOrder, String name, Deck deck, boolean isLocalPlayer, Stockpile storedEnergy) {
        this(turnOrder, name, deck, isLocalPlayer);
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

    public String playerName() {
        return (name != null ? name : "Player " + (playOrder + 1));
    }

    public void draw() {
        try {
            Card drawnCard = deck.draw();

            hand.addCard(drawnCard);

            EventService.getInstance().queueAndExecuteSingleEvent(
                    EventService.EventType.DRAW, playerName() + " drew a card.", getEntity());
            RenderQueue.getInstance().queueUpdate(RenderQueue.UpdateType.UPDATE_HAND);

        } catch (DeckOutOfCardsException ignored) {
        }
    }

    public void draw(int numberOfTimes) {
        for (int i = 0; i < numberOfTimes; i++) {
            draw();
        }
    }

    public Equipment playCard(EquipmentCard playedCard, Hero equipTo) throws ActionCancelledException {
        Equipment toEquip = playedCard.generateEntity(this, equipTo);
        equipTo.equip(toEquip);
        return toEquip;
    }

    public void playCard(Card playedCard, int position) throws PositionOccupiedException, IllegalMoveException {
        if (playedCard.getCost().isSatisfiedByOffering(getStoredEnergy().getCurrentEnergy())) {

            LogUtils.logCardPlayed(this, playedCard);
            BoardEntity entity = null;
            try {
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

    public void playCard(Card playedCard) throws CardNotFoundException {
        if (isLocalPlayer) {
            if (playedCard.getCost().isSatisfiedByOffering(getStoredEnergy().getCurrentEnergy())) {

                BoardEntity entity = null;

                LogUtils.logCardPlayed(this, playedCard);
                getStoredEnergy().useEnergy(playedCard.getCost());

                getHand().playCard(playedCard);

                // TODO: This seems very very wrong
                if (entity != null) {
                    entity.onPlay();
                }
            } else {
                LogUtils.log(LogUtils.LOG_TYPE.PRIVATE, "Not enough energy!");
            }
        } else {
            LogUtils.logCardPlayed(this, playedCard);
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

    public Map<String,Object> encode() {
        Map<String, Object> playerMap = new HashMap<>();
        playerMap.put("entityType", Player.class);
        playerMap.put("name", playerName());
        playerMap.put("health", getEntity().getCurrentHealth());
        playerMap.put("maxHealth", getEntity().getMaxHealth());
        playerMap.put("energy", storedEnergy.encode());

        ArrayList<Map<String, Object>> boardMap = new ArrayList<>();
        for (BoardPosition boardPosition : boardSpaces) {
            boardMap.add(boardPosition.encode());
        }
        playerMap.put("board", boardMap);

        return playerMap;
    }
}

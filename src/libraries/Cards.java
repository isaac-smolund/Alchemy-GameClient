package libraries;

import models.Player;
import models.board.*;
import models.cards.Card;
import models.cards.EquipmentCard;
import models.cards.HeroCard;
import models.cards.RitualCard;
import models.energyUtils.Cost;

import gameState.Game;
import models.energyUtils.EnergyState;
import models.exceptions.ActionCancelledException;
import models.exceptions.CardNotFoundException;
import utils.EventService;
import utils.LogUtils;

import java.util.ArrayList;
import java.util.Map;

import static utils.LogUtils.LOG_TYPE.PUBLIC;

public class Cards {
    public enum CARD_LISTING {
        Bx01(new card_one()),
        Bx02(new generic_soldier()),
        Bx03(new pointy_hat()),
        Bx04(new scrumpo_bungus()),
        Bx05(new exploding_chicken()),
        Bx06(new poison_gas()),
        Bx07(new guard_captain()),
        Bx08(new clockwork_knight()),
        Bx09(new royal_enchanter());

        public Card card;

        CARD_LISTING(Card card) {
            this.card = card;
        }
    }

    public enum TAG {
        BEAST,
        CLOCKWORK,
        ENFORCEMENT,
        HUMAN,
        FOREST,
        MONSTER;

        public String displayName() {
            return LogUtils.colorYellow(this.toString());
        }
    }

    public interface Ability {
        void execute(BoardEntity target);
    }

    private static String keyword(String str) {
        return LogUtils.colorGreen(str);
    }
    private static String basicAttackText(Cost cost, int damage) {
        return cost.toString() + ": " + ATTACK + " for (" + damage + ")";
    }
    private static final String ON_PLAY = keyword("ON PLAY: ");
    private static final String ON_REMOVE = keyword("ON REMOVE: ");
    private static final String ATTACK = keyword("ATTACK");
    private static final String ATK = keyword("ATK");
    private static final String DUR = keyword("DUR");

    private static String onPlay(String str) {
        return ON_PLAY + str;
    }

    private static String insertArgs(String str, String ... args) {
        int counter = 0;
        for (String arg : args) {
            str = str.replace("[arg" + counter + "]", arg);
            counter++;
        }
        return str;
    }

    public static ArrayList<Card> allCards() {
        ArrayList<Card> toReturn = new ArrayList<>();
        for (CARD_LISTING cardListing : CARD_LISTING.values()) {
            toReturn.add(cardListing.card);
        }
        return toReturn;
    }

    public static Card getCardFromName(String cardName) throws CardNotFoundException {
        Card currentCard;
        for (CARD_LISTING cardListing : CARD_LISTING.values()) {
            currentCard = cardListing.card;
            if (currentCard.getNameNoColor().equalsIgnoreCase(cardName)) {
                return currentCard;
            }
        }
        throw new CardNotFoundException("Card does not exist!", null);
    }

    static class card_one extends EquipmentCard {
        private card_one() {
            super("Card One", "WEAPON\n+(4)" + ATK + "\n+(1)" + DUR + "\n" + ON_PLAY + "Draw a card", "Wow, look, a card",
                    new Cost(2, 0, 0, 0),
                    1, 4, EQUIPMENT_TYPE.WEAPON);
        }

        @Override
        public card_one generateCard() {
            return new card_one();
        }

        @Override
        public Equipment generateEntity(Player player, Hero equipTo) {
            return new entity(player, equipTo, this);
        }

        private class entity extends Equipment {
            entity(Player player, Hero entity, EquipmentCard card) {
                super(player, entity, card);
            }

            @Override
            public void onPlay() {
                LogUtils.log(PUBLIC, this.getName() + "'s ability has activated.");
                getPlayer().draw();
            }
        }
    }

    static class generic_soldier extends HeroCard {
        final Cost ABILITY_ONE_COST = new Cost(0, 1, 0, 0);
        final Cost ABILITY_TWO_COST = new Cost(0, 3, 0, 0);
        private generic_soldier() {
            super("Generic Soldier",
                    "",
                    "The redshirts of the High Fantasy genre.",
                    new Cost(0, 1, 0, 0), 4);
            setText(basicAttackText(ABILITY_ONE_COST, 2) + "\n" +
                    basicAttackText(ABILITY_TWO_COST, 4));
            addTags(TAG.HUMAN, TAG.ENFORCEMENT);
        }

        @Override
        public generic_soldier generateCard() {
            return new generic_soldier();
        }

        @Override
        public Hero generateEntity(Player player, BoardPosition pos) {
            return new entity(player, pos, this);
        }

        private class entity extends Hero {

            entity(Player player, BoardPosition pos, HeroCard card) {
                super(player, pos, card);
            }

            private int abilityOneDamage() {
                return 2 + attackBonuses();
            }
            private int abilityTwoDamage() {
                return 4 + attackBonuses();
            }

            @Override
            public String getText() {
                return basicAttackText(ABILITY_ONE_COST, abilityOneDamage()) + "\n" +
                        basicAttackText(ABILITY_TWO_COST, abilityTwoDamage());
            }

            @Override
            public void onTurnEnd() {
                if (ABILITY_TWO_COST.isSatisfiedByOffering(getStoredEnergy())) {
                    attack(abilityTwoDamage());
                } else if (ABILITY_ONE_COST.isSatisfiedByOffering(getStoredEnergy())) {
                    attack(abilityOneDamage());
                }
            }
        }
    }

    static class pointy_hat extends EquipmentCard {
        private pointy_hat() {
            super("Pointy Hat", "HEAD EQUIPMENT\n+(1)" + ATK + "\n+(3)" + DUR, "It's SUPER pointy.",
                    new Cost(0, 0, 2, 0), "sorting_hat.png",
                    3, 1, EQUIPMENT_TYPE.HEAD);
        }

        @Override
        public pointy_hat generateCard() {
            return new pointy_hat();
        }

        @Override
        public Equipment generateEntity(Player player, Hero equipTo) {
            return new entity(player, equipTo, this);
        }

        private class entity extends Equipment {
            entity(Player player, Hero entity, EquipmentCard card) {
                super(player, entity, card);
            }

            @Override
            public void onRemove() {
                LogUtils.log(PUBLIC, getName() + "'s ability is activating\n");
            }
        }
    }

    static class scrumpo_bungus extends HeroCard {
        private Cost ABILITY_ONE_COST = new Cost(0, 2, 0, 2);
        private Cost ABILITY_TWO_COST = new Cost(2, 0, 2, 0);
        private scrumpo_bungus() {
            super("Scrumpo Bungus", "",
                    "You can scrumpo the bungo, but can you scrumpus the bungus?", new Cost(0, 0, 0, 2),
                    "scrumpo.png", 14);
            setText(ABILITY_ONE_COST.toString() + ": Deal damage to ALL other characters equal to your total " + EnergyState.ENERGY_TYPE.BLUE.displayName() + " energy.\n" +
                    ABILITY_TWO_COST.toString() + ": Destroy target Hero card.");
            addTags(TAG.HUMAN);
        }

        @Override
        public scrumpo_bungus generateCard() {
            return new scrumpo_bungus();
        }

        @Override
        public Hero generateEntity(Player player, BoardPosition pos) {
            return new entity(player, pos, this);
        }

        private class entity extends Hero {
            entity(Player player, BoardPosition pos, HeroCard card) {
                super(player, pos, card);
            }
            @Override
            public void onTurnEnd() {
                if (ABILITY_TWO_COST.isSatisfiedByOffering(getStoredEnergy())) {
                    CardSelector selector = new CardSelector(getCard(),
                        target -> {
                            if (target instanceof Hero) {
                                ((Hero) target).die();
                                LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, getName() + " destroyed " + target.getName() + "!");

                                Game.setStatus(Game.STATUS.ABILITY_PHASE);
                                Game.executeEndTurnAbilityForPosition(
                                        BoardState.getInstance().getNextPosition(this.getPosition())
                                );
                            }
                        }
                );
                Game.setStatusAbilityTargeting(selector, "Select target for " + getName() + "'s ability: Destroy target Hero card.");
            } else if (ABILITY_ONE_COST.isSatisfiedByOffering(getStoredEnergy())) {
                    int damage = Game.getCurrentPlayer().getStoredEnergy().blueEnergy();
                    LogUtils.log(PUBLIC, getName() + " deals " + damage + " damage to all characters.");
                    for (BoardEntity entity : BoardState.getInstance().allEntities()) {
                        if (entity != this) {
                            entity.dealDamage(damage);
                        }
                    }
                }
            }
        }
    }

    static class exploding_chicken extends HeroCard {
        private Map<String, Object> values() {
            return CardDetails.exploding_chicken_values();
        }

        private Cost ABILITY_COST = (Cost)values().get("ability_one_cost");

        private exploding_chicken() {
            super((String)CardDetails.exploding_chicken_values().get("name"),
                    "",
                    (String)CardDetails.exploding_chicken_values().get("description"),
                    (Cost)CardDetails.exploding_chicken_values().get("cost"),
                    "exploding_chicken.jpg",
                    (int)CardDetails.exploding_chicken_values().get("health"));
            setText(insertArgs((String)values().get("text"), "", ABILITY_COST.toString()));
            addTags(TAG.BEAST, TAG.CLOCKWORK);
        }

        @Override
        public exploding_chicken generateCard() {
            return new exploding_chicken();
        }

        @Override
        public Hero generateEntity(Player player, BoardPosition pos) {
            return new entity(player, pos, this);
        }

        private class entity extends Hero {
            private int tokens = 0;
            entity(Player player, BoardPosition pos, HeroCard card) {
                super(player, pos, card);
            }

            @Override
            public String getText() {
                return insertArgs((String)values().get("text"), " (" + tokens + " tokens)", ABILITY_COST.toString());
            }

            @Override
            public void onRemove() {
                LogUtils.log(PUBLIC, getName() + " deals " + tokens + " damage to all characters.");
                for (BoardEntity entity : BoardState.getInstance().allEntities()) {
                    entity.dealDamage(tokens);
                }
            }

            @Override
            public void onTurnEnd() {
                if (ABILITY_COST.isSatisfiedByOffering(getStoredEnergy())) {
                    tokens++;
                    LogUtils.log(PUBLIC, getName() + " gains a token. (Current tokens: " + tokens + ")");
                }
            }
        }

    }

    static class poison_gas extends RitualCard {
        int damage = 5;

        private poison_gas() {
            super("Poison Gas",
                    "",
                    "It smells preeeeetty bad.",
                    new Cost(0, 0, 0, 3));
            setText("Deal (" + damage + ") damage to target.");
        }

        @Override
        public poison_gas generateCard() {
            return new poison_gas();
        }

        @Override
        public void onPlay(int targetPosition) throws ActionCancelledException {
//            BoardEntity target = InputUtils.promptForTarget(this);
            BoardEntity target = null;
            target = BoardState.getInstance().getEntityForPosition(targetPosition);
            target.dealDamage(damage);
        }

    }

    static class guard_captain extends HeroCard {

        private guard_captain() {
            super((String)CardDetails.guard_captain_values().get("name"),
                    (String)CardDetails.guard_captain_values().get("text"),
                    (String)CardDetails.guard_captain_values().get("description"),
                    (Cost)CardDetails.guard_captain_values().get("cost"),
                    (int)CardDetails.guard_captain_values().get("health"));
            addTags(TAG.HUMAN, TAG.ENFORCEMENT);
        }

        @Override
        public guard_captain generateCard() {
            return new guard_captain();
        }

        @Override
        public Hero generateEntity(Player player, BoardPosition pos) {
            return new entity(player, pos, this);
        }

        private class entity extends Hero {
            private final Map<String, Object> values = CardDetails.guard_captain_values();
            private final int BASE_ATTACK = (int)values.get("atk");
            private final Cost ABILITY_ONE_COST = (Cost)values.get("ability_one_cost");

            entity(Player player, BoardPosition pos, HeroCard card) {
                super(player, pos, card);
            }

            private int abilityDamage() {
                return BASE_ATTACK + attackBonuses();
            }

            @Override
            public String getText() {
                return ON_PLAY + "Give all " + TAG.ENFORCEMENT.displayName() + " allies +(3) health.\n" +
                        CardDetails.basicAttackText(ABILITY_ONE_COST, abilityDamage(), abilityDamage() != BASE_ATTACK);
            }

            @Override
            public void onPlay() {
                LogUtils.log(PUBLIC, getName() + "'s abiity activates!");
                for (BoardPosition pos : getPlayer().getBoard()) {
                    if (!pos.isEmpty()) {
                        BoardEntity entity = pos.getEntity();
                        if (entity instanceof Hero && ((Hero) entity).hasTag(TAG.ENFORCEMENT)) {
                            entity.increaseMaxHealth(3);
                        }
                    }
                }
            }

            @Override
            public void onTurnEnd() {
                if (ABILITY_ONE_COST.isSatisfiedByOffering(this .getStoredEnergy())) {
                    attack(abilityDamage());
                }
            }
        }
    }

    static class clockwork_knight extends HeroCard {
        private clockwork_knight() {
            super((String)CardDetails.clockwork_knight_values().get("name"),
                    insertArgs((String)CardDetails.clockwork_knight_values().get("text"),
                            basicAttackText(
                                    (Cost)CardDetails.clockwork_knight_values().get("ability_one_cost"),
                                    (int)CardDetails.clockwork_knight_values().get("atk"))),
                    (String)CardDetails.clockwork_knight_values().get("description"),
                    (Cost)CardDetails.clockwork_knight_values().get("cost"),
                    "clockwork_knight.png",
                    (int)CardDetails.clockwork_knight_values().get("health")
            );
            addTags(TAG.CLOCKWORK);
        }

        @Override
        public clockwork_knight generateCard() {
            return new clockwork_knight();
        }

        @Override
        public Hero generateEntity(Player player, BoardPosition pos) {
            return new entity(player, pos, this);
        }

        private class entity extends Hero {
            private final Map<String, Object> values = CardDetails.clockwork_knight_values();

            entity(Player player, BoardPosition position, HeroCard card) {
                super(player, position, card);
            }
            private int attackDamage() {
                return (int)values.get("atk") + attackBonuses();
            }
            @Override
            public String getText() {
                return insertArgs((String)values.get("text"),
                        CardDetails.basicAttackText(
                                (Cost)values.get("ability_one_cost"),
                            attackDamage(),
                            attackDamage() != (int)values.get("atk"))
                );
            }
            @Override
            public void onTurnEnd() {
                if (((Cost)values.get("ability_two_cost")).isSatisfiedByOffering(getStoredEnergy())) {
                    LogUtils.log(PUBLIC, (String)values.get("ability_two_text"));
                    this.die();
                    for (BoardEntity entity : BoardState.getInstance().allEntities()) {
                        if (entity.getPlayer() != this.getPlayer()) {
                            entity.dealDamage(getStoredEnergy().redEnergy());
                        }
                    }
                } else if (((Cost)values.get("ability_one_cost")).isSatisfiedByOffering(getStoredEnergy())) {
                    attack(attackDamage());
                }
            }
        }
    }

    static class royal_enchanter extends HeroCard {
        private royal_enchanter() {
            super("Royal Enchanter",
                    "Whenever you draw a card, you gain (1) health.",
                    "Kind of like a doctor, but pickier.",
                    new Cost(0, 0, 1, 0),
                    3);
            addTags(TAG.FOREST);
        }

        @Override
        public royal_enchanter generateCard() {
            return new royal_enchanter();
        }

        @Override
        public Hero generateEntity(Player player, BoardPosition pos) {
            return new entity(player, pos, this);
        }

        private class entity extends Hero {
            private int healFactor = 1;

            entity(Player player, BoardPosition position, HeroCard card) {
                super(player, position, card);
            }

            @Override
            public void onPlay() {
                EventService.getInstance().registerListener(this, EventService.EventType.DRAW);
            }

            @Override
            public void notify(EventService.Event event) {
                PlayerEntity playerEntity = getPlayer().getEntity();
                if (event.getRelevantEntities().get(0).equals(playerEntity)) {
                    playerEntity.setCurrentHealth(playerEntity.getCurrentHealth() + healFactor);
                }
            }
        }
    }
}
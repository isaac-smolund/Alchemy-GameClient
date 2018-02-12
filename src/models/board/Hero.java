package models.board;

import gameState.Game;
import libraries.Cards;
import models.Player;
import models.cards.Card;
import models.cards.EquipmentCard.EQUIPMENT_TYPE;
import models.cards.HeroCard;
import models.energyUtils.EnergyState;
import utils.EventService;
import utils.GraphicsUtils;
import utils.InputUtils;
import utils.LogUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Isaac on 6/3/17.
 */
public class Hero extends BoardEntity {

    private BoardPosition position;
    private HeroCard card;

    private EnergyState storedEnergy;

    private ArrayList<Equipment> equipment;

    public Hero(Player player, BoardPosition position, HeroCard card) {
        super(player, card.getHealth());
        this.position = position;
        this.card = card;

        this.storedEnergy = new EnergyState(0, 0, 0, 0);
        this.equipment = new ArrayList<>();
    }

    public BoardPosition getPosition() {
        return position;
    }

    public HeroCard getCard() {
        return card;
    }

    public boolean hasTag(Cards.TAG tag) {
        return getCard().hasTag(tag);
    }

    public EnergyState getStoredEnergy() {
        return storedEnergy;
    }

    @Override
    public String getName() {
        return getCard().getName().replaceAll("\u001B.{1,3}m", "");
    }

    public String getText() {
        return getCard().getText();
    }

    public int attackBonuses() {
        int bonuses = 0;
        for (Equipment item : equipment) {
            bonuses += item.getDamage();
        }
        return bonuses;
    }

    public void equip(Equipment toEquip) {
        equipment.add(toEquip);
    }

    public void unequip(Equipment toRemove) {
        equipment.remove(toRemove);
    }

    public List<Equipment> getEquipment() {
        return equipment;
    }

    @Override
    public void die() {
        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, getName() + " has died.");
        getPosition().setEntity(null);
        EventService.getInstance().removeListenersForEntity(this);
        onRemove();
    }

    private String equipmentString() {
        if (equipment.size() < 1) {
            return "";
        }
        StringBuilder toReturn = new StringBuilder("\nEquipment:\n");
        for (Equipment item : equipment) {
            toReturn.append("\t").append(item.getName())
                    .append(" (+").append(item.getDamage()).append("/+")
                    .append(item.getArmor()).append(")\n");
        }
        return toReturn.toString();
    }

    private String armorString() {
        int totalArmor = 0;
        for (Equipment currentEquipment : equipment) {
            totalArmor += currentEquipment.getArmor();
        }
        return totalArmor > 0 ? " (+" + totalArmor + ")" : "";
    }

    public String toString() {
//        return card.toString(getText()) +
//                "\nStored energy: " + storedEnergy.toString() +
//                "\nHealth: " + healthString() +
//                equipmentString();
        return card.getName() + "\n" + healthString() + armorString();
    }

    public String infoString() {
        return card.toString(getText()) +
                "\nStored energy: " + storedEnergy.toString() +
                "\nHealth: " + healthString() +
                equipmentString();
    }


    public class CardSelector {
        private Card selectingCard;
        private Cards.Ability ability;
        public CardSelector(Card selectingCard, Cards.Ability ability) {
            this.selectingCard = selectingCard;
            this.ability = ability;
        }
        public void execute(BoardEntity target) {
            ability.execute(target);
        }
    }

    public void attack(int damage) {
        GraphicsUtils.setHudText("Select target for " + getName() + " to attack.");

        CardSelector selector = new CardSelector(getCard(),
                target -> {
                    target.dealDamage(damage);
                    LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, getName() + " deals " + damage + " damage to " + target.getName() + "!");

                    Game.setStatus(Game.STATUS.ABILITY_PHASE);

                    Game.executeEndTurnAbilityForPosition(
                            BoardState.getInstance().getNextPosition(this.getPosition())
                    );
                }
        );

        Game.setStatusAbilityTargeting(selector, "Select a target for " + getName() + " to attack.");
    }

    @Override
    public void dealDamage(int damage) {
        // Head equipment takes damage first, then body, then weapon:
        List<Equipment> toTakeDamage = new ArrayList<>();
        for (EQUIPMENT_TYPE type : EQUIPMENT_TYPE.values()) {
            for (Equipment item : equipment) {
                if (item.getType() == type) {
                    toTakeDamage.add(item);
                }
            }
        }
        int damageToDeal = damage;
        for (Equipment item : toTakeDamage) {
            int cappedDamage = damageToDeal > item.getArmor() ? item.getArmor() : damageToDeal;
            item.dealDamage(cappedDamage);
            damageToDeal -= cappedDamage;
            if (damageToDeal <= 0) {
                return;
            }
        }
        super.dealDamage(damageToDeal);
    }

    @Override
    public Map<String, Object> encode() {
        Map<String, Object> entityMap = super.encode();
        entityMap.put("entityType", Hero.class);
        entityMap.put("energy", storedEnergy.encode());
        entityMap.put("card", card.getName());

        ArrayList<Map<String, Object>> equipmentList = new ArrayList<>();
        for (Equipment equippedItem : equipment) {
            equipmentList.add(equippedItem.encode());
        }
        entityMap.put("equipment", equipmentList);

        return entityMap;
    }
}

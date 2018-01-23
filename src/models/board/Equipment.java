package models.board;

import models.Player;
import models.cards.EquipmentCard;
import utils.LogUtils;

/**
 * Created by Isaac on 6/25/17.
 */
public class Equipment extends BoardEntity {
    private EquipmentCard card;
    private Hero equippedTo;

    public Equipment(Player owningPlayer, Hero equippedTo, EquipmentCard card) {
        super(owningPlayer, card.getDurability());
        this.equippedTo = equippedTo;
        this.card = card;
    }

    @Override
    public String getName() {
        return card.getName();
    }

    public String longName() {
        return equippedTo.getName() + "'s " + card.getName();
    }

    public int getDamage() {
        return card.getDamage();
    }

    public int getArmor() {
        return card.getDurability();
    }

    public EquipmentCard.EQUIPMENT_TYPE getType() {
        return card.getType();
    }

    public EquipmentCard getCard() {
        return card;
    }

    @Override
    protected String damageDealtString(int damage) {
        return longName() + " takes " + damage + " damage! (Current durability: " + healthString() + ")";
    }

    public void die() {
        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, longName() + " broke!");
        equippedTo.unequip(this);
        onRemove();
    }


}

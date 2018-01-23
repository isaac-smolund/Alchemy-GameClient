package models.cards;

import models.Player;
import models.board.BoardEntity;
import models.board.Hero;
import models.board.Equipment;
import models.energyUtils.Cost;
import models.exceptions.ActionCancelledException;
import models.exceptions.CardNotFoundException;
import models.exceptions.IllegalMoveException;
import models.exceptions.PositionOccupiedException;

public abstract class EquipmentCard extends Card {

    private int durability;
    private int damage;
    private EQUIPMENT_TYPE type;

    public enum EQUIPMENT_TYPE {
        HEAD,
        BODY,
        WEAPON
    }

    public EquipmentCard(String name, String text, String description, Cost cost, int durability, int damage, EQUIPMENT_TYPE type) {
        this(name, text, description, cost, null, durability, damage, type);
    }

    public EquipmentCard(String name, String text, String description, Cost cost, String image, int durability, int damage, EQUIPMENT_TYPE type) {
        super(name, text, description, cost, image);
        this.durability = durability;
        this.damage = damage;
        this.type = type;
    }

    public int getDurability() {
        return durability;
    }

    public int getDamage() {
        return damage;
    }

    public EQUIPMENT_TYPE getType() {
        return type;
    }

    public abstract Equipment generateEntity(Player player, Hero equipTo);

//
//    @Override
//    public BoardEntity play(Player player, int position) throws ActionCancelledException {
//        return player.playCard(this);
//    }
}
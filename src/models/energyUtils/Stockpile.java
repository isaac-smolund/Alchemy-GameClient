package models.energyUtils;

import models.Player;
import models.board.Hero;
import utils.LogUtils;

/**
 * Created by Isaac on 5/30/17.
 */
public class Stockpile extends EnergyState {

    private Player player;
    private Hero owningEntity;

    private EnergyState currentEnergy;

    public Stockpile(Player player) {
        this(0, 0, 0, 0, player, null);
    }

    public Stockpile(Player player, Hero owningEntity) {
        this(0, 0, 0, 0, player, owningEntity);
    }

    private Stockpile(int red, int blue, int green, int yellow, Player player, Hero owningEntity) {
        super(red, blue, green, yellow);
        this.player = player;
        this.owningEntity = owningEntity;

        this.currentEnergy = new EnergyState(red, blue, green, yellow);
    }

    // For testing purposes only:
    public static Stockpile generateFullStockpile(Player player) {
        return new Stockpile(10, 10, 10, 10, player, null);
    }

    private String energyStringValue(ENERGY_TYPE type) {
        int currentValue = currentEnergy.getEnergy(type);
        int maxValue = this.getEnergy(type);
        return currentValue == maxValue ? Integer.toString(maxValue) : currentValue + "/" + maxValue;
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (ENERGY_TYPE type : ENERGY_TYPE.values()) {
            toReturn.append(type.shortName()).append(energyStringValue(type)).append(" ");
        }
        return toReturn.toString();
    }

    private String ownerName() {
        return owningEntity == null ? player.playerName() : owningEntity.getCard().getName();
    }

    public EnergyState getCurrentEnergy() {
        return currentEnergy;
    }

    public void useEnergy(Cost energyUsed) {
        useEnergy(ENERGY_TYPE.RED, energyUsed.red);
        useEnergy(ENERGY_TYPE.BLUE, energyUsed.blue);
        useEnergy(ENERGY_TYPE.GREEN, energyUsed.green);
        useEnergy(ENERGY_TYPE.YELLOW, energyUsed.yellow);
    }

    public void useEnergy(ENERGY_TYPE type, int toUse) {
        int negativeValue = Math.negateExact(toUse);
        getCurrentEnergy().addEnergy(type, negativeValue);
    }

    @Override
    public void addEnergy(EnergyState energyAdded) {
        addEnergy(ENERGY_TYPE.RED, energyAdded.red);
        addEnergy(ENERGY_TYPE.BLUE, energyAdded.blue);
        addEnergy(ENERGY_TYPE.GREEN, energyAdded.green);
        addEnergy(ENERGY_TYPE.YELLOW, energyAdded.yellow);
    }

    @Override
    public void addEnergy(ENERGY_TYPE type, int toAdd) {
        switch(type) {
            case RED:
                this.red += toAdd;
                this.currentEnergy.red += toAdd;
                break;
            case BLUE:
                this.blue += toAdd;
                this.currentEnergy.blue += toAdd;
                break;
            case GREEN:
                this.green += toAdd;
                this.currentEnergy.green += toAdd;
                break;
            case YELLOW:
                this.yellow += toAdd;
                this.currentEnergy.yellow += toAdd;
                break;
        }
        if (toAdd > 0) {
            LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, ownerName() + " gained " + toAdd + " " + type.displayName() + " energy.");
        }
    }

    public void refill() {
        currentEnergy = new EnergyState(red, blue, green, yellow);
    }
}

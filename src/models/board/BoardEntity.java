package models.board;

import models.Player;
import models.energyUtils.Stockpile;
import utils.EventService;
import utils.LogUtils;

/**
 * Created by Isaac on 6/11/17.
 */
public abstract class BoardEntity {

    private Player player;
    private int currentHealth;
    private int maxHealth;

    public BoardEntity(int health) {
        this.currentHealth = health;
        this.maxHealth = health;
    }

    public BoardEntity(Player player, int health) {
        this(health);
        this.player = player;
    }

    public Player getPlayer() {
        return player;
    }

    public int getCurrentHealth() {
        return currentHealth;
    }

    public int getMaxHealth() {
        return maxHealth;
    }

    public void setCurrentHealth(int newHealth) {
        this.currentHealth = newHealth;
    }

    public void increaseMaxHealth(int toAdd) {
        maxHealth += toAdd;
        currentHealth += toAdd;
        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, getName() + " gained " + toAdd + " max health.");
    }

    protected String damageDealtString(int damage) {
        return getName() + " takes " + damage + " damage! (Current health: " + healthString() + ")";
    }

    public void dealDamage(int damage) {
        this.currentHealth -= damage;
        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, damageDealtString(damage));
        if (currentHealth <= 0) {
            die();
        }
    }

    protected abstract void die();

    public abstract String getName();

    public String healthString() {
        String maxHealthString = LogUtils.colorGreen(Integer.toString(maxHealth));
        return maxHealth == currentHealth ?
                maxHealthString :
                LogUtils.colorRed(Integer.toString(currentHealth)) + "/" + maxHealthString;
    }

    // These do not have to be implemented by subclasses:
    public void onPlay() {
    }

    public void onRemove() {
    }

    public void onTurnEnd() {
    }

    // Notify the entity of any events that are being listened for:
    public void notify(EventService.Event event) {

    }
}

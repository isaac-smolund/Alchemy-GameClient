package models.board;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import models.Player;
import utils.EventService;
import utils.GraphicsUtils;
import utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isaac on 6/11/17.
 */
public abstract class BoardEntity {

    private Player player;

    @Expose
    private int playerTurn;
    @Expose
    private int currentHealth;
    @Expose
    private int maxHealth;

    BoardEntity() {}

    public BoardEntity(int health) {
        this.currentHealth = health;
        this.maxHealth = health;
    }

    public BoardEntity(Player player, int health) {
        this(health);
        this.player = player;
        this.playerTurn = player.getTurnOrder();
    }

    public Player getPlayer() {
        if (player == null) {
            for (Player player : BoardState.getInstance().getPlayers()) {
                if (player.getTurnOrder() == playerTurn) {
                    this.player = player;
                    return player;
                }
            }
        }
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
        GraphicsUtils.renderBoard();
    }

    protected void die() {}

    public String getName() {return "";}

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

    Map<String, Object> encode() {
        Map<String, Object> entityMap = new HashMap<>();
        entityMap.put("health", currentHealth);
        entityMap.put("maxHealth", maxHealth);

        return entityMap;
    }

    public abstract JsonObject serialize();
}

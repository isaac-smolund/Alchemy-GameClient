package models.board;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import gameState.Game;
import models.Player;
import utils.LogUtils;

/**
 * Created by Isaac on 6/13/17.
 */
public class PlayerEntity extends BoardEntity {

    private PlayerEntity() {

    }

    public PlayerEntity(Player player) {
        super(player, Game.STARTING_HEALTH);
    }

    @Override
    public String getName() {
        return getPlayer().playerName();
    }

    @Override
    public JsonObject serialize() {
        JsonObject json = new JsonObject();
        json.add("health", new JsonPrimitive(getCurrentHealth()));
        json.add("maxHealth", new JsonPrimitive(getMaxHealth()));
//        json.add("player", new JsonObject(getPlayer()));
        return json;
    }

    @Override
    public void die() {
        LogUtils.log(LogUtils.LOG_TYPE.PUBLIC, getPlayer().playerName() + " has died.");
        Game.killPlayer(this.getPlayer());
    }

    public String toString() {
        return getPlayer().playerName() + ":\n" +
                "Health: " + healthString();
    }

}

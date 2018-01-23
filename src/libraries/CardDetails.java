package libraries;

import models.energyUtils.Cost;
import models.energyUtils.EnergyState;
import utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isaac on 7/4/17.
 */
public class CardDetails {

    private static String keyword(String str) {
        return LogUtils.colorGreen(str);
    }
    static String basicAttackText(Cost cost, int damage) {
        return basicAttackText(cost, damage, false);
    }
    static String basicAttackText(Cost cost, int damage, boolean isAltered) {
        return cost.toString() + ": " + ATTACK + " for (" + (isAltered ? "*" : "") + damage + (isAltered ? "*" : "") + ")";
    }
    private static final String ON_PLAY = keyword("ON PLAY: ");
    private static final String ON_REMOVE = keyword("ON REMOVE: ");
    private static final String ATTACK = keyword("ATTACK");
    private static final String ATK = keyword("ATK");
    private static final String DUR = keyword("DUR");

    private static String onPlay(String str) {
        return ON_PLAY + str;
    }
    private static String onRemove(String str) {
        return ON_REMOVE + str;
    }

    // Exploding Chicken (Bx05)
    private static Map<String, Object> exploding_chicken;
    static Map<String, Object> exploding_chicken_values() {
        if (exploding_chicken == null) {
            exploding_chicken = new HashMap<>();
            exploding_chicken.put("name", "Exploding Chicken");
            exploding_chicken.put("text", onRemove("Deal damage to ALL characters equal to the number of Boom Tokens on this card.[arg0]") +
                "\n[arg1]: Add a Boom Token to this card.");
            exploding_chicken.put("description", "Little-known fact, this works on real chickens, too.");
            exploding_chicken.put("cost", new Cost(1, 0, 0, 0));
            exploding_chicken.put("health", 1);
            exploding_chicken.put("ability_one_cost", new Cost(1, 0, 0, 0));
        }

        return exploding_chicken;
    }

    // Guard Captain (Bx07)
    private static Map<String, Object> guard_captain;
    static Map<String, Object> guard_captain_values() {
        if (guard_captain == null) {
            final int BASE_ATTACK = 8;
            final int BASE_HEALTH = 8;
            final Cost COST = new Cost(0, 6, 0, 0);
            final Cost ABILITY_ONE_COST = new Cost(0, 4, 0, 0);

            guard_captain = new HashMap<>();
            guard_captain.put("name", "Guard Captain");
            guard_captain.put("cost", COST);
            guard_captain.put("ability_one_cost", ABILITY_ONE_COST);
            guard_captain.put("text", onPlay("Give all " + Cards.TAG.ENFORCEMENT.displayName() + " allies +(3) health.") + "\n" +
                    basicAttackText(ABILITY_ONE_COST, BASE_ATTACK));
            guard_captain.put("atk", BASE_ATTACK);
            guard_captain.put("health", BASE_HEALTH);
            guard_captain.put("description", "Wot's all this, then?");
        }

        return guard_captain;
    }

    // Clockwork Knight (Bx08)
    static final String clockwork_knight_name = "Clockwork Knight";
    static final int clockwork_knight_atk = 4;
    static final int clockwork_knight_health = 10;
    static final Cost clockwork_knight_cost = new Cost(4, 0, 0, 0);
    static final Cost clockwork_knight_ability_one_cost = new Cost(2, 0, 0, 0);
    static final Cost clockwork_knight_ability_two_cost = new Cost(5, 0, 0, 1);
    static final String clockwork_knight_text = "[arg0]\n" + clockwork_knight_ability_two_cost.toString() +
            ": Destroy this card and deal damage to all enemies equal to this card's " + EnergyState.ENERGY_TYPE.RED.displayName() + " energy.";
    static final String clockwork_knight_description = "01100111 01100111";
    static final String clockwork_knight_ability_two_text = clockwork_knight_name + " self-destructs!";

    private static Map<String, Object> clockwork_knight;
    static Map<String, Object> clockwork_knight_values() {
        if (clockwork_knight == null) {
            final String NAME = "Clockwork Knight";
            final Cost ABILITY_TWO_COST = new Cost(5, 0, 0, 1);

            clockwork_knight = new HashMap<>();
            clockwork_knight.put("name", NAME);
            clockwork_knight.put("atk", 4);
            clockwork_knight.put("health", 10);
            clockwork_knight.put("cost", new Cost(4, 0, 0, 0));
            clockwork_knight.put("ability_one_cost", new Cost(2, 0, 0, 0));
            clockwork_knight.put("ability_two_cost", ABILITY_TWO_COST);
            clockwork_knight.put("text", "[arg0]\n" + ABILITY_TWO_COST.toString() +
                ": Destroy this card and deal damage to all enemies equal to this card's " + EnergyState.ENERGY_TYPE.RED.displayName() + " energy.");
            clockwork_knight.put("description", "01100111 01100111");
            clockwork_knight.put("ability_two_text", NAME + " self-destructs!");
        }

        return clockwork_knight;
    }
}

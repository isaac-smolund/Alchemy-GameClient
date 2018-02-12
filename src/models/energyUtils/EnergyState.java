package models.energyUtils;

import com.jme3.math.ColorRGBA;
import utils.GraphicsUtils;
import utils.LogUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Isaac on 5/30/17.
 */
public class EnergyState {

    int red;
    int blue;
    int green;
    int yellow;

    public Map<String, Object> encode() {
        Map<String, Object> energyMap = new HashMap<>();
        energyMap.put("entityType", EnergyState.class);
        energyMap.put("red", red);
        energyMap.put("blue", blue);
        energyMap.put("green", green);
        energyMap.put("yellow", yellow);
        return energyMap;
    }

    public enum ENERGY_TYPE {
        RED("red", "R", LogUtils.colorRed("RED"), ColorRGBA.Red),
        BLUE("blue", "B", LogUtils.colorBlue("BLUE"), ColorRGBA.Blue),
        GREEN("green", "G", LogUtils.colorGreen("GREEN"), ColorRGBA.Green),
        YELLOW("yellow", "Y", LogUtils.colorYellow("YELLOW"), ColorRGBA.Yellow);

        private final String stringValue;
        private final String shortName;
        private final String fullName;
        private final ColorRGBA color;

        ENERGY_TYPE(String stringValue, String shortName, String fullName, ColorRGBA color) {
            this.stringValue = stringValue;
            this.shortName = shortName;
            this.fullName = fullName;
            this.color = color;
        }

        public String shortName() {
            return shortName;
        }
        public String displayName() {
            return fullName;
        }
        public ColorRGBA color() {
            return color;
        }
    }

    public static ENERGY_TYPE getEnumValueFromString(String energyType) {
        for (ENERGY_TYPE type : ENERGY_TYPE.values()) {
            if (energyType.equals(type.stringValue) || energyType.equals(type.shortName)) {
                return type;
            }
        }
        return null;
    }

    public EnergyState(int red, int blue, int green, int yellow) {
        this.red = red;
        this.blue = blue;
        this.green = green;
        this.yellow = yellow;
    }

    public int getEnergy(ENERGY_TYPE energy) {
        switch(energy) {
            case RED:
                return redEnergy();
            case BLUE:
                return blueEnergy();
            case GREEN:
                return greenEnergy();
            case YELLOW:
                return yellowEnergy();
            default:
                return 0;
        }
    }

    public int redEnergy() {
        return red;
    }

    public int blueEnergy() {
        return blue;
    }

    public int greenEnergy() {
        return green;
    }

    public int yellowEnergy() {
        return yellow;
    }

    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (ENERGY_TYPE type : ENERGY_TYPE.values()) {
            toReturn.append(type.shortName()).append(getEnergy(type)).append(" ");
        }
        return toReturn.toString();
    }

    public void reset() {
        this.red = 0;
        this.blue = 0;
        this.green = 0;
        this.yellow = 0;
    }

    public void addEnergy(EnergyState energyAdded) {
        addEnergy(ENERGY_TYPE.RED, energyAdded.red);
        addEnergy(ENERGY_TYPE.BLUE, energyAdded.blue);
        addEnergy(ENERGY_TYPE.GREEN, energyAdded.green);
        addEnergy(ENERGY_TYPE.YELLOW, energyAdded.yellow);
    }

    public void addEnergy(ENERGY_TYPE type, int toAdd) {
        switch(type) {
            case RED:
                this.red += toAdd;
                break;
            case BLUE:
                this.blue += toAdd;
                break;
            case GREEN:
                this.green += toAdd;
                break;
            case YELLOW:
                this.yellow += toAdd;
                break;
        }
    }

}

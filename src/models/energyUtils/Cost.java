package models.energyUtils;

public class Cost extends EnergyState {

    public boolean isSatisfiedByOffering(EnergyState other) {
        return redSatisfied(other.redEnergy()) &&
                blueSatisfied(other.blueEnergy()) &&
                greenSatisfied(other.greenEnergy()) &&
                yellowSatisfied(other.yellowEnergy());
    }

    private boolean redSatisfied(int other) {
        return other >= this.redEnergy();
    }

    private boolean blueSatisfied(int other) {
        return other >= this.blueEnergy();
    }

    private boolean greenSatisfied(int other) {
        return other >= this.greenEnergy();
    }

    private boolean yellowSatisfied(int other) {
        return other >= this.yellowEnergy();
    }

    public Cost(int red, int blue, int green, int yellow) {
        super(red, blue, green, yellow);
    }

    @Override
    public String toString() {
        StringBuilder toReturn = new StringBuilder();
        for (ENERGY_TYPE type : ENERGY_TYPE.values()) {
            int value = getEnergy(type);
            if (value > 0) {
                toReturn.append(Integer.toString(value)).append(type.shortName()).append(" ");
            }
        }
        return toReturn.toString();
    }
}
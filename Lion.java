/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Lion predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Lion extends Predator {

    private static final double EATING_PROBABILITY = 0.6;
    private static final HunterAttributes ATTRIBUTES =
            new HunterAttributes(130, 32, 2, 0.115, 0.01, 0.01, 19);

    /**
     * Constructor for a lion in the simulation.
     *
     * @param foodLevel The food level the lion is at initially.
     * @param randomAge Whether we assign this lion a random age or not.
     * @param field The field in which this lion resides.
     * @param location The location in which this lion is spawned into.
     */
    public Lion(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Lion::new);
    }

    /**
     * Getter method to return this lion's probability of eating if food is found.
     *
     * @return The lion's eating probability.
     */
    @Override
    public double getEatingProbability() {
        return EATING_PROBABILITY;
    }

    @Override
    protected boolean isRestingTime(TimeOfDay time) {
        return time == TimeOfDay.NIGHT;
    }

}

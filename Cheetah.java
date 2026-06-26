/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {

    private static final double EATING_PROBABILITY = 0.6;
    private static final HunterAttributes ATTRIBUTES =
            new HunterAttributes(140, 26, 2, 0.115, 0.01, 0.01, 19);

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param foodLevel The food level the cheetah is at initially.
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(boolean randomAge, Field field, Location location) {
        super(ATTRIBUTES, randomAge, field, location, Cheetah::new);
    }

    /**
     * Getter method to return this cheetah's probability of eating if food is found.
     *
     * @return The cheetah's eating probability.
     */
    @Override
    public double getEatingProbability() {
        return EATING_PROBABILITY;
    }

    @Override
    protected boolean isRestingTime(TimeOfDay time) {
        return time == TimeOfDay.EARLY_AFTERNOON;
    }

}

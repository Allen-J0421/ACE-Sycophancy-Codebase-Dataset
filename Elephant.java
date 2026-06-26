/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An Elephant prey in the simulation. Shares the standard prey tuning defined
 * in {@link Prey}, differing only in breeding probability and its low-activity
 * period.
 *
 * @version 2022.03.02
 */
public class Elephant extends Prey {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.3;
    // The elephant is 15% less active at sunset.
    private static final TimeOfDay LOW_ACTIVITY_TIME = TimeOfDay.SUNSET;
    private static final double LOW_ACTIVENESS = 0.85;

    /**
     * Constructor for an Elephant in the simulation.
     *
     * @param foodValue The food value the elephant is at initially.
     * @param randomAge Whether we assign this elephant a random age or not.
     * @param field The field in which this elephant resides.
     * @param location The location in which this elephant is spawned into.
     */
    public Elephant(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
    }

    /**
     * Getter method for the probability to breed of the elephant.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Create a new instance of Elephant.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Elephant instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Elephant(DEFAULT_FOOD_VALUE, true, field, location);
    }

    /**
     * Getter method for the time of day at which the elephant is less active.
     *
     * @return The TimeOfDay during which the elephant is less active.
     */
    @Override
    protected TimeOfDay getLowActivityTime() {
        return LOW_ACTIVITY_TIME;
    }

    /**
     * Getter method for the elephant's activeness during its low-activity time.
     *
     * @return A double value representing the reduced activeness.
     */
    @Override
    protected double getLowActiveness() {
        return LOW_ACTIVENESS;
    }
}

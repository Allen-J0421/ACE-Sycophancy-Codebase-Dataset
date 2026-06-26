/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Goat prey in the simulation. Shares the standard prey tuning defined in
 * {@link Prey}, differing only in breeding probability and its low-activity
 * period.
 *
 * @version 2022.03.02
 */
public class Goat extends Prey {

    // define fields
    private static final double BREEDING_PROBABILITY = 0.3065;
    // The goat is 20% less active in the late morning.
    private static final TimeOfDay LOW_ACTIVITY_TIME = TimeOfDay.LATE_MORNING;
    private static final double LOW_ACTIVENESS = 0.8;

    /**
     * Constructor for a Goat in the simulation.
     *
     * @param foodValue The food value the goat is at initially.
     * @param randomAge Whether we assign this goat a random age or not.
     * @param field The field in which this goat resides.
     * @param location The location in which this goat is spawned into.
     */
    public Goat(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location);
    }

    /**
     * Getter method for the probability to breed of the goat.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * Create a new instance of goat.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new goat instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Goat(DEFAULT_FOOD_VALUE, true, field, location);
    }

    /**
     * Getter method for the time of day at which the goat is less active.
     *
     * @return The TimeOfDay during which the goat is less active.
     */
    @Override
    protected TimeOfDay getLowActivityTime() {
        return LOW_ACTIVITY_TIME;
    }

    /**
     * Getter method for the goat's activeness during its low-activity time.
     *
     * @return A double value representing the reduced activeness.
     */
    @Override
    protected double getLowActiveness() {
        return LOW_ACTIVENESS;
    }
}

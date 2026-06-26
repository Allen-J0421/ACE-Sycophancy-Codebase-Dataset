/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Lion predator in the simulation. Shares the standard predator tuning
 * defined in {@link Predator}, differing only in lifespan, breeding age and
 * the time of day at which it rests.
 *
 * @version 2022.03.02
 */
public class Lion extends Predator {

    // define fields
    private static final int BREEDING_AGE = 32;
    private static final int MAX_AGE = 130;
    // The lion rests at night.
    private static final TimeOfDay INACTIVE_TIME = TimeOfDay.NIGHT;

    /**
     * Constructor for a lion in the simulation.
     *
     * @param foodLevel The food level the lion is at initially.
     * @param randomAge Whether we assign this lion a random age or not.
     * @param field The field in which this lion resides.
     * @param location The location in which this lion is spawned into.
     */
    public Lion(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Getter method for the maximum age of the lion.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the lion.
     *
     * @return An integer value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Create a new instance of Lion.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new lion instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Lion(DEFAULT_FOOD_LEVEL, true, field, location);
    }

    /**
     * Getter method for the time of day at which the lion rests.
     *
     * @return The TimeOfDay during which the lion is inactive.
     */
    @Override
    protected TimeOfDay getInactiveTime() {
        return INACTIVE_TIME;
    }
}

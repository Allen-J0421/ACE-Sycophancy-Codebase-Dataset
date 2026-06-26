/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation. Shares the standard predator tuning
 * defined in {@link Predator}, differing only in lifespan, breeding age and
 * the time of day at which it rests.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {

    // define fields
    private static final int BREEDING_AGE = 26;
    private static final int MAX_AGE = 140;
    // The cheetah rests in the early afternoon.
    private static final TimeOfDay INACTIVE_TIME = TimeOfDay.EARLY_AFTERNOON;

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param foodLevel The food level the cheetah is at initially.
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Getter method for the maximum age of the cheetah.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Getter method for the age of breeding of the cheetah.
     *
     * @return An integer value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Create a new instance of Cheetah.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Cheetah instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Cheetah(DEFAULT_FOOD_LEVEL, true, field, location);
    }

    /**
     * Getter method for the time of day at which the cheetah rests.
     *
     * @return The TimeOfDay during which the cheetah is inactive.
     */
    @Override
    protected TimeOfDay getInactiveTime() {
        return INACTIVE_TIME;
    }
}

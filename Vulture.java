/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Vulture scavenger in the simulation.
 *
 * @version 2022.03.02
 */
public class Vulture extends Scavenger {
    private static final int DEFAULT_FOOD_LEVEL = 40;
    private static final double BREEDING_PROBABILITY = 0.08;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int BREEDING_AGE = 12;
    private static final int MAX_AGE = 80;
    private static final double SPREAD_DISEASE_PROBABILITY = 0.01;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.001;

    /**
     * Constructor for a vulture in the simulation.
     *
     * @param foodLevel The food level of this vulture.
     * @param randomAge Whether the vulture should have a random age or not.
     * @param field The field in which the vulture resides.
     * @param location The location in which the vulture spawns into.
     */
    public Vulture(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location, Vulture.class, BREEDING_PROBABILITY,
                MAX_LITTER_SIZE, BREEDING_AGE, MAX_AGE, SPREAD_DISEASE_PROBABILITY,
                DEATH_BY_DISEASE_PROBABILITY, TimeOfDay.SUNSET);
    }

    /**
     * Create a new instance of Vulture.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Vulture instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Vulture(DEFAULT_FOOD_LEVEL, true, field, location);
    }
}

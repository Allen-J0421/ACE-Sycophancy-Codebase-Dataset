/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Cheetah predator in the simulation.
 *
 * @version 2022.03.02
 */
public class Cheetah extends Predator {
    private static final int DEFAULT_FOOD_LEVEL = 19;
    private static final double BREEDING_PROBABILITY = 0.115;
    private static final double EATING_PROBABILITY = 0.6;
    private static final int MAX_LITTER_SIZE = 2;
    private static final int BREEDING_AGE = 26;
    private static final int MAX_AGE = 140;
    private static final double SPREAD_DISEASE_PROBABILITY = 0.01;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.01;

    /**
     * Constructor for a Cheetah in the simulation.
     *
     * @param foodLevel The food level the cheetah is at initially.
     * @param randomAge Whether we assign this cheetah a random age or not.
     * @param field The field in which this cheetah resides.
     * @param location The location in which this cheetah is spawned into.
     */
    public Cheetah(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location, Cheetah.class, BREEDING_PROBABILITY,
                MAX_LITTER_SIZE, BREEDING_AGE, MAX_AGE, SPREAD_DISEASE_PROBABILITY,
                DEATH_BY_DISEASE_PROBABILITY, TimeOfDay.EARLY_AFTERNOON, EATING_PROBABILITY);
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
}

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Zebra prey in the simulation.
 *
 * @version 2022.03.02
 */
public class Zebra extends Herbivore {
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final double BREEDING_PROBABILITY = 0.305;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;
    private static final double SPREAD_DISEASE_PROBABILITY = 0.1;
    private static final double DEATH_BY_DISEASE_PROBABILITY = 0.001;

    /**
     * Constructor for a Zebra in the simulation.
     *
     * @param randomAge Whether we assign this zebra a random age or not.
     * @param field The field in which this zebra resides.
     * @param location The location in which this zebra is spawned into.
     */
    public Zebra(int foodValue, boolean randomAge, Field field, Location location) {
        super(foodValue, randomAge, field, location, Zebra.class, BREEDING_PROBABILITY,
                MAX_LITTER_SIZE, BREEDING_AGE, MAX_AGE, SPREAD_DISEASE_PROBABILITY,
                DEATH_BY_DISEASE_PROBABILITY, TimeOfDay.AROUND_MIDNIGHT, 0.9);
    }

    /**
     * Create a new instance of zebra.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new zebra instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Zebra(DEFAULT_FOOD_VALUE, true, field, location);
    }
}

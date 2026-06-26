/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Grass plant in the simulation.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant {
    private static final double DEFAULT_SIZE = 1.00;
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final double DEFAULT_GROWTH_RATE = 1.2;
    private static final double MAX_SIZE = 10.0;
    private static final int MAX_AGE = 25;
    private static final int BREEDING_AGE = 16;
    private static final int MAX_LITTER_SIZE = 2;
    private static final double LOW_BREEDING_PROBABILITY = 0.15;
    private static final double HIGH_BREEDING_PROBABILITY = 0.25;

    /**
     * Constructor for a Grass in the simulation.
     *
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field The field in which this grass resides.
     * @param location The location in which this grass is spawned into.
     */
    public Grass(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(false, foodValue, size, randomAge, field, location, MAX_SIZE, MAX_AGE, BREEDING_AGE,
                MAX_LITTER_SIZE, LOW_BREEDING_PROBABILITY, HIGH_BREEDING_PROBABILITY,
                WeatherType.RAIN, WeatherType.SUN);
        setGrowthRate(DEFAULT_GROWTH_RATE);
    }

    /**
     * Create a new instance of Grass.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new Grass instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new Grass(DEFAULT_FOOD_VALUE, DEFAULT_SIZE, true, field, location);
    }

}

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Grass plant in the simulation. Shares the standard plant tuning defined in
 * {@link Plant}, differing only in lifespan, litter size, breeding
 * probabilities and the weather it finds favourable.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant {

    // define fields
    private static final int MAX_AGE = 25;
    private static final double LOW_BREEDING_PROBABILITY = 0.15;
    private static final double HIGH_BREEDING_PROBABILITY = 0.25;
    private static final int MAX_LITTER_SIZE = 2;
    // Grass grows faster after recent rain or sun.
    private static final WeatherType[] FAVOURABLE_WEATHER = {WeatherType.RAIN, WeatherType.SUN};

    /**
     * Constructor for a Grass in the simulation.
     *
     * @param foodValue The food value of this grass.
     * @param size The initial size of this grass.
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field The field in which this grass resides.
     * @param location The location in which this grass is spawned into.
     */
    public Grass(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(false, foodValue, size, randomAge, field, location);
        setBreedingProbability(LOW_BREEDING_PROBABILITY);
    }

    /**
     * Getter method for the maximum age of the grass.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
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

    /**
     * Getter method for the maximum litter size of the newborn grass.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    @Override
    protected double getLowBreedingProbability() {
        return LOW_BREEDING_PROBABILITY;
    }

    @Override
    protected double getHighBreedingProbability() {
        return HIGH_BREEDING_PROBABILITY;
    }

    @Override
    protected WeatherType[] getFavourableWeather() {
        return FAVOURABLE_WEATHER;
    }
}

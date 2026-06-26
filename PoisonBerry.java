/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A PoisonBerry plant in the simulation. Shares the standard plant tuning
 * defined in {@link Plant}, differing only in lifespan, litter size, breeding
 * probabilities, the weather it finds favourable, and being poisonous.
 *
 * @version 2022.03.02
 */
public class PoisonBerry extends Plant {

    // define fields
    private static final int MAX_AGE = 20;
    private static final double LOW_BREEDING_PROBABILITY = 0.104;
    private static final double HIGH_BREEDING_PROBABILITY = 0.2;
    private static final int MAX_LITTER_SIZE = 3;
    // Poison berries grow faster after recent rain or snow.
    private static final WeatherType[] FAVOURABLE_WEATHER = {WeatherType.RAIN, WeatherType.SNOW};

    /**
     * Constructor for a PoisonBerry in the simulation.
     *
     * @param foodValue The food value of this berry.
     * @param size The initial size of this berry.
     * @param randomAge Whether the berry should have a random age or not.
     * @param field The field in which the berry resides.
     * @param location The location in which the berry spawns into.
     */
    public PoisonBerry(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(true, foodValue, size, randomAge, field, location);
        setBreedingProbability(LOW_BREEDING_PROBABILITY);
    }

    /**
     * Getter method for the maximum age of the berry.
     *
     * @return An integer value representing the maximum age.
     */
    @Override
    public int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * Create a new instance of this berry.
     * @param field The field in which the spawn will reside in.
     * @param location The location in which the spawn will occupy.
     * @return A new PoisonBerry instance.
     */
    @Override
    protected Organism createNewOrganism(Field field, Location location) {
        return new PoisonBerry(DEFAULT_FOOD_VALUE, DEFAULT_SIZE, true, field, location);
    }

    /**
     * Getter method for the maximum litter size of the berry's newborns.
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

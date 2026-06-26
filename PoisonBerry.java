import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An PoisonBerry plant present in the simulation.
 *
 * @version 2022.03.02
 */
public class PoisonBerry extends Plant {

    private static final double MAX_SIZE = 10.0;
    private static final int MAX_AGE = 20;
    private static final int BREEDING_AGE = 16;
    private static final double LOW_BREEDING_PROBABILITY = 0.104;
    private static final double HIGH_BREEDING_PROBABILITY = 0.2;
    private static final int MAX_LITTER_SIZE = 3;
    private static final double DEFAULT_SIZE = 1.00;
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final double DEFAULT_GROWTH_RATE = 1.2;

    /**
     * Constructor for a plant in the simulation.
     *
     * @param foodValue The food value of this plant.
     * @param size The initial size of this plant.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the plant resides.
     * @param location The location in which the plant spawns into.
     */
    public PoisonBerry(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(true, foodValue, size, randomAge, field, location);
        setGrowthRate(DEFAULT_GROWTH_RATE);
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
     * Getter method for the age of breeding of the berry.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
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
     * Abstract method for what the PoisonBerry does, i.e. what is always run at every step.
     *
     * @param newBerries A list of all newborn berries in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newBerries, Weather weather, TimeOfDay time) {
        if (isAlive()) {
            setBreedingProbability(LOW_BREEDING_PROBABILITY);

            //If it has recently rained or is snowy, grow at a higher growth rate
            if (weather.getRecentWeather().contains(WeatherType.RAIN) ||
                    weather.getRecentWeather().contains(WeatherType.SNOW)){
                setBreedingProbability(HIGH_BREEDING_PROBABILITY);
            }
            grow();
            giveBirth(newBerries);
        }
    }

    /**
     * Get maximum size of the berry.
     *
     * @return A double representing maximum size.
     */
    @Override
    public double getMaxSize() {
        return MAX_SIZE;
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

}

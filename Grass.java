import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A Grass plant in the simulation.
 *
 * @version 2022.03.02
 */
public class Grass extends Plant {

    // define fields
    private static final double MAX_SIZE = 10.0;
    private static final int MAX_AGE = 25;
    private static final int BREEDING_AGE = 16;
    private static final double LOW_BREEDING_PROBABILITY = 0.15;
    private static final double HIGH_BREEDING_PROBABILITY = 0.25;
    private static final int MAX_LITTER_SIZE = 2;
    private static final double DEFAULT_SIZE = 1.00;
    private static final int DEFAULT_FOOD_VALUE = 5;
    private static final double DEFAULT_GROWTH_RATE = 1.2;

    /**
     * Constructor for a Grass in the simulation.
     *
     * @param randomAge Whether we assign this grass a random age or not.
     * @param field The field in which this grass resides.
     * @param location The location in which this grass is spawned into.
     */
    public Grass(int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(false, foodValue, size, randomAge, field, location);
        setGrowthRate(DEFAULT_GROWTH_RATE);
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
     * Getter method for the age of breeding of the grass.
     *
     * @return A double value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
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
     * Method for what the grass does, i.e. what is always run at every step.
     *
     * @param newGrass A list of all newborn grass in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Organism> newGrass, Weather weather, TimeOfDay time) {
        if (isAlive()) {
            setBreedingProbability(LOW_BREEDING_PROBABILITY);

            //If it has recently rained or is sunny, grow at a higher growth rate
            if (weather.getRecentWeather().contains(WeatherType.RAIN) ||
                    weather.getRecentWeather().contains(WeatherType.SUN)){
                setBreedingProbability(HIGH_BREEDING_PROBABILITY);
            }
            grow();
            giveBirth(newGrass);
        }
    }

    /**
     * Get maximum size of the grass.
     *
     * @return A double representing maximum size.
     */
    @Override
    public double getMaxSize() {
        return MAX_SIZE;
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

}

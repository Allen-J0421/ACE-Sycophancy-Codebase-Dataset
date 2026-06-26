import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An abstract plant organism present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Plant extends Organism implements Growable, Consumable {

    // define fields
    // Tuning shared by all plants; individual species override only the values
    // that genuinely differ (see getMaxAge / getMaxLitterSize / the breeding
    // probabilities and favourable weather).
    private static final double MAX_SIZE = 10.0;
    private static final int BREEDING_AGE = 16;
    private static final double DEFAULT_GROWTH_RATE = 1.2;
    // Defaults used when spawning newborn plants.
    protected static final double DEFAULT_SIZE = 1.00;
    protected static final int DEFAULT_FOOD_VALUE = 5;

    private double size;
    private final int foodValue;
    private final boolean poisonous;
    // All plants share the same growth rate; species set their own breeding
    // probability (which also varies with the weather - see act).
    private double growthRate = DEFAULT_GROWTH_RATE;
    private double breedingProbability;

    /**
     * Constructor for a plant in the simulation.
     *
     * @param poisonous Whether the plant is poisonous or not.
     * @param foodValue The food value of this plant.
     * @param size The initial size of this plant.
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the plant resides.
     * @param location The location in which the plant spawns into.
     */
    public Plant(boolean poisonous, int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);

        this.size = size;
        this.foodValue = foodValue;
        this.poisonous = poisonous;
    }

    /**
     * What the plant does at every step: while alive it breeds faster when the
     * recent weather has been favourable, then grows and reproduces. This is
     * shared by all plants; species vary only in their breeding probabilities
     * and which weather they find favourable.
     *
     * @param newPlants A list of all newborn plants in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newPlants, Weather weather, TimeOfDay time) {
        if (!isAlive()) {
            return;
        }

        setBreedingProbability(getLowBreedingProbability());
        for (WeatherType favourable : getFavourableWeather()) {
            if (weather.getRecentWeather().contains(favourable)) {
                setBreedingProbability(getHighBreedingProbability());
                break;
            }
        }

        grow();
        giveBirth(newPlants);
    }

    /**
     * Getter method for this plant's breeding probability under normal weather.
     *
     * @return A double value representing the standard breeding probability.
     */
    abstract protected double getLowBreedingProbability();

    /**
     * Getter method for this plant's breeding probability when the weather has
     * recently been favourable.
     *
     * @return A double value representing the boosted breeding probability.
     */
    abstract protected double getHighBreedingProbability();

    /**
     * Getter method for the weather types under which this plant breeds faster.
     *
     * @return The weather types this plant finds favourable.
     */
    abstract protected WeatherType[] getFavourableWeather();

    /**
     * Grow in size in accordance with the current growth rate.
     */
    @Override
    public void grow() {
        size = size*getGrowthRate() > getMaxSize() ? 1 : size*getGrowthRate();
        if (size == 0) {
            remove();
        }
    }

    /**
     * Getter method for rate of growth of this plant.
     *
     * @return A double representing growth rate.
     */
    @Override
    public double getGrowthRate() {
        return this.growthRate;
    }

    /**
     * Setter method for rate of growth of this plant.
     *
     * @param rate A given growth rate.
     */
    @Override
    public void setGrowthRate(double rate) {
        this.growthRate = rate;
    }

    /**
     * Get maximum size of the plant.
     *
     * @return A double representing maximum size.
     */
    @Override
    public double getMaxSize() {
        return MAX_SIZE;
    }

    /**
     * Getter method for the age of breeding of the plant.
     *
     * @return An integer value representing the breeding age.
     */
    @Override
    public int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * Getter method for the maximum litter size of the plant's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    abstract public int getMaxLitterSize();

    /**
     * Checks if the plant meets specified conditions in order to breed.
     *
     * @return Whether this plant can breed or not.
     */
    @Override
    protected boolean canBreed() {
        return getAge() >= getBreedingAge();
    }

    /**
     * Get the current size of the plant.
     *
     * @return A double representing current size.
     */
    @Override
    public double getSize() {
        return this.size;
    }

    /**
     * Getter method of the value of the food of this plant.
     *
     * @return An integer for the plant's food value.
     */
    @Override
    public int getFoodValue() {
        return this.foodValue;
    }

    /**
     * Setter method to clear this plant from the simulation
     * when it is eaten.
     */
    @Override
    public void setEaten() {
        if (getLocation() != null) {
            getField().clear(getLocation());
            setLocationToNull();
            setField(null);
        }
    }

    /**
     * Returns whether this plant is poisonous or not.
     * @return Whether this plant is poisonous or not.
     */
    @Override
    public boolean isPoisonous() {
        return this.poisonous;
    }

    /**
     * Setter  method for the probability to breed of the organism.
     *
     * @param probability A given double value representing the breeding probability.
     */
    protected void setBreedingProbability(double probability) {
        this.breedingProbability = probability;
    }

    /**
     * Getter method for the probability to breed of the organism.
     *
     * @return A double value representing the breeding probability.
     */
    @Override
    public double getBreedingProbability() {
        return this.breedingProbability;
    }
}

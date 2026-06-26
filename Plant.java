import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * An abstract plant organism present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Plant extends Organism implements Growable, Consumable {

    private double size;
    private final int foodValue;
    private final boolean poisonous;
    private final PlantTraits traits;
    private double growthRate;
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
     * @param traits Immutable species configuration.
     */
    public Plant(boolean poisonous, int foodValue, double size, boolean randomAge, Field field,
                 Location location, PlantTraits traits) {
        super(randomAge, traits.maxAge, field, location);

        this.size = size;
        this.foodValue = foodValue;
        this.poisonous = poisonous;
        this.traits = traits;
        this.growthRate = traits.growthRate;
    }

    /**
     * Method for what the plant does, i.e. what is always run at every step.
     *
     * @param newPlants A list of all newborn plants in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newPlants, Weather weather, TimeOfDay time) {
        if (isAlive()) {
            setBreedingProbabilityForWeather(weather, traits.lowBreedingProbability,
                    traits.highBreedingProbability, traits.favorableWeather);
            grow();
            giveBirth(newPlants);
        }
    }

    /**
     * Grow in size in accordance with the current growth rate.
     */
    @Override
    public void grow() {
        double grownSize = size * getGrowthRate();
        size = grownSize > traits.maxSize ? 1 : grownSize;
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
        return traits.maxSize;
    }

    /**
     * Getter method for the maximum litter size of the plant's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return traits.maxLitterSize;
    }

    /**
     * Checks if the plant meets specified conditions in order to breed.
     *
     * @return Whether this plant can breed or not.
     */
    @Override
    protected boolean canBreed() {
        return getAge() >= traits.breedingAge;
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
        clearFromField();
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
     * Setter method for the probability to breed of the organism.
     *
     * @param probability A given double value representing the breeding probability.
     */
    protected void setBreedingProbability(double probability) {
        this.breedingProbability = probability;
    }

    /**
     * Set the breeding probability to the high value when the weather matches.
     *
     * @param weather The current weather state.
     * @param lowProbability The default breeding probability.
     * @param highProbability The breeding probability used for favorable weather.
     * @param favorableWeather The weather types that should trigger the higher probability.
     */
    protected void setBreedingProbabilityForWeather(Weather weather, double lowProbability,
                                                    double highProbability, WeatherType... favorableWeather) {
        setBreedingProbability(lowProbability);
        for (WeatherType weatherType : favorableWeather) {
            if (weather.getRecentWeather().contains(weatherType)) {
                setBreedingProbability(highProbability);
                break;
            }
        }
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

    /**
     * @return The maximum age for this plant species.
     */
    @Override
    public int getMaxAge() {
        return traits.maxAge;
    }

    /**
     * @return The breeding age for this plant species.
     */
    @Override
    public int getBreedingAge() {
        return traits.breedingAge;
    }
}

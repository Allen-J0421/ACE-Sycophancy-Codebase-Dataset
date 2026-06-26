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
    private final PlantAttributes attributes;
    private double size;
    private double growthRate;
    private double breedingProbability;

    /**
     * Constructor for a plant in the simulation.
     *
     * @param randomAge Whether the animal should have a random age or not.
     * @param field The field in which the plant resides.
     * @param location The location in which the plant spawns into.
     */
    public Plant(PlantAttributes attributes, boolean randomAge, Field field,
                 Location location, OrganismFactory offspringFactory) {
        super(randomAge, field, location, attributes, offspringFactory);
        this.attributes = attributes;
        this.size = attributes.getInitialSize();
        this.growthRate = attributes.getGrowthRate();
        this.breedingProbability = attributes.getLowBreedingProbability();
    }

    /**
     * Abstract method for what the plant does, i.e. what is always run at every step.
     *
     * @param newPlants A list of all newborn plants in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Organism> newPlants, Weather weather, TimeOfDay time) {
        if (isAlive()) {
            setBreedingProbability(getLowBreedingProbability());
            if (hasFavorableWeather(weather)) {
                setBreedingProbability(getHighBreedingProbability());
            }
            grow();
            giveBirth(newPlants);
        }
    }

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
        return attributes.getMaxSize();
    }

    /**
     * Getter method for the maximum litter size of the plant's newborns.
     *
     * @return An integer value representing the maximum allowed litter size.
     */
    @Override
    public int getMaxLitterSize() {
        return super.getMaxLitterSize();
    }

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
        return attributes.getFoodValue();
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
        return attributes.isPoisonous();
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

    protected boolean hasFavorableWeather(Weather weather) {
        for (WeatherType weatherType : weather.getRecentWeather()) {
            if (attributes.getFavorableWeather().contains(weatherType)) {
                return true;
            }
        }
        return false;
    }

    protected double getLowBreedingProbability() {
        return attributes.getLowBreedingProbability();
    }

    protected double getHighBreedingProbability() {
        return attributes.getHighBreedingProbability();
    }
}

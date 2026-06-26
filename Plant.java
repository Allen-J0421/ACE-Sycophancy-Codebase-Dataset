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
    private double size;
    private final int foodValue;
    private final boolean poisonous;
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
     */
    public Plant(boolean poisonous, int foodValue, double size, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);

        this.size = size;
        this.foodValue = foodValue;
        this.poisonous = poisonous;
    }

    /**
     * Abstract method for what the plant does, i.e. what is always run at every step.
     *
     * @param newPlants A list of all newborn plants in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    abstract public void act(List<Entity> newPlants, Weather weather, TimeOfDay time);

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
    abstract public double getMaxSize();

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
        clearLocation();
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

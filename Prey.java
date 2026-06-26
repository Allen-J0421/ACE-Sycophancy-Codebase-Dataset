import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Abstract class for a prey in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Prey extends Animal implements Consumable {

    // define fields
    private int foodValue;

    /**
     * Constructor for a Prey in the simulation.
     *
     * @param foodValue The value of food of this prey.
     * @param randomAge Whether we assign this prey a random age or not.
     * @param field The field in which this prey resides.
     * @param location The location in which this prey is spawned into.
     */
    public Prey(int foodValue, boolean randomAge, int maxAge, Field field, Location location) {
        super(randomAge, maxAge, field, location);

        this.foodValue = foodValue;
    }

    /**
     * Method for what the prey does, i.e. what is always run at every step.
     *
     * @param newPrey A list of all newborn prey in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    abstract public void act(List<Entity> newPrey, Weather weather, TimeOfDay time);

    /**
     * Checks all adjacent location for prey that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this prey can breed or not.
     */
    @Override
    abstract public boolean canBreed();

    /**
     * Clear the prey from the simulation as it has been eaten.
     */
    @Override
    public void setEaten() {
        clearFromField();
    }

    /**
     * Find a nearby plant and consume it if possible.
     *
     * @return The location of the plant that was eaten, or null.
     */
    @Override
    public Location findFood() {
        return findAdjacentObject(Plant.class, plant -> {
            if (!plant.isAlive()) {
                return false;
            }
            plant.setDead();
            return eat(plant);
        });
    }

    /**
     * Getter method for this prey's food value.
     * @return The food value of the prey.
     */
    @Override
    public int getFoodValue() {
        return this.foodValue;
    }

    /**
     * Increment the food value of this prey by a specified amount.
     * @param foodValue The given amount to increase the food value by.
     */
    protected void incrementFoodValue(int foodValue) {
        this.foodValue += foodValue;
    }

    /**
     * Called when this prey eats a plant.
     *
     * @param consumable The plant to be eaten.
     * @return Whether the plant was eaten.
     */
    @Override
    public boolean eat(Consumable consumable) {
        if ((consumable.isPoisonous()) && (!isInfected())) {
            infect(this);
        }
        incrementFoodValue(consumable.getFoodValue());
        consumable.setEaten();
        return true;
    }

    /**
     * Prey can never be poisonous.
     * @return false as prey don't have this attribute.
     */
    @Override
    public boolean isPoisonous() {
        return false;
    }

}

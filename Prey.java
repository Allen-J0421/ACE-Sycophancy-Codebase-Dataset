import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * Abstract class for a prey in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Prey extends Animal implements Consumable {

    private static final double DEFAULT_ACTIVENESS = 1;
    private static final int FOOD_VALUE_TO_STOP_EATING = 10;
    private static final Random rand = Randomizer.getRandom();

    private int foodValue;
    private double activeness;  // denotes how likely it is for the act method to be called

    /**
     * Constructor for a Prey in the simulation.
     *
     * @param foodValue The value of food of this prey.
     * @param randomAge Whether we assign this prey a random age or not.
     * @param field The field in which this prey resides.
     * @param location The location in which this prey is spawned into.
     */
    public Prey(int foodValue, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);

        this.foodValue = foodValue;
        this.activeness = DEFAULT_ACTIVENESS;
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
     * Run the common prey lifecycle for one simulation step.
     *
     * @param newPrey A list of all newborn prey in this simulation step.
     * @param time The current state of time in the simulation.
     * @param reducedActivenessTime The time of day when this prey is less active.
     * @param reducedActiveness The activeness to use during the reduced activity time.
     */
    protected void actAsPrey(List<Entity> newPrey, TimeOfDay time,
                             TimeOfDay reducedActivenessTime, double reducedActiveness) {
        incrementAge();
        setActiveness(DEFAULT_ACTIVENESS);

        if (isAlive()) {
            giveBirth(newPrey);

            if (diesFromDisease()) {
                remove();
                return;
            }

            if (time == reducedActivenessTime) {
                setActiveness(reducedActiveness);
            }

            if (rand.nextDouble() <= getActiveness()) {
                Location newLocation = findFoodOrInfectionTarget();

                if ((newLocation == null) || (getFoodValue() > FOOD_VALUE_TO_STOP_EATING)) {
                    newLocation = getField().freeAdjacentLocation(getLocation());
                }

                moveOrRemove(newLocation);
            }
        } else {
            decayIfDead();
        }
    }

    /**
     * Checks all adjacent location for prey that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this prey can breed or not.
     */
    @Override
    public boolean canBreed() {
        return hasNearbyMate(getClass());
    }

    /**
     * Clear the prey from the simulation as it has been eaten.
     */
    @Override
    public void setEaten() {
        clearLocation();
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

    /**
     * Find a nearby plant this prey can eat.
     *
     * @return The location of the eaten plant, or null if no food was found.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        for (Location where : field.adjacentLocations(getLocation())) {
            Organism organism = field.getOrganismAt(where);
            if (organism instanceof Plant) {
                Plant plant = (Plant) organism;
                if (plant.isAlive()) {
                    plant.setDead();
                    boolean eaten = eat(plant);
                    return eaten ? where : null;
                }
            }
        }
        return null;
    }

    /**
     * The activeness of this prey.
     * @return A double value for the activeness of this prey
     */
    public double getActiveness() {
        return activeness;
    }

    /**
     * Setter method for the activeness of this prey.
     *
     * @param activeness A given value for activeness.
     */
    public void setActiveness(double activeness) {
        this.activeness = activeness;
    }
}

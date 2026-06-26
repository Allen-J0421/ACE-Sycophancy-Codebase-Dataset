import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A predator animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Predator extends Animal {

    // shared random generator to generate consistent results
    private static final Random rand = Randomizer.getRandom();

    private int foodLevel;

    /**
     * Constructor for a predator in the simulation.
     *
     * @param foodLevel The food level of this predator.
     * @param randomAge Whether the predator should have a random age or not.
     * @param field The field in which the predator resides.
     * @param location The location in which the predator spawns into.
     */
    public Predator(int foodLevel, boolean randomAge, Field field, Location location) {
        super(randomAge, field, location);

        this.foodLevel = foodLevel;
    }

    /**
     * Abstract method for what the predator does, i.e. what is always run at every step.
     *
     * @param newPredators A list of all newborn predators in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    abstract public void act(List<Entity> newPredators, Weather weather, TimeOfDay time);

    /**
     * Run the common predator lifecycle for one simulation step.
     *
     * @param newPredators A list of all newborn predators in this simulation step.
     * @param time The current state of time in the simulation.
     * @param inactiveTime The time of day when this predator does not move or eat.
     */
    protected void actAsPredator(List<Entity> newPredators, TimeOfDay time, TimeOfDay inactiveTime) {
        incrementAge();
        incrementHunger();
        if (isAlive()) {
            giveBirth(newPredators);

            if (time == inactiveTime) {
                return;
            }

            if (diesFromDisease()) {
                remove();
                return;
            }

            Location newLocation = findFoodOrInfectionTarget();
            if (newLocation == null) {
                newLocation = getField().freeAdjacentLocation(getLocation());
            }

            moveOrRemove(newLocation);
        }
    }

    /**
     * Finds the nearest food source and returns its location.
     *
     * @return Location of food source.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();

            Organism organism = field.getOrganismAt(where);
            if(organism instanceof Prey) {
                Prey prey = (Prey) organism;
                if (prey.isAlive()) {
                    // kills prey
                    prey.setDead();
                    // random chance to eat
                    boolean eaten = eat(prey);

                    return eaten ? where : null;
                }
            }
        }
        return null;
    }

    /**
     * Getter method to return this predator's probability of eating if food is found.
     *
     * @return The predator's eating probability.
     */
    abstract public double getEatingProbability();

    /**
     * Called when a predator either eats or leaves a prey it
     * has killed.
     *
     * @param consumable The prey to be eaten.
     * @return Whether the prey has been eaten or not.
     */
    @Override
    public boolean eat(Consumable consumable) {
        if (rand.nextDouble() <= getEatingProbability()) {
            incrementFoodLevel(consumable.getFoodValue());
            consumable.setEaten();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Checks all adjacent location for predators that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this lion can breed or not.
     */
    @Override
    public boolean canBreed() {
        return hasNearbyMate(getClass());
    }

    /**
     * Increase the predator's food level by a given integer amount.
     *
     * @param foodLevel The value to increment food level by.
     */
    public void incrementFoodLevel(int foodLevel) {
        this.foodLevel += foodLevel;
    }

    /**
     * Make this predator more hungry. This could result in the predator's death.
     */
    public void incrementHunger() {
        foodLevel--;
        if (foodLevel <= 0) {
            remove();
        }
    }
}

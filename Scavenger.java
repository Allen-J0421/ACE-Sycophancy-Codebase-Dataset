import java.util.Iterator;
import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A scavenger animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Scavenger extends FoodDependentAnimal {

    /**
     * Constructor for a scavenger in the simulation.
     *
     * @param foodLevel The food level of this scavenger.
     * @param randomAge Whether the scavenger should have a random age or not.
     * @param field The field in which the scavenger resides.
     * @param location The location in which the scavenger spawns into.
     */
    public Scavenger(int foodLevel, boolean randomAge, Field field, Location location) {
        super(foodLevel, randomAge, field, location);
    }

    /**
     * Method for what the scavenger does, i.e. what is always run at every step.
     *
     * @param newScavengers A list of all newborn scavengers in this simulation step.
     * @param weather The current state of weather in the simulation.
     * @param time The current state of time in the simulation.
     */
    @Override
    public void act(List<Entity> newScavengers, Weather weather, TimeOfDay time) {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            giveBirth(newScavengers);

            if (time == TimeOfDay.SUNSET){
                return;
            }

            if (diesFromDisease()) {
                remove();
                return;
            }

            // Move towards a source of food if found.
            Location newLocation = findFoodOrInfectionTarget();

            if(newLocation == null) {
                // No food found - try to move to a free location.
                newLocation = Placement.freeAdjacentLocation(getField(), getLocation());
            }

            // See if it was possible to move.
            moveOrRemove(newLocation);
        }
    }

    /**
     * Find a food source the scavenger would want to eat.
     * @return The location of the food source.
     */
    @Override
    public Location findFood() {
        Field field = getField();
        List<Location> adjacent = field.adjacentLocations(getLocation());
        Iterator<Location> it = adjacent.iterator();
        while(it.hasNext()) {
            Location where = it.next();
            Organism organism = Placement.getOccupant(field, where);
            if (organism instanceof Prey) {
                Prey prey = (Prey) organism;
                // eats prey if dead only
                if (!prey.isAlive()) {
                    eat(prey);
                    return where;
                }
            }
        }
        return null;
    }

    /**
     * Eat the corpse of a dead prey.
     *
     * @param consumable The item to be eaten.
     * @return Whether the consumable was eaten or not.
     */
    @Override
    public boolean eat(Consumable consumable) {
        // the scavenger does not leave its prey
        consumable.setEaten();
        incrementFoodLevel(consumable.getFoodValue());
        return true;
    }

    /**
     * Checks all adjacent location for scavengers that meet specific
     * breeding conditions, and returns true if it is even possible.
     *
     * @return Whether this scavenger can breed or not.
     */
    @Override
    public boolean canBreed() {
        return hasNearbyMate(getClass());
    }

}

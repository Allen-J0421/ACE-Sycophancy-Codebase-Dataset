import java.util.Iterator;
import java.util.List;

/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A scavenger animal present in the simulation.
 *
 * @version 2022.03.02
 */
public abstract class Scavenger extends Hunter {

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
            Organism animal = field.getOrganismAt(where);
            if (animal instanceof Prey) {
                Prey prey = (Prey) animal;
                // eats animal if dead only
                if (!prey.isAlive()) {
                    //System.out.println("EATEN DEAD");
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

}

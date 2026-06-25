/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A contractual interface representing an ability to eat Consumables.
 *
 * @version 2022.03.02
 */
public interface AbleToEat {

    /**
     * Finds a nearby food source, and returns its location in the field.
     *
     * @return Location of food source.
     */
    Location findFood();

    /**
     * Called when a consumable food item may be eaten.
     *
     * @param consumable The item to be eaten.
     * @return Whether the item was successfully eaten or not.
     */
    boolean eat(Consumable consumable);
}

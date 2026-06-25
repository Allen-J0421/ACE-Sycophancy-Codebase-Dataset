/**
 * This file is part of the Predator-Prey Simulation.
 *
 * A consumable item in the simulation.
 *
 * @version 2022.03.02
 */
public interface Consumable {

    /**
     * Getter method of the value of the food of this consumable.
     *
     * @return An integer for the consumable's food value.
     */
    int getFoodValue();

    /**
     * Setter method called when this consumable has been consumed.
     */
    void setEaten();

    /**
     * Returns whether this consumable item is poisonous or not.
     * @return Whether this consumable item is poisonous or not.
     */
    boolean isPoisonous();
}

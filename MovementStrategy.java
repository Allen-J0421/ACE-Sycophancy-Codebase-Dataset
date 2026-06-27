/**
 * Strategy interface for animal movement decisions.
 *
 * Implementations encapsulate the priority logic that determines where an
 * animal moves each simulation step, allowing different movement behaviours
 * to be swapped in without touching Animal or its subclasses.
 *
 * @version 2022.03.01
 */
public interface MovementStrategy
{
    /**
     * Select the next location for the given animal to move to.
     *
     * @param animal The animal that is about to move.
     * @return The chosen destination, or null if no suitable location exists
     *         (the animal will die of overcrowding).
     */
    Location selectDestination(Animal animal);
}

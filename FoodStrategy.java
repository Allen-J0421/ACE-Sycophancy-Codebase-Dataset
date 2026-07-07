import java.util.ArrayList;

/**
 * Strategy interface for finding and consuming food. The returned Location is
 * where the animal should move after eating (e.g. the prey's cell for a
 * predator), or null if the move destination should be chosen independently.
 *
 * @version 2022.03.01
 */
public interface FoodStrategy
{
    /**
     * Search for food near the animal and eat it if found.
     *
     * @param animal (Animal) The animal performing the search.
     * @param neighbors (ArrayList<Animal>) Live animals in adjacent cells.
     * @return (Location) Destination to move to after eating, or null.
     */
    Location findFoodAndEat(Animal animal, ArrayList<Animal> neighbors);
}

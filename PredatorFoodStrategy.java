import java.util.ArrayList;

/**
 * Food strategy for predators: kills and eats the first non-Predator animal
 * found among the neighboring animals. Returns the prey's location so the
 * predator can move there, or null if no prey was found.
 *
 * @version 2022.03.01
 */
public class PredatorFoodStrategy implements FoodStrategy
{
    /**
     * Eat the first non-Predator neighbor, incrementing the predator's food
     * level by the prey's nutritional value.
     *
     * @param animal (Animal) The predator performing the hunt.
     * @param neighbors (ArrayList<Animal>) Live animals in adjacent cells.
     * @return (Location) The prey's location (null after setDead), or null if
     *         no prey was found.
     */
    public Location findFoodAndEat(Animal animal, ArrayList<Animal> neighbors)
    {
        for (Animal neighbor : neighbors) {
            if (!(neighbor instanceof Predator)) {
                neighbor.setDead();
                animal.incrementFoodLevel(neighbor.getNutritionalValue());
                return neighbor.getLocation();
            }
        }
        return null;
    }
}

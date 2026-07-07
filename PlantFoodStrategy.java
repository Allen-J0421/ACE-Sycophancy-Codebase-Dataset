import java.util.ArrayList;
import java.util.List;

/**
 * Food strategy for prey animals: scans adjacent cells for a live Plant and
 * eats the first one found. Returns null because prey do not move to their
 * food source — movement is resolved separately in Animal.makeMove().
 *
 * @version 2022.03.01
 */
public class PlantFoodStrategy implements FoodStrategy
{
    /**
     * Look for a live Plant in cells adjacent to the animal and eat it.
     *
     * @param animal (Animal) The animal performing the search.
     * @param neighbors (ArrayList<Animal>) Unused; present to satisfy the interface.
     * @return (Location) Always null — prey animals move independently of food.
     */
    public Location findFoodAndEat(Animal animal, ArrayList<Animal> neighbors)
    {
        Field field = animal.getField();
        List<Location> adjacent = field.adjacentLocations(animal.getLocation());
        for (Location where : adjacent) {
            Object obj = field.getObjectAt(where);
            if (obj instanceof Plant) {
                Plant plant = (Plant) obj;
                if (plant.isAlive()) {
                    plant.isEaten();
                    animal.incrementFoodLevel(plant.getNutritionalValue());
                    break;
                }
            }
        }
        return null;
    }
}

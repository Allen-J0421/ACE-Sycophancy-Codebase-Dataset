import java.util.Iterator;

/**
 * Dietary behaviour for herbivores: searches adjacent cells for plants
 * and eats the first available one when hungry.
 */
public class HerbivoreDiet implements DietaryBehaviour
{
    @Override
    public Location findFood(Animal self, Field field, SimRandom rand)
    {
        Iterator<Location> it = field.adjacentLocations(self.getLocation());

        while (it.hasNext())
        {
            Location where = it.next();
            Plant plant   = (Plant)  field.getObjectAt(where, Plant.class);
            Animal animal = (Animal) field.getObjectAt(where, Animal.class);

            // Only eat from a cell that no animal already occupies.
            if (animal == null && plant != null && plant.isAlive() && self.foodLevel < self.maxFoodLevel)
            {
                self.foodLevel += plant.beEaten();
                return where;
            }
        }

        return null;
    }
}

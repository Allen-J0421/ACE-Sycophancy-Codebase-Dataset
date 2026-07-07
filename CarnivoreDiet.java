import java.util.Iterator;
import java.util.List;

/**
 * Dietary behaviour for carnivores: searches adjacent cells for prey,
 * applies a catch-probability roll, and may become infected from a diseased kill.
 */
public class CarnivoreDiet implements DietaryBehaviour
{
    private final double preyCatchingProbability;

    public CarnivoreDiet(double preyCatchingProbability)
    {
        this.preyCatchingProbability = preyCatchingProbability;
    }

    @Override
    public Location findFood(Animal self, Field field, SimRandom rand)
    {
        List<Location> adjacent = field.adjacentLocations(self.getLocation());
        Iterator<Location> it = adjacent.iterator();

        while (it.hasNext())
        {
            Location where  = it.next();
            Object   object = field.getObjectAt(where, Animal.class);

            if (object instanceof Prey)
            {
                Prey prey = (Prey) object;
                if (prey.isAlive() && self.foodLevel < self.maxFoodLevel
                        && rand.nextDouble() < preyCatchingProbability)
                {
                    self.foodLevel += prey.beEaten();

                    if (prey.getIsInfected() && !self.immune
                            && rand.nextDouble() <= self.diseaseSpreadProbability)
                    {
                        self.infected = true;
                    }

                    return where;
                }
            }
        }

        return null;
    }
}

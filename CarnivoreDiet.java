import java.util.Iterator;

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
        Iterator<Location> it = field.adjacentLocations(self.getLocation());

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
                    self.tryInfectFrom(prey.getIsInfected(), rand);
                    return where;
                }
            }
        }

        return null;
    }
}

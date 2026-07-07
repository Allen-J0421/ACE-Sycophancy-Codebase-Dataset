import java.util.List;

/**
 * Reproduction strategy for predators: creates Predator offspring that inherit
 * the parent's species parameters and strength.
 *
 * @version 2022.03.01
 */
public class PredatorReproductionStrategy implements ReproductionStrategy
{
    // Inherited by all offspring of this predator species.
    private final int strength;

    /**
     * @param strength (int) The strength passed to each newborn Predator.
     */
    public PredatorReproductionStrategy(int strength)
    {
        this.strength = strength;
    }

    /**
     * Spawn Predator offspring in free cells adjacent to the parent.
     *
     * @param parent (Animal) The reproducing predator.
     * @param newOfThisKind (List<Species>) List to add the newborns to.
     */
    public void reproduce(Animal parent, List<Species> newOfThisKind)
    {
        Field field = parent.getField();
        if (field != null) {
            List<Location> free = field.getFreeAdjacentLocations(parent.getLocation());
            int births = parent.numberOfBirths();
            for (int b = 0; b < births && free.size() > 0; b++) {
                Location loc = free.remove(0);
                Predator young = new Predator(strength, field, loc, parent.getName(),
                        parent.getMaximumTemperature(), parent.getMinimumTemperature(),
                        parent.getNutritionalValue(), parent.getReproductionProbability(),
                        parent.getMaxAge(), parent.getBreedingAge(), parent.getMaxLitterSize(),
                        false, parent.getHibernates(), parent.getIsNocturnal());
                newOfThisKind.add(young);
            }
        }
    }
}

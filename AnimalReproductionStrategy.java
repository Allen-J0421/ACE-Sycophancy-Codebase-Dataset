import java.util.List;

/**
 * Reproduction strategy for prey animals: creates Animal offspring that share
 * the parent's species parameters.
 *
 * @version 2022.03.01
 */
public class AnimalReproductionStrategy implements ReproductionStrategy
{
    /**
     * Spawn Animal offspring in free cells adjacent to the parent.
     *
     * @param parent (Animal) The reproducing animal.
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
                Animal young = new Animal(field, loc, parent.getName(),
                        parent.getMaximumTemperature(), parent.getMinimumTemperature(),
                        parent.getNutritionalValue(), parent.getReproductionProbability(),
                        parent.getMaxAge(), parent.getBreedingAge(), parent.getMaxLitterSize(),
                        false, parent.getHibernates(), parent.getIsNocturnal());
                newOfThisKind.add(young);
            }
        }
    }
}

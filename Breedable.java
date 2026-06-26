import java.util.List;

/**
 * Mixin for organisms that reproduce through nearby free locations.
 *
 * @version 26/02/2022
 */
public interface Breedable extends OrganismContext
{
    Class<?> getBreedingSpaceType();

    int determineOffspringCount(int availableLocations);

    LivingOrganism spawnOffspringAt(Location location);

    default void populateOffspring(List<LivingOrganism> newAnimals)
    {
        Field field = currentField();
        List<Location> free = field.getFreeAdjacentLocations(currentLocation(), getBreedingSpaceType());
        int births = determineOffspringCount(free.size());

        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            newAnimals.add(spawnOffspringAt(loc));
        }
    }
}

import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Mixin for organisms that reproduce through nearby free locations.
 *
 * @version 26/02/2022
 */
public interface Breedable extends OrganismContext, Ageable
{
    Random RANDOM = Randomizer.getRandom();

    BreedingState getBreedingState();

    DiseaseState getDiseaseState();

    Animal spawnOffspring(Location location, boolean isInfected, boolean isImmune);

    default boolean canBreedNow()
    {
        return getAgeState().getAge() >= getBreedingState().getBreedingAge();
    }

    default void populateOffspring(List<LivingOrganism> newAnimals)
    {
        Field field = currentField();
        List<Location> free = field.getFreeAdjacentLocations(currentLocation(), Animal.class);
        int births = calculateBirths();

        for(int b = 0; b < births && free.size() > 0; b++) 
        {
            Location loc = free.remove(0);
            DiseaseState diseaseState = getDiseaseState();
            boolean offspringIsInfected = diseaseState.isInfected();
            boolean offspringIsImmune = diseaseState.isImmune();

            if (!diseaseState.isImmune() && diseaseState.isInfected() && RANDOM.nextDouble() < 0.15) 
            {
                offspringIsImmune = true;
                offspringIsInfected = false;
            }
            else if (diseaseState.isImmune() && RANDOM.nextDouble() < 0.9)
            {
                offspringIsImmune = false;
            }

            newAnimals.add(spawnOffspring(loc, offspringIsInfected, offspringIsImmune));
        }
    }

    default int calculateBirths()
    {
        Field field = currentField();
        List<Location> adjacent = field.adjacentLocations(currentLocation());
        Iterator<Location> it = adjacent.iterator();

        while(it.hasNext()) 
        {
            Location where = it.next();
            Animal animal = field.getObjectAt(where, Animal.class);

            if (animal != null && this.getClass().equals(animal.getClass())) 
            {
                if(!animal.getBreedingState().isFemale()) 
                {
                    return RANDOM.nextInt(getBreedingState().getMaxLitterSize()) + 1;
                }
            }
        }

        return 0;
    }
}

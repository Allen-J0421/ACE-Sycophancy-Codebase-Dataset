import java.util.List;
import java.util.Random;

/**
 * Shared movement helpers for animal movement strategies.
 */
public abstract class AbstractMovementStrategy implements MovementStrategy
{
    private static final Random rand = Randomizer.getRandom();

    protected void moveOrHandleBlocked(Animal animal, Location newLocation)
    {
        if(newLocation != null) {
            animal.setLocation(newLocation);
        }
        else if(moveToAdjacentGrass(animal)) {
            return;
        }
        else if(animal.diesFromOvercrowding()) {
            animal.setDead();
        }
    }

    private boolean moveToAdjacentGrass(Animal animal)
    {
        List<Location> adjacentGrassSpots = animal.getField().adjacentLocationsWithSpecies(animal.getLocation(), Grass.class);
        if(adjacentGrassSpots.isEmpty()) {
            return false;
        }

        animal.getField().clear(animal.getLocation());
        animal.setLocation(adjacentGrassSpots.get(rand.nextInt(adjacentGrassSpots.size())));
        return true;
    }
}

package models.animal.behaviour.breeding;

import java.util.List;

import field.Location;
import models.animal.Animal;

/**
 * Returns the location of the first free adjacent Block to put the offspring.
 *
 */
public class DefaultBreeding extends BreedingBehaviour {
    public DefaultBreeding(Animal animal) {
        super(animal);
    }

    /**
     * Returns the location of the first free location adjacent to the parent to put the offspring.
     * @param location the location of the animal
     * @return the Location where to create the offspring
     */
    @Override
    public Location act(Location location) {
        if (animal.getAge() < 0.1 * animal.getMaxAge()) {
            return null;
        }

        if (animal.getChildren() >= animal.getMaxChildren()) {
            return null;
        }

        List<Location> locations = field.getAdjacentLocations(location);
        for (Location loc : locations) {
            if (field.getBlockAt(loc).getEntity() == null) {
                animal.setChildren(animal.getChildren() + 1);
                return loc;
            }
        }
        return null;
    }
}

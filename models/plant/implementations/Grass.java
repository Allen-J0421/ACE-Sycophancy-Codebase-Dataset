package models.plant.implementations;

import java.util.List;

import field.Actor;
import field.Location;
import models.plant.Plant;

/**
 * Grass data model.
 *
 */
public class Grass extends Plant {
    public Grass(Location location) {
        super("grass", location, new int[] {0, 255, 0});

        // Changing some default parameters
        BREEDING_PROBABILITY = 0.05;
        ENERGY_EFFICIENCY = 0.4;
    }

    /**
     * Breed according to breeding logic, enact its behaviours and grow.
     */
    @Override
    public void act(List<Actor> newActors) {
        //if (field.getDayState() == DayState.NIGHT) return;
        Location location = breedingBehaviour.act(newActors, BREEDING_PROBABILITY);
        if (location != null) {
            Grass grass = new Grass(location);
            field.setObjectAt(location, grass);
            newActors.add(grass);
        }
        aliveBehaviour.act();
        grow();
    }
}

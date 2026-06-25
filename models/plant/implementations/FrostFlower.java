package models.plant.implementations;

import java.util.List;

import field.Actor;
import field.Location;
import models.plant.Plant;

/**
 * Frost flower data model.
 *
 */
public class FrostFlower extends Plant {
    public FrostFlower(Location location) {
        super("frost_flower", location, new int[] {200,233,233});

        // Changing some default parameters
        BREEDING_PROBABILITY = 0.07;
        ENERGY_EFFICIENCY = 0.7;
        OPTIMAL_TEMPERATURE = 0;
    }

    /**
     * Breed according to breeding logic, enact its behaviours and grow.
     */
    @Override
    public void act(List<Actor> newActors) {
        Location location = breedingBehaviour.act(newActors, BREEDING_PROBABILITY);
        if (location != null) {
            FrostFlower offspring = new FrostFlower(location);
            field.setObjectAt(location, offspring);
            newActors.add(offspring);
        }
        aliveBehaviour.act();
        grow();
    }
}

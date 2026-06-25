package models.plant.implementations;

import java.util.List;

import field.Actor;
import field.Location;
import models.plant.Plant;
import models.plant.behaviour.breeding.bfs.ZigZagBfsBreeding;

/**
 * Cactus data model.
 *
 */
public class Cactus extends Plant {
    public Cactus(Location location) {
        super("cactus", location, new int[] {92, 117, 94});

        // Changing some default parameters
        OPTIMAL_TEMPERATURE = 70;
        BREEDING_PROBABILITY = 0.05;
        ENERGY_EFFICIENCY = 0.6;
        ENERGY_LOSS = 0;

        breedingBehaviour = new ZigZagBfsBreeding(location.getRow(), location.getCol());
    }

    /**
     * Breed according to breeding logic, enact its behaviours and grow.
     */
    @Override
    public void act(List<Actor> newActors) {
        Location location = breedingBehaviour.act(newActors, BREEDING_PROBABILITY);
        if (location != null) {
            Cactus offspring = new Cactus(location);
            field.setObjectAt(location, offspring);
            newActors.add(offspring);
        }
        aliveBehaviour.act();
        grow();
    }
}
